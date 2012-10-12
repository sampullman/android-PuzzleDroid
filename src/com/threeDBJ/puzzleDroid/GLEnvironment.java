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
    ArrayList<Texture>	mTextureList = new ArrayList<Texture>();

    int mIndexCount = 0;

    IntBuffer   mVertexBuffer;
    FloatBuffer   mColorBuffer;
    FloatBuffer mTextureBuffer;
    ShortBuffer mIndexBuffer;

    int[] textures = new int[1];

    boolean texturesEnabled=false;

    public void addShape(GLShape shape) {
	mShapeList.add(shape);
	mIndexCount += shape.getIndexCount();
    }

    public GLVertex addVertex(float x, float y, float z) {
	GLVertex vertex = new GLVertex(x, y, z, mVertexList.size());
	mVertexList.add(vertex);
	return vertex;
    }

    public void addTexture(Texture tex) {
	mTextureList.add(tex);
    }

    public void transformVertex(GLVertex vertex, Mat4 transform) {
	vertex.update(mVertexBuffer, transform);
    }

    public void generate() {
	ByteBuffer bb = ByteBuffer.allocateDirect(mVertexList.size()*4*4);
	bb.order(ByteOrder.nativeOrder());
	mColorBuffer = bb.asFloatBuffer();

	bb = ByteBuffer.allocateDirect(mVertexList.size()*4*3);
	bb.order(ByteOrder.nativeOrder());
	mVertexBuffer = bb.asIntBuffer();

	bb = ByteBuffer.allocateDirect(mIndexCount*2);
	bb.order(ByteOrder.nativeOrder());
	mIndexBuffer = bb.asShortBuffer();

	bb = ByteBuffer.allocateDirect(mVertexList.size()*4*8);
	bb.order(ByteOrder.nativeOrder());
	mTextureBuffer = bb.asFloatBuffer();


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

    public void draw(GL11 gl) {
	if(texturesEnabled) {
	    gl.glEnable(GL11.GL_TEXTURE_2D);
	} else {
	    gl.glDisable(GL11.GL_TEXTURE_2D);
	}
    }

    // Get a new texture id:
    public static int newTextureID(GL10 gl) {
	int[] temp = new int[1];
	gl.glGenTextures(1, temp, 0);
	return temp[0];
    }

    public void enableTextures() {
	texturesEnabled = true;
    }

    public void disableTextures() {
	texturesEnabled = false;
    }

    // Will load a texture out of a drawable resource file, and return an OpenGL texture ID:
    public void loadTexture(GL11 gl, Context context, int resource) {
	//Get the texture from the Android resource directory
	InputStream is = context.getResources().openRawResource(resource);
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
	gl.glGenTextures(1, textures, 0);
	int error = gl.glGetError();
	if (error != GL11.GL_NO_ERROR) {
	    Log.e("Cube", "GLError: " + error + " (" + GLU.gluErrorString(error) + ")");
	}
	//...and bind it to our array
	gl.glBindTexture(GL11.GL_TEXTURE_2D, textures[0]);

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
    }

}