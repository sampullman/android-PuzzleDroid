package com.threeDBJ.puzzleDroid;

import java.util.ArrayList;

abstract class TextureViewAnimation {

    ArrayList<TextureView> mViews = new ArrayList<TextureView>();
    int tick, nTicks;
    boolean isStarted=false, isFinished=false;

    /* TODO - Think about implementing real time duration */
    public TextureViewAnimation(int nTicks) {
	this.nTicks = nTicks;
    }

    public void addView(TextureView view) {
	mViews.add(view);
    }

    public void startAnimation() {
	tick = 0;
	isStarted = true;
    }

    public void stepAnimation() {
	if(tick >= nTicks) {
	    isFinished = true;
	    isStarted = false;
	}
	tick += 1;
    }

    public boolean finished() {
	return isFinished;
    }

}