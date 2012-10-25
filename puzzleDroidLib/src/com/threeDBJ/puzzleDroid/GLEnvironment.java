package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLU;
import android.opengl.GLUtils;

public abstract class GLEnvironment {

    ArrayList<GLShape>	mShapeList = new ArrayList<GLShape>();
    ArrayList<GLVertex>	mVertexList = new ArrayList<GLVertex>();
    Texture mTexture;

    int mIndexCount = 0;

    FloatBuffer   mVertexBuffer;
    FloatBuffer   mColorBuffer;
    FloatBuffer mTextureBuffer;
    ShortBuffer mIndexBuffer;

    int w, h;
    float adjustWidth, adjustHeight, ratio;

    boolean texturesEnabled=false;

    public void clear() {
	mShapeList.clear();
	mVertexList.clear();
	mIndexCount = 0;
    }

    public void addShape(GLShape shape) {
	mShapeList.add(shape);
	mIndexCount += shape.getIndexCount();
    }

    public GLVertex addVertex(float x, float y, float z) {
	GLVertex vertex = new GLVertex(x, y, z, mVertexList.size());
	mVertexList.add(vertex);
	return vertex;
    }

    public void transformVertex(GLVertex vertex, Mat4 transform) {
	vertex.update(mVertexBuffer, transform);
    }

    public void genBuffers(int colorSize, int vertSize, int indSize, int texSize) {
	ByteBuffer bb = ByteBuffer.allocateDirect(colorSize);
	bb.order(ByteOrder.nativeOrder());
	mColorBuffer = bb.asFloatBuffer();

	bb = ByteBuffer.allocateDirect(vertSize);
	bb.order(ByteOrder.nativeOrder());
	mVertexBuffer = bb.asFloatBuffer();

	bb = ByteBuffer.allocateDirect(indSize);
	bb.order(ByteOrder.nativeOrder());
	mIndexBuffer = bb.asShortBuffer();

	bb = ByteBuffer.allocateDirect(texSize);
	bb.order(ByteOrder.nativeOrder());
	mTextureBuffer = bb.asFloatBuffer();
    }

    public void fillBuffers() {
	Iterator<GLVertex> iter1 = mVertexList.iterator();
	while (iter1.hasNext()) {
	    GLVertex vertex = iter1.next();
	    vertex.put(mVertexBuffer, mColorBuffer);
	}

	Iterator<GLShape> iter2 = mShapeList.iterator();
	while (iter2.hasNext()) {
	    GLShape shape = iter2.next();
	    shape.putIndices(mIndexBuffer);
	    shape.putTextures(mTextureBuffer);
	}
    }

    public void generate() {
	genBuffers(mVertexList.size()*4*4, mVertexList.size()*4*3, mIndexCount*2, mVertexList.size()*4*8);
	fillBuffers();
    }

    public void setDimensions(int w, int h) {
	this.w = w;
	this.h = h;
	this.adjustWidth = 1f / w;
	this.adjustHeight = 1f / h;
	this.ratio = (float)w / (float)h;
    }

    public void draw(GL11 gl) {
	if(texturesEnabled) {
	    gl.glEnable(GL11.GL_TEXTURE_2D);
	} else {
	    gl.glDisable(GL11.GL_TEXTURE_2D);
	}
    }

    public void setTexture(GL11 gl, Context c, int res) {
	mTexture = new Texture(res);
	loadTexture(gl, c, mTexture);
	enableTextures();
    }

    public void enableTextures() {
	texturesEnabled = true;
    }

    public void disableTextures() {
	texturesEnabled = false;
    }

    // Get a new texture id:
    public static int newTextureId(GL11 gl) {
	int[] temp = new int[1];
	gl.glGenTextures(1, temp, 0);
	return temp[0];
    }

    // Will load a texture out of a drawable resource file, and return an OpenGL texture ID:
    public static void loadTexture(GL11 gl, Context context, Texture t) {
	//Get the texture from the Android resource directory
	InputStream is = context.getResources().openRawResource(t.resource);
	Bitmap bitmap = null;
	try {
	    //BitmapFactory is an Android graphics utility for images
	    bitmap = BitmapFactory.decodeStream(is);

	} finally {
	    //Always clear and close
	    try {
		is.close();
		is = null;
	    } catch (IOException e) {
	    }
	}

	//Generate one texture pointer...
	t.id = newTextureId(gl);
	int error = gl.glGetError();
	if (error != GL11.GL_NO_ERROR) {
	    Log.e("Cube", "GLError: " + error + " (" + GLU.gluErrorString(error) + ")");
	}
	//...and bind it to our array
	gl.glBindTexture(GL11.GL_TEXTURE_2D, t.id);

	//Create Nearest Filtered Texture
	gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

	//Different possible texture parameters, e.g. GL11.GL_CLAMP_TO_EDGE
	gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	//gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP_TO_EDGE);
	//gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP_TO_EDGE);
	//gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
	//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
	GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);

	//Clean up
	bitmap.recycle();
	t.loaded = true;
    }

}