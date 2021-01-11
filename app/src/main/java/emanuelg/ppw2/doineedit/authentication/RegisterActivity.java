package emanuelg.ppw2.doineedit.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import emanuelg.ppw2.doineedit.MainActivity;
import emanuelg.ppw2.doineedit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import emanuelg.ppw2.doineedit.R;

public class RegisterActivity extends AppCompatActivity {

    //private Button loginButton;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    //widgets
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        createAccountButton = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        userNameEditText = findViewById(R.id.txtFirstName);


        createAccountButton.setOnClickListener(view -> {

            if (!TextUtils.isEmpty(emailEditText.getText().toString()) && !TextUtils.isEmpty(passwordEditText.getText().toString()) && !TextUtils.isEmpty(userNameEditText.getText().toString())) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String username = userNameEditText.getText().toString().trim();


                createUserEmailAccount(email, password);

            } else {
                Toast.makeText(RegisterActivity.this, "Empty fields aren't allowed",
                        Toast.LENGTH_LONG).show();
            }


        });
    }

    private void createUserEmailAccount(String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    //go to main activity
                    Log.d("AUTH", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    initializeDB(user);

                } else {
                    //something went wrong
                    // If sign in fails, display a message to the user.
                    Log.w("AUTH", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_LONG).show();
                    updateUI(null);
                }

            }).addOnFailureListener(e -> {
                Log.w("AUTH", "createUserWithEmail:failure", e.getCause());
                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                        Toast.LENGTH_LONG).show();
            });
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(RegisterActivity.this, "Please enter the credentials!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initializeDB(FirebaseUser user) {
        Map<String, Object> item = new HashMap<>();
        item.put("title","Item 1");
        item.put("price","$123");
        item.put("pic-url","images.google/1");

        db.collection(user.getUid()).add(item)
                .addOnSuccessListener(documentReference -> Log.d("DB", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("DB", "Error adding document", e));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.d("CurrentUserCheck", "not logged in or something went wrong");
        }
    }
}
