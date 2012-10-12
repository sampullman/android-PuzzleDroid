package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

public class cubeView extends GLSurfaceView {

    private CubeRenderer _renderer;
    private RubeCube rCube;
    private GLWorld mWorld;
    private CubeMenu mMenu;

    private GLWorld makeGLWorld() {
        GLWorld world = new GLWorld();
	rCube.addShapes();
	world.generate();
	return world;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);
	if(mWorld != null)
	    mWorld.setDimensions(w, h);
	Log.v("onSizeChanged", w + " "+h);
    }

    public cubeView(Context context, AttributeSet attrs) {
	super(context);
	mWorld = new GLWorld();
	rCube = new RubeCube(mWorld, 3);
	rCube.addShapes();
	mWorld.generate();
	mMenu = new CubeMenu();
	_renderer = new CubeRenderer(getContext(), mWorld, mMenu);
	// TODO -- set renderer in constructor
	rCube.setRenderer(_renderer);
	mWorld.setRubeCube(rCube);
	setRenderer(_renderer);
    }

    public boolean onTouchEvent(final MotionEvent e) {
	if(!mMenu.handleTouch(e)) {
	    rCube.handleTouch(e);
	}
	return true;
    }

}