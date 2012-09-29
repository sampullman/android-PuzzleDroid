package com.threeDBJ.puzzleDroid;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class CubeRenderer implements GLSurfaceView.Renderer {

    private float _width = 320f;
    private float _height = 480f;

    public static int[] viewport = new int[16];
    public static float[] modelViewMatrix = new float[16];
    public static float[] projectionMatrix = new float[16];
    public static float[] pointInPlane = new float[16];

    float xMin, xMax, yMin, yMax;
    boolean worldBoundsSet = false;

    private GLWorld mWorld;

    public CubeRenderer(GLWorld world) {
	mWorld = world;
    }

    @Override
    public void onSurfaceCreated(GL10 g, EGLConfig config) {
        //gl.glClearColor(0f, 0f, 0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 g) {
	GL11 gl = (GL11)g;
	// Clears the screen and depth buffer.
	gl.glClearColor(0.5f,0.5f,0.5f,1);
	gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	gl.glMatrixMode(GL11.GL_MODELVIEW);
        gl.glLoadIdentity();

	GLU.gluLookAt(gl, 0f, 0f, 7f,
		      0f, 0f, 0f,
		      0f, 1f, 0f);
	gl.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);
	if(!worldBoundsSet) {
	    getWorldBounds();
	    worldBoundsSet = true;
	}

        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
        gl.glEnable(GL11.GL_CULL_FACE);
        gl.glShadeModel(GL11.GL_SMOOTH);
        gl.glEnable(GL11.GL_DEPTH_TEST);

        mWorld.draw(gl);
    }

    private void getWorldBounds() {
	GLU.gluUnProject(0, viewport[3], 7f, modelViewMatrix, 0,
			 projectionMatrix, 0, viewport, 0, pointInPlane, 0);
	xMax = pointInPlane[0]*-6f;
	yMin = pointInPlane[1]*-6f;
	GLU.gluUnProject(_width, 0, 7f, modelViewMatrix, 0,
			 projectionMatrix, 0, viewport, 0, pointInPlane, 0);
	xMin = pointInPlane[0]*-6f;
	yMax = pointInPlane[1]*-6f;
	Log.e("xMin", xMin+"");
	Log.e("xMax", xMax+"");
	Log.e("yMin", yMin+"");
	Log.e("yMax", yMax+"");
    }

    public Vec3 screenToWorld(Vec3 p) {
	p.x = p.x * (xMax - xMin) + xMin;
	p.y = p.y * (yMax - yMin) + yMin;
	Log.e("yeahhh", p+"");
	// GLU.gluUnProject(p.x, p.y, 7f, modelViewMatrix, 0,
	// 		 projectionMatrix, 0, viewport, 0, pointInPlane, 0);
	// p.x = pointInPlane[0]*-6f;
	// p.y = pointInPlane[1]*-6f;
	return p;
    }

    @Override
    public void onSurfaceChanged(GL10 g, int w, int h) {
	GL11 gl = (GL11)g;
        Log.v("onSurfaceChanged()", "");
        _width = w;
        _height = h;
        gl.glViewport(0, 0, w, h);

	// Reset projection matrix
	float ratio = (float)w / h;
        gl.glMatrixMode(GL11.GL_PROJECTION);
        gl.glLoadIdentity();
        //gl.glFrustumf(-ratio, ratio, -1, 1, 0, 20);
	GLU.gluPerspective(gl, 45f, ratio, 5f, 10f);
	gl.glGetIntegerv(GL11.GL_VIEWPORT, viewport, 0);
	gl.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projectionMatrix, 0);
	// OpenGL goes for quality over performance by default
	//gl.glDisable(GL11.GL_DITHER);
        //gl.glActiveTexture(GL11.GL_TEXTURE0);
    }

}