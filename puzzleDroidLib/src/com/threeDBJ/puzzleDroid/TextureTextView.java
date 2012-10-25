package com.threeDBJ.puzzleDroid;

import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

import java.util.ArrayList;

public class TextureTextView extends TextureView {

    int MAX_CHAR_COUNT = 20;
    public int fontIndexCount=0, colorStart, vertStart, indStart, texStart;
    public char[] prevText = {};
    float textSize=16, paddingLeft, paddingTop, paddingRight, paddingBottom;
    TextureFont mFont;
    GLColor textColor = new GLColor(0, 0, 0);
    ArrayList<GLVertex> fontVerts = new ArrayList<GLVertex>();

    public TextureTextView(TextureFont font) {
	mFont = font;
    }

    public synchronized void setText(char[] chars) {
	mFont.generateText(this, chars, l, b, z+0.02f, textColor);
	prevText = chars.clone();
    }

    public void setText(String text) {
	char[] chars = text.toCharArray();
	setText(chars);
    }

    public void setPadding(float left, float top, float right, float bottom) {
	paddingLeft = left;
	paddingTop = top;
	paddingRight = right;
	paddingBottom = bottom;
    }

    public void setTextSize(float size) {
	textSize = size;
    }

    public void setTextColor(GLColor c) {
	textColor = c;
    }

    public synchronized void draw(GL11 gl) {
	super.draw(gl);
	if(fontIndexCount > 0) {
	    gl.glBindTexture(GL11.GL_TEXTURE_2D, mFont.id);
	    gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
	    gl.glVertexPointer(3, GL11.GL_FLOAT, 0, mVertexBuffer);
	    gl.glColorPointer(4, GL11.GL_FIXED, 0, mColorBuffer);
	    mIndexBuffer.position(indStart);
	    mColorBuffer.position(colorStart);
	    mVertexBuffer.position(vertStart);
	    mTextureBuffer.position(texStart);
	    gl.glDrawElements(GL11.GL_TRIANGLES, fontIndexCount, GL11.GL_UNSIGNED_SHORT, mIndexBuffer);
	}
    }

    public void translate(float x, float y, float z) {
	for(GLVertex v : fontVerts) {
	    v.translate(mVertexBuffer, x, y, z);
	}
	super.translate(x, y, z);
    }

    public void generate() {
	int maxVerts = MAX_CHAR_COUNT * 4;
	int maxVertSize = mVertexList.size() + maxVerts;
	genBuffers(maxVertSize*4*4, maxVertSize*4*3, (mIndexCount+maxVerts)*2, maxVertSize*4*8);
	fillBuffers();
	colorStart = mColorBuffer.position();
	vertStart = mVertexBuffer.position();
	indStart = mIndexBuffer.position();
	texStart = mTextureBuffer.position();
    }

}
