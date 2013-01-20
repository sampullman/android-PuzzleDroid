package com.threeDBJ.puzzleDroid;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import java.util.Arrays;
import java.util.ArrayList;

import com.threeDBJ.MGraphicsLib.GLEnvironment;
import com.threeDBJ.MGraphicsLib.Vec2;
import com.threeDBJ.MGraphicsLib.GLColor;
import com.threeDBJ.MGraphicsLib.TextureView;
import com.threeDBJ.MGraphicsLib.TranslateAnimation;
import com.threeDBJ.MGraphicsLib.OnValueChangedListener;
import com.threeDBJ.MGraphicsLib.TextureClickListener;
import com.threeDBJ.MGraphicsLib.TextureFont;
import com.threeDBJ.MGraphicsLib.TextureSlider;
import com.threeDBJ.MGraphicsLib.TextureButton;
import com.threeDBJ.MGraphicsLib.TextureTextView;
import com.threeDBJ.MGraphicsLib.TextureStateView;
import com.threeDBJ.MGraphicsLib.TextureTimer;

public class CubeMenu extends GLEnvironment {

    static final int NONE=0, SINGLE_TOUCH=1, MULTI_TOUCH=2;

    float MENU_HEIGHT, MENU_WIDTH;

    ArrayList<TextureView> items = new ArrayList<TextureView>();

    public static final int HIDDEN=0, SHOWING=1;

    RubeCube cube;

    TextureFont mFont;

    TextureStateView toggler;
    TextureView menuView;
    public TextureTimer timer;
    TextureTextView timerText, cubeText, sliderText;
    TextureButton showTimer, resetTimer, scrambleCube, resetCube;
    TextureSlider slider;

    boolean showing=false, showingTimer=false;
    float xMin, xMax, yMin, yMax;
    float x1, y1, x2, y2;
    int activePtrId=-1, touchMode=NONE;

    boolean restoreStartTimer=false, restoreOnSetBounds = false;
    int restoreTime=0, restoreCubeDim=3;

    public CubeMenu(RubeCube rCube, TextureFont font) {
	cube = rCube;
	mFont = font;
	toggler = new TextureStateView();
	menuView = new TextureView();
	timer = new TextureTimer(mFont);
	timerText = new TextureTextView(mFont);
	cubeText = new TextureTextView(mFont);
	sliderText = new TextureTextView(mFont);
	showTimer = new TextureButton(mFont);
	resetTimer = new TextureButton(mFont);
	scrambleCube = new TextureButton(mFont);
        resetCube = new TextureButton(mFont);
	//int[] sliderVals = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	slider = new TextureSlider(new ArrayList(Arrays.asList(2, 3, 4, 5, 6, 7, 8)));
	generate();
	enableTextures();
    }

