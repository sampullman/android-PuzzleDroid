package com.threeDBJ.puzzleDroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.util.Log;

public class puzzleDroid extends Activity {

    cubeView cv;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	cv = (cubeView) findViewById(R.id.cubeView);
	cv.initialize(PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
	super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
	super.onPause();
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	cv.save(prefs);
	cv.onPause();
    }

    @Override
    public void onResume() {
	super.onResume();
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	cv.restore(prefs);
	cv.onResume();
    }

}
