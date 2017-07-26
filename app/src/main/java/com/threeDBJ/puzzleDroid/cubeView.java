package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.threeDBJ.MGraphicsLib.TextureFont;

public class cubeView extends GLSurfaceView {

    private CubeRenderer _renderer;
    TextureFont font;
    RubeCube rCube;
    GLWorld mWorld;
    CubeMenu mMenu;

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mWorld != null) {
            mWorld.setDimensions(w, h);
            mMenu.setDimensions(w, h);
            //_renderer.worldBoundsSet = false;
        }
        Log.e("Cube-onSizeChanged", w + " " + h);
    }

    public cubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        font = new TextureFont(getContext(), R.drawable.roboto_regular, "roboto_regular_dims.txt");
        mWorld = new GLWorld();
    }

    public void initialize(SharedPreferences prefs) {
        rCube = new RubeCube(mWorld, prefs.getInt("dim", 3));
        mMenu = new CubeMenu(rCube, font);
        _renderer = new CubeRenderer(getContext(), font, mWorld, rCube, mMenu, prefs);
        rCube.setRenderer(_renderer);
        mWorld.setRubeCube(rCube);
        setRenderer(_renderer);
    }

    public void save(SharedPreferences prefs) {
        rCube.save(prefs);
        mMenu.save(prefs);
        mMenu.timer.stop();
    }

    public void restore(SharedPreferences prefs) {
        if (prefs.getBoolean("timer_started", false) && _renderer.GLDataLoaded) {
            mMenu.timer.start();
        }
    }

    @Override
    public void onPause() {
        mMenu.pause();
    }

    @Override
    public void onResume() {
        //mMenu.resume();
    }

    public boolean onTouchEvent(final MotionEvent e) {
        if (!mMenu.handleTouch(e)) {
            rCube.handleTouch(e);
        }
        return true;
    }

}