    public void init(GL11 gl, Context context) {
	toggler.addTexture(gl, context, R.drawable.menu_button1);
	toggler.addTexture(gl, context, R.drawable.menu_button2);
	toggler.setClickListener(new TextureClickListener() {
		public void onClick() {
		    if(!showing) {
			showing = true;
			menuView.animate(new TranslateAnimation(10, 0f, MENU_HEIGHT, 0f));
			toggler.animate(new TranslateAnimation(10, 0f, MENU_HEIGHT, 0f));
		    } else {
			showing = false;
			menuView.animate(new TranslateAnimation(10, 0f, -1f * MENU_HEIGHT, 0f));
			toggler.animate(new TranslateAnimation(10, 0f, -1f * MENU_HEIGHT, 0f));
		    }
		}
	    });

	menuView.setTexture(gl, context, R.drawable.menu_background);
	timer.setTexture(gl, context, R.drawable.transparent);

	timerText.setTexture(gl, context, R.drawable.transparent);
	cubeText.setTexture(gl, context, R.drawable.transparent);
	sliderText.setTexture(gl, context, R.drawable.transparent);

	showTimer.setTexture(gl, context, R.drawable.btn_transparent_normal);
	showTimer.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
	showTimer.setClickListener(new TextureClickListener() {
		public void onClick() {
		    showingTimer = !showingTimer;
		    timer.reset();
		    if(showingTimer) {
			timer.start();
			showTimer.setText("Off");
		    } else {
			timer.stop();
			showTimer.setText("On");
		    }
		}});

	resetTimer.setTexture(gl, context, R.drawable.btn_transparent_normal);
	resetTimer.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
	resetTimer.setClickListener(new TextureClickListener() {
		public void onClick() {
		    timer.reset();
		}});

	scrambleCube.setTexture(gl, context, R.drawable.btn_transparent_normal);
	scrambleCube.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
	scrambleCube.setClickListener(new TextureClickListener() {
		public void onClick() {
		    cube.scramble();
		}});

	resetCube.setTexture(gl, context, R.drawable.btn_transparent_normal);
	resetCube.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
	resetCube.setClickListener(new TextureClickListener() {
		public void onClick() {
		    cube.reset();
		}});

	slider.setTexture(gl, context, R.drawable.slider_bar, R.drawable.slider_button);
	slider.setOnValueChangedListener(new OnValueChangedListener() {
		public void onValueChanged(Object o) {
		    int newDim = (Integer)o;
		    sliderText.setText(newDim+"x"+newDim);
		    cube.setDimension(newDim);
		}
	    });
    }

