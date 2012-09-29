package com.threeDBJ.puzzleDroid;

public class Mat4 extends Mat {

    public Mat4() {
	DIM=4;
	m = new float[DIM][DIM];
    }

    public static Mat4 initIdentity() {
	Mat4 id = new Mat4();
	id.setIdentity();
	return id;
    }

    public static Mat4 mul(Mat4 mat1, Mat4 mat2) {
	Mat4 ret = new Mat4();
	float[][] m1 = mat1.m;
	float[][] m2 = mat2.m;
	float[][] r = ret.m;
	r[0][0] = m1[0][0] * m2[0][0] + m1[0][1] * m2[1][0] + m1[0][2] * m2[2][0] + m1[0][3] * m2[3][0];
        r[0][1] = m1[0][0] * m2[0][1] + m1[0][1] * m2[1][1] + m1[0][2] * m2[2][1] + m1[0][3] * m2[3][1];
        r[0][2] = m1[0][0] * m2[0][2] + m1[0][1] * m2[1][2] + m1[0][2] * m2[2][2] + m1[0][3] * m2[3][2];
        r[0][3] = m1[0][0] * m2[0][3] + m1[0][1] * m2[1][3] + m1[0][2] * m2[2][3] + m1[0][3] * m2[3][3];

        r[1][0] = m1[1][0] * m2[0][0] + m1[1][1] * m2[1][0] + m1[1][2] * m2[2][0] + m1[1][3] * m2[3][0];
        r[1][1] = m1[1][0] * m2[0][1] + m1[1][1] * m2[1][1] + m1[1][2] * m2[2][1] + m1[1][3] * m2[3][1];
        r[1][2] = m1[1][0] * m2[0][2] + m1[1][1] * m2[1][2] + m1[1][2] * m2[2][2] + m1[1][3] * m2[3][2];
        r[1][3] = m1[1][0] * m2[0][3] + m1[1][1] * m2[1][3] + m1[1][2] * m2[2][3] + m1[1][3] * m2[3][3];

        r[2][0] = m1[2][0] * m2[0][0] + m1[2][1] * m2[1][0] + m1[2][2] * m2[2][0] + m1[2][3] * m2[3][0];
        r[2][1] = m1[2][0] * m2[0][1] + m1[2][1] * m2[1][1] + m1[2][2] * m2[2][1] + m1[2][3] * m2[3][1];
        r[2][2] = m1[2][0] * m2[0][2] + m1[2][1] * m2[1][2] + m1[2][2] * m2[2][2] + m1[2][3] * m2[3][2];
        r[2][3] = m1[2][0] * m2[0][3] + m1[2][1] * m2[1][3] + m1[2][2] * m2[2][3] + m1[2][3] * m2[3][3];

        r[2][0] = m1[3][0] * m2[0][0] + m1[3][1] * m2[1][0] + m1[3][2] * m2[2][0] + m1[3][3] * m2[3][0];
        r[2][1] = m1[3][0] * m2[0][1] + m1[3][1] * m2[1][1] + m1[3][2] * m2[2][1] + m1[3][3] * m2[3][1];
        r[2][2] = m1[3][0] * m2[0][2] + m1[3][1] * m2[1][2] + m1[3][2] * m2[2][2] + m1[3][3] * m2[3][2];
        r[2][3] = m1[3][0] * m2[0][3] + m1[3][1] * m2[1][3] + m1[3][2] * m2[2][3] + m1[3][3] * m2[3][3];
	return ret;
    }

    public float[] getGLMatrix() {
	float[] ret = new float[DIM*DIM];
	for(int i=0;i<DIM;i+=1) {
	    for(int j=0;j<DIM;j+=1) {
		ret[i*DIM + j] = m[j][i];
	    }
	}
	return ret;
    }

    public float SVD() {
        float s;

        // this is a simple svd.
        // Not complete but fast and reasonable.

        s = (float)Math.sqrt(
			     ( (m[0][0] * m[0][0]) + (m[0][1] * m[0][1]) + (m[0][2] * m[0][2]) +
			       (m[1][0] * m[1][0]) + (m[1][1] * m[1][1]) + (m[1][2] * m[1][2]) +
			       (m[2][0] * m[2][0]) + (m[2][1] * m[2][1]) + (m[2][2] * m[2][2]) ) / 3.0f );
	return s;
    }

    public Mat4 setRotationMatrix(Mat3 m3) {
	float scale = SVD();
	setRotationScale(m3);
	mulRotationScale(scale);
	return this;
    }

    public void setRotationScale(Mat3 m1) {
        m[0][0] = m1.m[0][0]; m[1][0] = m1.m[1][0]; m[2][0] = m1.m[2][0];
        m[0][1] = m1.m[0][1]; m[1][1] = m1.m[1][1]; m[2][1] = m1.m[2][1];
        m[0][2] = m1.m[0][2]; m[1][2] = m1.m[1][2]; m[2][2] = m1.m[2][2];
    }

    public void mulRotationScale(float scale) {
        m[0][0] *= scale; m[1][0] *= scale; m[2][0] *= scale;
        m[0][1] *= scale; m[1][1] *= scale; m[2][1] *= scale;
        m[0][2] *= scale; m[1][2] *= scale; m[2][2] *= scale;
    }


}