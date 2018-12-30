package com.avelanarius.models;

import java.io.Serializable;
import java.util.Date;

public class GenerationInfo implements Serializable, Comparable<GenerationInfo> {
    public static final long serialVersionUID = 5207024837630814868L;
    
    private TextFileInput inputFile;
    private TextFileOutput outputFile;

    private Date generateStartDate;
    private Date generateEndDate;

    public TextFileInput getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(TextFileInput inputFile) {
        this.inputFile = inputFile;
    }

    public TextFileOutput getOutputFile() {
        return this.outputFile;
    }

    public void setOutputFile(TextFileOutput outputFile) {
        this.outputFile = outputFile;
    }

    public Date getGenerateStartDate() {
        return this.generateStartDate;
    }

    public void setGenerateStartDate(Date generateStartDate) {
        this.generateStartDate = generateStartDate;
    }

    public Date getGenerateEndDate() {
        return this.generateEndDate;
    }

    public void setGenerateEndDate(Date generateEndDate) {
        this.generateEndDate = generateEndDate;
    }
    
    public long getGenerationLengthMs() {
        return this.generateEndDate.getTime() - this.generateStartDate.getTime();
    }

    @Override
    public String toString() {
        return "GenerationInfo{" +
                "inputFile=" + this.inputFile +
                ", outputFile=" + this.outputFile +
                ", generateStartDate=" + this.generateStartDate +
                ", generateEndDate=" + this.generateEndDate +
                '}';
    }

    @Override
    public int compareTo(GenerationInfo o) {
        if (this.getInputFile().getName().equals(o.getInputFile().getName())) {
            return this.getGenerateEndDate().compareTo(o.getGenerateEndDate());
        }
        return this.getInputFile().getName().compareTo(o.getInputFile().getName());
    }
}