    public void setBounds(float xMin, float xMax, float yMin, float yMax) {
	this.xMin = xMin;
	this.xMax = xMax;
	this.yMin = yMin;
	this.yMax = yMax;
	Log.e("Cube", xMin+" "+xMax+" "+yMin+" "+yMax);
	float z = 1f;
	float rat = (xMax - xMin) / (yMax - yMin);
	float fRat = (rat + 1f) / 2f;

	float w, h, xl, xr, yb, yt;
	float l = xMin + 0.05f;
	float r = l + (0.5f * 0.8f) + (0.4f * rat);
	float b = yMin + 0.05f;
	float t = b + (0.7f * 0.6f) + (0.4f * rat);
        GLColor c = new GLColor(1f, 1f, 0);
	GLColor black = new GLColor(0, 0, 0);
	GLColor white = new GLColor(1, 1, 1);

	float xPadding = (r - l) / 16f;
	float yPadding = (t - b) / 10f;

	toggler.setFace(l+xPadding, r+xPadding, b, t, z, white);
	toggler.setTextureBounds(1f, 0.42f);

	MENU_HEIGHT = (t - b)*1.5f;
	MENU_WIDTH = (r - l)*2.8f;
	menuView.setFace(l, l + MENU_WIDTH, yMin - (0.05f + MENU_HEIGHT), b - 0.02f, z, white);
	menuView.setTextureBounds(1f, 1f);

	Vec2 tSize;
	GLColor textColor = new GLColor(0.5f, 0.5f, 0.5f);	h = ((t-b)/2-yPadding);
	w = MENU_WIDTH*0.3f;
	xl = l+xPadding;
	xr = xl + w - xPadding;

	timerText.setFace(xl, xr, yMin - h, yMin, z+0.02f, white);
	timerText.setTextColor(textColor);
	timerText.setTextureBounds(1f, 1f);
	timerText.setTextSize(12f*fRat);
	tSize = mFont.measureText("Timer:", timerText.textSize);
	timerText.setPadding(w/2f - tSize.x/2f, 0, 0, h/2f - tSize.y/2f);

	w = MENU_WIDTH*0.3f;
	xl = xr + xPadding;
	xr = xl + w - xPadding;
	showTimer.setFace(xl, xr, yMin - h, yMin, z+0.02f, white);
	showTimer.setTextureBounds(1f, 1f);
	showTimer.setTextColor(textColor);
	showTimer.setTextSize(12f*fRat);
	tSize = mFont.measureText("Of", showTimer.textSize);
	showTimer.setPadding(w/2f - (tSize.x/2f + xPadding), 0, 0, h/2f - tSize.y/2f);

	w = MENU_WIDTH*0.35f;
	xl = xr + xPadding;
	xr = xl + w - xPadding;
	resetTimer.setFace(xl, xr, yMin - h, yMin, z+0.02f, white);
	resetTimer.setTextureBounds(1f, 1f);
	resetTimer.setTextColor(textColor);
	resetTimer.setTextSize(12f*fRat);
	tSize = mFont.measureText("Reset", resetTimer.textSize);
	resetTimer.setPadding(w/2f - (tSize.x/2f + xPadding), 0, 0, h/2f - tSize.y/2f);

	float xMid = (xMax + xMin) / 2f;
	timer.setTextSize(16f*fRat);
	tSize = mFont.measureText(timer.getTimeString(), timer.textSize);
	timer.setFace(xMid-tSize.x/2f, xMid+tSize.x/2f, yMax - (tSize.y+yPadding), yMax-yPadding, z, white);
	timer.setTextureBounds(1f, 1f);

	w = MENU_WIDTH*0.25f;
	yt = yMin - (h + yPadding);
	yb = yt - h;
	xl = l+xPadding;
	xr = xl + w - xPadding;
	cubeText.setFace(xl, xr, yb, yt, z+0.02f, white);
	cubeText.setTextColor(textColor);
	cubeText.setTextureBounds(1f, 1f);
	cubeText.setTextSize(11f*fRat);
	tSize = mFont.measureText("Cube:", cubeText.textSize);
	cubeText.setPadding(w/2f - tSize.x/2f, 0, 0, h/2f - tSize.y/2f);

	w = MENU_WIDTH*0.35f;
	xl = xr + xPadding;
	xr = xl + w - xPadding;
	scrambleCube.setFace(xl, xr, yb, yt, z+0.02f, white);
	scrambleCube.setTextureBounds(1f, 1f);
	scrambleCube.setTextColor(textColor);
	scrambleCube.setTextSize(10f*fRat);
	tSize = mFont.measureText("Scramble", scrambleCube.textSize);
	scrambleCube.setPadding(w/2f - (tSize.x/2f + xPadding), 0, 0, h/2f - tSize.y/2f);

	xl = xr + xPadding;
	xr = xl + w - xPadding;
	resetCube.setFace(xl, xr, yb, yt, z+0.02f, white);
	resetCube.setTextureBounds(1f, 1f);
	resetCube.setTextColor(textColor);
	resetCube.setTextSize(12f*fRat);
	tSize = mFont.measureText("Reset", resetCube.textSize);
	resetCube.setPadding(w/2f - (tSize.x/2f + xPadding), 0, 0, h/2f - tSize.y/2f);

	w = MENU_WIDTH*0.25f;
	yt = yb - yPadding;
	yb = yt - h;
	xl = l+xPadding;
	xr = xl + w - xPadding;
	sliderText.setFace(xl, xr, yb, yt, z+0.02f, white);
	sliderText.setTextureBounds(1f, 1f);
	sliderText.setTextColor(textColor);
	sliderText.setTextSize(12f*fRat);
	tSize = mFont.measureText("3x3", sliderText.textSize);
	sliderText.setPadding(w/2f - tSize.x/2f, 0, 0, h/2f - tSize.y/1.5f);

	w = MENU_WIDTH*0.35f;
	xl = xr + xPadding;
	xr = xl + 2f*(w - xPadding);
	slider.setFace(xl, xr, yb, yt-2f*yPadding, z+0.02f, white);
	slider.setTextureBounds(1f, 1f);
	//slider.setIndex(1);

	menuView.addChild(timerText);
	menuView.addChild(resetTimer);
	menuView.addChild(showTimer);
	menuView.addChild(cubeText);
	menuView.addChild(resetCube);
	menuView.addChild(scrambleCube);
	menuView.addChild(sliderText);
	menuView.addChild(slider);

	generate();
	showTimer.setText("On");
	timerText.setText("Timer:");
	resetTimer.setText("Reset");
	cubeText.setText("Cube:");
	scrambleCube.setText("Scramble");
	resetCube.setText("Reset");
	//sliderText.setText(cube.dim+"x"+cube.dim);
	//if(restoreOnSetBounds) restore();
	sliderText.setText(restoreCubeDim+"x"+restoreCubeDim);
	slider.setIndex(restoreCubeDim-2);
	timer.setTime(restoreTime);
	if(restoreStartTimer) {
	    showingTimer = true;
	    timer.start();
	    showTimer.setText("Off");
	}
	restoreTime = 0;
	restoreStartTimer = false;
	restoreOnSetBounds = false;
    }

