PaletteBar
==========

A color picker bar with a horizontal rainbow and vertically changing hue values. The border of the picker changes to match the selection, and can be changed via <b>setColorMarginPx()</b>.
The bar will scale uniformly. Make sure to register for color selection via <b>setListener(PaletteBarListener listener)</b>. Note that this callback will be made with the starting color, black, as soon as the listener is registered.

In this screenshot, a light green has been selected:
![ScreenShot](/screenshots/paletteBarScreenshot.png)


Just add the single class file, PaletteBar.java, to your project.

![ScreenShot](/screenshots/paletteBarScreenshot2.png)   ![ScreenShot](/screenshots/paletteBarScreenshot3.png) ![ScreenShot](/screenshots/simpleDrawUse.gif)