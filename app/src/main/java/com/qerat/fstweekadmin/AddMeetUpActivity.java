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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.Calendar;

public class AddMeetUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button addDateButton, addTimeButton, saveButton, cancelButton, deleteButton, addPhoto, removePhoto;
    private TextView dateTextView, timeTextView;
    private EditText locationEditText;
    private ProgressBar progressBar;
    private MeetUpClass item;
    private ImageView speakerImage;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private boolean fstem = false;
    private String purpose = "";
    private String area = "";



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
        setContentView(R.layout.activity_meet_up);


        addDateButton = findViewById(R.id.addDateButton);
        dateTextView = findViewById(R.id.dateTextView);
        addTimeButton = findViewById(R.id.addTimeButton);
        timeTextView = findViewById(R.id.timeTextView);
        addPhoto = findViewById(R.id.addPhoto);
        speakerImage = findViewById(R.id.speakerImage);
        removePhoto = findViewById(R.id.removePhoto);
        locationEditText = findViewById(R.id.locationEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressBar = findViewById(R.id.saving);
        deleteButton = findViewById(R.id.deleteButton);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra("fstem") != null) {
            fstem = true;
            purpose = getIntent().getStringExtra("purpose");
            area = getIntent().getStringExtra("area");
        }

        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUri = null;
                speakerImage.setImageDrawable(null);
                speakerImage.setVisibility(View.GONE);
                removePhoto.setVisibility(View.GONE);
            }
        });


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (!checkIfAlreadyhavePermission()) {
                        requestForSpecificPermission();
                    } else {
                        openFileChooser();
                    }
                }
            }
        });

        addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddMeetUpActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        AddMeetUpActivity.this, AddMeetUpActivity.this, mYear, mMonth, mDay).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locStr = locationEditText.getText().toString();
                String dateStr = dateTextView.getText().toString();
                String timeStr = timeTextView.getText().toString();

                if (TextUtils.isEmpty(locStr.trim())) {
                    locationEditText.setError("Should not be empty");
                    return;
                }
                if (TextUtils.isEmpty(dateStr.trim())) {
                    Toast.makeText(AddMeetUpActivity.this, "Select a date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(timeStr.trim())) {
                    Toast.makeText(AddMeetUpActivity.this, "Select a time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (speakerImage.getDrawable() == null) {
                    Toast.makeText(AddMeetUpActivity.this, "Add a image of location!", Toast.LENGTH_SHORT).show();
                    return;
                }

                writeDataToFirebase(new MeetUpClass(dateStr, timeStr, locStr));


            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromFirebase();
            }
        });

        readDataFromFirebase();
    }

    private void deleteFromFirebase() {

        if (fstem) {
            FirebaseUtilClass.getDatabaseReference().child("MentorshipGroups").child(purpose + "_" + area).child("meetup").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseStorage.getInstance().getReference().child("Locations").child(purpose + "_" + area).delete();
                    Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(), "Failed to delete. Try again.", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            FirebaseUtilClass.getDatabaseReference().child("Meetup").child("conf").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseStorage.getInstance().getReference().child("Locations").child("meet_up_conf_loc").delete();
                    Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(), "Failed to delete. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void readDataFromFirebase() {
        saveButton.setEnabled(false);
        if (fstem) {
            FirebaseUtilClass.getDatabaseReference().child("MentorshipGroups").child(purpose + "_" + area).child("meetup").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        saveButton.setEnabled(true);
                    } else {

                        item = dataSnapshot.getValue(MeetUpClass.class);

                        deleteButton.setVisibility(View.VISIBLE);
                        saveButton.setText("Update");
                        locationEditText.setText(item.getLocation());
                        dateTextView.setText(item.getDate());
                        timeTextView.setText(item.getTime());
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Locations").child(purpose + "_" + area);


                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                saveButton.setEnabled(true);

                                Uri downloadUrl = uri;
                                Picasso.get().load(downloadUrl.toString()).into(speakerImage);
                                speakerImage.setVisibility(View.VISIBLE);
                                speakerImage.invalidate();

                                addPhoto.setText("Change Photo");
                                removePhoto.setVisibility(View.VISIBLE);
                                saveButton.setEnabled(true);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                saveButton.setEnabled(true);
                                //  holder.machineImage.setVisibility(View.GONE);
                            }
                        });
                    }
                    // dayList.clear();

                    //  mAdapter.notifyDataSetChanged();


                    //   Toast.makeText(getContext(), "Changed something", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            FirebaseUtilClass.getDatabaseReference().child("Meetup").child("conf").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        saveButton.setEnabled(true);
                    } else {

                        item = dataSnapshot.getValue(MeetUpClass.class);

                        deleteButton.setVisibility(View.VISIBLE);
                        saveButton.setText("Update");
                        locationEditText.setText(item.getLocation());
                        dateTextView.setText(item.getDate());
                        timeTextView.setText(item.getTime());
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Locations").child("meet_up_conf_loc");


                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                saveButton.setEnabled(true);
                                Uri downloadUrl = uri;
                                Picasso.get().load(downloadUrl.toString()).into(speakerImage);
                                speakerImage.setVisibility(View.VISIBLE);
                                speakerImage.invalidate();

                                addPhoto.setText("Change Photo");
                                removePhoto.setVisibility(View.VISIBLE);
                                saveButton.setEnabled(true);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                saveButton.setEnabled(true);
                                //  holder.machineImage.setVisibility(View.GONE);
                            }
                        });
                    }
                    // dayList.clear();

                    //  mAdapter.notifyDataSetChanged();


                    //   Toast.makeText(getContext(), "Changed something", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    public void writeDataToFirebase(final MeetUpClass item) {
        savingOn();
        if (fstem) {
            FirebaseUtilClass.getDatabaseReference().child("MentorshipGroups").child(purpose + "_" + area).child("meetup").setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    uploadFile(mImageUri, purpose + "_" + area);


                }
            });
        } else {


            FirebaseUtilClass.getDatabaseReference().child("Meetup").child("conf").setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    uploadFile(mImageUri, "meet_up_conf_loc");


                }
            });
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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


    private void savingOn() {
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }


    private void savingOff() {
        progressBar.setVisibility(View.GONE);
        saveButton.setEnabled(true);
        deleteButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    private void uploadFile(Uri imgUri, String code) {

        if (speakerImage.getDrawable() != null) {
            if (mImageUri != null) {


                final ProgressDialog dialog = ProgressDialog.show(AddMeetUpActivity.this, "",
                        "Uploading. Please wait...", true);
                dialog.show();
                final StorageReference fileReference = FirebaseStorage.getInstance().getReference("Locations").child(code);
                UploadTask mUploadTask;
                mUploadTask = (UploadTask) fileReference.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        savingOff();
                                        finish();
                                    }
                                }, 500);


                                Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                                finish();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddMeetUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                dialog.setMessage("Uploading Image: " + progress + "%");
                            }
                        });
            }else {
                Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            if (item != null) {
                FirebaseStorage.getInstance().getReference("Locations").child(code).delete();
                savingOff();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();


            Picasso.get().load(mImageUri).into(speakerImage);
            speakerImage.setVisibility(View.VISIBLE);
            addPhoto.setText("Change Photo");
            removePhoto.setVisibility(View.VISIBLE);

            //addImage.setImageURI(mImageUri);
            //    buttonAddImage.setText("delete");
            //   buttonAddImage.setTextColor(getResources().getColor(R.color.colorred));
        }
    }
}
