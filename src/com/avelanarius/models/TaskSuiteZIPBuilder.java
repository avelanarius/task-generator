package com.avelanarius.models;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TaskSuiteZIPBuilder {

    private TaskSuite taskSuite;
    private String filename;

    public TaskSuite getTaskSuite() {
        return this.taskSuite;
    }

    public void setTaskSuite(TaskSuite taskSuite) {
        this.taskSuite = taskSuite;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void generateZIP() {
        try {
            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(this.filename));
            this.addFileToZIP(this.taskSuite.getExecutableGenerator().getExecutablePath(), outputStream);
            for (TextFileInput textFileInput : this.taskSuite.getInputFiles()) {
                this.addFileToZIP(textFileInput.getPath(), outputStream);
            }
            this.taskSuite.setAllFilesToPath(this.taskSuite.getName() + "/");
            this.addObjectToFile(this.taskSuite.getName() + ".desc", this.taskSuite, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(TaskSuiteZIPBuilder.class.getName()).log(Level.SEVERE, "Error while generating ZIP", e);
        } catch (IOException e) {
            Logger.getLogger(TaskSuiteZIPBuilder.class.getName()).log(Level.SEVERE, "Error while generating ZIP", e);
        }
    }

    private void addFileToZIP(String path, ZipOutputStream outputStream) throws IOException {
        ZipEntry file = new ZipEntry(this.taskSuite.getName() + File.separator + Paths.get(path).getFileName().toString());
        outputStream.putNextEntry(file);
        outputStream.write(Files.readAllBytes(Paths.get(path)));
        outputStream.closeEntry();
    }

    private void addObjectToFile(String path, Object object, ZipOutputStream outputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        ZipEntry testSuiteDescription = new ZipEntry(this.taskSuite.getName() + File.separator + path);
        outputStream.putNextEntry(testSuiteDescription);
        objectOutputStream.writeObject(object);
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.closeEntry();
    }

    @Override
    public String toString() {
        return "TaskSuiteZIPBuilder{"
                + "taskSuite=" + this.taskSuite
                + ", filename='" + this.filename + '\''
                + '}';
    }
}
