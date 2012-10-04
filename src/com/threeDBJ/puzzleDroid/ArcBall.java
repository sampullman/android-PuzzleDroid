package com.threeDBJ.puzzleDroid;

import android.util.Log;

public class ArcBall {

    Vec3 start, end;
    float adjustWidth, adjustHeight;

    public ArcBall() {
	this(0, 0);
    }

    public ArcBall(int w, int h) {
	setDimensions(w, h);
    }

    public void setDimensions(int w, int h) {
	adjustWidth = 1.4f / w;
	adjustHeight = 1.4f / h;
    }

    public Vec3 mapToSphere(float x, float y) {
	float sX = x * adjustWidth - 0.7f;
	float sY = 0.7f - y * adjustHeight;
	float l2 = sX * sX + sY * sY;
	if(l2 > 1.0f) {
	    float norm = 1f / (float)Math.sqrt(l2);
	    return new Vec3(sX * norm, sY * norm, 0f);
	} else {
	    return new Vec3(sX, sY, (float)Math.sqrt(1f - l2));
	}
    }

    public void dragStart(float x, float y) {
	start = mapToSphere(x, y);
    }

    public Quaternion drag(float x, float y) {
	Quaternion ret;
	end = mapToSphere(x, y);
	Vec3 cross = new Vec3(start);
	cross.crs(end);
	if(cross.len() > 0.00001) {
	    ret = new Quaternion(cross, 100f * start.dot(end));
	} else {
	    ret = new Quaternion();
	}
	start = end;
	return ret;
    }

    public void dragEnd() {

    }

}