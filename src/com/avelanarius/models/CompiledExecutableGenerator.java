package com.avelanarius.models;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompiledExecutableGenerator extends ExecutableGenerator {

    @Override
    public void prepareGenerator() {
        String newPath = this.getExecutablePath().replace(".cpp", "");
        try {
            String command = "g++ -Ofast -march=native -o '"
                    + newPath + "' '" + this.getExecutablePath() + "'";
            Runtime.getRuntime().exec(new String[]{"bash", "-c", command}).waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CompiledExecutableGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setExecutablePath(newPath);
    }
}
