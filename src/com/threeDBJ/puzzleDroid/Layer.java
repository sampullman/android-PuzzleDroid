package com.threeDBJ.puzzleDroid;
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

import android.util.Log;

import java.util.HashSet;

public class Layer {

    RubeCube cube;
    HashSet<Cube> cubes = new HashSet<Cube>();
    //Cube[] cubes;
    // which axis do we rotate around?
    // 0 for X, 1 for Y, 2 for Z
    int axis;
    static public final int XAxis = 0;
    static public final int YAxis = 1;
    static public final int ZAxis = 2;
    static public final float PI = (float)Math.PI;
    static public final float PI2 = 2f * PI;
    static public final float HALFPI = PI / 2f;
    static public final float TODEG = 180f / PI;
    static final int POS=0, NEG=1, H=0, V=1;
    int mode, type, fixInd = 21, index;
    float angle=0f, fixAngle;
    Vec3 zero, axisVec;
    Quaternion localRot;

    public Layer(RubeCube cube, Vec3 zero, int axis, int index) {
	this.zero = zero;
	this.axis = axis;
	this.axisVec = new Vec3(zero);
	this.cube = cube;
	this.index = index;
	transInv.trn(zero);
	trans = new Mat4(transInv).inv();
    }

    public void setType(int type) {
	this.type = type;
    }

    public void clear() {
	cubes.clear();
    }

    public void add(Cube c) {
	cubes.add(c);
    }

    public void replaceCube(Cube oldCube, Cube newCube) {
	cubes.remove(oldCube);
	cubes.add(newCube);
    }

    public void startAnimation() {
	for(Cube cube : cubes) {
	    if (cube != null) {
		cube.startAnimation();
	    }
	}
    }

    public void endAnimation() {
	for (Cube cube : cubes) {
	    if (cube != null) {
		cube.endAnimation();
	    }
	}
    }

    /* Rotates the layer to a stable position. Calls cube.endLayerAnimation
       when finished to update layers and sides. */
    public void animate() {
	if(fixInd < 10) {
	    setAngle(fixAngle);
	    fixInd += 1;
	    if(fixInd == 10) {
		cube.endLayerAnimation(axis, angle, index);
		angle = 0f;
	    }
	}
    }

    public void drag(Vec2 dir, int face) {
	float angle;
	if(face == Cube.kRight) {
	    angle = ((type == H) ? dir.x : dir.y);
	} else if(face == Cube.kBack) {
	    angle = ((type == H) ? dir.x : dir.y);
	} else if(face == Cube.kBottom) {
	    angle = ((type == H) ? -1f * dir.x : -1f * dir.y);
	} else {
	    angle = ((type == H) ? dir.x : -1f * dir.y);
	}
	if(axis == YAxis && type == H) angle *= -1f;
	setAngle(angle);
    }

    public void dragEnd() {
    	float a = angle % HALFPI;
    	Log.e("Cube angle", a+"");
    	if(a < 0) {
    	    if(a < -1f * (HALFPI / 2f)) {
    		fixAngle = (-1 * HALFPI - a) / 10f;
    		fixInd = 0;
    	    } else {
    		fixAngle = -1f * a / 10f;
    		fixInd = 0;
    	    }
    	} else {
    	    if(a > HALFPI / 2f) {
    		fixAngle = (HALFPI - a) / 10f;
    		fixInd = 0;
    	    } else {
    		fixAngle = -1f * a / 10f;
    		fixInd = 0;
    	    }
    	}

    }

    public void setAngle(float angle) {
	// normalize the angle
	while (angle >= PI) angle -= PI2;
	while (angle < -1 * PI) angle += PI2;
	this.angle += angle;

	localRot = new Quaternion(axisVec, angle, true);
	for (Cube cube : cubes) {
	    if (cube != null) {
		cube.animateTransform(localRot, trans, transInv);
	    }
	}
    }
}
