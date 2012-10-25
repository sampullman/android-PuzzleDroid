package com.threeDBJ.puzzleDroid;

import android.view.MotionEvent;

public interface Clickable {

    public boolean handleActionDown(Vec2 p);
    public boolean handleActionUp(Vec2 p);
    public boolean handleActionMove(Vec2 p);

}