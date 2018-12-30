package com.avelanarius.models;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutableGenerator implements Serializable {

    public static final long serialVersionUID = -2061453742071984816L;
    private String executablePath;

    public ExecutableGenerator() {
    }

    public ExecutableGenerator(String executablePath) {
        this.executablePath = executablePath;
    }

    public String getExecutablePath() {
        return this.executablePath;
    }

    public void setExecutablePath(String executablePath) {
        this.executablePath = executablePath;
    }

    public void generateOutput(TextFileInput textFileInput, TextFileOutput textFileOutput) {
        try {
            Process process = this.setupProcess();
            this.loadInputIntoProcess(process, textFileInput);
            this.saveOutputFromProcess(process, textFileOutput);
        } catch (IOException e) {
            Logger.getLogger(ExecutableGenerator.class.getName()).log(Level.SEVERE, "Error while generating Test: " + textFileInput.toString(), e);
        }
    }

    private Process setupProcess() throws IOException {
        try {
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(Paths.get(this.getExecutablePath()), perms);
        } catch (UnsupportedOperationException ex) {
        }
        return new ProcessBuilder(this.getExecutablePath()).start();
    }

    private void loadInputIntoProcess(Process process, TextFileInput textFileInput) {
        Runnable loadTask = () -> {
            try {
                process.getOutputStream().write(textFileInput.getContents());
                process.getOutputStream().flush();
                process.getOutputStream().close();
            } catch (IOException ex) {
                Logger.getLogger(ExecutableGenerator.class.getName()).log(Level.SEVERE, "Error while generating Test: " + textFileInput.toString(), ex);
            }
        };
        Thread loadThread = new Thread(loadTask);
        loadThread.start();
    }

    private void saveOutputFromProcess(Process process, TextFileOutput textFileOutput) throws IOException {
        StringBuilder outputContents = new StringBuilder();
        this.readOutputFromProcess(process, textFileOutput, outputContents);
        textFileOutput.setContents(outputContents.toString());
    }

    private void readOutputFromProcess(Process process, TextFileOutput textFileOutput, StringBuilder outputContents) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.parseLine(line, outputContents, textFileOutput);
            }
        }
    }

    private void parseLine(String line, StringBuilder outputContents, TextFileOutput textFileOutput) {
        if (line.trim().startsWith(TextFileOutput.additionalInformationLineStart)) {
            String[] additionalInformation = line.split(" ");
            textFileOutput.getAdditionalInformation().put(additionalInformation[1], Long.valueOf(additionalInformation[2]));
        } else {
            outputContents.append(line).append("\n");
        }
    }

    @Override
    public String toString() {
        return "ExecutableGenerator{"
                + "executablePath='" + this.executablePath + '\''
                + '}';
    }
}
