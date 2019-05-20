package com.qerat.fstweekadmin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TalkEventAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button addDateButton, addTimeButton, saveButton, cancelButton, deleteButton;
    private TextView dateTextView, timeTextView;
    private EditText titleEditText, locationEditText;
    private ProgressBar progressBar, loadingSpinner;
    private Button addSpeakerButton;


    private EventClass item;
    private List<SpeakerClass> speakerClasses = new ArrayList<>();
    private Spinner daySpinner, speakerNameSpinner;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addDateButton = findViewById(R.id.addDateButton);
        dateTextView = findViewById(R.id.dateTextView);
        addTimeButton = findViewById(R.id.addTimeButton);
        timeTextView = findViewById(R.id.timeTextView);
        titleEditText = findViewById(R.id.titleEditText);
        locationEditText = findViewById(R.id.locationEditText);
        speakerNameSpinner = findViewById(R.id.speakerNameSpinner);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressBar = findViewById(R.id.saving);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        addSpeakerButton = findViewById(R.id.addSpeakerButton);

        daySpinner = findViewById(R.id.daySpinner);
        deleteButton = findViewById(R.id.deleteButton);



        addSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SpeakerAddActivity.class);

                startActivity(i);
            }
        });
        addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TalkEventAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String pmam = "AM";
                        if (selectedHour >= 12) {
                            pmam = "PM";
                            selectedHour -= 12;
                        }
                        if (selectedHour == 0) {
                            selectedHour = 12;
                        }
                        timeTextView.setText(selectedHour + ":" + String.format("%02d", selectedMinute) + " " + pmam);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(
                        TalkEventAddActivity.this, TalkEventAddActivity.this, mYear, mMonth, mDay).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleStr = titleEditText.getText().toString();
                String speakerNameStr="";
                if(speakerNameSpinner.getSelectedItem()!=null){
                    speakerNameStr= speakerNameSpinner.getSelectedItem().toString();
                }

                String dateStr = dateTextView.getText().toString();
                String timeStr = timeTextView.getText().toString();
                String locStr = locationEditText.getText().toString();
                String dayStr = daySpinner.getSelectedItem().toString();
                String pushId;

                if(TextUtils.isEmpty(titleStr.trim())){
                    titleEditText.setError("Should not be empty");
                    return;
                }
                if(TextUtils.isEmpty(speakerNameStr.trim())){
                    Toast.makeText(TalkEventAddActivity.this,"Add a speaker first then select!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(dateStr.trim())){
                    Toast.makeText(TalkEventAddActivity.this,"Select a date!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(timeStr.trim())){
                    Toast.makeText(TalkEventAddActivity.this,"Select a time!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(locStr.trim())){
                    locationEditText.setError("Should not be empty");
                    return;
                }
                if (item == null) {
                    pushId = FirebaseUtilClass.getDatabaseReference().child("Talks").push().getKey();

                } else {
                    pushId = item.getPushId();
                    if (!item.getDay().equals(dateStr)) {
                        deleteFromFirebase();
                    }
                }

                writeDataToFirebase(new EventClass(dayStr, pushId, titleStr, speakerNameStr, dateStr, timeStr, locStr, speakerClasses.get(speakerNameSpinner.getSelectedItemPosition()).getPushId()));


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (getIntent().getSerializableExtra("item") != null) {
            EventClass item = (EventClass) getIntent().getSerializableExtra("item");
            dateTextView.setText(item.getEventDate());
            timeTextView.setText(item.getEventTime());
            titleEditText.setText(item.getEventTitle());

            locationEditText.setText(item.getEventLocation());
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setText("Update");
            this.item = item;
            saveButton.setEnabled(false);
            daySpinner.setSelection(getIndex(daySpinner, item.getDay()));


        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromFirebase();
            }
        });

        loadProductsToSpinner();
    }


    private void spinnerLoadingOn() {
        loadingSpinner.setVisibility(View.VISIBLE);
        speakerNameSpinner.setEnabled(false);
        saveButton.setEnabled(false);
    }

    private void spinnerLoadingOff() {
        loadingSpinner.setVisibility(View.GONE);
        speakerNameSpinner.setEnabled(true);
        saveButton.setEnabled(true);
    }

    private void savingOn() {
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    private void savingOff() {
        progressBar.setVisibility(View.GONE);
        saveButton.setEnabled(true);
        deleteButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02d", dayOfMonth));
        stringBuilder.append("-");
        stringBuilder.append(String.format("%02d", month + 1));
        stringBuilder.append("-");
        stringBuilder.append(year);

        dateTextView.setText(stringBuilder.toString());
    }

    private void loadProductsToSpinner() {
        spinnerLoadingOn();

        FirebaseUtilClass.getDatabaseReference().child("Speakers").orderByChild("speakerName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> loc = new ArrayList<>();
                speakerClasses.clear();


                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    SpeakerClass l = dsp.getValue(SpeakerClass.class); //add result into array list
                    loc.add(l.getSpeakerName());
                    speakerClasses.add(l);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, loc);
                ;
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                speakerNameSpinner.setAdapter(adapter);
                if (item != null) {
                    speakerNameSpinner.setSelection(getIndexFromSpinner(speakerNameSpinner, item.getSpeakerName()));
                }
                spinnerLoadingOff();
                // mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getIndexFromSpinner(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }


    public void writeDataToFirebase(final EventClass item) {
        savingOn();


        FirebaseUtilClass.getDatabaseReference().child("Talks").child(item.getDay()).child(item.getPushId()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                savingOff();
                finish();
                Toast.makeText(getBaseContext(),"Success!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void deleteFromFirebase() {
        savingOn();


        FirebaseUtilClass.getDatabaseReference().child("Talks").child(item.getDay()).child(item.getPushId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        });
    }


}
