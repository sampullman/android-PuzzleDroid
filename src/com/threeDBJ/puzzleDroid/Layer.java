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
    static final int POS=0, NEG=1, H=0, V=1;
    int mode, type, fixInd = 21;
    float angle=0f, fixAngle;
    Vec3 zero;
    Mat4 rotation = new Mat4();

    public Layer(RubeCube cube, Vec3 zero, int axis) {
	this.zero = zero;
	this.axis = axis;
	this.cube = cube;
    }

    public void setType(int type) {
	this.type = type;
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
		cube.endLayerAnimation(type, angle);
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
	    angle = angle = ((type == H) ? dir.x : -1f * dir.y);
	}
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

	float sin = (float)Math.sin(angle);
	float cos = (float)Math.cos(angle);
	float[] m = rotation.val;
	switch (axis) {
	case XAxis:
	    m[Mat4.M11] = cos;
	    m[Mat4.M12] = sin;
	    m[Mat4.M21] = -sin;
	    m[Mat4.M22] = cos;
	    m[Mat4.M00] = 1f;
	    m[Mat4.M01] = m[Mat4.M02] = m[Mat4.M10] = m[Mat4.M20] = 0f;
	    break;
	case YAxis:
	    m[Mat4.M00] = cos;
	    m[Mat4.M02] = sin;
	    m[Mat4.M20] = -sin;
	    m[Mat4.M22] = cos;
	    m[Mat4.M11] = 1f;
	    m[Mat4.M01] = m[Mat4.M10] = m[Mat4.M12] = m[Mat4.M21] = 0f;
	    break;
	case ZAxis:
	    m[Mat4.M00] = cos;
	    m[Mat4.M01] = sin;
	    m[Mat4.M10] = -sin;
	    m[Mat4.M11] = cos;
	    m[Mat4.M22] = 1f;
	    m[Mat4.M20] = m[Mat4.M21] = m[Mat4.M02] = m[Mat4.M12] = 0f;
	    break;
	}
	for (Cube cube : cubes) {
	    if (cube != null) {
		cube.animateTransform(rotation);
	    }
	}
    }
}
