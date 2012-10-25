readme.txt
Cubex by Eric Dietz (c) 2003 root@wrongway.org
a program to solve the Rubik's Cube.
Updated: 2003 Dec 26
Note: not affiliated with Rubik's Cube trademark.
Note: it is recommended you disable word-wrap to view this document.

IF YOU HAVE ANY QUESTIONS ABOUT HOW TO USE THIS PROGRAM PLEASE EMAIL ME!!!
(MY ADDRESS IS AT THE BOTTOM)

Contents:
I. Warranty & License
1. Compilation & Execution
2. Notes on the source code

I. *** Warranty & License ***

This program is released under the GNU General Public License.  It may be modified
and/or redistributed under the terms of said license.  The creator is not responsible
for damages occuring because of this program.  IT MAY NOT BE REPRODUCED OR MODIFIED
WITHOUT EXPLICIT CREDIT GIVEN TO THE AUTHOR.

1. *** Compilation & Execution ***

This program can run on Windows or Linux.
There MAY be a precompiled binary included for the Windows version, however if you wish to compile it yourself you may do so.
To compile on Linux:
make
To compile on Windows:
(from the Microsoft Visual Studio .NET command prompt):
nmake -f makefile.win

Here are the files that should be included in this .tar.gz (or .zip):

filename        comment
cubex.cpp     Source code - core solver
cubex.h       Source Code - core solver definitions
main.cpp      Source Code - user interface
Makefile      Used by make to compile with g++ (GCC)
makefile.win  Used by nmake to compile with cl (MSVS)
readme.txt    This file
MAY be included as well:
cubex.exe     The Win32 binary
cubex.bin     A precompiled linux binary that you shouldn't use unless all else fails

Notes on inputting cubes and how to interpret the output:

if you just run cubex (with no parameters), you'll get some helpful hints.

