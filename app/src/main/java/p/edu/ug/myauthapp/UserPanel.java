package p.edu.ug.myauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

import java.util.Map;

public class UserPanel extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView nameET, ageET, mailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        nameET = findViewById(R.id.nameTextView);
        nameET.setText("");
        ageET = findViewById(R.id.ageTextView);
        ageET.setText("");
        mailET = findViewById(R.id.mailTextView);
        mailET.setText("");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot doc : task.getResult()) {
                            if(doc.getId().equals(mAuth.getUid())) {
                                nameET.append("Name: "+(String)doc.getData().get("name"));
                                ageET.append("Age: "+(String)doc.getData().get("age"));
                                mailET.append("Mail: "+(String)doc.getData().get("mail"));
                            }
                        }
                    } else {
                        System.out.println("There was error in query!");
                    }
                })
                .addOnFailureListener(err -> {
                    Toast.makeText(this,err.getMessage(),Toast.LENGTH_LONG).show();
                });
    }

    public void onLogoutClicked(View v)
    {
        mAuth.signOut();
        startActivity(new Intent(this,Login.class));
    }

    public void onDeleteInfoClicked(View v)
    {
        startActivity(new Intent(this,DeleteAccount.class));
    }
}