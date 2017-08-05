# Cube Droid

An NxNxN Rubik's Cube implementation for Android, featuring:
* Slider to switch between cube dimensions
* Timer with state saved between sessions
* Scramble and reset the cube
* [Arcball](https://en.wikibooks.org/wiki/OpenGL_Programming/Modern_OpenGL_Tutorial_Arcball) implementation for cube rotation
* Touch to rotate cube layers, which snap to a position when the touch ends.
* Cube and timer states are saved when the users exits the app or it is forced to the background.

## How to build

This project can be built with the latest version of Android Studio and gradle.

Currently it relies on the latest commit of the master branch of the android-mgraphicslib library using Jitpack, but once mgraphicslib stabilizes it will use a release version.