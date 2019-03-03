package com.avelanarius.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.avelanarius.models.TaskInS3;
import com.avelanarius.models.TaskSuite;
import com.avelanarius.models.TaskSuiteReport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllTasksManager {

    private static final long REFRESH_TIME = TimeUnit.SECONDS.toMillis(120);
    private AmazonS3 s3;
    private ParallelReportGenerator parallelReportGenerator;
    private TasksInS3Manager manager;
    private SecureRandom random = new SecureRandom();
    private HashMap<String, String> mapNameToDirectory = new HashMap<>();
    private HashMap<String, Date> mapNameToUpdatedTime = new HashMap<>();
    private HashMap<String, TaskSuiteReport> mapNameToTaskSuiteReport = new HashMap<>();

    public AllTasksManager() {
        this.s3 = new AmazonS3Client();
        int cores = Runtime.getRuntime().availableProcessors();
        this.parallelReportGenerator = new ParallelReportGenerator(cores);
        this.manager = new TasksInS3Manager(this.s3);
    }

    public void run() {
        while (true) {
            List<TaskInS3> taskList = this.manager.listTasks();
            if (taskList != null) {
                taskList.forEach(task -> this.downloadTask(task));
                ArrayList<TaskSuiteReport> newReports = new ArrayList<>();
                for (TaskInS3 task : taskList) {
                    if (mapNameToTaskSuiteReport.containsKey(task.getName())) {
                        newReports.add(mapNameToTaskSuiteReport.get(task.getName()));
                    }
                }
                this.parallelReportGenerator.replaceTaskSuiteReports(newReports);
            }
            try {
                Thread.sleep(REFRESH_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(AllTasksManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void downloadTask(TaskInS3 task) {
        if (!shouldDownloadTask(task)) {
            return;
        }
        ObjectInputStream ois = null;
        try {
            String directoryName = String.valueOf(Math.abs(random.nextLong()));
            s3.getObject(new GetObjectRequest("outgeneratordesc", task.getName() + ".zip"), new File(task.getName() + ".zip"));
            try {
                Runtime.getRuntime().exec("unzip -o " + task.getName() + ".zip -d " + directoryName).waitFor();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(AllTasksManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            String path = directoryName + File.separator + task.getName() + File.separator + task.getName() + ".desc";
            ois = new ObjectInputStream(new FileInputStream(path));
            TaskSuite taskSuite = (TaskSuite) ois.readObject();
            taskSuite.setAllFilesToPath(Paths.get(path).getParent().toAbsolutePath().toString() + File.separator);
            taskSuite.getExecutableGenerator().prepareGenerator();
            ois.close();

            TaskSuiteReport taskSuiteReport = new TaskSuiteReport();
            taskSuiteReport.setTaskSuite(taskSuite);

            this.mapNameToDirectory.put(task.getName(), directoryName);
            this.mapNameToUpdatedTime.put(task.getName(), task.getUpdatedAt());
            this.mapNameToTaskSuiteReport.put(task.getName(), taskSuiteReport);

        } catch (Exception ex) {
            Logger.getLogger(AllTasksManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AllTasksManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean shouldDownloadTask(TaskInS3 task) {
        if (!mapNameToDirectory.containsKey(task.getName())) {
            return true;
        }
        if (task.getUpdatedAt().after(mapNameToUpdatedTime.get(task.getName()))) {
            return true;
        }
        return false;
    }
}
