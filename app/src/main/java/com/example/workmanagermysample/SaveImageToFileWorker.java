package com.example.workmanagermysample;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.workmanagermysample.Constants.KEY_IMAGE_URI;

public class SaveImageToFileWorker extends Worker {
    public SaveImageToFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static final String TAG = SaveImageToFileWorker.class.getSimpleName();
    public static final String TITLE = "Convolved image";
public static final SimpleDateFormat DATE_FORMATTER =
        new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());

    @NonNull
    @Override
    public Worker.Result doWork() {

        Context applicationContext = getApplicationContext();
        WorkerUtils.makeStatusNotification("Saving Image",applicationContext);
        WorkerUtils.sleep();

        ContentResolver resolver =applicationContext.getContentResolver();

        try {
            String resourceUri = getInputData()
                    .getString(Constants.KEY_IMAGE_URI);
            Bitmap bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));
            String imageUrl = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, TITLE, DATE_FORMATTER.format(new Date()));
            if (TextUtils.isEmpty(imageUrl)) {
                Log.e(TAG, "Writing to MediaStore failed");
                return Result.failure();
            }
            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, imageUrl)
                    .build();
            return Result.success(outputData);
        } catch (Exception exception) {
            Log.e(TAG, "Unable to save image to Gallery", exception);
            return Result.failure();
        }
    }

    }

