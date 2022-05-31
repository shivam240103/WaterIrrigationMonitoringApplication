package com.shivam.waterirrigationmonitoringapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.DragStartHelper;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button lButton;
    private EditText email;
    private EditText passw;
    private TextView forgetPass;
    private TextView spButton;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lButton = (Button) findViewById(R.id.loginButton);
        spButton = (TextView) findViewById(R.id.signupButton);
        email = (EditText) findViewById(R.id.editEmailAddress);
        passw = (EditText) findViewById(R.id.editPassword);
        forgetPass = (TextView) findViewById(R.id.frgtPasswordID);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {

            mUser = firebaseAuth.getCurrentUser();

            if(mUser != null){
                //Toast.makeText(MainActivity.this, "Signed IN!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this, "NOT Signed IN!", Toast.LENGTH_SHORT).show();
            }
        };

        spButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
                finish();
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLinktoMail();
            }
        });

        lButton.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(passw.getText().toString())){

                String eml = email.getText().toString();
                String pwd = passw.getText().toString();

                login(eml, pwd);
            }
            else{

            }
        });
    }

    private void sendLinktoMail() {
        String Emailt = email.getText().toString();
        if(Patterns.EMAIL_ADDRESS.matcher(Emailt).matches() && Emailt.length()>8){
            AlertDialog.Builder passwordReset = new AlertDialog.Builder(this);
            passwordReset.setTitle("Reset Password ?");
            passwordReset.setMessage("Press Yes to receive the reset link");
            passwordReset.setPositiveButton("YES",(dialog, which) ->
            {
                String resetEmail = email.getText().toString();
                mAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Reset Email link has been sent to your emailId",Toast.LENGTH_SHORT).show();
                    }
                });
            });
            passwordReset.setNegativeButton("NO",(dialog, which) -> {});
            passwordReset.create().show();
        }
        else{
            email.setError("Please Enter a valid Email");
        }
    }

    private void login(String eml, String pwd) {
        mAuth.signInWithEmailAndPassword(eml, pwd).addOnCompleteListener(this, task -> {

            if(task.isSuccessful()){
                Toast.makeText(MainActivity.this, "Signed in !!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, PostListActivity.class));
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}