package com.avelanarius.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFileOutput implements Serializable {
    public static final long serialVersionUID = 8376840105439353105L;

    private String name;
    private String path;

    private Map<String, Long> additionalInformation = new HashMap<String, Long>();
    public static final String additionalInformationLineStart = "@@@@@ ";

    public TextFileOutput() {
    }

    public TextFileOutput(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Long> getAdditionalInformation() {
        return this.additionalInformation;
    }

    public void setAdditionalInformation(Map<String, Long> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setContents(String outputContents) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.getPath()))) {
            bw.write(outputContents);
            bw.close();
        } catch (IOException e) {
            Logger.getLogger(TextFileOutput.class.getName()).log(Level.SEVERE, "Error while saving contents of: " + this.toString(), e);
        }
    }

    @Override
    public String toString() {
        return "TextFileOutput{" +
                "name='" + this.name + '\'' +
                ", path='" + this.path + '\'' +
                ", additionalInformation=" + this.additionalInformation +
                '}';
    }

}
