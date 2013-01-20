package com.threeDBJ.puzzleDroid;

import java.nio.FloatBuffer;

public class Texture {

    public int resource;
    public int id;
    public int width;
    public int height;
    public boolean loaded;

    public Texture() {
        reset();
    }

    public Texture(int resource) {
	reset();
	this.resource = resource;
    }

    public void reset() {
        resource = -1;
        id = -1;
        width = 0;
        height = 0;
        loaded = false;
    }

    public float coords[] = {
	//Mapping coordinates for the vertices
	1.0f, 1.0f,
	1.0f, 0.0f,
	0.0f, 1.0f,
	0.0f, 0.0f };

    public void putCoords(FloatBuffer texBuffer) {
	texBuffer.put(coords);
    }

}