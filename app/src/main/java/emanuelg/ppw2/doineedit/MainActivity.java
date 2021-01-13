package emanuelg.ppw2.doineedit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import emanuelg.ppw2.doineedit.authentication.LoginActivity;
import emanuelg.ppw2.doineedit.list.ProductListActivity;

public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        getStartedButton = findViewById(R.id.startButton);
        if(mAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        getStartedButton.setOnClickListener(view -> {
            if(mAuth.getCurrentUser()==null)
            //We go to login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}