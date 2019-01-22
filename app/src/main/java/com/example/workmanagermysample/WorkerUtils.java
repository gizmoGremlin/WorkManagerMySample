package com.example.workmanagermysample;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;

import static com.example.workmanagermysample.Constants.CHANNEL_ID;
import static com.example.workmanagermysample.Constants.DELAY_TIME_MILLIS;

final class WorkerUtils {

    public static final String TAG = WorkerUtils.class.getSimpleName();

    static void makeStatusNotification(String message, Context context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(Constants.NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        // Show the notification
        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, builder.build());
    }

    static void sleep(){
        try{
            Thread.sleep(DELAY_TIME_MILLIS,0);

        }catch (InterruptedException e){
            Log.d(TAG,e.getMessage());
        }
    }

    static Bitmap convolveBitmap(@NonNull Bitmap bitmap,
                                 @NonNull Context applicationContext) {

        RenderScript rsContext = null;
        try {

            // Create the output bitmap
            Bitmap output = Bitmap.createBitmap(
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            // Blur the image
            rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG);
            Allocation inAlloc = Allocation.createFromBitmap(rsContext, bitmap);
            Allocation outAlloc = Allocation.createTyped(rsContext, inAlloc.getType());
            ScriptIntrinsicConvolve3x3 theIntrinsic =
                    ScriptIntrinsicConvolve3x3.create(rsContext, Element.U8_4(rsContext));
            theIntrinsic.setCoefficients(new float[]
                    {
                            -2, -1, 0,
                            -1, 1, 1,
                            0, 1, 2
                    });
            theIntrinsic.setInput(inAlloc);
            theIntrinsic.forEach(outAlloc);
            outAlloc.copyTo(output);

            return output;
        } finally {
            if (rsContext != null) {
                rsContext.finish();
            }
        }
    }


    /**
     * Writes bitmap to a temporary file and returns the Uri for the file
     * @param applicationContext Application context
     * @param bitmap Bitmap to write to temp file
     * @return Uri for temp file with bitmap
     * @throws FileNotFoundException Throws if bitmap file cannot be found
     */
    static Uri writeBitmapToFile(
            @NonNull Context applicationContext,
            @NonNull Bitmap bitmap) throws FileNotFoundException {

        String name = String.format("convolve-output-%s.png", UUID.randomUUID().toString());
        File outputDir = new File(applicationContext.getFilesDir(), Constants.OUTPUT_PATH);
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // should succeed
        }
        File outputFile = new File(outputDir, name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
        return Uri.fromFile(outputFile);
    }

    private WorkerUtils() {
    }
}



