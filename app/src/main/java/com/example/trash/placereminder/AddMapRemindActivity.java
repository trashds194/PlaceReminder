package com.example.trash.placereminder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

public class AddMapRemindActivity extends AppCompatActivity {

    EditText addTitleReminderEt;
    EditText addShortDescriptionEt;
    EditText addNamePlaceEt;

    TextInputLayout addTitleReminderLabel;
    TextInputLayout addNamePlaceLabel;
    TextInputLayout addShortDescriptionLabel;

    Button saveBtn;
    Button clearBtn;

    MapView mMapView;
    GoogleMap mGoogleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_map_remind);

        findElements();

        changeText();

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(getApplicationContext());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                if (ActivityCompat.checkSelfPermission(AddMapRemindActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddMapRemindActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mGoogleMap.setMyLocationEnabled(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTitleReminderEt.setText(null);
                addNamePlaceEt.setText(null);
                addShortDescriptionEt.setText(null);
                requestFocus(addTitleReminderEt);
                Toast.makeText(getApplicationContext(), "Все поля очищены", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    void findElements() {
        addTitleReminderEt = findViewById(R.id.add_title_reminder_et);
        addNamePlaceEt = findViewById(R.id.add_name_place_et);
        addShortDescriptionEt = findViewById(R.id.add_short_description_et);
        addTitleReminderLabel = findViewById(R.id.add_title_reminder_label);
        addNamePlaceLabel = findViewById(R.id.add_name_place_label);
        addShortDescriptionLabel = findViewById(R.id.add_short_description_label);
        saveBtn = findViewById(R.id.save_btn);
        clearBtn = findViewById(R.id.clear_btn);
    }

    private void submitForm() {
        if (!validateTitle()) {
            return;
        }
        if (!validateName()) {
            return;
        }
        if (!validateDescription()) {
            return;
        }
        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    void changeText() {
        addTitleReminderEt.addTextChangedListener(new MyTextWatcher(addTitleReminderEt));
        addNamePlaceEt.addTextChangedListener(new MyTextWatcher(addNamePlaceEt));
        addShortDescriptionEt.addTextChangedListener(new MyTextWatcher(addShortDescriptionEt));
    }

    private boolean validateTitle() {
        if (addTitleReminderEt.getText().toString().trim().isEmpty()) {
            addTitleReminderLabel.setError(getString(R.string.title_reminder_error));
            requestFocus(addTitleReminderEt);
            return false;
        } else {
            addTitleReminderLabel.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateName() {
        if (addNamePlaceEt.getText().toString().trim().isEmpty()) {
            addNamePlaceLabel.setError(getString(R.string.title_place_error));
            requestFocus(addNamePlaceEt);
            return false;
        } else {
            addNamePlaceLabel.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDescription() {
        String mDescription = addShortDescriptionEt.getText().toString().trim();

        if (mDescription.length() > 120) {
            addShortDescriptionLabel.setError(getString(R.string.exceeded_limit));
            requestFocus(addShortDescriptionEt);
            return false;
        } else {
            addShortDescriptionLabel.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View mView;

        private MyTextWatcher(View view) {
            this.mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (mView.getId()) {
                case R.id.add_title_reminder_et:
                    validateTitle();
                    break;
                case R.id.add_name_place_et:
                    validateName();
                    break;
                case R.id.add_short_description_et:
                    validateDescription();
                    break;
            }
        }
    }
}
