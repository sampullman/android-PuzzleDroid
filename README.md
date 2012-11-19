# Cube Droid
### by Sam Pullman

This is an NxNxN Rubik's Cube implementation for Android, featuring:
* Use a slider to switch between cube sizes (set to 2x2 - 8x8 currently).
* A second resolution timer that can be started, stopped, and reset.
* Buttons to scramble and reset the cube.
* Arcball implementation for cube rotation.
* Touch to rotate cube layers, which snap to a position when the touch ends.
* Cube and timer states are saved when the users exits the app or it is forced to the background.

## Instructions for getting this project up and running from the command line.

If you are an Eclipse/IntelliJ/NetBeans user you should replace any shell commands with an action appropriate to your IDE.
The 'update command' I will be referring to is simply:
android update project -p .

1. Clone this repository to a directory of your choice. This directory will be referred to as PROJ_HOME.

2. Clone MGraphicsLib - available on my github - into a folder named mGraphicsLib in the same directory as PROJ_HOME (NOT in PROJ_HOME).

3. cd into mGraphicsLib and run the update command.

4. cd into PROJ_HOME/puzzleDroidLib/ (up one level) and run the update command.

5. cd into PROJ_HOME/puzzleDroidPaid/ and run the update command. If you wish to build the free version, run the command in puzzleDroidFree.

6. The project is now ready to be built. There is a script included in the Paid/Free directories called "run" that cleans, debugs, and installs the app on whatever device you have connected. You can use the standard ant commands (clean, debug, etc.), although to release you would have to use your own keystore and refer to it in the ant.properties file in both the Lib and Paid/Free directories.

7. Make changes in the puzzleDroidLib project, and they will be reflected in both the Paid and Free projects. All you need to do is build the Paid/Free project and install it. Note that the Lib project cannot be installed as a standalone app.

8. Let me know if you have any issues or questions.