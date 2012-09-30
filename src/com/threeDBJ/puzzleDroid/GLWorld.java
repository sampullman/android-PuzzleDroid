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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLU;

public class GLWorld {

    public static float radToDeg = 180f / (float)Math.PI;

    private ArrayList<GLShape>	mShapeList = new ArrayList<GLShape>();
    private ArrayList<GLVertex>	mVertexList = new ArrayList<GLVertex>();

    private int mIndexCount = 0;

    private IntBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ShortBuffer mIndexBuffer;

    RubeCube cube;

    public float ztrans = 0f;

    int drawCount = 0;

    Quaternion curQuat = new Quaternion(0f, 0f, 0f, 1f);
    Quaternion startQuat = new Quaternion(0f, 0f, 0f, 1f);
    Mat4 rotate = new Mat4(), scale = new Mat4(), transScale = new Mat4();
    Mat3 lastRot = new Mat3();
    Mat3 thisRot = new Mat3();
    Vec3 startPos = new Vec3(), curPos = new Vec3();
    ArcBall arcBall = new ArcBall();
    int w, h;
    float adjustWidth, adjustHeight;

    public void GLWorld() {
	transScale.setScale(0.5f, 0.5f, 0.5f);
    }

    public void setRubeCube(RubeCube cube) {
	this.cube = cube;
    }

    public void addShape(GLShape shape) {
	mShapeList.add(shape);
	mIndexCount += shape.getIndexCount();
    }

    public void generate() {
	ByteBuffer bb = ByteBuffer.allocateDirect(mVertexList.size()*4*4);
	bb.order(ByteOrder.nativeOrder());
	mColorBuffer = bb.asIntBuffer();

	bb = ByteBuffer.allocateDirect(mVertexList.size()*4*3);
	bb.order(ByteOrder.nativeOrder());
	mVertexBuffer = bb.asIntBuffer();

	bb = ByteBuffer.allocateDirect(mIndexCount*2);
	bb.order(ByteOrder.nativeOrder());
	mIndexBuffer = bb.asShortBuffer();

	Iterator<GLVertex> iter2 = mVertexList.iterator();
	while (iter2.hasNext()) {
	    GLVertex vertex = iter2.next();
	    vertex.put(mVertexBuffer, mColorBuffer);
	}

	Iterator<GLShape> iter3 = mShapeList.iterator();
	while (iter3.hasNext()) {
	    GLShape shape = iter3.next();
	    shape.putIndices(mIndexBuffer);
	}

    }

    public GLVertex addVertex(float x, float y, float z) {
	GLVertex vertex = new GLVertex(x, y, z, mVertexList.size());
	mVertexList.add(vertex);
	return vertex;
    }

    public void transformVertex(GLVertex vertex, Mat4 transform) {
	vertex.update(mVertexBuffer, transform);
    }

    public void draw(GL10 gl) {
	//gl.glPushMatrix();
	cube.animate();
	gl.glMultMatrixf(rotate.val, 0);
	mColorBuffer.position(0);
	mVertexBuffer.position(0);
	mIndexBuffer.position(0);
	gl.glFrontFace(GL10.GL_CW);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        drawCount++;
	//gl.glPopMatrix();
	gl.glFlush();
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

}
