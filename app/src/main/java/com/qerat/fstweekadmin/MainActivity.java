package com.qerat.fstweekadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button addTalkButton, addSpeakerButton, fsteepMeetUpButton, fstemMeetUpButton, panelDiscButton;
    private Button editTalkButton, editSpeakerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTalkButton = findViewById(R.id.talkEventButton);
        editTalkButton = findViewById(R.id.editTalkEventButton);
        addSpeakerButton = findViewById(R.id.addSpeakerButton);
        editSpeakerButton = findViewById(R.id.editSpeakerButton);
        fsteepMeetUpButton = findViewById(R.id.addMeetUpFSTeepButton);
        fstemMeetUpButton = findViewById(R.id.addMeetUpFSTemButton);
        panelDiscButton = findViewById(R.id.approveMessageButton);

        addTalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), TalkEventAddActivity.class);

                startActivity(i);
            }
        });
        editTalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), EventsFragment.class);

                startActivity(i);
            }
        });
        addSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SpeakerAddActivity.class);

                startActivity(i);
            }
        });
        editSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SpeakerViewActivity.class);

                startActivity(i);
            }
        });
        fsteepMeetUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddMeetUpActivity.class);

                startActivity(i);
            }
        });
        panelDiscButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PanelPostActivity.class);

                startActivity(i);
            }
        });
        fstemMeetUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MentorGroupsAcitivity.class);

                startActivity(i);
            }
        });

    }
}
