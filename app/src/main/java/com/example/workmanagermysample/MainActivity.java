package com.example.workmanagermysample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private ConvolveViewModel convolveViewModel;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button goButton, outputButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convolveViewModel = ViewModelProviders.of(this).get(ConvolveViewModel.class);

        imageView =findViewById(R.id.image_view);


    }
}
