package ch.zli.TempGuide;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.EventListener;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView test;
    private TextView coldMessageView;
    private TextView heatMessageView;
    private SeekBar coldBar;
    private SeekBar heatBar;
    private Button saveButton;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.setTitle("Settings");
        coldMessageView = findViewById(R.id.coldMessageView);
        heatMessageView = findViewById(R.id.heatMessageView);
        coldBar = findViewById(R.id.coldBar);
        heatBar = findViewById(R.id.heatBar);
        coldBar.setMax(10);
        heatBar.setMax(10);


        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        sp = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        coldMessageView.setText(sp.getString("cold", "it's cold, you should get a blanket!"));
        heatMessageView.setText(sp.getString("heat", "it's hot, you should get a water bottle!"));
        coldBar.setProgress(sp.getInt("coldBar", 0));
        heatBar.setProgress(sp.getInt("heatBar", 0));





    }

    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cold", coldMessageView.getText().toString());
        editor.putString("heat", heatMessageView.getText().toString());
        editor.putInt("coldBar", coldBar.getProgress());
        editor.putInt("heatBar", heatBar.getProgress());
        editor.apply();
    }


    public SharedPreferences getSp() {
        return sp;
    }

}