package com.example.roomatch.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roomatch.R;
import com.example.roomatch.view.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateProfileFragment extends Fragment {

    private EditText editFullName, editAge, editGender, editLifestyle, editInterests;
    private RadioGroup userTypeGroup;
    private Button saveProfileButton;
    private RadioGroup genderGroup;
    private CheckBox checkboxClean, checkboxSmoker, checkboxNightOwl, checkboxQuiet, checkboxParty;
    private CheckBox checkboxMusic, checkboxSports, checkboxTravel, checkboxCooking, checkboxReading;



    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public CreateProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editFullName = view.findViewById(R.id.editFullName);
        editAge = view.findViewById(R.id.editAge);
        genderGroup = view.findViewById(R.id.genderGroup);
        checkboxClean = view.findViewById(R.id.checkboxClean);
        checkboxSmoker = view.findViewById(R.id.checkboxSmoker);
        checkboxNightOwl = view.findViewById(R.id.checkboxNightOwl);
        checkboxQuiet = view.findViewById(R.id.checkboxQuiet);
        checkboxParty = view.findViewById(R.id.checkboxParty);
        checkboxMusic = view.findViewById(R.id.checkboxMusic);
        checkboxSports = view.findViewById(R.id.checkboxSports);
        checkboxTravel = view.findViewById(R.id.checkboxTravel);
        checkboxCooking = view.findViewById(R.id.checkboxCooking);
        checkboxReading = view.findViewById(R.id.checkboxReading);
        userTypeGroup = view.findViewById(R.id.userTypeGroup);
        saveProfileButton = view.findViewById(R.id.buttonSaveProfile);

        saveProfileButton.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        String fullName = editFullName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        String gender;
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.radioMale) {
            gender = "זכר";
        } else if (selectedGenderId == R.id.radioFemale) {
            gender = "נקבה";
        } else if (selectedGenderId == R.id.radioOther) {
            gender = "אחר";
        } else {
            Toast.makeText(getContext(), "בחר מין", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> lifestyleList = new ArrayList<>();
        if (checkboxClean.isChecked()) lifestyleList.add("נקי");
        if (checkboxSmoker.isChecked()) lifestyleList.add("מעשן");
        if (checkboxNightOwl.isChecked()) lifestyleList.add("חיית לילה");
        if (checkboxQuiet.isChecked()) lifestyleList.add("שקט");
        if (checkboxParty.isChecked()) lifestyleList.add("אוהב מסיבות");

        String lifestyle = TextUtils.join(", ", lifestyleList); // שמירה כמחרוזת
        List<String> interestList = new ArrayList<>();
        if (checkboxMusic.isChecked()) interestList.add("מוזיקה");
        if (checkboxSports.isChecked()) interestList.add("ספורט");
        if (checkboxTravel.isChecked()) interestList.add("טיולים");
        if (checkboxCooking.isChecked()) interestList.add("בישול");
        if (checkboxReading.isChecked()) interestList.add("קריאה");

        String interests = TextUtils.join(", ", interestList); // מחרוזת מופרדת בפסיקים

        // בדיקת שם
        if (fullName.isEmpty() || fullName.length() < 2) {
            Toast.makeText(getContext(), "הכנס שם מלא (לפחות 2 תווים)", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקת גיל
        Integer age = tryParseInt(ageStr);
        if (age == null || age <= 0) {
            Toast.makeText(getContext(), "הכנס גיל תקין(גדול מ0)", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקת סוג משתמש
        int selectedId = userTypeGroup.getCheckedRadioButtonId();
        String userType;
        if (selectedId == R.id.radioSeeker) {
            userType = "seeker";
        } else if (selectedId == R.id.radioOwner) {
            userType = "owner";
        } else {
            Toast.makeText(getContext(), "בחר סוג משתמש", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("fullName", fullName);
        profile.put("age", age);
        profile.put("gender", gender);
        profile.put("lifestyle", lifestyle);
        profile.put("interests", interests);
        profile.put("userType", userType);

        db.collection("users").document(uid)
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "הפרופיל נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity(userType);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "שגיאה בשמירת הפרופיל: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }


    private Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void navigateToMainActivity(String userType) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("fragment", userType.equals("owner") ? "owner_apartments" : "seeker_home");
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish(); // סגירת הפעילות הנוכחית
        }
    }
}