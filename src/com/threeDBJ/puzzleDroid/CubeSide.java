package com.threeDBJ.puzzleDroid;

import android.util.Log;

public class CubeSide {

    static final float eps = 0.0001f;

    int frontFace, dim;
    Vec3 normal, aPoint;
    GLWorld world;

    Layer[] hLayers, vLayers;
    float[] bounds = new float[6];
    CubeSide hNeg, hPos, vNeg, vPos;

    public CubeSide(GLWorld world, int dim, int frontFace, float xMin, float xMax,
		    float yMin, float yMax, float zMin, float zMax) {
	this.frontFace = frontFace;
	this.world = world;
	this.dim = dim;
	this.normal = new Vec3(xMin + xMax, yMin + yMax, zMin + zMax);
	this.normal.nor();
	this.aPoint = new Vec3(normal);
	bounds[0] = xMin - eps;
	bounds[1] = xMax + eps;
	bounds[2] = yMin - eps;
	bounds[3] = yMax + eps;
	bounds[4] = zMin - eps;
	bounds[5] = zMax + eps;
    }

    public void setHLayers(Layer[] l) {
	hLayers = l;
    }

    public void setVLayers(Layer[] l) {
	vLayers = l;
    }

    public Vec3 getNormal() {
	Vec3 temp = new Vec3(normal);
	return temp.rot(world.rotate);
    }

    public Vec3 getPointOnPlane() {
	Vec3 temp = new Vec3(aPoint);
	return temp.rot(world.rotate);
    }

    public Layer getHLayer(Vec2 ind) {
	if(frontFace == Cube.kTop) {
	    return hLayers[(int)ind.y];
	}
	return hLayers[dim - (int)ind.y - 1];
    }

    public Layer getVLayer(Vec2 ind) {
	if(frontFace == Cube.kRight) {
	    return vLayers[dim - (int)ind.x - 1];
	}else if(frontFace == Cube.kBack) {
	    return vLayers[dim - (int)ind.x - 1];
	}
	return vLayers[(int)ind.x];
    }

    private Vec2 getPlaneValues(Vec3 v) {
	Vec2 ret = new Vec2();
	switch(frontFace) {
	case Cube.kFront:
	    return new Vec2(v.x, v.y);
	case Cube.kBack:
	    return new Vec2(1f - v.x, v.y);
	case Cube.kLeft:
	    return new Vec2(v.z, v.y);
	case Cube.kRight:
	    return new Vec2(1f - v.z, v.y);
	case Cube.kTop:
	    return new Vec2(v.x, 1f - v.z);
	case Cube.kBottom:
	    return new Vec2(v.x, v.z);
	}
	return null;
    }

    /* Returns the hit point on the plane containing this side, regardless of
       whether the side was hit */
    public Vec2 getPlaneHitLoc(Vec3 start, Vec3 dir) {
	Vec3 norm = getNormal();
	float denom = dir.dot(norm);
	Vec3 p = getPointOnPlane();
	p.sub(start);
	float d = p.dot(norm) / denom;
	Vec3 hp = dir.mul(d).add(start);
	Mat4 rotInv = new Mat4(world.rotate);
	rotInv.inv();
	hp.rot(rotInv);
	hp.x = (hp.x + 1f) / 2f;
	hp.y = (hp.y + 1f) / 2f;
	hp.z = (hp.z + 1f) / 2f;
	Vec2 v = getPlaneValues(hp);
	v.x = (v.x * (float)dim);
	v.y = ((1f - v.y) * (float)dim);
	return v;
    }

    /* Gets the point on this side pointed to by a vector begining at start
       in the direction of dir. Returns null if the side is not hit. */
    private Vec3 hitPoint(Vec3 start, Vec3 dir) {
	Vec3 norm = getNormal();
	float denom = dir.dot(norm);
	if(denom <= 0f) return null;
	Vec3 p = getPointOnPlane();
	p.sub(start);
	float d = p.dot(norm) / denom;
	if(true) {
	    return dir.mul(d).add(start);
	} else {
	    return null;
	}
    }

    public Vec2 getHitLoc(Vec3 start, Vec3 dir) {
	Vec3 hp = hitPoint(start, dir);
	if(hp == null) return null;
	Mat4 rotInv = new Mat4(world.rotate);
	rotInv.inv();
	hp.rot(rotInv);
	if(hp.x >= bounds[0] && hp.x <= bounds[1] && hp.y >= bounds[2] &&
	   hp.y <= bounds[3] && hp.z >= bounds[4] && hp.z <= bounds[5]) {
	    hp.x = (hp.x + 1f) / 2f;
	    hp.y = (hp.y + 1f) / 2f;
	    hp.z = (hp.z + 1f) / 2f;
	    Vec2 v = getPlaneValues(hp);
	    v.x = (v.x * (float)dim);
	    v.y = ((1f - v.y) * (float)dim);
	    return v;
	} else {
	}
	return null;
    }

}