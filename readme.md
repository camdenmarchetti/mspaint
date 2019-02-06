README - Microsoft Paint Recreation

Author: Camden Marchetti
Email: cjm377@drexel.edu

Microsoft Paint is an electronic painter’s easel and canvas which allows the user to draw pictures on their computer with electronic support (functionality such as undo/redo, erase, select and drag, stretch, skew, flip, rotate, and save/load).

This implementation uses no external libraries. All sprites and cursors are packaged with the program where necessary. 


To run this program, execute the MSPaint.jar file located in the base directory of the archive (the same as this README file).


Moderate testing of the existing functionality was performed, but due to the complexity of the program, there has never been a single instance where all functions were tested in all possible orders. Instead, testing was completed in batches following development, then re-tested periodically to ensure new development did not cause disruptions in completed features. 

Located in the src file is the development source.
This README and the executable JAR file are located in the base directory.


Things which remain, that unfortunately could not be completed are:
    ⁃   Status bar 
    ⁃   Caused disruptions with the position of the color palette 
    ⁃   Due to this development of the status bar was halted
    ⁃   Custom zoom dialog module
    ⁃   Print files
    ⁃   Set images as the user’s background 
    ⁃   “View Bitmap” menu option
    ⁃   “View Thumbnail” menu option
    ⁃   Zoom tool has no mouse interaction
    ⁃   Zooming is done when the user selects a zoom amount 

Known bugs in the program which I was unable to correct are:
    ⁃   All text is drawn as a non-styled font
    ⁃   Text will not draw on the canvas with the expected line-wraps
    ⁃   All explicit line terminations are drawn as expected
    ⁃   Automatic line-wrapping is not transferred to the canvas as the user may expect
    ⁃   Background clipping for the select tools do not function as expected
    ⁃   It seems like only half the background is clipped
    ⁃   Free-form selections leave a partial single-pixel border of missing data
    ⁃   Committing a floating selection (rectangular or free-form) leaves a slightly blurred effect
    ⁃   Selection boxes are drawn in the currently-selected color
    ⁃   In Paint, these boxes are drawn in a contrasting color
    ⁃   I’m unsure how to change the color of a line based on the existing colors in the canvas

