package com.threeDBJ.puzzleDroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

import java.util.Random;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class RubeCube {

    public static float MAX_SPIN_RATE = 0.08f;

    // current permutation of starting position
    int[] mPermutation;

    Handler handler = new Handler();
    GLSurfaceView mView;
    GLWorld world;
    CubeRenderer mRenderer;
    Cube[][][] cubes;
    CubeSide[] cubeSides = new CubeSide[6];
    int[][][] faceColors;
    CubeSide front, back, left, right, top, bottom, curSide;
    Layer[] lx, ly, lz;
    Layer curLayer;
    Vec3 coords, newCoords;
    Vec2 hitVec, dragVec, vel, dir = new Vec2();
    boolean spinEnabled=true;
    GLColor[] colors = new GLColor[6];

    // for random cube movements
    Random mRandom = new Random(System.currentTimeMillis());

    float x1=0, x2=0, y1=0, y2=0,
	dx=0, dy=0, zdist=0f,
	xtrans=0f, ytrans=0f, ztrans=-6f, cubeSize, space;

    public static int NONE=0,DRAG=1,ZOOM=2,SPIN=3;
    private final float TOUCH_SCALE_FACTOR = (float)Math.PI / 180;

    int mode=NONE,activePtrId=-1, dim;

    public RubeCube(GLWorld world) {
	this(world, 3);
    }

    public RubeCube(GLWorld world, int dim) {
	this.dim = dim;
	this.world = world;
        colors[Cube.kTop] = new GLColor(1f, 0, 0);
        colors[Cube.kFront] = new GLColor(0, 1f, 0);
	colors[Cube.kBack] = new GLColor(0, 0, 1f);
        colors[Cube.kLeft] = new GLColor(1f, 1f, 0);
	colors[Cube.kBottom] = new GLColor(1f, 0.5f, 0);
        colors[Cube.kRight] = new GLColor(1f, 1f, 1f);
	setup();
    }

    public void setDimension(int dim) {
	this.dim = dim;
	handler.removeCallbacks(setWorldDim);
	handler.post(setWorldDim);
    }

    public void setup() {
	faceColors = new int[6][dim][dim];
	lx = new Layer[dim];
	ly = new Layer[dim];
	lz = new Layer[dim];
	cubes = new Cube[dim][dim][dim];
	back = new CubeSide(world, dim, Cube.kBack, -1f, 1f, -1f, 1f, -1f, -1f);
	front = new CubeSide(world, dim, Cube.kFront, -1f, 1f, -1f, 1f, 1f, 1f);
	left = new CubeSide(world, dim, Cube.kLeft, -1f, -1f, -1f, 1f, -1f, 1f);
	right = new CubeSide(world, dim, Cube.kRight, 1f, 1f, -1f, 1f, -1f, 1f);
	bottom = new CubeSide(world, dim, Cube.kBottom, -1f, 1f, -1f, -1f, -1f, 1f);
	top = new CubeSide(world, dim, Cube.kTop, -1f, 1f, 1f, 1f, -1f, 1f);
	cubeSides[Cube.kFront] = front;
	cubeSides[Cube.kBack] = back;
	cubeSides[Cube.kLeft] = left;
	cubeSides[Cube.kRight] = right;
	cubeSides[Cube.kBottom] = bottom;
	cubeSides[Cube.kTop] = top;
    }

    public void setRenderer(CubeRenderer mRenderer) {
	this.mRenderer = mRenderer;
    }

    public void init() {
	addShapes(world);
	initSideColors();
	setupLayers();
	world.translate(0f, 0f, getZTrans());
    }

    public void addShapes(GLWorld world) {
		float curX, curY, curZ;
	curX = curY = curZ = -1f;
	// TODO -- scale with dim
	space = 1f / (dim * 15f);
	cubeSize = (2f - ((float)dim-1f)*space) / (float)dim;
	// Add cubes and layers
	int i, j, k, n=0;
	float xleft, ybot, zback, x, y, z;
	x = y = z = 0f;
	z = curZ;
	for(k = 0;k<dim;k+=1) {
	    y = curY;
	    for(j = 0;j<dim;j+=1) {
		x = curX;
		for(i = 0;i<dim;i+=1) {
		    xleft = x;
		    ybot = y;
		    zback = z;
		    Cube c = new Cube(world, xleft, ybot, zback,
				      xleft+cubeSize, ybot+cubeSize, zback+cubeSize);
			cubes[k][j][i] = c;
		    n += 1;
		    x += cubeSize + space;
		    world.addShape(c);
		}
		y += cubeSize + space;
	    }
	    z += cubeSize + space;
	}

	// Paint all sides black by default
        GLColor black = new GLColor(0, 0, 0, 1f);
	for(i = 0; i < dim; i+=1) {
	    for(j = 0; j < dim; j+=1) {
		for(k = 0; k < dim; k+=1) {
		    Cube cube = cubes[i][j][k];
		    for(int w=0; w<6; w+=1) {
			cube.setFaceColorAll(w, black);
		    }
		}
	    }
	}
	for(i=0; i<dim; i+=1) {
	    lz[i] = new Layer(this, new Vec3(0f, 0f, curZ), Layer.ZAxis, i);
	    lx[i] = new Layer(this, new Vec3(curX, 0f, 0f), Layer.XAxis, i);
	    ly[i] = new Layer(this, new Vec3(0f, curY, 0f), Layer.YAxis, i);
	}
    }

    public void initSideColors() {
	int i, j, k;
	for(i=0;i<faceColors.length;i+=1) {
	    for(j=0; j<dim; j+=1) {
		for(k=0; k<dim; k+=1) {
		    faceColors[i][j][k] = i;
		}
	    }
	}
    }

    public void setupSides() {
	int i, j, k;
	// Paint back blue
	i=0;
	for(j=0; j<dim; j+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kBack, colors[faceColors[Cube.kBack][j][k]]);
	    }
	}

	// Paint front green
	i=dim-1;
	for(j=0; j<dim; j+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kFront, colors[faceColors[Cube.kFront][j][k]]);
	    }
	}

	// Paint right white.
	k=dim-1;
	for(i=0; i<dim; i+=1) {
	    for(j=0; j<dim;j+=1) {
		cubes[i][j][k].setFaceColor(Cube.kRight, colors[faceColors[Cube.kRight][i][j]]);
	    }
	}

	// Paint bottom orange
	j=0;
	for(i=0; i<dim; i+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kBottom, colors[faceColors[Cube.kBottom][i][k]]);
	    }
	}

	// Paint top red
	j=dim-1;
	for(i=0; i<dim; i+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kTop, colors[faceColors[Cube.kTop][i][k]]);
	    }
	}
	// Paint left yellow
	k=0;
	for(i=0; i<dim; i+=1) {
	    for(j=0; j<dim;j+=1) {
		cubes[i][j][k].setFaceColor(Cube.kLeft, colors[faceColors[Cube.kLeft][i][j]]);
	    }
	}
    }

    public void setupTextures() {
	
    }

    public void setupLayers() {
	float curX, curY, curZ;
	curX = curY = curZ = -1f;
	// Record z layer
	curZ += (cubeSize) / 2f;
	int i, j, k;
	for(i=0;i<dim;i+=1) {
	    lz[i].clear();
	    curZ += cubeSize + space;
	    for(j=0;j<dim;j+=1) {
		for(k=0;k<dim;k+=1) {
		    lz[i].add(cubes[i][j][k]);
		}
	    }
	}
	top.setHLayers(lz);
	bottom.setHLayers(lz);
	left.setVLayers(lz);
	right.setVLayers(lz);
	// Record x layer
	curX += (cubeSize) / 2f;
	for(k=0;k<dim;k+=1) {
	    lx[k].clear();
	    curX += cubeSize + space;
	    for(j=0;j<dim;j+=1) {
		for(i=0;i<dim;i+=1) {
		    lx[k].add(cubes[i][j][k]);
		}
	    }
	}
	front.setVLayers(lx);
	back.setVLayers(lx);
	top.setVLayers(lx);
	bottom.setVLayers(lx);
	// Record y layer and register the cubes with the world
	curY += (cubeSize) / 2f;
	for(j=0;j<dim;j+=1) {
	    ly[j].clear();
	    curY += cubeSize + space;
	    for(i=0;i<dim;i+=1) {
		for(k=0;k<dim;k+=1) {
		    ly[j].add(cubes[i][j][k]);
		}
	    }
	}
	front.setHLayers(ly);
	back.setHLayers(ly);
	left.setHLayers(ly);
	right.setHLayers(ly);
    }

    public Vec3 getRatio(float x, float y) {
	Vec3 w = new Vec3();
	w.x = x * world.adjustWidth;
	w.y = 1f - y * world.adjustHeight;
	w.z = 2f;
	return w;
    }

    public float getZTrans() {
	return ztrans;
    }

    public void animate() {
	for(int i=0;i<dim;i+=1) {
	    lx[i].animate();
	    ly[i].animate();
	    lz[i].animate();
	}
    }

    public void reset() {
	handler.post(resetWorld);
    }

    public void scramble() {
	int index;
	CubeSide s;
	Layer l;
	float dir;
	Vec2 layerInd = new Vec2();
	for(int i=0;i<40;i+=1) {
	    index = mRandom.nextInt(dim);
	    dir = (float)mRandom.nextInt(2);
	    if(dir == 0f) dir = -1f;
	    s = cubeSides[mRandom.nextInt(cubeSides.length)];
	    if(mRandom.nextInt(2) == 0) {
		layerInd.x = index;
		l = s.getHLayer(layerInd);
	    } else {
		layerInd.y = index;
		l = s.getVLayer(layerInd);
	    }
	    l.setAngle(Layer.HALFPI * dir);
	    l.angle = 0;
	    transposeCubes((int)dir, l.axis, l.index);
	    setupLayers();
	}
    }

    public void transposeColorSidePos(int side) {
	int[][] newSide = new int[dim][dim];
	for(int i=0;i<dim;i+=1) {
	    for(int j=0;j<dim;j+=1) {
		newSide[i][j] = faceColors[side][j][dim-i-1];
	    }
	}
	faceColors[side] = newSide;
    }

    public void transposeColorSideNeg(int side) {
	int[][] newSide = new int[dim][dim];
	for(int i=0;i<dim;i+=1) {
	    for(int j=0;j<dim;j+=1) {
		newSide[i][j] = faceColors[side][dim-j-1][i];
	    }
	}
	faceColors[side] = newSide;
    }

    public void transposeColorsXPos(int index) {
	int temp1, temp2;
	for(int i=0;i<dim;i+=1) {
	    temp1 = faceColors[Cube.kFront][dim-i-1][index];
	    faceColors[Cube.kFront][dim-i-1][index] = faceColors[Cube.kBottom][dim-i-1][index];
	    temp2 = faceColors[Cube.kTop][i][index];
	    faceColors[Cube.kTop][i][index] = temp1;
	    temp1 = faceColors[Cube.kBack][i][index];
	    faceColors[Cube.kBack][i][index] = temp2;
	    faceColors[Cube.kBottom][dim-i-1][index] = temp1;
	}
	if(index == 0) {
	    transposeColorSidePos(Cube.kLeft);
	} else if(index == dim-1) {
	    transposeColorSidePos(Cube.kRight);
	}
    }

    public void transposeColorsXNeg(int index) {
	int temp1, temp2;
	for(int i=0;i<dim;i+=1) {
	    temp1 = faceColors[Cube.kFront][dim-i-1][index];
	    faceColors[Cube.kFront][dim-i-1][index] = faceColors[Cube.kTop][i][index];
	    temp2 = faceColors[Cube.kBottom][dim-i-1][index];
	    faceColors[Cube.kBottom][dim-i-1][index] = temp1;
	    temp1 = faceColors[Cube.kBack][i][index];
	    faceColors[Cube.kBack][i][index] = temp2;
	    faceColors[Cube.kTop][i][index] = temp1;
	}
	if(index == 0) {
	    transposeColorSideNeg(Cube.kLeft);
	} else if(index == dim-1) {
	    transposeColorSideNeg(Cube.kRight);
	}
    }

    public void transposeColorsYPos(int index) {
	int temp1, temp2;
	for(int i=0;i<dim;i+=1) {
	    temp1 = faceColors[Cube.kFront][index][i];
	    faceColors[Cube.kFront][index][i] = faceColors[Cube.kRight][dim-(i+1)][index];
	    temp2 = faceColors[Cube.kLeft][i][index];
	    faceColors[Cube.kLeft][i][index] = temp1;
	    temp1 = faceColors[Cube.kBack][index][dim-i-1];
	    faceColors[Cube.kBack][index][dim-i-1] = temp2;
	    faceColors[Cube.kRight][dim-(i+1)][index] = temp1;
	}
	if(index == 0) {
	    transposeColorSideNeg(Cube.kBottom);
	} else if(index == dim-1) {
	    transposeColorSideNeg(Cube.kTop);
	}
    }

    public void transposeColorsYNeg(int index) {
	int temp1, temp2;
	// Stopped here, figure out the side transposition
	for(int i=0;i<dim;i+=1) {
	    temp1 = faceColors[Cube.kFront][index][i];
	    faceColors[Cube.kFront][index][i] = faceColors[Cube.kLeft][i][index];
	    temp2 = faceColors[Cube.kRight][dim-(i+1)][index];
	    faceColors[Cube.kRight][dim-(i+1)][index] = temp1;
	    temp1 = faceColors[Cube.kBack][index][dim-i-1];
	    faceColors[Cube.kBack][index][dim-i-1] = temp2;
	    faceColors[Cube.kLeft][i][index] = temp1;
	}
	if(index == 0) {
	    transposeColorSidePos(Cube.kBottom);
	} else if(index == dim-1) {
	    transposeColorSidePos(Cube.kTop);
	}
    }

    public void transposeColorsZNeg(int index) {
	int temp1, temp2;
	for(int i=0;i<dim;i+=1) {
	    temp1 = faceColors[Cube.kTop][index][i];
	    faceColors[Cube.kTop][index][i] = faceColors[Cube.kRight][index][dim-i-1];
	    temp2 = faceColors[Cube.kLeft][index][i];
	    faceColors[Cube.kLeft][index][i] = temp1;
	    temp1 = faceColors[Cube.kBottom][index][dim-i-1];
	    faceColors[Cube.kBottom][index][dim-i-1] = temp2;
	    faceColors[Cube.kRight][index][dim-i-1] = temp1;
	}
	if(index == 0) {
	    transposeColorSideNeg(Cube.kBack);
	} else if(index == dim-1) {
	    transposeColorSideNeg(Cube.kFront);
	}
    }

    public void transposeColorsZPos(int index) {
	int temp1, temp2;
	for(int i=0;i<dim;i+=1) {
	    temp1 = faceColors[Cube.kTop][index][i];
	    faceColors[Cube.kTop][index][i] = faceColors[Cube.kLeft][index][i];
	    temp2 = faceColors[Cube.kRight][index][dim-i-1];
	    faceColors[Cube.kRight][index][dim-i-1] = temp1;
	    temp1 = faceColors[Cube.kBottom][index][dim-i-1];
	    faceColors[Cube.kBottom][index][dim-i-1] = temp2;
	    faceColors[Cube.kLeft][index][i] = temp1;
	}
	if(index == 0) {
	    transposeColorSidePos(Cube.kBack);
	} else if(index == dim-1) {
	    transposeColorSidePos(Cube.kFront);
	}
    }

    public void transposeCubes(int nTurns, int axis, int index) {
	Cube[][] t = new Cube[dim][dim];
	int a1, a2, m1, m2, n;
	if(nTurns > 0) {
	    n = -1;
	} else {
	    n = 1;
	}
	while(nTurns != 0) {
	    switch(axis) {
	    case Layer.XAxis:
		if(nTurns < 0) {
		    transposeColorsXNeg(index);
		} else {
		    transposeColorsXPos(index);
		}
		break;
	    case Layer.YAxis:
		if(nTurns < 0) {
		    transposeColorsYNeg(index);
		} else {
		    transposeColorsYPos(index);
		}
		break;
	    case Layer.ZAxis:
		if(nTurns < 0) {
		    transposeColorsZNeg(index);
		} else {
		    transposeColorsZPos(index);
		}
		break;
	    }
	    for(int i=0; i<dim; i+=1) {
		for(int j=0; j<dim; j+=1) {
		    switch(axis) {
		    case Layer.XAxis:
			if(nTurns < 0) {
			    t[i][j] = cubes[dim-j-1][i][index];
			} else {
			    t[i][j] = cubes[j][dim-i-1][index];
			}
			break;
		    case Layer.YAxis:
			if(nTurns < 0) {
			    t[i][j] = cubes[j][index][dim-i-1];
			} else {
			    t[i][j] = cubes[dim-j-1][index][i];
			}
			break;
		    case Layer.ZAxis:
			if(nTurns < 0) {
			    t[i][j] = cubes[index][dim-j-1][i];
			} else {
			    t[i][j] = cubes[index][j][dim-i-1];
			}
			break;
		    }
		}
	    }
	    for(int i=0; i<dim; i+=1) {
		for(int j=0; j<dim; j+=1) {
		    switch(axis) {
		    case Layer.XAxis:
			cubes[i][j][index] = t[i][j];
			break;
		    case Layer.YAxis:
			cubes[i][index][j] = t[i][j];
			break;
		    case Layer.ZAxis:
			cubes[index][i][j] = t[i][j];
		    }
		}
	    }
	    nTurns += n;
	}
    }

    /* Update layers and sides after a rotation */
    public void endLayerAnimation(int axis, float angle, int index) {
	int nTurns = (int)(angle  / (Layer.HALFPI - 0.01f));
	transposeCubes(nTurns, axis, index);

	
	// world.pauseCube(true);
	// world.clear();
	// addShapes(world);
	// setupSides();
	// world.generate();
	// world.pauseCube(false);
	

	setupLayers();
	//setupSides();
	curSide = null;
	curLayer = null;
	dir = new Vec2();
	spinEnabled(true);
    }

    public void spinEnabled(boolean spin) {
	this.spinEnabled = spin;
    }

    public void handleTouch(MotionEvent e) {
	// Eventually detect cube hit here
	final int action = e.getAction();
	switch(action & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN: {
	    x1 = e.getX();
	    y1 = e.getY();
	    mode=DRAG;
	    activePtrId = e.getPointerId(0);
	    world.dragStart(x1, y1);
	    coords = mRenderer.screenToWorld(getRatio(x1, y1));
	    if(!spinEnabled) break;
	    for(int i=0;i<cubeSides.length;i+=1) {
		hitVec = cubeSides[i].getHitLoc(coords, new Vec3(0f, 0f, 1f));
		if(hitVec != null) {
		    dragVec = hitVec;
		    mode = SPIN;
		    curSide = cubeSides[i];
		    break;
		}
	    }
	    break;
	}
	case MotionEvent.ACTION_MOVE: {
	    if(activePtrId < 0 || activePtrId >= e.getPointerCount()) break;
	    final int ptrInd = e.findPointerIndex(activePtrId);
	    x2 = e.getX(ptrInd);
	    y2 = e.getY(ptrInd);
	    dx = x2-x1;
	    dy = y2-y1;
	    if(mode == DRAG) {

		world.drag(x2, y2);

		x1 = x2;
		y1 = y2;
	    } else if(mode == ZOOM) {
		/*
		float x1 = e.getX(0);
		float x2 = e.getX(1);
		float y1 = e.getY(0);
		float y2 = e.getY(1);
		float dist = (float)Math.sqrt(x1*x2 + y1*y2);
		float distdiff = (float)Math.abs(zdist - dist);
		if(dist > zdist && zdist > 3f) {
		    world.scale(0.99f);
		} else if(zdist > 5f) {
		    world.scale(1.01f);
		}
		zdist = dist;
		*/
	    } else if(mode == SPIN && curSide != null) {
		newCoords = mRenderer.screenToWorld(getRatio(x2, y2));
		Vec2 hp = curSide.getPlaneHitLoc(newCoords, new Vec3(0f, 0f, 1f));
		vel = new Vec2(hp).sub(dragVec);
		if(curLayer == null) {
		    dir.add(vel);
		    float xAbs = Math.abs(dir.x);
		    float yAbs = Math.abs(dir.y);
		    if(xAbs > yAbs && xAbs > 0.02f) {
			curLayer = curSide.getHLayer(hitVec);
			curLayer.setType(Layer.H);
		    } else if(yAbs > 0.02f) {
			curLayer = curSide.getVLayer(hitVec);
			curLayer.setType(Layer.V);
		    }
		}
		dragVec = hp;
		// Normalize rotation velocity
		vel.x *= 1.3f;
		vel.y *= 1.3f;
		if(vel.x < -1f * MAX_SPIN_RATE) vel.x = -1f * MAX_SPIN_RATE;
		else if(vel.x > MAX_SPIN_RATE) vel.x = MAX_SPIN_RATE;
		if(vel.y < -1f * MAX_SPIN_RATE) vel.y = -1f * MAX_SPIN_RATE;
		else if(vel.y > MAX_SPIN_RATE) vel.y = MAX_SPIN_RATE;
		//vel.x *= world.ratio;
		//vel.y *= 1f / world.ratio;
		if(curLayer != null) {
		    curLayer.drag(vel, curSide.frontFace);
		}
		coords = newCoords;
	    }
	    break;
	}
	case MotionEvent.ACTION_POINTER_DOWN: {
	    if(curLayer != null && mode == SPIN)
		curLayer.dragEnd();
	    float x1 = e.getX(0);
	    float x2 = e.getX(1);
	    float y1 = e.getY(0);
	    float y2 = e.getY(1);
	    float xdist1 = Math.abs(x1 - x2);
	    float ydist1 = Math.abs(y1 - y2);
	    if(xdist1 > 5f && ydist1 > 5f) {
		zdist = (float)Math.sqrt(x1*x2 + y1*y2);
		mode=ZOOM;
	    }
	    break;
	}
	case MotionEvent.ACTION_UP: {
	    activePtrId = -1;
	    mode = NONE;
	    if(curLayer != null)
		curLayer.dragEnd();
	    break;
	}

	case MotionEvent.ACTION_CANCEL: {
	    activePtrId = -1;
	    mode = NONE;
	    if(curLayer != null)
		curLayer.dragEnd();
	    break;
	}
	case MotionEvent.ACTION_POINTER_UP: {
	    // Back to translate
	    final int ptrInd = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
		>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	    final int ptrId = e.getPointerId(ptrInd);
	    final int nPtrInd = ptrInd == 0 ? 1 : 0;
	    x1 = e.getX(nPtrInd);
	    y1 = e.getY(nPtrInd);
	    mode=DRAG;
	    world.dragStart(x1, y1);
	    activePtrId = e.getPointerId(nPtrInd);
	    break;
	}
	}
    }

    final Runnable resetWorld = new Runnable() {
	    public void run() {
		GLWorld temp = new GLWorld();
		addShapes(temp);
		initSideColors();
		setupLayers();
		setupSides();
		temp.generate();
		world.mShapeList = temp.mShapeList;
		world.mVertexList = temp.mVertexList;
		world.mVertexBuffer = temp.mVertexBuffer;
		world.mIndexBuffer = temp.mIndexBuffer;
		world.mColorBuffer = temp.mColorBuffer;
		//world.mTextureBuffer = temp.mTextureBuffer;
	    }
	};

    final Runnable setWorldDim = new Runnable() {
	    public void run() {
		world.pauseCube(true);
		world.clear();
		setup();
		init();
		setupSides();
		world.generate();
		world.pauseCube(false);
	    }
	};

    public int getColorInd(GLColor c, int def) {
	for(int i=0;i<colors.length;i+=1) {
	    if(c.equals(colors[i])) return i;
	}
	return def;
    }

    public void save(SharedPreferences prefs) {
	SharedPreferences.Editor edit = prefs.edit();
	int i, j, k;
	GLColor c;
	int n = 0;
	for(i=0;i<faceColors.length;i+=1) {
	    for(j=0; j<dim; j+=1) {
		for(k=0; k<dim; k+=1) {
		    edit.putInt(i+""+j+""+k, faceColors[i][j][k]);
		}
	    }
	}
	edit.putInt("dim", dim);
	edit.putBoolean("saved", true);
	edit.commit();
    }

    public void restore(SharedPreferences prefs) {
	int i, j, k;
	GLColor c;
	int n = 0;
	for(i=0;i<faceColors.length;i+=1) {
	    for(j=0; j<dim; j+=1) {
		for(k=0; k<dim; k+=1) {
		    faceColors[i][j][k] = prefs.getInt(i+""+j+""+k, i);
		}
	    }
	}
	setupSides();
    }

}