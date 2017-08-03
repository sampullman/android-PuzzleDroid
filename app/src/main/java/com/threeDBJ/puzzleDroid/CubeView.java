package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.threeDBJ.MGraphicsLib.texture.TextureFont;
import com.threeDBJ.puzzleDroid.util.Util;

import timber.log.Timber;

public class CubeView extends GLSurfaceView {

    private CubeRenderer renderer;
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
            //renderer.worldBoundsSet = false;
        }
        Timber.d("onSizeChanged %d %d", w, h);
    }

    public CubeView(Context context) {
        this(context, null);
    }

    public CubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        font = new TextureFont(getContext(), R.drawable.roboto_regular, "roboto_regular_dims.txt");
        world = new GLWorld();
    }

    public void initialize(SharedPreferences prefs) {
        cube = new RubeCube(world, Util.getDimension(prefs));
        mMenu = new CubeMenu(cube, font);
        renderer = new CubeRenderer(getContext(), font, world, cube, mMenu, prefs);
        cube.setRenderer(renderer);
        world.setRubeCube(cube);
        setRenderer(renderer);
    }

    public void save(SharedPreferences prefs) {
        cube.save(prefs);
        mMenu.save(prefs);
        mMenu.timer.stop();
    }

    public void restore(SharedPreferences prefs) {
        if (prefs.getBoolean("timer_started", false) && renderer.GLDataLoaded) {
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