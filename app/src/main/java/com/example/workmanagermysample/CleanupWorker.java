package com.example.workmanagermysample;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.workmanagermysample.Constants.OUTPUT_PATH;

public class CleanupWorker extends Worker {
    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
public static final String TAG = CleanupWorker.class.getSimpleName();
    @NonNull
    @Override
    public Worker.Result doWork() {
        Context applicationContext =getApplicationContext();

    WorkerUtils.makeStatusNotification("Cleaning up old teporary files",applicationContext);
    WorkerUtils.sleep();

    try {
        File outputDirectory = new File(applicationContext.getFilesDir(), OUTPUT_PATH);

        if(outputDirectory.exists()){
    File[] entries = outputDirectory.listFiles();
            if(entries != null && entries.length > 0){
                for (File entry: entries){
                String name =entry.getName();
                    if(!TextUtils.isEmpty(name) && name.endsWith(".png")){
                        boolean deleted =entry.delete();
                        Log.i(TAG, String.format("Deleted %s - %s",name,deleted));
                    }
                }
            }
        }
            return Result.success();

    }catch (Exception exception){
        Log.e(TAG, "Error cleaning up", exception);
        return Result.failure();
    }

    }
}
