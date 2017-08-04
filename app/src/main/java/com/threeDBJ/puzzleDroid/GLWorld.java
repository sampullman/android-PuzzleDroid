package com.threeDBJ.puzzleDroid;

import com.threeDBJ.MGraphicsLib.ArcBall;
import com.threeDBJ.MGraphicsLib.GLEnvironment;
import com.threeDBJ.MGraphicsLib.math.Mat4;
import com.threeDBJ.MGraphicsLib.math.Quaternion;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class GLWorld extends GLEnvironment {

    private RubeCube cube;

    private Quaternion startQuat = new Quaternion(0f, 0f, 0f, 1f);
    Mat4 rotate = new Mat4();
    private ArcBall arcBall = new ArcBall();
    private float scale = 1f;
    private boolean paused = false;

    public void setRubeCube(RubeCube cube) {
        this.cube = cube;
    }

    public void pauseCube(boolean pause) {
        this.paused = pause;
    }

    public void draw(GL11 gl) {
        super.draw(gl);
        //gl.glBlendFunc( gl.GL_ONE, gl.GL_SRC_ALPHA );
        gl.glDepthFunc(GL11.GL_LEQUAL);

        gl.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);
        gl.glMultMatrixf(rotate.val, 0);
        if (!paused) {
            cube.animate();
            //gl.glBindTexture(GL11.GL_TEXTURE_2D, mTexture.id);
            mColorBuffer.position(0);
            mTextureBuffer.position(0);
            mVertexBuffer.position(0);
            mIndexBuffer.position(0);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
            gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, mTextureBuffer);
            gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        }
        gl.glPopMatrix();
    }

    public void dragStart(float x, float y) {
        arcBall.dragStart(x, y);
    }

    public void drag(float x, float y) {
        Quaternion q = arcBall.drag(x, y);
        startQuat.mulLeft(q);
        rotate.set(startQuat);
    }

    public void setDimensions(int w, int h) {
        super.setDimensions(w, h);
        arcBall.setDimensions(w, h);
    }

}
