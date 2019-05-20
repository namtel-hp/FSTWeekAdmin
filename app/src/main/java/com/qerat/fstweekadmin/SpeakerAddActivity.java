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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class SpeakerAddActivity extends AppCompatActivity {
    private Button saveButton, cancelButton, addPhoto, removePhoto, deleteButton;

    private EditText nameEditText, detailsEditText, dayEditText, keyNoteEditText;
    private ProgressBar progressBar;
    private ImageView speakerImage;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private SpeakerClass item;


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
        setContentView(R.layout.activity_add_speakert);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEditText = findViewById(R.id.nameEditText);
        detailsEditText = findViewById(R.id.detaisEditText);
        dayEditText = findViewById(R.id.dayOfTalkEditText);
        keyNoteEditText = findViewById(R.id.keynotesEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressBar = findViewById(R.id.saving);
        addPhoto = findViewById(R.id.addPhoto);
        speakerImage = findViewById(R.id.speakerImage);
        removePhoto = findViewById(R.id.removePhoto);

        deleteButton = findViewById(R.id.deleteButton);


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


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEditText.getText().toString();
                String detailsStr = detailsEditText.getText().toString();
                String dayStr = dayEditText.getText().toString();
                String keyNoteStr = keyNoteEditText.getText().toString();
                String pushId;

                if (TextUtils.isEmpty(nameStr.trim())) {
                    nameEditText.setError("Should not be empty");
                    return;
                }
                if (TextUtils.isEmpty(detailsStr.trim())) {
                    detailsEditText.setError("Should not be empty");
                    return;
                }

                if (TextUtils.isEmpty(dayStr.trim())) {
                    dayEditText.setError("Should not be empty");
                    return;
                }
                if (TextUtils.isEmpty(keyNoteStr.trim())) {
                    keyNoteEditText.setError("Should not be empty");
                    return;
                }
                if(speakerImage.getDrawable()==null){
                    Toast.makeText(SpeakerAddActivity.this,"Add a speaker image!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item == null) {
                    pushId = FirebaseUtilClass.getDatabaseReference().child("Speakers").push().getKey();

                } else {
                    pushId = item.getPushId();

                }

                writeDataToFirebase(new SpeakerClass(pushId, nameStr, detailsStr, dayStr, keyNoteStr));


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (getIntent().getSerializableExtra("item") != null) {
            SpeakerClass item = (SpeakerClass) getIntent().getSerializableExtra("item");
            nameEditText.setText(item.getSpeakerName());
            detailsEditText.setText(item.getSpeakerDetails());
            dayEditText.setText(item.getDayOfTalk());
            keyNoteEditText.setText(item.getSpeakerName());

            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setText("Update");
            this.item = item;
            saveButton.setEnabled(false);


            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Speakers").child(item.getPushId());


            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {


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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromFirebase();
            }
        });
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public void writeDataToFirebase(final SpeakerClass item) {
        savingOn();


        FirebaseUtilClass.getDatabaseReference().child("Speakers").child(item.getPushId()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                uploadFile(mImageUri, item.getPushId());


            }
        });
    }


    public void deleteFromFirebase() {
        savingOn();


        FirebaseUtilClass.getDatabaseReference().child("Speakers").child(item.getPushId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                speakerImage.setImageDrawable(null);
                uploadFile(mImageUri, item.getPushId());


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void uploadFile(Uri imgUri, String code) {

        if (speakerImage.getDrawable() != null) {
            if (mImageUri != null) {


                final ProgressDialog dialog = ProgressDialog.show(SpeakerAddActivity.this, "",
                        "Uploading. Please wait...", true);
                dialog.show();
                final StorageReference fileReference = FirebaseStorage.getInstance().getReference("Speakers").child(code);
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
                                Toast.makeText(SpeakerAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                FirebaseStorage.getInstance().getReference("Speakers").child(item.getPushId()).delete();
                savingOff();
                finish();
            }
        }
    }
}
