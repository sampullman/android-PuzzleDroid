package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.threeDBJ.MGraphicsLib.texture.TextureFont;

public class CubeView extends GLSurfaceView {

    private CubeRenderer _renderer;
    TextureFont font;
    RubeCube cube;
    GLWorld world;
    CubeMenu mMenu;

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (world != null) {
            world.setDimensions(w, h);
            mMenu.setDimensions(w, h);
            //_renderer.worldBoundsSet = false;
        }
        Log.e("Cube-onSizeChanged", w + " " + h);
    }

    public CubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        font = new TextureFont(getContext(), R.drawable.roboto_regular, "roboto_regular_dims.txt");
        world = new GLWorld();
    }

    public void initialize(SharedPreferences prefs) {
        cube = new RubeCube(world, prefs.getInt("dim", 3));
        mMenu = new CubeMenu(cube, font);
        _renderer = new CubeRenderer(getContext(), font, world, cube, mMenu, prefs);
        cube.setRenderer(_renderer);
        world.setRubeCube(cube);
        setRenderer(_renderer);
    }

    public void save(SharedPreferences prefs) {
        cube.save(prefs);
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
            cube.handleTouch(e);
        }
        return true;
    }

}