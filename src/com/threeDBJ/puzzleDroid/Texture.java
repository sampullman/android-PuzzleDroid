package com.threeDBJ.puzzleDroid;

import java.nio.FloatBuffer;

public class Texture {

    private float coords[] = {
	//Mapping coordinates for the vertices
	1.0f, 1.0f,
	1.0f, 0.0f,
	0.0f, 1.0f,
	0.0f, 0.0f };

    public void putCoords(FloatBuffer texBuffer) {
	texBuffer.put(coords);
    }

}