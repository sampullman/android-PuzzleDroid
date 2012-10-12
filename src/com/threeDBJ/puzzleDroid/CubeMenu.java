package com.threeDBJ.puzzleDroid;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class CubeMenu extends GLEnvironment {

    public static final int HIDDEN=0, SHOWING=1;

    int mode=HIDDEN;
    float xMin, xMax, yMin, yMax;

    public CubeMenu() {
	generate();
	enableTextures();
	Log.e("Cube", mIndexCount+"");
    }

    public void setBounds(float xMin, float xMax, float yMin, float yMax) {
	this.xMin = xMin;
	this.xMax = xMax;
	this.yMin = yMin;
	this.yMax = yMax;
	Log.e("Cube", xMin+" "+xMax+" "+yMin+" "+yMax);
	float z = 1f;
	float rat = (xMax - xMin) / (yMax - yMin);

	float xl = xMin + 0.1f;
	float xr = xl + (0.5f * 0.8f) + (0.4f * rat);
	float xdiff = (xr - xl) / 6f;
	float yb = yMin + 0.05f;
	float yt = yb + (0.7f * 0.9f) + (0.1f * rat);
        GLColor orange = new GLColor(1f, 0.5f, 0);
	GLShape s = new GLShape(this);
	GLVertex a = s.addVertex(xr, yb, z);
	GLVertex b = s.addVertex(xr, yt, z);
	GLVertex c = s.addVertex(xl, yb, z);
	GLVertex d = s.addVertex(xl, yt, z);
	GLFace f = new GLFace(a, b, c, d);
	f.setColor(orange);
	f.setTexture(this, new Texture());
	s.addFace(f);
	addShape(s);

	generate();
	String str="";
	for(int i=0;i<mColorBuffer.capacity();i+=1) {
	    str += mColorBuffer.get(i)+" ";
	}
	Log.e("Cube", str);
    }

    public void draw(GL11 gl) {
	super.draw(gl);
	//gl.glBindTexture(GL11.GL_TEXTURE_2D, textures[0]);
	gl.glPushMatrix();

	gl.glFrontFace(GL11.GL_CW);
        //gl.glShadeModel(GL11.GL_FLAT);
	gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
        gl.glVertexPointer(3, GL11.GL_FIXED, 0, mVertexBuffer);
        gl.glColorPointer(4, GL11.GL_FIXED, 0, mColorBuffer);
	//Log.e("Cube", textureBuffer.position()+" "+mIndexCount+" "+mVertexBuffer.position());
	mIndexBuffer.position(0);
	mColorBuffer.position(0);
	mVertexBuffer.position(0);
	mTextureBuffer.position(0);
        gl.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_SHORT, mIndexBuffer);
	gl.glPopMatrix();
    }

    public boolean handleTouch(MotionEvent e) {
	// Eventually detect cube hit here
	final int action = e.getAction();
	switch(action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	    int activePtrId = e.getPointerId(0);
	    break;
	}
	return false;
    }

}