package com.threeDBJ.puzzleDroid;

public class Vec3 {

    float x, y, z;

    public Vec3() {
	x = 0;
	y = 0;
	z = 0;
    }

    public Vec3(float x, float y, float z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public static Vec3 cross(Vec3 v1, Vec3 v2) {
	float x = (v1.y*v2.z) - (v2.y*v1.z);
	float y = (v1.z*v2.x) - (v2.z*v1.x);
	float z = (v1.x*v2.y) - (v2.x*v1.y);
	return new Vec3(x, y, z);
    }

    public static float dot(Vec3 v1, Vec3 v2) {
	return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public void normalize() {
	float mag = magnitude();
	if(mag != 0f) {
	    x = x / mag;
	    y = y / mag;
	    z = z / mag;
	}
    }

    public float magnitude() {
	return (float)Math.sqrt((x*x + y*y + z*z));
    }

    public void sub(Vec3 v) {
	this.x -= v.x;
	this.y -= v.y;
	this.z -= v.z;
    }

    public String toString() {
	return "["+x+", "+y+", "+z+"]";
    }

}