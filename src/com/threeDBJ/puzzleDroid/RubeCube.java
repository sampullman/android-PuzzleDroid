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
    Cube[] cubes;
    CubeSide[] cubeSides = new CubeSide[6];
    CubeSide front, back, left, right, top, bottom, curSide;
    Layer[] lx, ly, lz;
    Layer curLayer;
    CubeRegistry cubeRegistry = new CubeRegistry();
    Vec3 coords, newCoords;
    Vec2 hitVec;

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
	cubes = new Cube[num_cubes];
	Layer[] layers = new Layer[num_cubes];

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
		    cubes[n] = new Cube(world, xleft, ybot, zback,
					xleft+cubeSize, ybot+cubeSize, zback+cubeSize);
		    cubeRegistry.register(cubes[n]);
		    n += 1;
		    x += cubeSize + space;
		}
		y += cubeSize + space;
	    }
	    z += cubeSize + space;
	}
	Log.e("Cube-init", x + " " + y + " " + z);

	// Paint all sides black by default
	for(i = 0; i < num_cubes; i+=1) {
	    Cube cube = cubes[i];
	    for(j = 0; j < 6; j += 1) {
		cube.setFaceColor(j, black);
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
	for(i = dim2-1; i >= 0; i -= 1) {
	    cubes[i].setFaceColor(Cube.kBack, blue);
	    back.addCube(cubes[i]);
	}

	// Paint front green
	for(i = num_cubes - (int)dim; i >= num_cubes - dim2; i -= dim) {
	    for(j=0;j<dim;j+=1) {
		cubes[i+j].setFaceColor(Cube.kFront, green);
		front.addCube(cubes[i+j]);
	    }
	}

	// Paint left yellow
	for(i = dim2 - dim; i >= 0; i-=dim) {
	    for(j=0;j<num_cubes;j+=dim2) {
		cubes[i+j].setFaceColor(Cube.kLeft, yellow);
		left.addCube(cubes[i+j]);
	    }
	}

	for(i = num_cubes-1; i >= num_cubes-dim2 ; i-=dim) {
	    for(j=0;j<num_cubes-1;j+=dim2) {
		cubes[i-j].setFaceColor(Cube.kRight, white);
		right.addCube(cubes[i-j]);
	    }
	}

	// Paint bottom orange
	for(i = num_cubes - dim2; i >= 0; i-=dim2) {
	    for(j = 0; j < dim; j+=1) {
		cubes[i+j].setFaceColor(Cube.kBottom, orange);
		bottom.addCube(cubes[i+j]);
	    }
	}

	// Paint top red
	for(i = dim2-dim; i < num_cubes; i+=dim2) {
	    for(j = 0; j < dim; j+=1) {
		cubes[i+j].setFaceColor(Cube.kTop, red);
		top.addCube(cubes[i+j]);
	    }
	}

	// Record z layer
	curZ += (cubeSize) / 2f;
	for(i=0;i<dim;i+=1) {
	    lz[i] = new Layer(new Vec3(0f, 0f, curZ), Layer.ZAxis);
	    curZ += cubeSize + space;
	    for(j=0;j<dim;j+=1) {
		for(k=0;k<dim;k+=1) {
		    lz[i].add(cubes[i*dim2 + j*dim + k]);
		}
	    }
	}
	top.setHLayers(lz);
	bottom.setHLayers(lz);
	left.setVLayers(lz);
	right.setVLayers(lz);
	// Record x layer
	curX += (cubeSize) / 2f;
	for(i=0;i<dim;i+=1) {
	    lx[i] = new Layer(new Vec3(curX, 0f, 0f), Layer.XAxis);
	    curX += cubeSize + space;
	    for(j=0;j<dim;j+=1) {
		for(k=0;k<dim;k+=1) {
		    lx[i].add(cubes[i + j*dim + k*dim2]);
		}
	    }
	}
	front.setVLayers(lx);
	back.setVLayers(lx);
	top.setVLayers(lx);
	bottom.setVLayers(lx);
	// Record y layer
	curY += (cubeSize) / 2f;
	for(i=0;i<dim;i+=1) {
	    ly[i] = new Layer(new Vec3(0f, curY, 0f), Layer.YAxis);
	    curY += cubeSize + space;
	    for(j=0;j<dim;j+=1) {
		for(k=0;k<dim;k+=1) {
		    ly[i].add(cubes[i*dim + j*dim2 + k]);
		}
	    }
	}
	front.setHLayers(ly);
	back.setHLayers(ly);
	left.setHLayers(ly);
	right.setHLayers(ly);

	for(i = 0; i < num_cubes; i+=1)
	    world.addShape(cubes[i]);

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

    public Cube[] getLayer(Cube c, int dir) {
	Cube[] layer = new Cube[dim*dim];
	/* Horizontal layer */
	if(dir == 0) {
	    
	/* Vertical layer */
	} else {
	    
	}
	return layer;
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
		    mode = SPIN;
		    curSide = cubeSides[i];
		    break;
		}
	    }
	    //front.getHitCube(screenToWorld(x1, y1), new Vec3(0f, 0f, 1f));
	    Log.e("Cube-end", " ");
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
		if(curLayer == null) {
		    if(Math.abs(newCoords.x - coords.x) > Math.abs(newCoords.y - coords.y)) {
			curLayer = curSide.getHLayer(hitVec);
			curLayer.setType(Layer.H);
		    } else {
			curLayer = curSide.getVLayer(hitVec);
			curLayer.setType(Layer.V);
		    }
		}
		curLayer.drag(coords.sub(newCoords));
		coords = newCoords;
	    }
	    break;
	}
	case MotionEvent.ACTION_UP: {
	    activePtrId = -1;
	    mode = NONE;
	    curSide = null;
	    curLayer = null;
	    break;
	}

	case MotionEvent.ACTION_CANCEL: {
	    activePtrId = -1;
	    mode = NONE;
	    curSide = null;
	    curLayer = null;
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