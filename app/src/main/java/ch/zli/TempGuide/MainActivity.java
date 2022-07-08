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
    private String heatImageKey;
    private String coldImageKey;
    private int coldCutoff;
    private int heatCutoff;
    private TextView tempView;
    private SensorManager mSensorManager;
    private Sensor tempSensor;
    private boolean tempSensorAvailability;
    private Button settingsButton;
    private Drawable background;
    private ConstraintLayout layout;
    private Context context;
    private StorageReference mStorageRef;
    private int state;
    private SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempView = findViewById(R.id.tempView);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        settingsButton = findViewById(R.id.SettingsButton);
        layout = findViewById(R.id.background);
        context = getBaseContext();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        state = 1;

        sp = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        coldCutoff = sp.getInt("coldbar", 0);
        heatCutoff = sp.getInt("heatBar", 0);
        coldMessage = sp.getString("cold", "no Cold message");
        heatMessage = sp.getString("heat", "no Heat message");

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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(MainActivity.this, "blabla");
        NotificationManagerCompat managerCompat =  NotificationManagerCompat.from(MainActivity.this);
        builder.setContentText("test");
        builder.setContentTitle("title");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        managerCompat.notify(1, builder.build());

    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(MainActivity.this, "blabla");
        NotificationManagerCompat managerCompat =  NotificationManagerCompat.from(MainActivity.this);
        builder.setContentText("test");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        double temperature = sensorEvent.values[0];
        tempView.setText(temperature+"C");
        if(temperature <= 0) {
            if(state != 0) {
                state = 0;
                builder.setContentText(coldMessage);
                managerCompat.notify(1, builder.build());
            }
            setBackgroundImage("ice");
        } else if (temperature >= 30) {
            if(state != 2) {
                state = 2;
                builder.setContentText(heatMessage);
                managerCompat.notify(1, builder.build());
            }
            setBackgroundImage("fire");
        } else {
            if(state != 1) {
                state = 1;
            }
            layout.setBackground(null);
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