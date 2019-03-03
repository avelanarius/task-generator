package com.avelanarius.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.avelanarius.models.GenerationInfo;
import com.avelanarius.models.TaskSuiteReport;
import com.avelanarius.models.TextFileInput;
import com.avelanarius.models.TextFileOutput;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelReportGenerator implements Runnable {

    private ArrayList<TaskSuiteReport> taskSuiteReport = new ArrayList<>();
    private ArrayList<Thread> threads;
    private AmazonS3 s3;

    public ParallelReportGenerator(int numThreads) {
        this.threads = new ArrayList<>();
        this.s3 = new AmazonS3Client();
        this.s3.setRegion(com.amazonaws.regions.Region.getRegion(com.amazonaws.regions.Regions.EU_CENTRAL_1));
        this.startThreads(numThreads);
    }

    private void startThreads(int numThreads) {
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(this);
            thread.start();
            this.threads.add(thread);
        }
    }

    @Override
    public void run() {
        while (true) {
            TaskSuiteReport currentTaskSuiteReport = this.getCurrentTaskSuiteReport();
            if (currentTaskSuiteReport != null) {
                TextFileInput textFileInput = this.getCurrentTextFileInput(currentTaskSuiteReport);
                GenerationInfo generationInfo = this.generate(textFileInput, currentTaskSuiteReport);
                if (generationInfo != null) {
                    this.saveGenerationInfo(generationInfo, currentTaskSuiteReport);
                    Logger.getLogger(ParallelReportGenerator.class.getName()).info("Succesfully generated GenerationInfo: " + generationInfo.toString());
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParallelReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private TaskSuiteReport getCurrentTaskSuiteReport() {
        synchronized (this.taskSuiteReport) {
            int reportsSize = this.taskSuiteReport.size();
            if (reportsSize > 0) {
                return this.taskSuiteReport.get(new SecureRandom().nextInt(reportsSize));
            }
        }
        return null;
    }

    public void replaceTaskSuiteReports(ArrayList<TaskSuiteReport> newReports) {
        ArrayList<TaskSuiteReport> reportsToSync = new ArrayList<>();
        synchronized (this.taskSuiteReport) {
            for (TaskSuiteReport oldReport : this.taskSuiteReport) {
                if (!newReports.contains(oldReport)) {
                    reportsToSync.add(oldReport);
                }
            }
            this.taskSuiteReport.clear();
            this.taskSuiteReport.addAll(newReports);
        }
        for (TaskSuiteReport reportToSync : reportsToSync) {
            synchronized (reportToSync) {
                String path = reportToSync.generatePath();
                reportToSync.save(path);
                reportToSync.upload(this.s3, path);
            }
        }
    }

    private TextFileInput getCurrentTextFileInput(TaskSuiteReport currentTaskSuiteReport) {
        int inputFilesSize = currentTaskSuiteReport.getTaskSuite().getInputFiles().size();
        int randomIndex = new SecureRandom().nextInt(inputFilesSize);
        return currentTaskSuiteReport.getTaskSuite().getInputFiles().get(randomIndex);
    }

    private GenerationInfo generate(TextFileInput textFileInput, TaskSuiteReport currentTaskSuiteReport) {
        try {
            TextFileOutput textFileOutput = currentTaskSuiteReport.getTaskSuite().generateTextFileOutputTest(textFileInput);

            GenerationInfo generationInfo = new GenerationInfo();
            generationInfo.setInputFile(textFileInput);
            generationInfo.setOutputFile(textFileOutput);
            generationInfo.setGenerateStartDate(Calendar.getInstance().getTime());

            currentTaskSuiteReport.getTaskSuite().getExecutableGenerator().generateOutput(textFileInput, textFileOutput);

            generationInfo.setGenerateEndDate(Calendar.getInstance().getTime());
            return generationInfo;
        } catch (Exception ex) {
            Logger.getLogger(ParallelReportGenerator.class.getName()).log(Level.SEVERE, "Błąd przy generowaniu!", ex);
        }
        return null;
    }

    private void saveGenerationInfo(GenerationInfo generationInfo, TaskSuiteReport currentTaskSuiteReport) {
        synchronized (currentTaskSuiteReport) {
            currentTaskSuiteReport.getGenerationInfos().add(generationInfo);
            if (currentTaskSuiteReport.shouldSave()) {
                String path = currentTaskSuiteReport.generatePath();
                currentTaskSuiteReport.save(path);
                currentTaskSuiteReport.upload(this.s3, path);
            }
        }
    }
}
