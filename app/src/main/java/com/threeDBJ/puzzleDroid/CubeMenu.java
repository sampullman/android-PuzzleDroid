package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.threeDBJ.MGraphicsLib.GLColor;
import com.threeDBJ.MGraphicsLib.GLEnvironment;
import com.threeDBJ.MGraphicsLib.math.Vec2;
import com.threeDBJ.MGraphicsLib.texture.TextureButton;
import com.threeDBJ.MGraphicsLib.texture.TextureView.TextureClickListener;
import com.threeDBJ.MGraphicsLib.texture.TextureFont;
import com.threeDBJ.MGraphicsLib.texture.TextureSlider;
import com.threeDBJ.MGraphicsLib.texture.TextureSlider.OnValueChangedListener;
import com.threeDBJ.MGraphicsLib.texture.TextureStateView;
import com.threeDBJ.MGraphicsLib.texture.TextureTextView;
import com.threeDBJ.MGraphicsLib.texture.TextureTimer;
import com.threeDBJ.MGraphicsLib.texture.TextureView;
import com.threeDBJ.MGraphicsLib.texture.TranslateAnimation;
import com.threeDBJ.puzzleDroid.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL11;

import timber.log.Timber;

public class CubeMenu extends GLEnvironment {

    private static final int NONE = 0, SINGLE_TOUCH = 1, MULTI_TOUCH = 2;

    private float MENU_HEIGHT, MENU_WIDTH;

    private RubeCube cube;

    private TextureFont font;

    private TextureStateView toggler;
    private TextureView menuView;
    public TextureTimer timer;
    private TextureTextView timerText, cubeText, sliderText;
    private TextureButton showTimer, resetTimer, scrambleCube, resetCube;
    private TextureSlider slider;

    private boolean showing = false, showingTimer = false;
    private float xMin, xMax, yMin, yMax;
    private float x1, y1;
    private int activePtrId = -1, touchMode = NONE;

    private boolean restoreStartTimer = false, restoreOnSetBounds = false;
    private int restoreTime = 0, restoreCubeDim = 3;

    public CubeMenu(RubeCube cube, TextureFont font) {
        this.cube = cube;
        this.font = font;
        toggler = new TextureStateView();
        menuView = new TextureView();
        timer = new TextureTimer(this.font);
        timerText = new TextureTextView(this.font);
        cubeText = new TextureTextView(this.font);
        sliderText = new TextureTextView(this.font);
        showTimer = new TextureButton(this.font);
        resetTimer = new TextureButton(this.font);
        scrambleCube = new TextureButton(this.font);
        resetCube = new TextureButton(this.font);
        //int[] sliderVals = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        slider = new TextureSlider(new ArrayList<>(Arrays.asList(2, 3, 4, 5, 6, 7, 8)));
        generate();
        enableTextures();
    }

