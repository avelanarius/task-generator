package com.avelanarius.models;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import java.io.*;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TaskSuiteReport implements Serializable {

    public static final long serialVersionUID = -4995709324172841293L;

    private static final long SAVE_TIME = TimeUnit.SECONDS.toMillis(120);

    private TaskSuite taskSuite;
    private ArrayList<GenerationInfo> generationInfos = new ArrayList<>();

    private Date lastTimeSaved = new Date();

    public TaskSuite getTaskSuite() {
        return this.taskSuite;
    }

    public void setTaskSuite(TaskSuite taskSuite) {
        this.taskSuite = taskSuite;
    }

    public ArrayList<GenerationInfo> getGenerationInfos() {
        return this.generationInfos;
    }

    public void setGenerationInfos(ArrayList<GenerationInfo> generationInfos) {
        this.generationInfos = generationInfos;
    }

    @Override
    public String toString() {
        return "TaskSuiteReport{"
                + "taskSuite=" + this.taskSuite
                + ", generationInfos=" + this.generationInfos
                + '}';
    }

    public String generatePath() {
        return this.getTaskSuite().generateSuiteReportPath();
    }

    public void save(String path) {
        this.lastTimeSaved = new Date();
        FileLock lock = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            lock = fileOutputStream.getChannel().lock();
            ZipOutputStream outputStream = new ZipOutputStream(fileOutputStream);
            this.addSuiteReportToZIP(outputStream);
            for (GenerationInfo generationInfo : this.getGenerationInfos()) {
                this.addGenerationInfoToZIP(outputStream, generationInfo);
            }
            this.generationInfos.clear();
            lock.release();
            outputStream.close();
        } catch (Exception e) {
            Logger.getLogger(TaskSuiteReport.class.getName()).log(Level.SEVERE, "Error writing TaskSuiteReport: " + this.toString(), e);
        } finally {
            if (lock != null && lock.isValid()) {
                try {
                    lock.release();
                } catch (IOException ex) {
                    Logger.getLogger(TaskSuiteReport.class.getName()).log(Level.SEVERE, "Error writing TaskSuiteReport: " + this.toString(), ex);
                }
            }
        }
    }

    public boolean shouldSave() {
        return new Date().getTime() - this.lastTimeSaved.getTime() >= TaskSuiteReport.SAVE_TIME;
    }

    public void upload(AmazonS3 s3, String path) {
        try {
            String filename = new File(path).getName();
            s3.putObject(new PutObjectRequest("outgenerator2", filename, new File(path)).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            Logger.getLogger(TaskSuiteReport.class.getName()).log(Level.SEVERE, "Error uploading TaskSuiteReport: " + this.toString(), e);
        }
    }

    private void addSuiteReportToZIP(ZipOutputStream outputStream) throws IOException {
        ZipEntry suiteReport = new ZipEntry(this.getTaskSuite().getName() + ".srep");
        outputStream.putNextEntry(suiteReport);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.closeEntry();
    }

    private void addGenerationInfoToZIP(ZipOutputStream outputStream, GenerationInfo generationInfo) throws IOException {
        ZipEntry generationEntry = new ZipEntry(Paths.get(generationInfo.getOutputFile().getPath()).getFileName().toString());
        outputStream.putNextEntry(generationEntry);
        outputStream.write(Files.readAllBytes(Paths.get(generationInfo.getOutputFile().getPath())));
        outputStream.closeEntry();
    }
}
