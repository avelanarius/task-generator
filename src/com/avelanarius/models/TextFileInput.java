package com.avelanarius.models;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFileInput implements Serializable {
    public static final long serialVersionUID = -6264297817296680678L;

    private String name;
    private String path;

    public TextFileInput() {
    }

    public TextFileInput(String name, String path) {
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

    public byte[] getContents() {
        try {
            return Files.readAllBytes(Paths.get(this.getPath()));
        } catch (IOException ex) {
            Logger.getLogger(TextFileInput.class.getName()).log(Level.SEVERE, "Error reading: " + this.toString(), ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "TextFileInput{"
                + "name='" + this.name + '\''
                + ", path='" + this.path + '\''
                + '}';
    }
}
