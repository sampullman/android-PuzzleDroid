package com.threeDBJ.puzzleDroid;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import javax.microedition.khronos.opengles.GL11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class TextureFont extends Texture {

    ArrayList<TextureLetter> prevText = new ArrayList<TextureLetter>();
    HashMap<Character, TextureLetter> letters = new HashMap<Character, TextureLetter>();
    float width, height, worldW, worldH, maxCharW=-1f, maxCharH=-1;
    GLColor defaultColor = new GLColor(0f, 0f, 0f);

    public TextureFont(Context context, int resource, String infoFile) {
	super(resource);
	loadInfo(context, infoFile);
    }

    public void setWorldDimensions(float w, float h) {
	this.worldW = w;
	this.worldH = h;
    }

    public Vec2 measureText(char[] chars, float textSize) {
	Vec2 s = new Vec2();
	TextureLetter tl;
	float sizeF = textSize / 90f;
	float adjustH = sizeF / maxCharH;
	float adjustW = sizeF / maxCharW;
	float h;
	for(int i=0;i<chars.length;i+=1) {
	    tl = letters.get(new Character(chars[i]));
	    s.x += tl.w * adjustW;
	    h = tl.h * adjustH;
	    if(h > s.y) s.y = h;
	}
	return s;
    }

    public Vec2 measureText(String text, float textSize) {
	return measureText(text.toCharArray(), textSize);
    }

    public void generateText(TextureTextView view, char[] chars,
			     float x, float y, float z) {
	generateText(view, chars, x, y, z, defaultColor);
    }

    public void generateText(TextureTextView view, char[] chars,
			     float x, float y, float z, GLColor c) {
	float sizeF = view.textSize / 90f;
	float adjustH = sizeF / maxCharH;
	float adjustW = sizeF / maxCharW;
	float space = sizeF / 16f;
	float xr, yb, yt;
	GLFace f;
	TextureLetter tl;
	GLVertex v1, v2, v3, v4;
	int vertCount = view.mVertexList.size(), indCount=0;
	view.fontVerts.clear();
	FloatBuffer texBuf = view.mTextureBuffer.duplicate();
	FloatBuffer colorBuf = view.mColorBuffer.duplicate();
	ShortBuffer indBuf = view.mIndexBuffer.duplicate();
	FloatBuffer vertBuf = view.mVertexBuffer.duplicate();
	indBuf.position(view.indStart);
	colorBuf.position(view.colorStart);
	vertBuf.position(view.vertStart);
	texBuf.position(view.texStart);
	x += view.paddingLeft;
	y += view.paddingBottom;
	for(int i=0;i<chars.length;i+=1) {
	    tl = letters.get(new Character(chars[i]));
	    xr = x + tl.w * adjustW;
	    // TODO -- skip first n chars, where each char maps to prevChars
	    //if(i >= view.prevText.length || chars[i] != view.prevText[i]) {
	    yb = y + getYOffset(chars[i], tl.h, adjustH);
		yt = yb + tl.h * adjustH;
		v1 = new GLVertex(xr, yb, z, vertCount);
		v2 = new GLVertex(xr, yt, z, vertCount+1);
		v3 = new GLVertex(x, yb, z, vertCount+2);
		v4 = new GLVertex(x, yt, z, vertCount+3);
		f = new GLFace(v1, v2, v3, v4);
		// TODO -- small optimization here
		view.fontVerts.add(v1);
		view.fontVerts.add(v2);
		view.fontVerts.add(v3);
		view.fontVerts.add(v4);
		f.setColor(c);
		v1.put(vertBuf, colorBuf);
		v2.put(vertBuf, colorBuf);
		v3.put(vertBuf, colorBuf);
		v4.put(vertBuf, colorBuf);
		f.putIndices(indBuf);
		tl.putCoords(texBuf);
		/*} else {
		vertBuf.position(vertBuf.position() + 12);
		colorBuf.position(colorBuf.position() + 16);
		indBuf.position(indBuf.position() + 6);
		texBuf.position(texBuf.position() + 8);
		}*/
	    indCount += 6;
	    vertCount += 4;
	    x = xr + space;
	    //Log.e("Cube", chars[i]+"");
	}
	view.fontIndexCount = indCount;
	view.mVertexBuffer = vertBuf;
	view.mColorBuffer = colorBuf;
	view.mIndexBuffer = indBuf;
	view.mTextureBuffer = texBuf;
    }

    public float getYOffset(char c, float h, float adjustH) {
	switch(c) {
	case '"':
	case '\'':
	case '^':
	    return (maxCharH - h) * adjustH;
	case '*':
	case '+':
	case '-':
	case '~':
	case ':':
	case ';':
	case '<':
	case '=':
	case '>':
	    return (maxCharH - h) * adjustH / 5f;
	}
	return 0;
    }

    public void init(GL11 gl, Context context) {
	GLEnvironment.loadTexture(gl, context, this);
    }

    public void loadInfo(Context context, String infoFile) {
	try {
	    AssetManager am = context.getAssets();
	    InputStreamReader reader = new InputStreamReader(am.open(infoFile));
	    BufferedReader r = new BufferedReader(reader);
	    String line = r.readLine();
	    String[] lineArr = line.split(" ");
	    width = Float.parseFloat(lineArr[0]);
	    height = Float.parseFloat(lineArr[1]);
	    TextureLetter l;
	    // Little hack to account for the first letter being a space
	    // TODO -- fix this booboo with a band aid, not duct tape
	    line = "*"+r.readLine().substring(1);
	    lineArr = line.split(" ");
	    lineArr[0] = " ";
	    while(true) {
		int x = Integer.parseInt(lineArr[1]);
		int y = Integer.parseInt(lineArr[2]);
		int w = Integer.parseInt(lineArr[3]);
		int h = Integer.parseInt(lineArr[4]);
		float xl = (float)x / width;
		float xr = xl + (float)w / width;
		float yb = (float)(y+h) / height;
		float yt = (float)y / height;
		l = new TextureLetter(lineArr[0].charAt(0), xl, xr, yb, yt, w, h);
		if(w > maxCharW) maxCharW = (float)w;
		if(h > maxCharH) maxCharH = (float)h;
		letters.put(l.let, l);
		line = r.readLine();
		if(line == null || line.length() == 0) break;
		lineArr = line.split(" ");
	    }
	} catch(Exception e) {
	    Log.e("Cube", "Texture font failed to load. "+e.getMessage());
	}
    }

    private class TextureLetter extends Texture {

	float xl, xr, yb, yt, w, h;
	Character let;

	TextureLetter(char let, float xl, float xr, float yb, float yt, int w, int h) {
	    this.w = (float)w; this.h = (float)h;
	    this.let = new Character(let);
	    this.xl = xl; this.xr = xr; this.yb = yb; this.yt = yt;
	    coords[0] = coords[2] = xr;
	    coords[1] = coords[5] = yb;
	    coords[4] = coords[6] = xl;
	    coords[3] = coords[7] = yt;
	}

	public String toString() {
	    return "xl: "+xl+" xr: "+xr+" yb: "+yb+" yt: "+yt+" w: "+w+" h: "+h;
	}

    }

}