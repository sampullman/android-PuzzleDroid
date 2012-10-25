package com.threeDBJ.puzzleDroid;

import javax.microedition.khronos.opengles.GL11;

import android.content.Context;

public class TextureButton extends TextureTextView {

    Texture mNormalTexture, mPressedTexture;

    public TextureButton(TextureFont font) {
	super(font);
    }

    public void draw(GL11 gl) {
	if(pressed) {
	    mTexture = mPressedTexture;
	} else {
	    mTexture = mNormalTexture;
	}
	super.draw(gl);
    }

    public void setPressedTexture(GL11 gl, Context c, int res) {
        mPressedTexture= new Texture(res);
	loadTexture(gl, c, mPressedTexture);
    }

    public void setTexture(GL11 gl, Context c, int res) {
	super.setTexture(gl, c, res);
	mNormalTexture = mTexture;
    }

    public boolean handleActionDown(Vec2 p) {
	if(touchHit(p)) {
	    pressed = true;
	    return true;
	}
	return false;
    }

    public boolean handleActionMove(Vec2 p) {
	if(pressed) {
	    if(!touchHit(p)) {
		pressed = false;
	    }
	    return true;
	} else {
	    return false;
	}
    }

    public boolean handleActionUp(Vec2 p) {
	if(pressed) {
	    pressed = false;
	    if(mListener != null && touchHit(p)) {
		mListener.onClick();
		return true;
	    }
	}
	return false;
    }

}
