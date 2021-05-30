package p.edu.ug.myauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText nameET, ageET, mailET, passwdET;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nameET = (EditText) findViewById(R.id.fullnameRegi);
        ageET = (EditText) findViewById(R.id.ageRegi);
        mailET = (EditText) findViewById(R.id.emailRegi);
        passwdET = (EditText) findViewById(R.id.passwdRegi);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegi);
    }

    public void onRegisterClick(View v)
    {
        String name = nameET.getText().toString().trim();
        String age = ageET.getText().toString().trim();
        String mail = mailET.getText().toString().trim();
        String passwd = passwdET.getText().toString().trim();

        if(name.isEmpty()) {
            nameET.setError("Full name is required");
            nameET.requestFocus();
            return;
        }

        if(age.isEmpty()) {
            ageET.setError("Age is required");
            ageET.requestFocus();
            return;
        }

        if(Integer.parseInt(age) <= 0) {
            ageET.setError("Age must be above 0");
            ageET.requestFocus();
            return;
        }

        if(mail.isEmpty()) {
            mailET.setError("Mail is required");
            mailET.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            mailET.setError("Provide valid email");
            mailET.requestFocus();
            return;
        }

        if(passwd.isEmpty()) {
            passwdET.setError("Password is required");
            passwdET.requestFocus();
            return;
        }

        if(passwd.length() < 5) {
            passwdET.setError("Password must have 5 or more symbols");
            passwdET.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(mail,passwd)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        User user = new User(name,age,mail);
                        db.collection("Users").document(mAuth.getCurrentUser().getUid()).set(user)
                                .addOnFailureListener(err -> {
                                    Toast.makeText(this,err.getMessage(),Toast.LENGTH_LONG).show();
                                });
                        mAuth.getCurrentUser().sendEmailVerification();
                        Toast.makeText(this,"Registered. Please verify your mail before login",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, Login.class));
                    } else {
                        Toast.makeText(this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> System.out.println(e.getMessage()));

    }
}