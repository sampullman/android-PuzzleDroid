package com.threeDBJ.puzzleDroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;


public class GLBase {

    /**
     * Make a direct NIO FloatBuffer from an array of floats
     * @param arr The array
     * @return The newly created FloatBuffer
     */
    protected static FloatBuffer makeFloatBuffer(float[] arr) {
	ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
	bb.order(ByteOrder.nativeOrder());
	FloatBuffer fb = bb.asFloatBuffer();
	fb.put(arr);
	fb.position(0);
	return fb;
    }

    /**
     * Make a direct NIO IntBuffer from an array of ints
     * @param arr The array
     * @return The newly created IntBuffer
     */
    protected static IntBuffer makeFloatBuffer(int[] arr) {
	ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
	bb.order(ByteOrder.nativeOrder());
	IntBuffer ib = bb.asIntBuffer();
	ib.put(arr);
	ib.position(0);
	return ib;
    }

    /**
     * Create a texture and send it to the graphics system
     * @param gl The GL object
     * @param bmp The bitmap of the texture
     * @param reverseRGB Should the RGB values be reversed?  (necessary workaround for loading .pngs...)
     * @return The newly created identifier for the texture.
     */
    protected static int loadTexture(GL10 gl, Bitmap bmp) {
	return loadTexture(gl, bmp, false);
    }
	
    /**
     * Create a texture and send it to the graphics system
     * @param gl The GL object
     * @param bmp The bitmap of the texture
     * @param reverseRGB Should the RGB values be reversed?  (necessary workaround for loading .pngs...)
     * @return The newly created identifier for the texture.
     */
    protected static int loadTexture(GL10 gl, Bitmap bmp, boolean reverseRGB) {
	ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight()*bmp.getWidth()*4);
	bb.order(ByteOrder.BIG_ENDIAN);
	IntBuffer ib = bb.asIntBuffer();

	for (int y=bmp.getHeight()-1;y>-1;y--)
	    for (int x=0;x<bmp.getWidth();x++) {
		int pix = bmp.getPixel(x,bmp.getHeight()-y-1);
		// Convert ARGB -> RGBA
		@SuppressWarnings("unused")
		    byte alpha = (byte)((pix >> 24)&0xFF);
		byte red = (byte)((pix >> 16)&0xFF);
		byte green = (byte)((pix >> 8)&0xFF);
		byte blue = (byte)((pix)&0xFF);
				
		// It seems like alpha is currently broken in Android...
		ib.put(red << 24 | green << 16 | blue << 8 | 0xFF);//255-alpha);
	    }
	ib.position(0);
	bb.position(0);

	int[] tmp_tex = new int[1];

	gl.glGenTextures(1, tmp_tex, 0);
	int tex = tmp_tex[0];
	gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
	gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.getWidth(), bmp.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

	return tex;
    }

    /**
     * Constructor
     */
    public GLBase() {

    }

}