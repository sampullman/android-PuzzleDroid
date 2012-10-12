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

public class Cube extends GLShape {

    public static final int kBottom = 0;
    public static final int kFront = 1;
    public static final int kLeft = 2;
    public static final int kRight = 3;
    public static final int kBack = 4;
    public static final int kTop = 5;

    public int face, id;
    public Vec3 normal;
    CubeSide side;
    CubeRegistry cubeRegistry = new CubeRegistry();
    Layer hLayer, vLayer;

    public Cube(GLWorld world, float left, float bottom,
		float back, float right, float top, float front) {
	super(world);
       	GLVertex lbBack = addVertex(left, bottom, back);
        GLVertex rbBack = addVertex(right, bottom, back);
       	GLVertex ltBack = addVertex(left, top, back);
        GLVertex rtBack = addVertex(right, top, back);
       	GLVertex lbFront = addVertex(left, bottom, front);
        GLVertex rbFront = addVertex(right, bottom, front);
       	GLVertex ltFront = addVertex(left, top, front);
        GLVertex rtFront = addVertex(right, top, front);

	// Bottom
	addCubeSide(rbBack, rbFront, lbBack, lbFront);
	// Front
	addCubeSide(rbFront, rtFront, lbFront, ltFront);
	// Left
	addCubeSide(lbFront, ltFront, lbBack, ltBack);
	// Right
	addCubeSide(rbBack, rtBack, rbFront, rtFront);
	// Back
	addCubeSide(lbBack, ltBack, rbBack, rtBack);
	// Top
	addCubeSide(rtFront, rtBack, ltFront, ltBack);
    }

    private void addCubeSide(GLVertex rb, GLVertex rt, GLVertex lb, GLVertex lt) {
    	addFace(new GLFace(rb, rt, addVertex(lb), lt));
    }

    public Vec3 getNormal(int face) {
	return getFace(face).normal;
    }

    public void setSide(CubeSide side) {
	this.side = side;
    }

    public void setLayers(Layer hLayer, Layer vLayer) {
	this.hLayer = hLayer;
	this.vLayer = vLayer;
    }

    public void snapToAxis() {
	float[] euler = rot.getEulerAngles();
	Log.e("Cube!", euler[0]+" "+euler[1]+" "+euler[2]);
    }

}