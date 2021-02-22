package com.blueprint.tiltsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.blueprint.tiltsensor.data_model.Statistics;
import com.blueprint.tiltsensor.sensor_access.StatisticsViewModel;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;

import static com.blueprint.tiltsensor.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    private Button welcomeButton;
    private Button exerciseButton;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtOutput = findViewById(R.id.txtOutput);
        TextView alertTextView = findViewById(R.id.alertTextview);
        ImageView imageView = findViewById(R.id.imageView);

        notificationManager = NotificationManagerCompat.from(this);

        welcomeButton = (Button) findViewById(R.id.welcomeButton);
        welcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2(v);
            }
        });

        exerciseButton = (Button) findViewById(R.id.exerciseButton);
        exerciseButton.setBackgroundColor(Color.TRANSPARENT);
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity3(v);
            }
        });

        StatisticsViewModel statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        statisticsViewModel.getStatistics().observe(this, new Observer<Statistics>() {

            long timeCount = 0;

            @Override
            public void onChanged(@Nullable Statistics statistics) {
                int angle = Math.abs((int)statistics.getRollAngle());
                txtOutput.setText(String.valueOf(angle) + "°");

                if (angle <= 35){
                    txtOutput.setTextColor(0xFF008037);
                    alertTextView.setText("Your posture is okay!");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.green_ok_neck, null));

                    timeCount = 0;
                    exerciseButton.setBackgroundColor(Color.TRANSPARENT);
                }
                else if (angle > 35 && angle <= 60){
                    txtOutput.setTextColor(0xFFFFDE59);
                    alertTextView.setText("Be careful about your posture! It's not ideal.");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.yellow_wrapped_neck, null));

                    timeCount = 0;
                    exerciseButton.setBackgroundColor(Color.TRANSPARENT);
                }
                else{
                    txtOutput.setTextColor(0xFFFF1616);
                    alertTextView.setText("You are straining your neck. Lift your head!");
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.red_warning_neck, null));

                    if (timeCount == 0) {
                        timeCount = statistics.getTimeElapsed() / 1000;
                    }

                    if ((statistics.getTimeElapsed() / 1000) >= (timeCount + 180)) {
                        //Audrey's portion
                        exerciseButton.setBackgroundColor(0xFFFF1616);
                        sendOnChannel2();
                        timeCount = 0;
                    }
                }

            }
        });
    }

    public void openActivity2 (View v) {
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

    public void openActivity3 (View v) {
        Intent intent = new Intent(this, Activity3.class);
        startActivity(intent);
    }

    public void sendOnChannel1() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("Notification Title")
                .setContentText("Notification")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_two)
                .setContentTitle("Warning!")
                .setContentText("Adjust your posture.")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();

        notificationManager.notify(2, notification);
    }
}