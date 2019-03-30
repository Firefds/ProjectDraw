package com.shauli.ProjectDraw;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Shauli Bracha
 * This class is the Graphics View which will all the drawing will be made
 */
public class GraphicsView extends View implements OnTouchListener {
    private Path path = new Path();
    private Paint paint = new Paint();
    private boolean isMoving = false;
    private int shapeID;
    private int bgColor = Color.WHITE;
    private boolean isSaved = false;
    private boolean undoState = false;
    private Bitmap bgImage;
    private Bitmap undoImage;

    /**
     * Constructors
     */
    public GraphicsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public GraphicsView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * Checks if there is a background image and draws it if there is.
         * if not it will color background of the bitmap in default background color  (white)
         */
        if (bgImage != null) {
            canvas.drawBitmap(bgImage, 0, 0, null);
        } else {
            canvas.drawColor(bgColor);
        }
        canvas.drawPath(path, paint);
    }

    /**
     * Sets the color of the current paint
     */
    public void setColor(int color) {
        paint.setColor(color);
    }

    /**
     * returns the color of the current paint
     */
    public int getColor() {
        return paint.getColor();
    }

    /**
     * Sets the background color of the current view
     */
    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    /**
     * Sets the width of the current paint
     */
    public void setDrawWidth(int width) {
        paint.setStrokeWidth(width);
    }

    /**
     * Returns the current path of the view
     */
    public Path getPath() {
        return path;
    }

    /**
     * Sets the current path of the view
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * Returns the current paint of the view
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Returns the  width of the current paint
     */
    public float getDrawWidth() {
        return paint.getStrokeWidth();
    }

    /**
     * Sets the current paint of the view
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Returns the isMoving state of the view
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Sets the isMoving state of the view
     */
    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    /**
     * Sets the StrokeCap to the current paint
     */
    public void setStrokeCap(Cap cap) {
        paint.setStrokeCap(cap);
    }

    /**
     * Sets the StrokeStyle of the current paint
     */
    public void setStyle(Style stroke) {
        paint.setStyle(stroke);
    }

    /**
     * Returns the StrokeStyle of the current paint
     */
    public Style getStyle() {
        return paint.getStyle();

    }

    /**
     * Returns the current ShapeID of the view
     */
    public int getShapeID() {
        return shapeID;
    }

    /**
     * Sets the ShapeID to of the view
     */
    public void setShapeID(int shapeID) {
        this.shapeID = shapeID;
    }

    /**
     * Sets the Anti Aliasing state to of the view
     */
    public void setAA(boolean isAA) {
        paint.setAntiAlias(isAA);
    }

    /**
     * Returns the UndoState of the view
     */
    public boolean isUndoState() {
        return undoState;
    }

    public void setUndoState(boolean undoState) {
        this.undoState = undoState;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public Bitmap getbgImage() {
        return bgImage;
    }

    public boolean onTouch(View v, MotionEvent event) {
        int numOfPointer = event.getPointerCount();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                undoImage = Bitmap.createBitmap(getBitmap()); // Saves the current bitmap for the undo action
                isMoving = true;
                isSaved = false; // Graphics view is not saved as we just started drawing
                undoState = true;
                for (int i = 0; i < numOfPointer; i++) // for each touch on screen draws the chosen shape
                    drawShape(event.getX(i), event.getY(i));
                //invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < numOfPointer; i++) // for each touch on screen draws the chosen shape
                    drawShape(event.getX(i), event.getY(i));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                bgImage = Bitmap.createBitmap(getBitmap()); //saves the new canvas as the current bitmap
                invalidate();
                path = new Path();
                break;
        }
        return true;
    }

    /**
     * Clears the view
     */
    public void clear() {
        new Path();
        new Paint();
        isSaved = false;
        undoState = false;
        bgImage = null;
        undoImage = null;
        invalidate();
    }

    /**
     * Switches between the current bitmap to the undo bitmap
     */
    public void undo() {
        if (undoState) {
            Bitmap tempBitmap;
            tempBitmap = Bitmap.createBitmap(bgImage);
            bgImage = Bitmap.createBitmap(undoImage);
            undoImage = Bitmap.createBitmap(tempBitmap);
            invalidate();
        }
    }

    /**
     * Returns the current canvas as a bitmap with its original width and height
     */
    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        return bitmap;
    }

    /**
     * Returns the current canvas as a bitmap with a custom width and height
     */
    public Bitmap getBitmap(int width, int hight) {
        Bitmap bitmap = Bitmap.createBitmap(width, hight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        return bitmap;
    }

    /**
     * Sets the background image
     */
    public void setBgImage(Bitmap bgImage) {
        undoState = true;
        undoImage = Bitmap.createBitmap(getBitmap());
        this.bgImage = Bitmap.createBitmap(bgImage);
    }

    /**
     * Draws a chosen shape
     */
    public void drawShape(float x, float y) {
        Path tempPath = new Path();
        switch (shapeID) {
            case ProjectConstants.SHAPE_SMALL_CIRCLE:
                path.addCircle(x, y, 10, Path.Direction.CW);
                break;
            case ProjectConstants.SHAPE_MEDIUM_CIRCLE:
                path.addCircle(x, y, 30, Path.Direction.CW);
                break;
            case ProjectConstants.SHAPE_LARGE_CIRCLE:
                path.addCircle(x, y, 50, Path.Direction.CW);
                break;
            case ProjectConstants.SHAPE_SMALL_SQUARE:
                path.addRect(x - 20, y - 20, x + 20, y + 20, Path.Direction.CW);
                break;
            case ProjectConstants.SHAPE_MEDIUM_SQUARE:
                path.addRect(x - 40, y - 40, x + 40, y + 40, Path.Direction.CW);
                break;
            case ProjectConstants.SHAPE_LARGE_SQUARE:
                path.addRect(x - 60, y - 60, x + 60, y + 60, Path.Direction.CW);
                break;
            case ProjectConstants.SHAPE_SMALL_TRI:
                if (paint.getStyle() == Style.FILL_AND_STROKE)
                    paint.setStyle(Style.FILL);
                tempPath.moveTo(x, y - 20);
                tempPath.lineTo(x - 20, y + 20);
                tempPath.lineTo(x + 20, y + 20);
                tempPath.close();
                path.addPath(new Path(tempPath));
                tempPath.reset();
                break;
            case ProjectConstants.SHAPE_MEDIUM_TRI:
                if (paint.getStyle() == Style.FILL_AND_STROKE)
                    paint.setStyle(Style.FILL);
                tempPath.moveTo(x, y - 40);
                tempPath.lineTo(x - 40, y + 40);
                tempPath.lineTo(x + 40, y + 40);
                tempPath.close();
                path.addPath(new Path(tempPath));
                tempPath.reset();
                break;
            case ProjectConstants.SHAPE_LARGE_TRI:
                if (paint.getStyle() == Style.FILL_AND_STROKE)
                    paint.setStyle(Style.FILL);
                tempPath.moveTo(x, y - 60);
                tempPath.lineTo(x - 60, y + 60);
                tempPath.lineTo(x + 60, y + 60);
                tempPath.close();
                path.addPath(new Path(tempPath));
                tempPath.reset();
                break;
        }
    }
}