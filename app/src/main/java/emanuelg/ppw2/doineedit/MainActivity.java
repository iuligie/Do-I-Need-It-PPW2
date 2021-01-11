package emanuelg.ppw2.doineedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import emanuelg.ppw2.doineedit.authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStartedButton = findViewById(R.id.startButton);

        getStartedButton.setOnClickListener(view -> {
            //We go to login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}