package com.threeDBJ.puzzleDroidFree;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.threeDBJ.MGraphicsLib.texture.TextureFont;
import com.threeDBJ.puzzleDroidFree.util.Util;

import timber.log.Timber;

public class CubeView extends GLSurfaceView {

    private CubeRenderer renderer;
    TextureFont font;
    RubeCube cube;
    GLWorld world;
    CubeMenu menu;

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (world != null) {
            world.setDimensions(w, h);
            menu.setDimensions(w, h);
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
        menu = new CubeMenu(cube, font);
        renderer = new CubeRenderer(getContext(), font, world, cube, menu, prefs);
        cube.setRenderer(renderer);
        world.setRubeCube(cube);
        setRenderer(renderer);
    }

    public void save(SharedPreferences prefs) {
        cube.save(prefs);
        menu.save(prefs);
        menu.timer.stop();
    }

    public void restore(SharedPreferences prefs) {
        if (Util.getTimerStarted(prefs) && renderer.GLDataLoaded) {
            menu.timer.start();
        }
    }

    @Override
    public void onPause() {
        menu.pause();
    }

    @Override
    public void onResume() {
        //menu.resume();
    }

    public boolean onTouchEvent(final MotionEvent e) {
        if (!menu.handleTouch(e)) {
            cube.handleTouch(e);
        }
        return true;
    }

}