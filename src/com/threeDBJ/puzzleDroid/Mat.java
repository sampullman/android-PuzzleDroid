package com.threeDBJ.puzzleDroid;

public abstract class Mat {

    int DIM;

    float[][] m;

    public void setIdentity() {
	for(int i=0;i<DIM;i+=1) {
	    m[i][i] = 1.0f;
	}
    }

}