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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText mailET, passwdET;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mailET = findViewById(R.id.email);
        passwdET = findViewById(R.id.passwd);
        progressBar = findViewById(R.id.progressBar);

    }

    public void onRegisterLoginClicked(View v)
    {
        startActivity(new Intent(this,Register.class));
    }

    public void onLoginClicked(View v)
    {
        String mail = mailET.getText().toString().trim();
        String passwd = passwdET.getText().toString().trim();

        if(mail.isEmpty()) {
            mailET.setError("Mail field is empty");
            mailET.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            mailET.setError("Provide valid e-mail");
            mailET.requestFocus();
            return;
        }

        if(passwd.isEmpty()) {
            passwdET.setError("Password field required");
            passwdET.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(mail,passwd)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user.isEmailVerified()) {
                            startActivity(new Intent(this,UserPanel.class));
                            Toast.makeText(this,"Welcome!",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,"Mail not verified, sending verification link to "+user.getEmail(),Toast.LENGTH_LONG).show();
                            user.sendEmailVerification();
                        }
                    } else {
                        Toast.makeText(this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> System.out.println(e.getMessage()));
    }

    public void onResetPasswdClicked(View v)
    {
        startActivity(new Intent(this,ResetPassword.class));
    }
}