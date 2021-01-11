package emanuelg.ppw2.doineedit.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import emanuelg.ppw2.doineedit.MainActivity;
import emanuelg.ppw2.doineedit.R;
import emanuelg.ppw2.doineedit.list.ProductListActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //widgets
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private TextView txtRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.btnLogIn);
        progressBar = findViewById(R.id.progressBar);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
       //userNameEditText = findViewById(R.id.txtFirstName);
        txtRegister = findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RegisterActivity.class);
            startActivity(intent);

        });

        loginButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(emailEditText.getText().toString()) && !TextUtils.isEmpty(passwordEditText.getText().toString())) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();


                LogIn(email, password);

            } else {
                Toast.makeText(LoginActivity.this, "Empty fields aren't allowed",
                        Toast.LENGTH_LONG).show();
            }


        });
    }

    void LogIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SIGN-IN", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SIGN-IN", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // ...
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, ProductListActivity.class);
            startActivity(intent);
        } else {
            Log.d("CurrentUserCheck", "not logged in or something went wrong");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}