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
	_renderer = new CubeRenderer(mWorld);
	// TODO -- set renderer in constructor
	rCube.setRenderer(_renderer);
	setRenderer(_renderer);
    }

    public boolean onTouchEvent(final MotionEvent e) {
	rCube.handleTouch(e);
	return true;
    }

    protected void init(GL10 gl) {
	gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	gl.glEnable(GL10.GL_DEPTH_TEST);
	gl.glEnable(GL10.GL_CULL_FACE);
	gl.glDepthFunc(GL10.GL_LEQUAL);
	gl.glClearDepthf(1.0f);
	gl.glShadeModel(GL10.GL_SMOOTH);
    }

}