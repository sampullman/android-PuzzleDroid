package com.threeDBJ.puzzleDroid;

public class CubeRegistry {

    public int curId = 0;

    public void register(Cube c) {
	c.id = curId;
	curId += 1;
    }

}