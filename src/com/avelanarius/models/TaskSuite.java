package com.avelanarius.models;

import com.avelanarius.controller.ExecutableGenerator;
import java.io.Serializable;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class TaskSuite implements Serializable {
    public static final long serialVersionUID = -4995709324172841293L;

    private String name;
    private String description;
    
    private int version;

    private String path;
    private ArrayList<TextFileInput> inputFiles = new ArrayList<>();
    private ExecutableGenerator executableGenerator;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<TextFileInput> getInputFiles() {
        return this.inputFiles;
    }

    public void setInputFiles(ArrayList<TextFileInput> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public ExecutableGenerator getExecutableGenerator() {
        return this.executableGenerator;
    }

    public void setExecutableGenerator(ExecutableGenerator executableGenerator) {
        this.executableGenerator = executableGenerator;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TextFileOutput generateTextFileOutputTest(TextFileInput textFileInput) {
        TextFileOutput outputTest = new TextFileOutput();
        String filename = textFileInput.getName() + "."  + Math.abs(new SecureRandom().nextInt(Integer.MAX_VALUE)) + Thread.currentThread().getId() + ".out";
        outputTest.setName(filename);
        outputTest.setPath(Paths.get(this.getPath(), filename).toAbsolutePath().toString());
        return outputTest;
    }

    public String generateSuiteReportPath() {
        String filename = this.getName() + "."  + ComputerIDGenerator.getID() +  Math.abs(new SecureRandom().nextInt(Integer.MAX_VALUE)) + ".srep.zip";
        return Paths.get(this.getPath(), filename).toAbsolutePath().toString();
    }

    public void setAllFilesToPath(String path) {
        this.setPath(path);
        this.getExecutableGenerator().setExecutablePath(path + Paths.get(this.getExecutableGenerator().getExecutablePath()).getFileName().toString());
        for (TextFileInput textFileInput : this.getInputFiles()) {
            textFileInput.setPath(path + Paths.get(textFileInput.getPath()).getFileName().toString());
        }
    }

    @Override
    public String toString() {
        return "TaskSuite{" +
                "name='" + this.name + '\'' +
                ", path='" + this.path + '\'' +
                ", inputFiles=" + this.inputFiles +
                ", executableGenerator=" + this.executableGenerator +
                '}';
    }

}
