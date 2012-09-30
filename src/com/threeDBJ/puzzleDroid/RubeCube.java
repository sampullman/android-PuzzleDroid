package com.threeDBJ.puzzleDroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

import java.util.Random;

import android.util.Log;
import android.view.MotionEvent;

public class RubeCube {

    // current permutation of starting position
    int[] mPermutation;

    GLSurfaceView mView;
    GLWorld world;
    CubeRenderer mRenderer;
    Cube[][][] cubes;
    CubeSide[] cubeSides = new CubeSide[6];
    CubeSide front, back, left, right, top, bottom, curSide;
    Layer[] lx, ly, lz;
    Layer curLayer;
    CubeRegistry cubeRegistry = new CubeRegistry();
    Vec3 coords, newCoords;
    Vec2 hitVec, dragVec, vel, dir = new Vec2();

    // for random cube movements
    Random mRandom = new Random(System.currentTimeMillis());

    float x1 = 0,x2 = 0,y1 = 0,y2 = 0,
	dx = 0,dy = 0,xdist1 = 0,ydist1 = 0,xdist2 = 0,ydist2 = 0,
	xtrans = 0f,ytrans = 0f,ztrans = 0f;

    public static int NONE=0,DRAG=1,ZOOM=2,SPIN=3;
    private final float TOUCH_SCALE_FACTOR = (float)Math.PI / 180;

    int mode=NONE,activePtrId=-1, dim;

    public RubeCube(GLWorld world) {
	this(world, 3);
    }

    public RubeCube(GLWorld world, int dim) {
	this.dim = dim;
	this.world = world;
	lx = new Layer[dim];
	ly = new Layer[dim];
	lz = new Layer[dim];
    }

    public void setRenderer(CubeRenderer mRenderer) {
	this.mRenderer = mRenderer;
    }

    public void addShapes() {
	int one = 0x10000;
        int half = 0x08000;
        GLColor red = new GLColor(one, 0, 0);
        GLColor green = new GLColor(0, one, 0);
        GLColor blue = new GLColor(0, 0, one);
        GLColor yellow = new GLColor(one, one, 0);
        GLColor orange = new GLColor(one, half, 0);
        GLColor white = new GLColor(one, one, one);
        GLColor black = new GLColor(0, 0, 0);

	int num_cubes = dim*dim*dim, dim2 = dim*dim;
	cubes = new Cube[dim][dim][dim];
	Layer[] layers = new Layer[dim2];

	float curX, curY, curZ;
	curX = curY = curZ = -1f;
	// TODO -- scale with dim
	float space = 1f/20f;
	float cubeSize = (2f - ((float)dim-1f)*space) / (float)dim;
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
		    cubes[k][j][i] = new Cube(world, xleft, ybot, zback,
					xleft+cubeSize, ybot+cubeSize, zback+cubeSize);
		    cubeRegistry.register(cubes[k][j][i]);
		    n += 1;
		    x += cubeSize + space;
		}
		y += cubeSize + space;
	    }
	    z += cubeSize + space;
	}

	// Paint all sides black by default
	for(i = 0; i < dim; i+=1) {
	    for(j = 0; j < dim; j+=1) {
		for(k = 0; k < dim; k+=1) {
		    Cube cube = cubes[i][j][k];
		    for(int w=0; w<6; w+=1) {
			cube.setFaceColor(w, black);
		    }
		}
	    }
	}

	// Initialize side objects
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

	// Paint back blue
	i=0;
	for(j=0; j<dim; j+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kBack, blue);
	    }
	}

	// Paint front green
	i=dim-1;
	for(j=0; j<dim; j+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kFront, green);
	    }
	}

	// Paint left yellow
	k=0;
	for(i=0; i<dim; i+=1) {
	    for(j=0; j<dim;j+=1) {
		cubes[i][j][k].setFaceColor(Cube.kLeft, yellow);
	    }
	}

	// Paint right white.
	k=dim-1;
	for(i=0; i<dim; i+=1) {
	    for(j=0; j<dim;j+=1) {
		cubes[i][j][k].setFaceColor(Cube.kRight, white);
	    }
	}

	// Paint bottom orange
	j=0;
	for(i=0; i<dim; i+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kBottom, orange);
	    }
	}

	// Paint top red
	j=dim-1;
	for(i=0; i<dim; i+=1) {
	    for(k=0; k<dim; k+=1) {
		cubes[i][j][k].setFaceColor(Cube.kTop, red);
	    }
	}

	// Record z layer
	curZ += (cubeSize) / 2f;
	for(i=0;i<dim;i+=1) {
	    lz[i] = new Layer(this, new Vec3(0f, 0f, curZ), Layer.ZAxis);
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
	    lx[k] = new Layer(this, new Vec3(curX, 0f, 0f), Layer.XAxis);
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
	    ly[j] = new Layer(this, new Vec3(0f, curY, 0f), Layer.YAxis);
	    curY += cubeSize + space;
	    for(i=0;i<dim;i+=1) {
		for(k=0;k<dim;k+=1) {
		    ly[j].add(cubes[i][j][k]);
		    world.addShape(cubes[i][j][k]);
		}
	    }
	}
	front.setHLayers(ly);
	back.setHLayers(ly);
	left.setHLayers(ly);
	right.setHLayers(ly);

	world.translate(0f, 0f, getZTrans());
	world.generate();
    }

    public Vec3 getRatio(float x, float y) {
	Vec3 w = new Vec3();
	w.x = x * world.adjustWidth;
	w.y = 1f - y * world.adjustHeight;
	w.z = 2f;
	return w;
    }

    public float getZTrans() {
	return -6f;
    }

    public void animate() {
	for(int i=0;i<dim;i+=1) {
	    lx[i].animate();
	    ly[i].animate();
	    lz[i].animate();
	}
    }

    /* Update layers and sides */
    public void endLayerAnimation(int type, float angle) {
	int nTurns = (int)(angle  / (Layer.HALFPI - 0.01f));
	Log.e("nTurns", nTurns+"");
	if(type == Layer.H) {
	    switch(curSide.frontFace) {
	    case Cube.kFront:
		
		break;
	    case Cube.kBack:

		break;
	    case Cube.kLeft:

		break;
	    case Cube.kRight:

		break;
	    case Cube.kTop:

		break;
	    case Cube.kBottom:

		break;
	    }
	} else {

	}
	curSide = null;
	curLayer = null;
	dir = new Vec2();
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
	case MotionEvent.ACTION_POINTER_DOWN: {
	    xdist1 = Math.abs(e.getX(0)-e.getX(1));
	    ydist1 = Math.abs(e.getY(0)-e.getY(1));
	    if(xdist1 > 5f && ydist1 > 5f) mode=ZOOM;
	    break;
	}
	case MotionEvent.ACTION_MOVE: {
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
		// Zoom
	    } else if(mode == SPIN) {
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
		/* Possible if the user touched the very edge of a cube.
		   Should be resolved in the future. (TODO) */
		if(curLayer != null) {
		    curLayer.drag(vel, curSide.frontFace);
		}
		coords = newCoords;
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
	    activePtrId = e.getPointerId(nPtrInd);
	    break;
	}
	}
    }

}