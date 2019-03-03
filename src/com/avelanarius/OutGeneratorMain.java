package com.avelanarius;

import com.avelanarius.controller.AllTasksManager;
import com.avelanarius.views.AnalyzeForm;
import com.avelanarius.views.MainWizardForm;
import java.util.Scanner;

public class OutGeneratorMain {

    public enum StartupMode {
        SERVER,
        WIZARD,
        ANALYZE
    }

    public static void main(String args[]) {
        StartupMode mode = readStartupMode(args);
        start(mode, args);
    }

    private static StartupMode readStartupMode(String[] args) {
        if (isServerMode(args)) {
            return StartupMode.SERVER;
        }

        System.out.println("Available modes: ");
        for (StartupMode mode : StartupMode.values()) {
            System.out.println(" - " + mode.toString());
        }
        
        System.out.print("Mode to start: ");

        Scanner stdIn = new Scanner(System.in);
        String modeString = stdIn.next();

        return StartupMode.valueOf(modeString);
    }

    private static void start(StartupMode mode, String[] args) {
        switch (mode) {
            case SERVER:
                AllTasksManager atm = new AllTasksManager();
                atm.run();
                break;
            case WIZARD:
                MainWizardForm.main(args);
                break;
            case ANALYZE:
                AnalyzeForm.main(args);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported mode");
        }
    }

    private static boolean isServerMode(String[] args) {
        return args.length > 0 && args[0].equals("server");
    }
}
