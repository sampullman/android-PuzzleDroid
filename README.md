# Cube Droid

An NxNxN Rubik's Cube implementation for Android, featuring:
* Slider to switch between cube dimensions
* Timer
* Scramble and reset the cube
* [Arcball](https://en.wikibooks.org/wiki/OpenGL_Programming/Modern_OpenGL_Tutorial_Arcball) implementation for cube rotation
* Touch to rotate cube layers, which snap to position
* Cube and timer states saved between sessions

## Project notes
- Uses [MGraphicsLib](https://github.com/sampullman/android-mgraphicslib) for OpenGL backend
    - Uses latest Github commit, usually updates in tandem

## TODO
- Save cube state for all dimensions
- Make a nice background