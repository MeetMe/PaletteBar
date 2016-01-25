PaletteBar for Android
==========

A color picker bar with a horizontal rainbow and vertically changing hue values, implemented as a custom View. Touch and optionally drag to change colors, then release to select, as shown in the gifs below. The border of the picker changes to match the selection, and its width can be changed via <b>setColorMarginPx()</b>.
The bar will scale uniformly. Make sure to register for color selection via <b>setListener(PaletteBarListener listener)</b>. Note that the <code>onColorSelected(int color)</code> callback will be made with the starting color, black, as soon as the listener is registered.

In this screenshot, a light green has been selected:
![ScreenShot](/screenshots/paletteBarScreenshot.png)


Just add the single class file, [PaletteBar.java](PaletteBar.java), to your Android project.

Here's PaletteBar in the [MeetMe app's](https://play.google.com/store/apps/details?id=com.myyearbook.m) chat photo editor:

![ScreenShot](/screenshots/selfiePaletteBar.gif)

and in a simple drawing app:

![ScreenShot](/screenshots/simpleDrawUse.gif)
Pardon the grainyness and mouse cursor.

## License

 Apache 2.0

    Copyright 2016 MeetMe, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
