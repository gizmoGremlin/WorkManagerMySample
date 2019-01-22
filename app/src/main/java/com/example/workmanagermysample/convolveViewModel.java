package com.example.workmanagermysample;

import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static com.example.workmanagermysample.Constants.IMAGE_MANIPULATION_WORK_NAME;
import static com.example.workmanagermysample.Constants.KEY_IMAGE_URI;
import static com.example.workmanagermysample.Constants.TAG_OUTPUT;

public class convolveViewModel extends ViewModel {
    private WorkManager workManager;
    private Uri imageUri;
    private Uri outputUri;
    private LiveData<List<WorkInfo>> savedWorkInfo;

public convolveViewModel(){
    workManager = WorkManager.getInstance();
    savedWorkInfo = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT);


}

void applyConvolve(int convolveLevel){

//add workrequest to cleanup temp images
    WorkContinuation continuation = workManager.beginUniqueWork(
                                    IMAGE_MANIPULATION_WORK_NAME, ExistingWorkPolicy.REPLACE,
                                    OneTimeWorkRequest.from(CleanupWorker.class));

    OneTimeWorkRequest.Builder blurBuilder =
            new OneTimeWorkRequest.Builder(ConvolveImageWorker.class);

    blurBuilder.setInputData(createInputDataForUri());

    continuation = continuation.then(blurBuilder.build());

    Constraints constraints = new Constraints.Builder()
            .setRequiresCharging(true)
            .build();

    OneTimeWorkRequest save = new OneTimeWorkRequest.Builder(SaveImageToFileWorker.class)
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build();
    continuation = continuation.then(save);

    // Actually start the work
    continuation.enqueue();

}

    /**
     * Cancel work using the work's unique name
     */
    void cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME);
    }


    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (imageUri != null) {
            builder.putString(KEY_IMAGE_URI, imageUri.toString());
        }
        return builder.build();
    }
    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }
    void setImageUri(String uri) {
        imageUri = uriOrNull(uri);
    }

    void setOutputUri(String outputImageUri) {
        outputUri = uriOrNull(outputImageUri);
    }

    Uri getImageUri() {
        return imageUri;
    }

    Uri getOutputUri() { return outputUri; }

    LiveData<List<WorkInfo>> getOutputWorkInfo() { return savedWorkInfo; }



}
