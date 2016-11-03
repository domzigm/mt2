package com.domzi.mt2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button newGame;
    Button loadGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bNewGame).setOnClickListener(newGameHandler);
        findViewById(R.id.bLoadGame).setOnClickListener(loadGameHandler);

    }

    View.OnClickListener newGameHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent newGameIntent = new Intent(MainActivity.this, NewGame.class);
            startActivity(newGameIntent);
        }
    };

    View.OnClickListener loadGameHandler = new View.OnClickListener() {
        public void onClick(View v) {
            // TODO
        }
    };
}
