package p.edu.ug.myauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class DeleteAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView deleteTextView;
    private EditText deleteInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        deleteTextView = findViewById(R.id.deleteCodeTextView);
        deleteInput = findViewById(R.id.deleteCodeInput);

        deleteTextView.setText(OnCreateGenerateRandomCode(10));
    }

    public void onDeleteClicked(View v)
    {
        String givenCodeToDelete = deleteInput.getText().toString().trim();

        if(IsDeleteCodeCorrect(givenCodeToDelete)) {
            String deletedUserUID = mAuth.getCurrentUser().getUid();

            Objects.requireNonNull(mAuth.getCurrentUser()).delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "User deleted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Couldn't delete user", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(err -> {
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show();
                    });

            db.collection("Users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.getId().equals(deletedUserUID)) {
                                    doc.getReference().delete();
                                }
                            }
                        } else {
                            System.out.println("Error on deleting from db");
                        }
                    })
                    .addOnFailureListener(err -> {
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show();
                    });

            startActivity(new Intent(this, Login.class));
        } else {
            Toast.makeText(this, "Incorrect delete code", Toast.LENGTH_LONG).show();
        }
    }

    private String OnCreateGenerateRandomCode(int length)
    {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] output = new char[length];

        for(int i = 0; i < length; i++) {
            int index = (int)(Math.random() * alphabet.length-1);
            System.out.println(index);
            output[i] = alphabet[index];
        }

        return String.valueOf(output);
    }

    private boolean IsDeleteCodeCorrect(String inputCodeToDelete)
    {
        String validCode = deleteTextView.getText().toString();

        return validCode.equals(inputCodeToDelete);
    }
}