package com.threeDBJ.puzzleDroid;

import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class TextureSlider extends TextureView {

    Texture mButtonTexture;
    int btnIndexCount, colorStart, vertStart, indStart, texStart, vertListStart;
    float btn_l, btn_r, btn_t, btn_b, w, h, step;
    int prevInd=0;
    Vec2 prevTouch;

    OnValueChangedListener mListener;

    ArrayList values;

    public TextureSlider(ArrayList values) {
	super();
	this.values = values;
    }

    public void setTexture(GL11 gl, Context c, int sliderRes, int btnRes) {
	super.setTexture(gl, c, sliderRes);
	mButtonTexture = new Texture(btnRes);
	loadTexture(gl, c, mButtonTexture);
    }

    public void setFace(float l, float r, float b, float t, float z, GLColor c) {
	super.setFace(l, r, b, t, z, c);
	w = r - l;
	h = t - b;
	step = w / (float)values.size();
	colorStart = mColorBuffer.position();
	vertStart = mVertexBuffer.position();
	indStart = mIndexBuffer.position();
	texStart = mTextureBuffer.position();
	vertListStart = mVertexList.size();
	int tempIndexCount = mIndexCount;
	z += 0.02;
	float h = (t - b)*1.5f;
	btn_b = b - h / 6f;
	btn_t = btn_b + h;
	btn_l = l;
	btn_r = btn_l + h;
	GLShape btn = new GLShape(this);
	rb = btn.addVertex(btn_r, btn_b, z);
	rt = btn.addVertex(btn_r, btn_t, z);
	lb = btn.addVertex(btn_l, btn_b, z);
	lt = btn.addVertex(btn_l, btn_t, z);
	GLFace f = new GLFace(rb, rt, lb, lt);
	f.setColor(c);
	btn.addFace(f);
	btn.setTexture(mButtonTexture);
	addShape(btn);
	generate();
	btnIndexCount = mIndexCount - tempIndexCount;
	mIndexCount = tempIndexCount;
    }

    public void draw(GL11 gl) {
	super.draw(gl);
	gl.glBindTexture(GL11.GL_TEXTURE_2D, mButtonTexture.id);
	gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL11.GL_FIXED, 0, mColorBuffer);
	mIndexBuffer.position(indStart);
	mColorBuffer.position(colorStart);
	mVertexBuffer.position(vertStart);
	mTextureBuffer.position(texStart);
        gl.glDrawElements(GL11.GL_TRIANGLES, btnIndexCount, GL11.GL_UNSIGNED_SHORT, mIndexBuffer);
    }

    public void setIndex(int ind) {
	if(ind >= 0 && ind < values.size()) {
	    float diff = (l + ((float)ind) * (step-1)) - btn_l;
	    translateButton(diff);
	    prevInd = ind;
	    snapToIndex();
	}
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
	mListener = listener;
    }

    public void translate(float x, float y, float z) {
	super.translate(x, y, z);
	btn_b += y; btn_t += y;
	btn_l += x; btn_r += x;
    }

    public boolean handleActionDown(Vec2 p) {
	if(p.x > btn_l && p.x < btn_r && p.y > btn_b && p.y < btn_t) {
	    pressed = true;
	    prevTouch = p;
	    return true;
	} else if(touchHit(p)) {
	    pressed = true;
	    prevTouch = p;
	    float diff = p.x - btn_l;
	    translateButton(diff);
	    checkNewInd();
	    snapToIndex();
	    return true;
	}
	return false;
    }

    public boolean handleActionMove(Vec2 p) {
	if(pressed) {
	    float diff = p.x - prevTouch.x;
	    translateButton(diff);
	    checkNewInd();
	    prevTouch = p;
	    return true;
	}
	return false;
    }

    private void checkNewInd() {
	int newInd = getNewIndex();
	if(mListener != null && newInd != prevInd) {
	    mListener.onValueChanged(values.get(newInd));
	    prevInd = newInd;
	}
    }

    private int getNewIndex() {
	float btn_center = btn_l - l;
	int ind = (int)(btn_center / step);
	if(btn_center % step > step / 2f) {
	    ind += 1;
	}
	if(ind < 0) ind = 0;
	if(ind >= values.size()) ind = values.size()-1;
	return ind;
    }

    public void translateButton(float x) {
	if(btn_l + x < l) {
	    x = l - btn_l;
	    btn_l = l;
	} else if(btn_r + x > r) {
	    x = r - btn_r;
	    btn_r = r;
	}
	btn_l += x; btn_r += x;
	for(int i=vertListStart;i<mVertexList.size();i+=1) {
	    mVertexList.get(i).translate(mVertexBuffer, x, 0f, 0f);
	}
    }

    private void snapToIndex() {
	float diff = ((float)prevInd * step + l) - btn_l;
	translateButton(diff);
    }

    public boolean handleActionUp(Vec2 p) {
	if(pressed) {
	    snapToIndex();
	    prevTouch = null;
	    pressed = false;
	    return true;
	}
	return false;
    }

}