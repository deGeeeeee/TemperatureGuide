package ch.zli.TempGuide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.EventListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private String heatMessage;
    private String coldMessage;
    private int coldCutoff;
    private int heatCutoff;
    private TextView tempView;
    private SensorManager mSensorManager;
    private Sensor tempSensor;
    private boolean tempSensorAvailability;
    private Button settingsButton;
    private ConstraintLayout layout;
    private Context context;
    private int state;
    private SharedPreferences sp;
    private static final String CHANNEL_ID = "defaultChannel";
    private static final String CHANNEL_NAME = "Default Channel";
    private NotificationManager notificationManager;
    private double temperature;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempView = findViewById(R.id.tempView);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        settingsButton = findViewById(R.id.SettingsButton);
        layout = findViewById(R.id.background);
        context = getBaseContext();
        state = 1;

        sp = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        coldCutoff = sp.getInt("coldBar", 0);
        heatCutoff = sp.getInt("heatBar", 0);

        settingsButton.setOnClickListener(v -> {
            openSettings();
        });



        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            tempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            tempSensorAvailability=true;
        } else {
            tempView.setText("no Temperature Sensor");
            tempSensorAvailability=false;
        }

        this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        state = 1;
        tempView.setTextColor(Color.BLACK);
        setBackgroundImage("grass");
        temperature=15;
        tempView.setText(temperature+"C");

    }


    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        coldCutoff = sp.getInt("coldBar", 0);
        heatCutoff = sp.getInt("heatBar", 0);
        coldMessage = sp.getString("cold", "no Cold message");
        heatMessage = sp.getString("heat", "no Heat message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("TempGuide")
                .setContentText("TempGuide is now online!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        temperature = sensorEvent.values[0];
        tempView.setText(temperature+"C");
        if(temperature <= coldCutoff) {
            if(state != 0) {
                state = 0;
                tempView.setTextColor( Color. rgb(255, 100, 0));
                builder.setContentText(coldMessage);
                notificationManager.notify(1, builder.build());
            }
            setBackgroundImage("ice");
        } else if (temperature >= 25+heatCutoff) {
            if(state != 2) {
                state = 2;
                tempView.setTextColor(Color.CYAN);
                builder.setContentText(heatMessage);
                notificationManager.notify(1, builder.build());
            }
            setBackgroundImage("fire");
        } else {
            if(state != 1) {
                state = 1;
                tempView.setTextColor(Color.BLACK);

            }
            setBackgroundImage("grass");
        }
    }

    public void setBackgroundImage(String img) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(img, "drawable", context.getPackageName());
        Drawable d = resources.getDrawable(resourceId);
        layout.setBackground(d);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tempSensorAvailability) {
            mSensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tempSensorAvailability) {
            mSensorManager.unregisterListener(this);
        }
    }
}