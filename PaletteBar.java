package com.meetme.android.palettebar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * A colorful bar fading from left to right between the ROYGBIV rainbow of colors (hues), preceded
 * by gray and brown. Starting from the center and moving up, color lighten into white, and from the
 * center down, darken into black. When a color is touched, a callback on the optional listener is
 * fired. The selected color can also be displayed as a border around the palette and by a cursor
 * atop the palette.
 *
 * @author brianherbert
 *
 */
public class PaletteBar extends RelativeLayout {
    public static final String TAG = "PaletteBar";

    public static final int DEFAULT_COLOR_MARGIN_DP = 4;

    static final int GRAY = Color.rgb(128, 128, 128);
    static final int BROWN = Color.rgb(128, 64, 0);
    static final int RED = Color.rgb(255, 0, 0);
    static final int YELLOW = Color.rgb(255, 255, 0);
    static final int GREEN = Color.rgb(0, 255, 0);
    static final int TEAL = Color.rgb(128, 255, 255);
    static final int BLUE = Color.rgb(0, 0, 255);
    static final int VIOLET = Color.rgb(255, 0, 255);

    static final GradientDrawable.Orientation LR_ORIENTATION = GradientDrawable.Orientation.LEFT_RIGHT;

    static final GradientDrawable[] COLOR_GRADIENTS = {
            new GradientDrawable(LR_ORIENTATION, new int[] { GRAY, BROWN }),
            new GradientDrawable(LR_ORIENTATION, new int[] { BROWN, RED }),
            new GradientDrawable(LR_ORIENTATION, new int[] { RED, YELLOW }),
            new GradientDrawable(LR_ORIENTATION, new int[] { YELLOW, GREEN }),
            new GradientDrawable(LR_ORIENTATION, new int[] { GREEN, TEAL }),
            new GradientDrawable(LR_ORIENTATION, new int[] { TEAL, BLUE }),
            new GradientDrawable(LR_ORIENTATION, new int[] { BLUE, VIOLET }),
    };

