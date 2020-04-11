package com.example.noteskursach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.noteskursach.user_sign.LoginActivity;
import com.example.noteskursach.user_sign.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private Button btnReg, btnLog;
    ImageView swiftRxLogo;
    private FirebaseAuth fAuth;
    private Animation frombottom, fromtop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnReg = (Button) findViewById(R.id.start_reg_btn);
        btnLog = (Button) findViewById(R.id.start_log_btn);
        swiftRxLogo = (ImageView) findViewById(R.id.swiftRx);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        btnReg.setAnimation(frombottom);
        btnLog.setAnimation(frombottom);
        swiftRxLogo.setAnimation(fromtop);

        fAuth = FirebaseAuth.getInstance();
        updateUI();

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(regIntent);
    }

    private void login() {
        Intent logIntent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(logIntent);
    }

    private void updateUI() {
        if (fAuth.getCurrentUser() != null) {
            Log.i("StartActivity", "fAuth != null");
            Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            Log.i("StartActivity", "fAuth != null");
        }

    }

}
