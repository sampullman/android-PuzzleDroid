package com.threeDBJ.puzzleDroid;

public class Quaternion {

    public float x, y, z, w;

    public Quaternion() {
	x = y = z = w = 0f;
    }

    public Quaternion(float x, float y, float z) {
	this(x, y, z, 0f);
    }

    public Quaternion(float x, float y, float z, float w) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.w = w;
    }

    public Quaternion(Vec3 v, float w) {
	x = v.x;
	y = v.y;
	z = v.z;
	this.w = w;
    }

    public Quaternion(Quaternion q) {
	this(q.x, q.y, q.z, q.w);
    }

    public void mul(Quaternion q) {
	float oldx=x, oldy=y, oldz=z, oldw=w;
	w = oldw*q.w - oldx*q.x  -  oldy*q.y  -  oldz*q.z;
	x = oldw*q.x + oldx*q.w  +  oldy*q.z  -  oldz*q.y;
	y = oldw*q.y + oldy*q.w  +  oldz*q.x  -  oldx*q.z;
	z = oldw*q.z + oldz*q.w  +  oldx*q.y  -  oldy*q.x;
    }

    public void normalize() {
	float mag = (float)Math.sqrt((x*x) + (y*y) + (z*z) + (w*w));
	if(mag != 0.0) {
	    x = x / mag;
	    y = y / mag;
	    z = z / mag;
	    w = w / mag;
	}
    }

    public Quaternion conj() {
	return new Quaternion(x * -1f, y * -1f, z * -1f);
    }

    public float[] toAxisAngle() {
	float[] result = new float[4];
	float scale = (float)Math.sqrt(x * x + y * y + z * z);
	scale = (scale == 0f) ? 1f : scale;
	result[1] = x / scale;
	result[2] = y / scale;
	result[3] = z / scale;
	result[0] = (float)Math.acos(Math.min(1f, w)) * 2.0f;
	return result;
    }

    public static Quaternion fromAxisAngle(Vec3 axis, float angle) {
	float half = angle / 2;
	float mult = (float)Math.sin(half);
	Quaternion q = new Quaternion();
	q.w = (float)Math.cos(half);
	q.x = axis.x * mult;
	q.y = axis.y * mult;
	q.z = axis.z * mult;
	return q;
    }

    public Mat3 getMatrix() {
	Mat3 ret = new Mat3();
	float[][] r = ret.m;
	float n, s;
	n = (x * x) + (y * y) + (z * z) + (w * w);
        s = (n > 0.0f) ? (2.0f / n) : 0.0f;
	float x22 = s * x * x;
	float y22 = s * y * y;
	float z22 = s * z * z;
	float xy2 = s * x * y;
	float xz2 = s * x * z;
	float yz2 = s * y * z;
	float wx2 = s * w * x;
	float wy2 = s * w * y;
	float wz2 = s * w * z;
	r[0][0] = 1f - y22 - z22;
	r[0][1] = xy2 + wz2;
	r[0][2] = xz2 - wy2;

	r[1][0] = xy2 - wz2;
	r[1][1] = 1f - x22 - z22;
	r[1][2] = yz2 - wx2;

	r[2][0] = xz2 + wy2;
	r[2][1] = yz2 + wx2;
	r[2][2] = 1f - x22 - y22;
	return ret;
    }

    public String toString() {
	return "[ "+x+", "+y+", "+z+", ("+w+")]";
    }

}
