package p.edu.ug.myauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class ResetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        mailET = (EditText) findViewById(R.id.emailResetPasswd);
    }

    public void onResetPasswdBtnClicked(View v)
    {
        String mail = mailET.getText().toString().trim();

        if(mail.isEmpty()) {
            mailET.setError("Mail is empty!");
            mailET.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            mailET.setError("Provide valid mail!");
            mailET.requestFocus();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(mail)
                .addOnCompleteListener(task -> {
                    if(task.getResult().getSignInMethods().isEmpty()) {
                        Toast.makeText(this,"Provided mail doesn't exist in database",Toast.LENGTH_LONG).show();
                    } else {
                        mAuth.sendPasswordResetEmail(mail)
                                .addOnFailureListener(err -> {
                                    Toast.makeText(this,err.getMessage(),Toast.LENGTH_LONG).show();
                                });
                        Toast.makeText(this,"Reset password mail sent to "+mail,Toast.LENGTH_LONG).show();
                    }
                });
    }
}