/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLU;

import com.threeDBJ.MGraphicsLib.GLEnvironment;
import com.threeDBJ.MGraphicsLib.Quaternion;
import com.threeDBJ.MGraphicsLib.Mat4;
import com.threeDBJ.MGraphicsLib.Mat3;
import com.threeDBJ.MGraphicsLib.Vec3;
import com.threeDBJ.MGraphicsLib.ArcBall;

public class GLWorld extends GLEnvironment {

    public static float radToDeg = 180f / (float)Math.PI;

    RubeCube cube;

    public float ztrans = 0f;

    int drawCount = 0;

    Quaternion curQuat = new Quaternion(0f, 0f, 0f, 1f);
    Quaternion startQuat = new Quaternion(0f, 0f, 0f, 1f);
    Mat4 rotate = new Mat4(), transScale = new Mat4();
    Mat3 lastRot = new Mat3();
    Mat3 thisRot = new Mat3();
    Vec3 startPos = new Vec3(), curPos = new Vec3();
    ArcBall arcBall = new ArcBall();
    float scale=1f;
    boolean paused = false;

    public void GLWorld() {
	transScale.setScale(0.5f, 0.5f, 0.5f);
    }

    public void init(GL11 gl, Context c) {
	//setTexture(gl, c, R.drawable.cube_texture);
    }

    public void setRubeCube(RubeCube cube) {
	this.cube = cube;
    }

    public void pauseCube(boolean pause) {
	this.paused = pause;
    }

    public void draw(GL11 gl) {
	super.draw(gl);
	//gl.glBlendFunc( gl.GL_ONE, gl.GL_SRC_ALPHA );
	gl.glDepthFunc(GL11.GL_LEQUAL);

	gl.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	gl.glPushMatrix();
	gl.glScalef(scale, scale, scale);
	gl.glMultMatrixf(rotate.val, 0);
	if(!paused) {
	    cube.animate();
	    //gl.glBindTexture(GL11.GL_TEXTURE_2D, mTexture.id);
	    mColorBuffer.position(0);
	    mTextureBuffer.position(0);
	    mVertexBuffer.position(0);
	    mIndexBuffer.position(0);
	    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
	    gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
	    gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
	    gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}
	gl.glPopMatrix();
    }

    public void generate() {
	super.generate();
	int n=0;
	for(int i=0;i<mTextureBuffer.capacity();i+=8) {
	    if(mTextureBuffer.get(i) == 0f) n+=1;
	    //Log.e("Cube", mTextureBuffer.get(i)+" "+mTextureBuffer.get(i+1)+" "+mTextureBuffer.get(i+2)+" "+mTextureBuffer.get(i+3)+" "+
	    //	  mTextureBuffer.get(i+4)+" "+mTextureBuffer.get(i+5)+" "+mTextureBuffer.get(i+6)+" "+mTextureBuffer.get(i+7));
	}
    }

    public void dragStart(float x, float y) {
	lastRot = thisRot;
	arcBall.dragStart(x, y);
    }

    public void drag(float x, float y) {
	Quaternion q = arcBall.drag(x, y);
	startQuat.mulLeft(q);
	rotate.set(startQuat);
    }

    public void setDimensions(int w, int h) {
	super.setDimensions(w, h);
	arcBall.setDimensions(w, h);
    }

    public void translate(float x, float y, float z) {
	transScale.setTranslation(x, y, z);
    }

    public void scale(float m) {
	scale *= m;
	if(scale < 0.2f) scale = 0.2f;
	if(scale > 1.5f) scale = 1.5f;
    }

}
