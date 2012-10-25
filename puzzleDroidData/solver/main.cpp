/*
 * main.cpp
 * by Eric Dietz (c) 2003
 * Cube Solver, 'RCP' Output (designed to get piped to 'Rubik Control Protocol' receiver)
 * Email: root@wrongway.org
 * NOTE: This program is unaffiliated with the Rubik's Cube Trademark.
 */

#include <cstdio>
#include <time.h>
#include <string>
//#include <conio.h>
using namespace std;
#include "cubex.h"

// shared variables...
const char* guiver = "1.30";
const int N = Cubex::N;
Cubex thecube;
string thesolution = "";
bool randm = false;
int inmode = 0;
string app = "cubex";
string cmd = ""; int cmdc;
// send a protocol-compliant solution to the receiver program.
void SendSolution() {
  if (!thecube.cubeinit) return;
  int MOV = thecube.MOV, mov[thecube.MOV+1], m = 0, mv;
  string a = ""; string cm = "";
  for (int i = 0; i <= MOV; i++) mov[i] = thecube.mov[i];
  if (thecube.cenfix) mv = MOV; else mv = MOV - 1;
  if (randm) {
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        cm += *thecube.face(j,2,-i)+48;
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        cm += *thecube.face(-2,-i,-j)+48;
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        cm += *thecube.face(j,-i,-2)+48;
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        cm += *thecube.face(2,-i,j)+48;
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        cm += *thecube.face(-j,-i,2)+48;
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        cm += *thecube.face(j,-2,i)+48;
    if (thecube.cenfix) {
      cm += *thecube.face(0,1,0)+48;
      cm += *thecube.face(-1,0,0)+48;
      cm += *thecube.face(0,0,-1)+48;
      cm += *thecube.face(1,0,0)+48;
      cm += *thecube.face(0,0,1)+48;
      cm += *thecube.face(0,-1,0)+48;
    }
  }
  printf("200 cube solved ok.\n");
  printf("101 version %s%s by %s\n", guiver, Cubex::ver, Cubex::author);
  printf("202 %i moves %i groups", mov[0], mv);
  for (int i = 1; i <= mv; i++)
    printf(" %i", mov[i]);
  printf("\n");
  printf("220 starting diagram:\n");
  thecube.RenderScreen();
  printf("221 diagram end.\n");
  printf("210 sending solution:\n");
  for (int i = 1; i <= mv; i++) {
    for (int j = 1; j <= mov[i]; j++) {
      m++;
      a = thesolution.substr(m * 3 - 3, 3);
      if      (a == "UL.") { thecube.UL(); a = "UL"; } // top left
      else if (a == "UR.") { thecube.UR(); a = "UR"; } // top right
      else if (a == "DL.") { thecube.DL(); a = "DL"; } // bottom left
      else if (a == "DR.") { thecube.DR(); a = "DR"; } // bottom right
      else if (a == "LU.") { thecube.LU(); a = "LU"; } // left up
      else if (a == "LD.") { thecube.LD(); a = "LD"; } // left down
      else if (a == "RU.") { thecube.RU(); a = "RU"; } // right up
      else if (a == "RD.") { thecube.RD(); a = "RD"; } // right down
      else if (a == "FC.") { thecube.FC(); a = "FC"; } // front clockwise
      else if (a == "FA.") { thecube.FA(); a = "FA"; } // front counterclockwise
      else if (a == "BC.") { thecube.BC(); a = "BC"; } // back clockwise
      else if (a == "BA.") { thecube.BA(); a = "BA"; } // back counterclockwise
      else if (a == "ML.") { thecube.CL(); a = "CL"; } // middle left
      else if (a == "MR.") { thecube.CR(); a = "CR"; } // middle right
      else if (a == "MU.") { thecube.CU(); a = "CU"; } // middle up
      else if (a == "MD.") { thecube.CD(); a = "CD"; } // middle down
      else if (a == "MC.") { thecube.CC(); a = "CC"; } // middle clockwise
      else if (a == "MA.") { thecube.CA(); a = "CA"; } // middle counterclockwise
      else if (a == "CL.") { thecube.CL(); a = "CL"; } // whole cube left
      else if (a == "CR.") { thecube.CR(); a = "CR"; } // whole cube right
      else if (a == "CU.") { thecube.CU(); a = "CU"; } // whole cube up
      else if (a == "CD.") { thecube.CD(); a = "CD"; } // whole cube down
      else if (a == "CC.") { thecube.CC(); a = "CC"; } // whole cube clockwise
      else if (a == "CA.") { thecube.CA(); a = "CA"; } // whole cube counterclockwise
      printf("%s, ", a.c_str());
    }
  }
  printf("\n");
  printf("211 completed solution.\n");
//  printf("220 ending diagram:\n");
//  thecube.RenderScreen();
//  printf("221 diagram end.\n");
  if (randm)
    printf("203 cmd: %s %s\n", app.c_str(), cm.c_str());
  printf("201 terminating successfully.\n");
}
// take the command-line parameter and make a cube out of it
int ScrambleCubeGet() {
  string cm = "";
  srand(time(NULL));
  if (cmdc < 2) { return 1; }
  cm = cmd;
  thecube.ResetCube();
  if (cm == "random") {
    thecube.ScrambleCube();
    randm = true;
    return 0;
  }
  else if (cm == "random-centers") {
    thecube.ScrambleCube();
    thecube.cenfix = 1;
    randm = true;
    return 0;
  }
  if ((int)cm.find(" ") >= 0)
  {
    // NEW routine to deal with optional "U: D: L: R: F: B: C: " command lines
    int u,d,l,r,f,b,c,max=0,min=0;
    u = cm.find("U:"); if (u < 0) u = cm.find("u:");
    d = cm.find("D:"); if (d < 0) d = cm.find("d:");
    l = cm.find("L:"); if (l < 0) l = cm.find("l:");
    r = cm.find("R:"); if (r < 0) r = cm.find("r:");
    f = cm.find("F:"); if (f < 0) f = cm.find("f:");
    b = cm.find("B:"); if (b < 0) b = cm.find("b:");
    c = cm.find("C:"); if (c < 0) c = cm.find("c:");
    if (u < min) min = u; if (u > max) max = u;
    if (d < min) min = d; if (d > max) max = d;
    if (l < min) min = l; if (l > max) max = l;
    if (r < min) min = r; if (r > max) max = r;
    if (f < min) min = f; if (f > max) max = f;
    if (b < min) min = b; if (b > max) max = b;
    if (min < 0 || max > cm.length() - N * N - 2 || c > cm.length() - 6 - 2)
      return 1;
    cm  = cmd.substr(u+2,N*N);
    cm += cmd.substr(l+2,N*N);
    cm += cmd.substr(f+2,N*N);
    cm += cmd.substr(r+2,N*N);
    cm += cmd.substr(b+2,N*N);
    cm += cmd.substr(d+2,N*N);
    if (c >= 0)
      cm += cmd.substr(c+2,6);
  }
  thecube.cubeinit = false;
  if (cm.length() < N*N*6) {
    return 1;
  }
  for (int i = 1; i <= N*N*6; i++) {
    if (cm.at(i-1)-48 < 1 || cm.at(i-1)-48 > 6) {
      return 1;
    }
  }
  for (int i = -1; i <= 1; i++) {
    for (int j = -1; j <= 1; j++) {
      *thecube.face(j, 2, -i) = cm.at(i*3+j+4)-48;
      *thecube.face(-2, -i, -j) = cm.at(i*3+j+13)-48;
      *thecube.face(j, -i, -2) = cm.at(i*3+j+22)-48;
      *thecube.face(2, -i, j) = cm.at(i*3+j+31)-48;
      *thecube.face(-j, -i, 2) = cm.at(i*3+j+40)-48;
      *thecube.face(j, -2, i) = cm.at(i*3+j+49)-48;
    }
  }
  thecube.cubeinit = true;
  cm = cm.substr(N*N*6, cm.length() - N*N*6);
  thecube.cenfix = 0;
  if (cm.length() >= 6) {
    thecube.cenfix = 1;
    for (int i = 1; i <= 6; i++) {
      if (cm.at(i-1)-48 < 0 || cm.at(i-1)-48 > 3)
        thecube.cenfix = 0;
    }
    if (thecube.cenfix == 1) {
      for (int i = 0; i <= 0; i++) {
        for (int j = 0; j <= 0; j++) {
          *thecube.face(j, 1, -i) = cm.at(i*1+j+0)-48;
          *thecube.face(-1, -i, -j) = cm.at(i*1+j+1)-48;
          *thecube.face(j, -i, -1) = cm.at(i*1+j+2)-48;
          *thecube.face(1, -i, j) = cm.at(i*1+j+3)-48;
          *thecube.face(-j, -i, 1) = cm.at(i*1+j+4)-48;
          *thecube.face(j, -1, i) = cm.at(i*1+j+5)-48;
        }
      }
      cm = cm.substr(6, cm.length() - 6);
    }
  }
  return 0;
}
// main function...
int main(int argc, char* argv[]) {
  cmdc = argc;
  if (cmdc >= 7) {
    inmode = 1;
    cmd = argv[1];
    for (int i = 2; i <= 6; i++) {
      cmd += " ";
      cmd += argv[i];
    }
    if (cmdc >= 8) {
      cmd += " ";
      cmd += argv[7];
    }
  }
  else if (cmdc >= 2) {
    inmode = 0;
    cmd = argv[1];
  }
  app = argv[0];
  int n;
  n = app.rfind("\\");
  if (n > 0)
    app = app.substr(n+1, app.length()-n-1);
  n = app.rfind("/");
  if (n > 0)
    app = app.substr(n+1, app.length()-n-1);
  int x = 0, y = 0;
  y = ScrambleCubeGet();
  if (y == 0) {
    x = thecube.SolveCube();
    if (x == 0) {
      thesolution = thecube.solution;
      SendSolution();
    }
    else {
      printf("500 ERROR: solver failed for the following reason:\n");
      if      (x == 1)
        printf("511 ERROR: cubelet error - incorrect cubelets - cube mispainted.\n");
      else if (x == 2)
        printf("512 ERROR: parity error - nondescript - cube misassembled.\n");
      else if (x == 3)
        printf("513 ERROR: parity error - center rotation - cube misassembled.\n");
      else if (x == 4)
        printf("514 ERROR: cubelet error - backward centers or corners - cube mispainted.\n");
      else if (x == 5)
        printf("515 ERROR: parity error - edge flipping - cube misassembled.\n");
      else if (x == 6)
        printf("516 ERROR: parity error - edge swapping - cube misassembled.\n");
      else if (x == 7)
        printf("517 ERROR: parity error - corner rotation - cube misassembled.\n");
      printf("101 version %s%s by %s\n", guiver, Cubex::ver, Cubex::author);
      printf("220 your diagram:\n");
      thecube.RenderScreen();
      printf("221 diagram end.\n");
      printf("501 terminating unsuccessfully.\n");
    }
  }
  else {
    printf("500 ERROR: solver failed for the following reason:\n");
    printf("510 ERROR: non-protocol input entered.\n");
    printf("101 version %s%s by %s\n", guiver, Cubex::ver, Cubex::author);
    printf(
"\
400 syntax:\n\
%s <cube-layout>[<center-rotations>]\n\
%s random[-centers]\n\
example command-lines:\n\
  cubex 212212212333333333626626626555555555141141141464464464\n\
  cubex 111111111222333222555222555444555444333444333666666666100003\n\
  cubex random\n\
notes:\n\
  - hint: try using the random parameter to get a feel for how to input cubes.\n\
  - read readme.txt (included with this distribution) for protocol details.\n\
  - use a terminal with scrollback feature to use this program! (or pipe it)\n\
401 end of notes.\n\
",
app.c_str(), app.c_str()
    );
    printf("501 terminating unsuccessfully.\n");
  }
  if (x != 0 || y != 0) return -1;
  return 0;
}
