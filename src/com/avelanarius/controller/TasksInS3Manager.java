package com.avelanarius.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.avelanarius.models.TaskInS3;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TasksInS3Manager {

    private AmazonS3 s3;

    public TasksInS3Manager(AmazonS3 s3) {
        this.s3 = s3;
    }

    public List<TaskInS3> listTasks() {
        ArrayList<TaskInS3> tasks = new ArrayList<>();
        try {
            ObjectListing objList = s3.listObjects("outgeneratordesc");
            List<S3ObjectSummary> summaries = objList.getObjectSummaries();

            for (S3ObjectSummary summary : summaries) {
                ObjectMetadata objectMetadata = s3.getObjectMetadata("outgeneratordesc", summary.getKey());
                Map userMetadataMap = objectMetadata.getUserMetadata();
                TaskInS3 newTask = new TaskInS3();
                newTask.setName(summary.getKey().replace(".zip", ""));
                newTask.fillFromMetadata(userMetadataMap);
                tasks.add(newTask);
            }
            return tasks;
        } catch (Exception ex) {
            Logger.getLogger(TasksInS3Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
