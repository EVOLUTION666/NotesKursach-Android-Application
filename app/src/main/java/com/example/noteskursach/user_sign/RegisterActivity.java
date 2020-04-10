package com.example.noteskursach.user_sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.noteskursach.MainActivity;
import com.example.noteskursach.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button btnReg;
    private TextInputLayout inName, inEmail, inPass;
    private FirebaseAuth fAuth;
    private DatabaseReference fUserDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnReg = (Button) findViewById(R.id.btn_reg);
        inName = (TextInputLayout) findViewById(R.id.input_reg_name);
        inEmail = (TextInputLayout) findViewById(R.id.input_reg_email);
        inPass = (TextInputLayout) findViewById(R.id.input_reg_pass);

        fAuth = FirebaseAuth.getInstance();
        fUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = inName.getEditText().getText().toString().trim();
                String uEmail = inEmail.getEditText().getText().toString().trim();
                String uPass = inPass.getEditText().getText().toString().trim();

                registerUser(uName, uEmail, uPass);
            }
        });
    }

    private void registerUser(final String name, String email, String password) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing tour request, please wait...");

        progressDialog.show();

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fUserDatabase.child(fAuth.getCurrentUser()
                                    .getUid()).child("basic")
                                    .child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();

                                        Intent mainIntent= new Intent(RegisterActivity.this, MainActivity.class);
startActivity(mainIntent);
finish();
Toast.makeText(RegisterActivity.this,"User created: ",Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this,
                                                "ERROR: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "ERROR: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

}