    static final GradientDrawable[] TINT_GRADIENTS = {
            new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] { Color.WHITE, Color.TRANSPARENT }),
            new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] { Color.BLACK, Color.TRANSPARENT })
    };

    private int mPaletteWidth = 0;
    private int mPaletteHeight = 0;

    /** The border around the palette that indicates the selected color */
    private int mColorMargin = -1;

    private boolean mShowColorInMargin = true;

    private int mCurrentColor = Color.BLACK;

    private PaletteBarListener mListener;

    public PaletteBar(Context context) {
        this(context, null);
    }

    public PaletteBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    /**
     * Set the width of the border that indicates the selected color, in pixels. The default value
     * is {@value #DEFAULT_COLOR_MARGIN_DP}dp
     *
     * @param colorMarginPx The size of the margin in pixels
     */
    public void setColorMarginPx(int colorMarginPx) {
        mColorMargin = colorMarginPx;

        if (getContext() != null) {
            init(getContext());
            invalidate();
        }
    }

    /**
     * Sets up the view. There are three layers; from bottom to top: hues, tints (black and white),
     * selector (optional)
     */
    public void init(Context context) {
        removeAllViews();

        LinearLayout linHues = new LinearLayout(context);
        LinearLayout linTints = new LinearLayout(context);

        RelativeLayout.LayoutParams lpHuesAndTint = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        if (mColorMargin < 0) {
            mColorMargin = (int) (context.getResources().getDisplayMetrics().density * DEFAULT_COLOR_MARGIN_DP + .5);
        }

        lpHuesAndTint.setMargins(mColorMargin, mColorMargin, mColorMargin, mColorMargin);

        linHues.setLayoutParams(lpHuesAndTint);
        linTints.setLayoutParams(lpHuesAndTint);

        // Set up the range of hues; this is the bottom layer
        LinearLayout.LayoutParams lpGradients = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        lpGradients.weight = 1;

        // Add the gradients to the linear layout, creating a horizontal rainbow
        for (int i = 0; i < COLOR_GRADIENTS.length; i++) {
            View view = new View(context);
            view.setLayoutParams(lpGradients);
            view.setBackgroundDrawable(COLOR_GRADIENTS[i]);
            linHues.addView(view);
        }

        addView(linHues);

        // Set up the range of tints; this is the second layer
        linTints.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpTints = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        lpTints.weight = 1;

        for (int i = 0; i < TINT_GRADIENTS.length; i++) {
            View view = new View(context);
            view.setLayoutParams(lpTints);
            view.setBackgroundDrawable(TINT_GRADIENTS[i]);
            linTints.addView(view);
        }

        addView(linTints);

        setBackgroundColor(mCurrentColor);
    }

    /**
     * Get the current colour
     */
    public int getCurrentColor() {
        return mCurrentColor;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        PaletteBarSavedState savedState = new PaletteBarSavedState(super.onSaveInstanceState());
        savedState.currentColor = mCurrentColor;
        savedState.colorMargin = mColorMargin;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof PaletteBarSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        PaletteBarSavedState savedState = (PaletteBarSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mColorMargin = savedState.colorMargin;
        mCurrentColor = savedState.currentColor;
        setBackgroundColor(mCurrentColor);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaletteWidth = w - (mColorMargin * 2);
        mPaletteHeight = h - (mColorMargin * 2);
    }

    /** Touch listener that adjusts coords for positioning the selector and determining the color */
    private View.OnTouchListener mTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            float x = event.getX();
            float y = event.getY();

            // Adjust coords to be inside palette if they're in the margin
            if (x < mColorMargin) {
                x = mColorMargin;
            } else if (x >= mPaletteWidth + mColorMargin) {
                x = mPaletteWidth + mColorMargin - 1;
            }

            if (y < mColorMargin) {
                y = mColorMargin;
            } else if (y >= mPaletteHeight + mColorMargin) {
                y = mPaletteHeight + mColorMargin - 1;
            }

            // Figure out what color was touched;
            mCurrentColor = getColorFromCoords(x, y);

            if (action == MotionEvent.ACTION_UP && mListener != null) {
                mListener.onColorSelected(mCurrentColor);
            } else if (mShowColorInMargin && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)) {
                setBackgroundColor(mCurrentColor);
            }

            return true;
        }
    };

    public int getColorFromCoords(float x, float y) {
        // Adjust the coords to be relative to the palette, ie (0,0) is the top right corner of the
        // palette
        x -= mColorMargin;
        y -= mColorMargin;

        // This is the width (or height for tint) in pixels of each gradient drawable
        float gradientSize = (float) mPaletteWidth / COLOR_GRADIENTS.length;

        // The current gradient we're in. Ex: if we're in block 2, the drawable is
        // gradient2_f00_to_ff0, or red through yellow. Next we find what
        // percentage of the way through that gradient we are to get an exact hue.
        float gradientIdx = (int) (x / gradientSize);

        // The decimal portion is what percent of the way through our current gradient we are
        float mantissa = (x / gradientSize) % 1;

        float r = 0;
        float g = 0;
        float b = 0;

        // Get the hue
        if (gradientIdx == 0) { // 888 to 840
            r = 127;
            g = 127 - (mantissa * 63);
            b = 127 - (mantissa * 127);
        } else if (gradientIdx == 1) { // 840 to f00
            r = 127 + (mantissa * 127);
            g = 63 - (mantissa * 63);
            b = 0;
        } else if (gradientIdx == 2) { // f00 to ff0
            r = 255;
            g = mantissa * 255;
            b = 0;
        } else if (gradientIdx == 3) { // ff0 to 0f0
            r = 255 - mantissa * 255;
            g = 255;
            b = 0;
        } else if (gradientIdx == 4) { // 0f0 to 0ff
            r = 0;
            g = 255;
            b = mantissa * 255;
        } else if (gradientIdx == 5) { // 0ff to 00f
            r = 0;
            g = 255 - mantissa * 255;
            b = 255;
        } else if (gradientIdx >= 6) { // 00f to f0f
            r = mantissa * 255;
            g = 0;
            b = 255;
        }

        // Now we do the same thing for the vertical white and black gradients to get the tint
        gradientSize = mPaletteHeight / 2;
        gradientIdx = (int) (y / gradientSize);
        mantissa = (y / gradientSize) % 1;

        if (gradientIdx == 0) { // Add white
            float whiteness = 255 - mantissa * 255;
            r = Math.min(255, r + whiteness);
            g = Math.min(255, g + whiteness);
            b = Math.min(255, b + whiteness);
        } else { // "Add" black
            float blackness = mantissa * 255;
            r = Math.max(r - blackness, 0);
            g = Math.max(g - blackness, 0);
            b = Math.max(b - blackness, 0);
        }

        return Color.argb(255, (int) r, (int) g, (int) b);
    }

    public void setListener(PaletteBarListener listener) {
        mListener = listener;

        // We'll start listening for touches now that the implementer cares about them
        if (listener == null) {
            setOnTouchListener(null);
        } else {
            setOnTouchListener(mTouchListener);

            // Notify the listener of our current color
            mListener.onColorSelected(mCurrentColor);
        }
    }

    /**
     * Interface for receiving color selection in {@link PaletteBar}
     *
     * @author brianherbert
     *
     */
    public interface PaletteBarListener {
        public void onColorSelected(int color);
    }

    /**
     * Custom class to handle saving/restoring instance state
     *
     * @author jobrien
     *
     */
    private static class PaletteBarSavedState extends BaseSavedState {
        public Integer colorMargin;
        public Integer currentColor;

        PaletteBarSavedState(Parcelable superState) {
            super(superState);
        }

        private PaletteBarSavedState(Parcel in) {
            super(in);
            currentColor = in.readInt();
            colorMargin = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentColor);
            out.writeInt(colorMargin);
        }

        public static final Parcelable.Creator<PaletteBarSavedState> CREATOR = new Parcelable.Creator<PaletteBarSavedState>() {
            public PaletteBarSavedState createFromParcel(Parcel in) {
                return new PaletteBarSavedState(in);
            }

            public PaletteBarSavedState[] newArray(int size) {
                return new PaletteBarSavedState[size];
            }
        };
    }
}
