package com.threeDBJ.puzzleDroid;

public class Mat3 extends Mat {

    public Mat3() {
	DIM = 3;
	m = new float[DIM][DIM];
    }

    public static Mat3 initIdentity() {
	Mat3 id = new Mat3();
	id.setIdentity();
	return id;
    }

    public static Mat3 mul(Mat3 mat1, Mat3 mat2) {
	Mat3 ret = new Mat3();
	for(int i=0; i<ret.DIM; i+=1) {
            for(int j=0; j<ret.DIM; j+=1) {
		for(int k=0; k<ret.DIM; k+=1){
		    ret.m[i][j] += mat1.m[i][k] * mat2.m[k][j];
		}
            }
	}
	return ret;
    }

    public static Mat3 mulInline(Mat3 mat1, Mat3 mat2) {
	float[][] m1 = mat1.m;
	float[][] m2 = mat2.m;
	Mat3 ret = new Mat3();
	float[][] r = ret.m;
	r[0][0] = (m1[0][0] * m2[0][0]) + (m1[0][1] * m2[1][0]) + (m1[0][2] * m2[2][0]);
        r[0][1] = (m1[0][0] * m2[0][1]) + (m1[0][1] * m2[1][1]) + (m1[0][2] * m2[2][1]);
        r[0][2] = (m1[0][0] * m2[0][2]) + (m1[0][1] * m2[1][2]) + (m1[0][2] * m2[2][2]);

        r[1][0] = (m1[1][0] * m2[0][0]) + (m1[1][1] * m2[1][0]) + (m1[1][2] * m2[2][0]);
        r[1][1] = (m1[1][0] * m2[0][1]) + (m1[1][1] * m2[1][1]) + (m1[1][2] * m2[2][1]);
        r[1][2] = (m1[1][0] * m2[0][2]) + (m1[1][1] * m2[1][2]) + (m1[1][2] * m2[2][2]);

        r[2][0] = (m1[2][0] * m2[0][0]) + (m1[2][1] * m2[1][0]) + (m1[2][2] * m2[2][0]);
        r[2][1] = (m1[2][0] * m2[0][1]) + (m1[2][1] * m2[1][1]) + (m1[2][2] * m2[2][1]);
        r[2][2] = (m1[2][0] * m2[0][2]) + (m1[2][1] * m2[1][2]) + (m1[2][2] * m2[2][2]);
	return ret;
    }

}