    public Vec2 screenToWorld(float x, float y) {
	Vec2 p = new Vec2(x * adjustWidth, 1f - y*adjustHeight);
	p.x = p.x * (xMax - xMin) + xMin;
	p.y = p.y * (yMax - yMin) + yMin;
	return p;
    }

    public void draw(GL11 gl) {
	super.draw(gl);
	gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	gl.glPushMatrix();

        gl.glShadeModel(GL11.GL_FLAT);

	menuView.animate();
	menuView.draw(gl);

	toggler.animate();
	toggler.draw(gl);

	if(showingTimer) {
	    timer.draw(gl);
	}

	gl.glPopMatrix();
    }

    public void pause() {
	if(timer != null) {
	    timer.pause();
	}
    }

    public void resume() {
	if(timer != null) {
	    timer.resume();
	}
    }

    public boolean handleTouch(MotionEvent e) {
	// Eventually detect cube hit here
	Vec2 worldCoords;
	final int action = e.getAction();
	switch(action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	    touchMode = SINGLE_TOUCH;
	    activePtrId = e.getPointerId(0);
	    x1 = e.getX();
	    y1 = e.getY();
	    worldCoords = screenToWorld(x1, y1);
	    if(toggler.handleActionDown(worldCoords)) {
		return true;
	    }
	    return menuView.handleActionDown(worldCoords);
	case MotionEvent.ACTION_UP:
	    touchMode = NONE;
	    activePtrId = -1;
	    worldCoords = screenToWorld(x1, y1);
	    if(toggler.handleActionUp(worldCoords)) {
		return true;
	    }
	    return menuView.handleActionUp(worldCoords);
	case MotionEvent.ACTION_CANCEL:
	    activePtrId = -1;
	    touchMode = NONE;
	    break;
	case MotionEvent.ACTION_MOVE:
	    if(touchMode == SINGLE_TOUCH) {
		final int ptrInd = e.findPointerIndex(activePtrId);
		float x = e.getX(ptrInd);
		float y = e.getY(ptrInd);
		if(touchMode == SINGLE_TOUCH) {
		    worldCoords = screenToWorld(x, y);
		    if(toggler.handleActionMove(worldCoords)) return true;
		    return menuView.handleActionMove(worldCoords);
		}
	    }
	    break;
	case MotionEvent.ACTION_POINTER_DOWN:
	    touchMode = MULTI_TOUCH;
	    break;
	case MotionEvent.ACTION_POINTER_UP:
	    if(e.getPointerCount() == 1)
		touchMode = SINGLE_TOUCH;
	    break;
	}
	return false;
    }

    public void save(SharedPreferences prefs) {
	SharedPreferences.Editor edit = prefs.edit();
	edit.putInt("timer_time", timer.getTime());
	edit.putBoolean("timer_started", timer.started || timer.paused);
	edit.commit();
    }

    public void restore() {
	sliderText.setText(restoreCubeDim+"x"+restoreCubeDim);
	slider.setIndex(restoreCubeDim-2);
	timer.setTime(restoreTime);
	if(restoreStartTimer) {
	    showingTimer = true;
	    timer.start();
	    showTimer.setText("Off");
	}
	restoreTime = 0;
	restoreStartTimer = false;
	restoreOnSetBounds = false;
    }

    public void setRestore(SharedPreferences prefs) {
	restoreTime = prefs.getInt("timer_time", 0);
	restoreStartTimer = prefs.getBoolean("timer_started", false);
	restoreCubeDim = prefs.getInt("dim", 3);
	restoreOnSetBounds = true;
    }

}