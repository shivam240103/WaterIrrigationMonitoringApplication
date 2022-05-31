package com.shivam.waterirrigationmonitoringapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createAccountBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ImageView backBtn;
    private ImageView passview;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        mDatabaseReference.setValue("");

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);

        firstName = (EditText) findViewById(R.id.editFirstName);
        lastName = (EditText) findViewById(R.id.editLastName);
        email = (EditText) findViewById(R.id.editSpEmail);
        password = (EditText) findViewById(R.id.editSpPassword);
        createAccountBtn = (Button) findViewById(R.id.SpButton);
        passview = (ImageView) findViewById(R.id.eyeicon);
        backBtn = (ImageView) findViewById(R.id.signBackID);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                finish();
            }
        });

        passview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass(v);
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }


    private void ShowHidePass(View v){
        if(v.getId()==R.id.eyeicon){

            if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(v)).setImageResource(R.drawable.closeeye);

                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(v)).setImageResource(R.drawable.eye);
                
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    private void createNewAccount() {

        String name = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String em  = email.getText().toString().trim();
        String pwd = password.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pwd)){

            mProgressDialog.setMessage("Creating Account....");
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(em, pwd).addOnSuccessListener(authResult -> {
                if (authResult != null){
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DatabaseReference currentUserdb = mDatabaseReference.child(userId);
                    currentUserdb.child("firstname").setValue(name);
                    currentUserdb.child("lastname").setValue(lname);
                    mProgressDialog.dismiss();


                    Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }
}