    public void init(GL11 gl, Context context) {
        toggler.addTexture(gl, context, R.drawable.menu_button1);
        toggler.addTexture(gl, context, R.drawable.menu_button2);
        toggler.setClickListener(new TextureClickListener() {
            public void onClick() {
                if (!showing) {
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
                if (showingTimer) {
                    timer.start();
                    showTimer.setText("Off");
                } else {
                    timer.stop();
                    showTimer.setText("On");
                }
            }
        });

        resetTimer.setTexture(gl, context, R.drawable.btn_transparent_normal);
        resetTimer.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
        resetTimer.setClickListener(new TextureClickListener() {
            public void onClick() {
                timer.reset();
            }
        });

        scrambleCube.setTexture(gl, context, R.drawable.btn_transparent_normal);
        scrambleCube.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
        scrambleCube.setClickListener(new TextureClickListener() {
            public void onClick() {
                cube.scramble();
            }
        });

        resetCube.setTexture(gl, context, R.drawable.btn_transparent_normal);
        resetCube.setPressedTexture(gl, context, R.drawable.btn_transparent_pressed);
        resetCube.setClickListener(new TextureClickListener() {
            public void onClick() {
                cube.reset();
            }
        });

        slider.setTexture(gl, context, R.drawable.slider_bar, R.drawable.slider_button);
        slider.setOnValueChangedListener(new OnValueChangedListener() {
            public void onValueChanged(Object o) {
                int newDim = (Integer) o;
                sliderText.setText(newDim + "x" + newDim);
                cube.setDimension(newDim);
            }
        });
    }

    public void setBounds(float xMin, float xMax, float yMin, float yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        Timber.d("Cube %f %f %f %f", xMin, xMax, yMin, yMax);
        float z = 1f;
        float rat = (xMax - xMin) / (yMax - yMin);
        float fRat = (rat + 1f) / 2f;

        float w, h, xl, xr, yb, yt;
        float l = xMin + 0.05f;
        float r = l + (0.5f * 0.8f) + (0.4f * rat);
        float b = yMin + 0.05f;
        float t = b + (0.7f * 0.6f) + (0.4f * rat);
        GLColor white = new GLColor(1, 1, 1);

        float xPadding = (r - l) / 16f;
        float yPadding = (t - b) / 10f;

        toggler.setFace(l + xPadding, r + xPadding, b, t, z, white);
        toggler.setTextureBounds(1f, 0.42f);

        MENU_HEIGHT = (t - b) * 1.5f;
        MENU_WIDTH = (r - l) * 2.8f;
        menuView.setFace(l, l + MENU_WIDTH, yMin - (0.05f + MENU_HEIGHT), b - 0.02f, z, white);
        menuView.setTextureBounds(1f, 1f);

        Vec2 tSize;
        GLColor textColor = new GLColor(0.5f, 0.5f, 0.5f);
        h = ((t - b) / 2 - yPadding);
        w = MENU_WIDTH * 0.3f;
        xl = l + xPadding;
        xr = xl + w - xPadding;

        timerText.setFace(xl, xr, yMin - h, yMin, z + 0.02f, white);
        timerText.setTextColor(textColor);
        timerText.setTextureBounds(1f, 1f);
        timerText.setTextSize(12f * fRat);
        tSize = font.measureText("Timer:", timerText.textSize);
        timerText.setPadding(w / 2f - tSize.x / 2f, 0, 0, h / 2f - tSize.y / 2f);

        w = MENU_WIDTH * 0.3f;
        xl = xr + xPadding;
        xr = xl + w - xPadding;
        showTimer.setFace(xl, xr, yMin - h, yMin, z + 0.02f, white);
        showTimer.setTextureBounds(1f, 1f);
        showTimer.setTextColor(textColor);
        showTimer.setTextSize(12f * fRat);
        tSize = font.measureText("Of", showTimer.textSize);
        showTimer.setPadding(w / 2f - (tSize.x / 2f + xPadding), 0, 0, h / 2f - tSize.y / 2f);

        w = MENU_WIDTH * 0.35f;
        xl = xr + xPadding;
        xr = xl + w - xPadding;
        resetTimer.setFace(xl, xr, yMin - h, yMin, z + 0.02f, white);
        resetTimer.setTextureBounds(1f, 1f);
        resetTimer.setTextColor(textColor);
        resetTimer.setTextSize(12f * fRat);
        tSize = font.measureText("Reset", resetTimer.textSize);
        resetTimer.setPadding(w / 2f - (tSize.x / 2f + xPadding), 0, 0, h / 2f - tSize.y / 2f);

        float xMid = (xMax + xMin) / 2f;
        timer.setTextSize(16f * fRat);
        tSize = font.measureText(timer.getTimeString(), timer.textSize);
        timer.setFace(xMid - tSize.x / 2f, xMid + tSize.x / 2f, yMax - (tSize.y + yPadding), yMax - yPadding, z, white);
        timer.setTextureBounds(1f, 1f);

        w = MENU_WIDTH * 0.25f;
        yt = yMin - (h + yPadding);
        yb = yt - h;
        xl = l + xPadding;
        xr = xl + w - xPadding;
        cubeText.setFace(xl, xr, yb, yt, z + 0.02f, white);
        cubeText.setTextColor(textColor);
        cubeText.setTextureBounds(1f, 1f);
        cubeText.setTextSize(11f * fRat);
        tSize = font.measureText("Cube:", cubeText.textSize);
        cubeText.setPadding(w / 2f - tSize.x / 2f, 0, 0, h / 2f - tSize.y / 2f);

        w = MENU_WIDTH * 0.35f;
        xl = xr + xPadding;
        xr = xl + w - xPadding;
        scrambleCube.setFace(xl, xr, yb, yt, z + 0.02f, white);
        scrambleCube.setTextureBounds(1f, 1f);
        scrambleCube.setTextColor(textColor);
        scrambleCube.setTextSize(10f * fRat);
        tSize = font.measureText("Scramble", scrambleCube.textSize);
        scrambleCube.setPadding(w / 2f - (tSize.x / 2f + xPadding), 0, 0, h / 2f - tSize.y / 2f);

        xl = xr + xPadding;
        xr = xl + w - xPadding;
        resetCube.setFace(xl, xr, yb, yt, z + 0.02f, white);
        resetCube.setTextureBounds(1f, 1f);
        resetCube.setTextColor(textColor);
        resetCube.setTextSize(12f * fRat);
        tSize = font.measureText("Reset", resetCube.textSize);
        resetCube.setPadding(w / 2f - (tSize.x / 2f + xPadding), 0, 0, h / 2f - tSize.y / 2f);

        w = MENU_WIDTH * 0.25f;
        yt = yb - yPadding;
        yb = yt - h;
        xl = l + xPadding;
        xr = xl + w - xPadding;
        sliderText.setFace(xl, xr, yb, yt, z + 0.02f, white);
        sliderText.setTextureBounds(1f, 1f);
        sliderText.setTextColor(textColor);
        sliderText.setTextSize(12f * fRat);
        tSize = font.measureText("3x3", sliderText.textSize);
        sliderText.setPadding(w / 2f - tSize.x / 2f, 0, 0, h / 2f - tSize.y / 1.5f);

        w = MENU_WIDTH * 0.35f;
        xl = xr + xPadding;
        xr = xl + 2f * (w - xPadding);
        slider.setFace(xl, xr, yb, yt - 2f * yPadding, z + 0.02f, white);
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
        sliderText.setText(restoreCubeDim + "x" + restoreCubeDim);
        slider.setIndex(restoreCubeDim - 2);
        timer.setTime(restoreTime);
        if (restoreStartTimer) {
            showingTimer = true;
            timer.start();
            showTimer.setText("Off");
        }
        restoreTime = 0;
        restoreStartTimer = false;
        restoreOnSetBounds = false;
    }

    public Vec2 screenToWorld(float x, float y) {
        Vec2 p = new Vec2(x * adjustWidth, 1f - y * adjustHeight);
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

        if (showingTimer) {
            timer.draw(gl);
        }

        gl.glPopMatrix();
    }

    public void pause() {
        timer.pause(true);
    }

    public void resume() {
        timer.pause(false);
    }

    private void resetTouch() {
        touchMode = NONE;
        activePtrId = -1;
    }

    public boolean handleTouch(MotionEvent e) {
        // Eventually detect cube hit here
        Vec2 worldCoords;
        final int action = e.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchMode = SINGLE_TOUCH;
                activePtrId = e.getPointerId(0);
                x1 = e.getX();
                y1 = e.getY();
                worldCoords = screenToWorld(x1, y1);
                if (toggler.handleActionDown(worldCoords)) {
                    return true;
                }
                return menuView.handleActionDown(worldCoords);
            case MotionEvent.ACTION_UP:
                resetTouch();
                worldCoords = screenToWorld(x1, y1);
                if (toggler.handleActionUp(worldCoords)) {
                    return true;
                }
                return menuView.handleActionUp(worldCoords);
            case MotionEvent.ACTION_CANCEL:
                resetTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchMode == SINGLE_TOUCH) {
                    final int ptrInd = e.findPointerIndex(activePtrId);
                    float x = e.getX(ptrInd);
                    float y = e.getY(ptrInd);
                    if (touchMode == SINGLE_TOUCH) {
                        worldCoords = screenToWorld(x, y);
                        return toggler.handleActionMove(worldCoords) || menuView.handleActionMove(worldCoords);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                touchMode = MULTI_TOUCH;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (e.getPointerCount() == 1) {
                    touchMode = SINGLE_TOUCH;
                }
                break;
        }
        return false;
    }

    public void save(SharedPreferences prefs) {
        Util.saveTimerTime(prefs, timer.getTime(), timer.isStarted() || timer.isPaused());
    }

    public void restore() {
        sliderText.setText(restoreCubeDim + "x" + restoreCubeDim);
        slider.setIndex(restoreCubeDim - 2);
        timer.setTime(restoreTime);
        if (restoreStartTimer) {
            showingTimer = true;
            timer.start();
            showTimer.setText("Off");
        }
        restoreTime = 0;
        restoreStartTimer = false;
        restoreOnSetBounds = false;
    }

    public void setRestore(SharedPreferences prefs) {
        restoreTime = Util.getTimerTime(prefs);
        restoreStartTimer = Util.getTimerStarted(prefs);
        restoreCubeDim = Util.getDimension(prefs);
        restoreOnSetBounds = true;
    }

}