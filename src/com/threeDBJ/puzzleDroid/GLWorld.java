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

import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLU;

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
    int w, h;
    float adjustWidth, adjustHeight, scale=1f;

    public void GLWorld() {
	transScale.setScale(0.5f, 0.5f, 0.5f);
    }

    public void setRubeCube(RubeCube cube) {
	this.cube = cube;
    }

    public void draw(GL11 gl) {
	super.draw(gl);
	gl.glEnable(GL11.GL_DEPTH_TEST);
	gl.glDepthFunc(GL11.GL_LEQUAL);
	gl.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	gl.glPushMatrix();
	cube.animate();
	gl.glScalef(scale, scale, scale);
	gl.glMultMatrixf(rotate.val, 0);
	gl.glFrontFace(GL10.GL_CW);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
	gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
	mColorBuffer.position(0);
	mTextureBuffer.position(0);
	mVertexBuffer.position(0);
	mIndexBuffer.position(0);
        gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        drawCount++;
	gl.glPopMatrix();
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
	arcBall.setDimensions(w, h);
	this.w = w;
	this.h = h;
	this.adjustWidth = 1f / w;
	this.adjustHeight = 1f / h;
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
