package com.example.workmanagermysample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class ConvolveImageWorker extends Worker {
    private static final String TAG = ConvolveImageWorker.class.getSimpleName();

    public ConvolveImageWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParameters) {
        super(appContext, workerParameters);


    }

    @NonNull
    @Override
    public Worker.Result doWork() {
        Context context = getApplicationContext();

        Uri outputURI = null;
        try {
            Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.maxresdefault);

            Bitmap output = WorkerUtils.convolveBitmap(pic, context);

            outputURI = WorkerUtils.writeBitmapToFile(context, output);

            WorkerUtils.makeStatusNotification("Output is " + outputURI.toString(), context);
            return Worker.Result.success();
        } catch (Throwable throwable) {
            Log.e(TAG, "error on convolve", throwable);
            return Worker.Result.failure();
        }
    }
}
