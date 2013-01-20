package com.threeDBJ.puzzleDroid;

import javax.microedition.khronos.opengles.GL11;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class TextureView extends GLEnvironment implements Clickable {

    ArrayList<TextureView> children = new ArrayList<TextureView>();
    GLShape surface;
    float l, r, b, t, z, percentX, percentY, texRight, texTop;
    GLVertex lb;
    GLVertex lt;
    GLVertex rt;
    GLVertex rb;
    boolean visible = true;
    TextureClickListener mListener;
    boolean pressed = false;

    TextureViewAnimation mAnimation;

    public TextureView() {
    }

    public void setFace(float l, float r, float b, float t, float z, GLColor c) {
	this.l = l; this.r = r; this.b = b; this.t = t; this.z = z;
	mVertexList.clear();
	mShapeList.clear();
	surface = new GLShape(this);
	rb = surface.addVertex(r, b, z);
	rt = surface.addVertex(r, t, z);
	lb = surface.addVertex(l, b, z);
	lt = surface.addVertex(l, t, z);
	GLFace f = new GLFace(rb, rt, lb, lt);
	f.setColor(c);
	surface.addFace(f);
	surface.setTexture(mTexture);
	addShape(surface);
	generate();
    }

    public void setTextureBounds(float percentX, float percentY) {
	this.percentX = percentX;
	this.percentY = percentY;
	texRight = (percentX * (r - l)) + l;
	texTop = (percentY * (t - b)) + b;
    }

    public void draw(GL11 gl) {
	super.draw(gl);
	gl.glBindTexture(GL11.GL_TEXTURE_2D, mTexture.id);
	mIndexBuffer.position(0);
	mColorBuffer.position(0);
	mVertexBuffer.position(0);
	mTextureBuffer.position(0);
	gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL11.GL_FIXED, 0, mColorBuffer);
        gl.glDrawElements(GL11.GL_TRIANGLES, mIndexCount, GL11.GL_UNSIGNED_SHORT, mIndexBuffer);
	for(TextureView child : children) {
	    child.draw(gl);
	}
    }

    public void addChild(TextureView child) {
	children.add(child);
    }

    public void setClickListener(TextureClickListener listener) {
	mListener = listener;
    }

    // public void setVisibility(boolean visible) {
    // 	this.visible = visible;
    // 	for(TextureView child : children) {
    // 	    child.setVisibility(visible);
    // 	}
    // }

    // public boolean isVisible() {
    // 	return visible;
    // }

    public void animate(TextureViewAnimation animation) {
	setAnimation(animation);
	startAnimation();
    }

    public void setAnimation(TextureViewAnimation animation) {
	mAnimation = animation;
	animation.addView(this);
	for(TextureView child : children) {
	    child.setAnimation(animation);
	}
    }

    public void startAnimation() {
	mAnimation.startAnimation();
    }

    public void animate() {
	if(mAnimation != null) {
	    mAnimation.stepAnimation();
	    if(mAnimation.finished()) {
		mAnimation = null;
	    }
	}
    }

    /* Translates the view. Not a good idea to translate in z direction,
       wierd things will happen unless translated back within a small time frame */
    public void translate(float x, float y, float z) {
	l += x; r += x; texRight += x;
	b += y; t += y; texTop += y;
	for(GLVertex v : mVertexList) {
	    v.translate(mVertexBuffer, x, y, z);
	}
    }

    public boolean touchHit(Vec2 p) {
	if(p.x > l && p.x < texRight && p.y > b && p.y < texTop) {
	    return true;
	}
	return false;
    }

    public boolean handleActionDown(Vec2 p) {
	if(touchHit(p)) {
	    pressed = true;
	    for(TextureView child : children) {
		child.handleActionDown(p);
	    }
	    return true;
	}
	return false;
    }

    public boolean handleActionMove(Vec2 p) {
	if(pressed) {
	    for(TextureView child : children) {
		child.handleActionMove(p);
	    }
	    return true;
	} else {
	    return false;
	}
    }

    public boolean handleActionUp(Vec2 p) {
	if(pressed) {
	    if(mListener != null && touchHit(p)) {
		mListener.onClick();
	    }
	    for(TextureView child : children) {
		child.handleActionUp(p);
	    }
	    pressed = false;
	    return true;
	}
	return false;
    }

}