input:
cubex <cube-layout>[<center-rotations>]
cubex random[-centers]
  where the parameters are as such (NO spaces):
  random will randomly scramble a cube then generate a solution
  random-centers will do the same but it will also fix center rotations
  <cube-layout>
    a 54-character string, representing each facelet on the cube,
    going from face to face in this order:
      top, left, front, right, back, bottom,
    with a number (1 through 6), in the order:
      left to right, top to bottom (as you're looking at each face),
  <center-rotations>
    an OPTIONAL 6-character addition to the string, representing the rotation
    of each center, with a number (0 through 3), in this order:
      top, left, front, right, back, bottom,
    in which the rotations are like so:
      0, 1, 2, 3,
    which mean (respectively):
      center is rotated correctly,
      center is rotated 90 degrees clockwise,
      center is rotated 180 degrees,
      center is rotated 90 degrees counterclockwise.
example command-lines:
  cubex 212212212333333333626626626555555555141141141464464464\n\
  cubex 111111111222333222555222555444555444333444333666666666100003\n\
  cubex random
output (if successful):
  200 cube solved ok.
  101 version #.##.### by ...
  202 # moves # groups # # # # # # #...
  220 starting diagram:
  <human-readable diagram>
  221 diagram end.
  210 sending solution:
  <side><direction>. <side><direction>. ...
  211 completed solution.
  220 ending diagram:
  <human-readable diagram>
  221 diagram end.
  203 cmd: <command-line>
  201 terminating successfully.
  <side>
    U, D, L, R, F, B =
    top, bottom, left, right, front, back
  <direction>
    L, R, U, D, C, A =
    left, right, up, down, clockwise, counterclockwise
  (note: BC means back clockwise, as looking from the front, not looking from the back)
error codes possible:
  500 ERROR: solver failed for the following reason:
  510 ERROR: non-protocol input entered.
  511 ERROR: cubelet error - incorrect cubelets - cube mispainted.
  512 ERROR: parity error - nondescript - cube misassembled.
  513 ERROR: parity error - center rotation - cube misassembled.
  514 ERROR: cubelet error - backward centers or corners - cube mispainted.
  515 ERROR: parity error - edge flipping - cube misassembled.
  516 ERROR: parity error - edge swapping - cube misassembled.
  517 ERROR: parity error - corner rotation - cube misassembled.
  ...
  501 terminating unsuccessfully.
notes:
  - hint: try using the random parameter to get a feel for how to input cubes.\n\
  - use a terminal with scrollback feature to use this program! (or pipe it)
  - output may vary slightly from this; the only certain return codes are:
    200, 201, 210, 211, 500 and 501
  - just play with the program a little to get used to it.

2. *** Notes on the source code ***

NOTES ON Cube class (from cubex.cpp and cubex.h)
(HELPFUL IF YOU'RE TRYING TO USE IT FOR YOUR OWN PROGRAM):
special functions and variables for you to use:
I recommend you read through the whole thing twice at least if you really
intend to use this class...

let's say you make a new Cube by doing
	Cubex thecube;

Now this is what you can do with it:
	thecube.ResetCube();
this will reset the cube configuration, and also will reset cenfix
to its default value (false).  ResetCube is already executed when you
first create a Cubex object.

now, you can do the following things:
display the cube in text form on stdout:
	thecube.RenderScreen();

see if the cube is solved or not:
	bool x = thecube.IsSolved();

rotate faces:
	thecube.UL(); // top left
	thecube.UR(); // top right
	thecube.DL(); // bottom left
	thecube.DR(); // bottom right
	thecube.LU(); // left up
	thecube.LD(); // left down
	thecube.RU(); // right up
	thecube.RD(); // right down
	thecube.FC(); // front clockwise
	thecube.FA(); // front counterclockwise
	thecube.BC(); // back clockwise
	thecube.BA(); // back counterclockwise
	thecube.ML(); // middle left
	thecube.MR(); // middle right
	thecube.MU(); // middle up
	thecube.MD(); // middle down
	thecube.MC(); // middle clockwise
	thecube.MA(); // middle counterclockwise
	thecube.CL(); // whole cube left
	thecube.CR(); // whole cube right
	thecube.CU(); // whole cube up
	thecube.CD(); // whole cube down
	thecube.CC(); // whole cube clockwise
	thecube.CA(); // whole cube counterclockwise

Scramble up the cube:
	thecube.ScrambleCube();
NOTE: if you do a ScrambleCube, a ResetCube will also be done and cenfix
will be reset to its default value (false).  If you want to solve center
rotations after a ScrambleCube then you will also need to do a
thecube.cenfix = true; after the command above.

execute the solution returned by SolveCube():
	thecube.DoSolution();

find a given center, edge or corner:
	int centaxis = thecube.FindCent(int a);
	int edgeaxis = thecube.FindEdge(int a, int b);
	int cornaxis = thecube.FindCorn(int a, int b, int c);
(note: these above 3 functions also make output in the variables:)
	int locx = thecube.fx;
	int locy = thecube.fy;
	int locz = thecube.fz;

strip out any reduncies in a given solution (don't use this unless
you know what you're doing; the solver will automatically make use
of this for you):
	string shortersolution = thecube.Concise(string longersolution);
(in which e.g. string longersolution = thecube.solution; for the above)

do a complicated string analysis to shorten a solution (same disclaimer
as above, except that this one is completely unfinished so just ignore
it:)
	string efficientsolution = thecube.Efficient(thecube.solution);

SOLVE THE CUBE (yeah):
	int x = thecube.SolveCube();
	string thesolution = thecube.solution;
if there is an error solving the cube, then thecube.SolveCube() will
return one of these:
 1 = cubelet error - incorrect cubelets - cube mispainted.
 2 = parity error - nondescript - cube misassembled.
 3 = parity error - center rotation - cube misassembled.
 4 = cubelet error - backward centers or corners - cube mispainted.
 5 = parity error - edge flipping - cube misassembled.
 6 = parity error - edge swapping - cube misassembled.
 7 = parity error - corner rotation - cube misassembled.

If you want to edit the cube, then use these pointer functions:
	*thecube.face(x, y, z);
 it goes like this:
 top face =
	*thecube.face( x, 2, z);
 front face =
	*thecube.face( x, y,-2);
 left face =
	*thecube.face(-2, y, z);
 back face =
	*thecube.face( x, y, 2);
 right face =
	*thecube.face( 2, y, z);
 bottom face =
	*thecube.face( x,-2, z);
 in which
  x = -1 to 1,
  y = -1 to 1,
  z = -1 to 1,
note: you must always treat face() as a pointer (use a *).  Another way to do it is like this:
	int x = *thecube.face(x, y, z);
	*thecube.face(x, y, z) = 7;
is the same as:
	int x = thecube.Cub[ x +2][ y +2][ z +2];
	thecube.Cub[ x +2][ y +2][ z +2] = 7;
 note the +2 is because C arrays cannot have a lower bound other than 0.
NOTE: you MUST either do thecube.ResetCube() before editing a cube, or alternatively,
set thecube.cubeinit = true; when you're done, otherwise SolveCube() and IsSolved() will
return an error that the cube was not initialized.

if SolveCube() returns ok (0) then you will also get this array:
(whose purpose is to show how many moves are in each phase of the solution)
	int movesets[9] = thecube.mov[];
 as such:
 mov[0] = # total moves
 mov[1] = # moves for top edges
 mov[2] = # moves for top corners
 mov[3] = # moves for middle (edges)
 mov[4] = # moves for bottom edges orient
 mov[5] = # moves for bottom edges position
 mov[6] = # moves for bottom corners position
 mov[7] = # moves for bottom corners orient
 mov[8] = # moves for center rotating (optional)
also, the total number of groups (8) is stored in:
	int MOV = thecube.MOV
(note: MOV will remain 8 regardless of whether cenfix is true or false, even though cenfix
being false would mean there's really only 7 relevant groups)

There are also a bunch of flags you can give the solver:
To make the solver not care/not care about fixing centers:
	thecube.cenfix = true;
or
	thecube.cenfix = false;
(note: once this is set it will stay that way until changed.  Default is false).

to set whether the solver should strip out redundant moves:
	thecube.shorten = true;
or
	thecube.shorten = false;
the purpose of this is so that if you want the program to be more educational,
you can have the solution retain moves deemed "redundant" to give more of a
better understanding of how each seperate phase of the solver works.
(again: once this is set it will stay that way until changed.  Default is true).

if SolveCube() returns ok (0) then you will additionally have the solution in this
string:
	string thesolution = thecube.solution;

if SolveCube() returned an error, it will also be stored here:
	int errcode = thecube.erval;

You can test if two cubes are equal by doing:
	if (firstcube == nextcube) // cubes are equal
or
	if (firstcube != nextcube) // cubes are unequal

You can also access some other useful data:
To get the total number of currently instantiated cubes:
	int numcubes = Cubex::numcubes

Version of the solver:
	char* version = Cubex::ver;
and author:
	char* author = Cubex::author;

..........

Questions?  Comments?

- Eric, root@wrongway.org
