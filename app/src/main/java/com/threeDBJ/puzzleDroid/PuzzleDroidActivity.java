package com.threeDBJ.puzzleDroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PuzzleDroidActivity extends Activity {
    SharedPreferences prefs;
    CubeView cubeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cubeView = new CubeView(this);
        cubeView.initialize(prefs);
        setContentView(cubeView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        cubeView.save(prefs);
        cubeView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        cubeView.restore(prefs);
        cubeView.onResume();
    }

}
