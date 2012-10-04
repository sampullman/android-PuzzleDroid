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
       	GLVertex leftBottomBack = addVertex(left, bottom, back);
        GLVertex rightBottomBack = addVertex(right, bottom, back);
       	GLVertex leftTopBack = addVertex(left, top, back);
        GLVertex rightTopBack = addVertex(right, top, back);
       	GLVertex leftBottomFront = addVertex(left, bottom, front);
        GLVertex rightBottomFront = addVertex(right, bottom, front);
       	GLVertex leftTopFront = addVertex(left, top, front);
        GLVertex rightTopFront = addVertex(right, top, front);

        /* Vertices added in clockwise order (when viewed from the front) */
        // bottom
        addFace(new GLFace(leftBottomBack, leftBottomFront,
			   rightBottomFront, rightBottomBack));
        // front
        addFace(new GLFace(leftBottomFront, leftTopFront,
			   rightTopFront, rightBottomFront));
        // left
        addFace(new GLFace(leftBottomBack, leftTopBack,
			   leftTopFront, leftBottomFront));
        // right
        addFace(new GLFace(rightBottomBack, rightBottomFront,
			   rightTopFront, rightTopBack));
        // back
        addFace(new GLFace(leftBottomBack, rightBottomBack,
			   rightTopBack, leftTopBack));
        // top
        addFace(new GLFace(leftTopBack, rightTopBack,
			   rightTopFront, leftTopFront));
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

    @Override
    public int hashCode() {
	return id;
    }

    @Override
    public boolean equals(Object o) {
	return id == ((Cube)o).id;
    }

}