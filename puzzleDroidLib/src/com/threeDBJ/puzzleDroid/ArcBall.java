package com.threeDBJ.puzzleDroid;

import android.util.Log;

public class ArcBall {

    Vec3 start, end;
    float adjustWidth, adjustHeight, halfRat;

    public ArcBall() {
	this(0, 0);
    }

    public ArcBall(int w, int h) {
	setDimensions(w, h);
    }

    public void setDimensions(int w, int h) {
	float rat1 = (float) w / (float) h;
	float rat2 = (float) h / (float) w;
	float rat;
	if(rat1 > rat2) {
	    rat = (rat1 + rat2 + rat2) / 3f;
	} else {
	    rat = (rat1 + rat1 + rat2) / 3f;
	}
	halfRat = rat / 2f;
	adjustWidth = 1f * rat / w;
	adjustHeight = 1f * rat / h;
    }

    public Vec3 mapToSphere(float x, float y) {
	float sX = x * adjustWidth - halfRat;
	float sY = halfRat - y * adjustHeight;
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
	    ret = new Quaternion(cross, 130f * start.dot(end));
	} else {
	    ret = new Quaternion();
	}
	start = end;
	return ret;
    }

    public void dragEnd() {

    }

}