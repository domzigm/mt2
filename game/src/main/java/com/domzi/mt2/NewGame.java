package com.domzi.mt2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewGame extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int playerCount;
    TextView txPlayer3;
    TextView txPlayer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame);

        List<String> playerStr = new ArrayList<String>();
        playerStr.add("2 Spieler");
        playerStr.add("3 Spieler");
        playerStr.add("4 Spieler");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playerStr);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner players = (Spinner)findViewById(R.id.playerCount);
        players.setAdapter(dataAdapter);
        players.setOnItemSelectedListener(this);

        playerCount = 2;
        txPlayer3 = (TextView)findViewById(R.id.namePlayer3);
        txPlayer4 = (TextView)findViewById(R.id.namePlayer4);
        enablePlayers();

        findViewById(R.id.bStartGame).setOnClickListener(startGameHandler);
        findViewById(R.id.bStartCancel).setOnClickListener(cancelGameHandler);
    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position)
        {
            case 0:
                playerCount = 2;
                break;
            case 1:
                playerCount = 3;
                break;
            case 2:
                playerCount = 4;
                break;
        }

        enablePlayers();
    }

    private void enablePlayers()
    {
        switch(playerCount)
        {
            case 2:
                txPlayer3.setEnabled(false);
                txPlayer4.setEnabled(false);
                break;
            case 3:
                txPlayer3.setEnabled(true);
                txPlayer4.setEnabled(false);
                break;
            case 4:
                txPlayer3.setEnabled(true);
                txPlayer4.setEnabled(true);
                break;
        }
    }

    View.OnClickListener startGameHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent newGameIntent = new Intent(NewGame.this, GameScreen.class);
            newGameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newGameIntent);
        }
    };

    View.OnClickListener cancelGameHandler = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

}
