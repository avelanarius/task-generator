package com.avelanarius;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.avelanarius.models.AllTasksManager;
import com.avelanarius.models.ParallelReportGenerator;
import com.avelanarius.models.TaskSuite;
import com.avelanarius.models.TaskSuiteReport;
import com.avelanarius.views.AnalyzeForm;
import com.avelanarius.views.MainWizardForm;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutGeneratorMain {

    public static void main(String args[]) {
        Scanner stdIn = new Scanner(System.in);
        int mode;
        if (args.length > 0 && args[0].equals("server")) {
            mode = 4;
        } else {
            mode = stdIn.nextInt();
        }

        if (mode == 4) {
            AllTasksManager atm = new AllTasksManager();
            atm.run();
            /*String name = stdIn.next();
            AmazonS3 s3 = new AmazonS3Client();
            s3.getObject(new GetObjectRequest("outgeneratordesc", name + ".zip"), new File(name + ".zip"));
            
            try {
                Runtime.getRuntime().exec("unzip -o " + name + ".zip").waitFor();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(OutGeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ObjectInputStream ois = null;
            try {
                String path = name + File.separator + name + ".desc";
                ois = new ObjectInputStream(new FileInputStream(path));
                TaskSuite taskSuite = (TaskSuite)ois.readObject();
                taskSuite.setAllFilesToPath(Paths.get(path).getParent().toAbsolutePath().toString() + File.separator);
                ois.close();
                
                TaskSuiteReport taskSuiteReport = new TaskSuiteReport();
                taskSuiteReport.setTaskSuite(taskSuite);
                
                int cores = Runtime.getRuntime().availableProcessors();
                ParallelReportGenerator parallelReportGenerator = new ParallelReportGenerator(taskSuiteReport, cores);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(OutGeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (ois != null) ois.close();
                } catch (IOException ex) {
                    Logger.getLogger(OutGeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
        } else if (mode == 0) {
            ObjectInputStream ois = null;
            try {
                String path = stdIn.next();
                ois = new ObjectInputStream(new FileInputStream(path));
                TaskSuite taskSuite = (TaskSuite) ois.readObject();
                taskSuite.setAllFilesToPath(Paths.get(path).getParent().toAbsolutePath().toString() + File.separator);
                ois.close();

                TaskSuiteReport taskSuiteReport = new TaskSuiteReport();
                taskSuiteReport.setTaskSuite(taskSuite);

                int cores = Runtime.getRuntime().availableProcessors();
                ParallelReportGenerator parallelReportGenerator = new ParallelReportGenerator(cores);
                parallelReportGenerator.replaceTaskSuiteReports(new ArrayList<TaskSuiteReport>(Arrays.asList(taskSuiteReport)));
            } catch (IOException ex) {
                Logger.getLogger(OutGeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(OutGeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    ois.close();
                } catch (IOException ex) {
                    Logger.getLogger(OutGeneratorMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (mode == 1) {
            MainWizardForm.main(args);
        } else {
            AnalyzeForm.main(args);
        }
    }
}
