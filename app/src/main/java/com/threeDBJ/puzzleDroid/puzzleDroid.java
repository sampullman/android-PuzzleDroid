package com.threeDBJ.puzzleDroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class puzzleDroid extends Activity {
    SharedPreferences prefs;
    cubeView cv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cv = (cubeView) findViewById(R.id.cubeView);
        cv.initialize(prefs);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        cv.save(prefs);
        cv.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        cv.restore(prefs);
        cv.onResume();
    }

}
