package com.shauli.ProjectDraw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author Shauli Bracha
 * This class gets in a bundle the current shape, width and fill state of the current brush
 * and returns a color which is selected here
 */
public class ColorSelect extends AppCompatActivity implements OnClickListener, OnTouchListener {

    private int shape;
    private GraphicsView gv = null;
    private int DrawWidth;
    private boolean fill;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_select);

        getIntentExtras();
        setListeners();
        createGrid();
    }

    /**
     * Creates the grid for choosing the color
     */
    public void createGrid() {
        /**
         * Sets the size of the bitmap which will be created
         */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / 3;

        /**
         * for each color creates a bitmap using the GraphicsView class and sets it as an ImageView
         */
        for (int i = 1; i < 10; i++) {
            gv = new GraphicsView(this);

            if (fill)
                gv.setStyle(Style.FILL_AND_STROKE);
            else
                gv.setStyle(Style.STROKE);

            setColor(i);
            gv.setShapeID(shape);
            gv.setDrawWidth(DrawWidth);
            gv.drawShape(width / 2, width / 2);
            gv.invalidate();

            switch (i) {
                case ProjectConstants.COLOR_BLACK:
                    ((ImageView) findViewById(R.id.black)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_BLUE:
                    ((ImageView) findViewById(R.id.blue)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_GREEN:
                    ((ImageView) findViewById(R.id.green)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_YELLOW:
                    ((ImageView) findViewById(R.id.yellow)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_RED:
                    ((ImageView) findViewById(R.id.red)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_GRAY:
                    ((ImageView) findViewById(R.id.gray)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_PURPLE:
                    ((ImageView) findViewById(R.id.purple)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_ORANGE:
                    ((ImageView) findViewById(R.id.orange)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.COLOR_BROWN:
                    ((ImageView) findViewById(R.id.brown)).setImageBitmap(gv.getBitmap(width, width));
                    break;

            }
        }
    }


    /**
     * When the color is selected returns its ID in an intent to the main activity
     */
    public void onClick(View v) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.black:
                bundle.putInt("color", ProjectConstants.COLOR_BLACK);
                break;
            case R.id.blue:
                bundle.putInt("color", ProjectConstants.COLOR_BLUE);
                break;
            case R.id.green:
                bundle.putInt("color", ProjectConstants.COLOR_GREEN);
                break;
            case R.id.yellow:
                bundle.putInt("color", ProjectConstants.COLOR_YELLOW);
                break;
            case R.id.red:
                bundle.putInt("color", ProjectConstants.COLOR_RED);
                break;
            case R.id.gray:
                bundle.putInt("color", ProjectConstants.COLOR_GRAY);
                break;
            case R.id.purple:
                bundle.putInt("color", ProjectConstants.COLOR_PURPLE);
                break;
            case R.id.orange:
                bundle.putInt("color", ProjectConstants.COLOR_ORANGE);
                break;
            case R.id.brown:
                bundle.putInt("color", ProjectConstants.COLOR_BROWN);
                break;
            case R.id.btnCancelColor:
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                break;
        }
        resultIntent.putExtra("bundle", bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * Sets the color of the preview bitmap
     */
    public void setColor(int color) {
        switch (color) {
            case ProjectConstants.COLOR_BLACK:
                gv.setColor(Color.BLACK);
                break;
            case ProjectConstants.COLOR_BLUE:
                gv.setColor(Color.BLUE);
                break;
            case ProjectConstants.COLOR_GREEN:
                gv.setColor(Color.GREEN);
                break;
            case ProjectConstants.COLOR_YELLOW:
                gv.setColor(Color.YELLOW);
                break;
            case ProjectConstants.COLOR_RED:
                gv.setColor(Color.RED);
                break;
            case ProjectConstants.COLOR_GRAY:
                gv.setColor(Color.GRAY);
                break;
            case ProjectConstants.COLOR_PURPLE:
                gv.setColor(getResources().getColor(R.color.purple));
                break;
            case ProjectConstants.COLOR_ORANGE:
                gv.setColor(getResources().getColor(R.color.orange));
                break;
            case ProjectConstants.COLOR_BROWN:
                gv.setColor(getResources().getColor(R.color.brown));
                break;
        }
    }

    /**
     * Draws a selected color with a different background color
     */
    public void createSelectCell(View v, int fgColor, int bgColor) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / 3;

        gv = new GraphicsView(this);

        if (fill)
            gv.setStyle(Style.FILL_AND_STROKE);
        else
            gv.setStyle(Style.STROKE);

        setColor(fgColor);
        gv.setBgColor(bgColor);
        gv.setShapeID(shape);
        gv.setDrawWidth(DrawWidth);
        gv.drawShape(width / 2, width / 2);
        gv.invalidate();
        ((ImageView) v).setImageBitmap(gv.getBitmap(width, width));
    }


    /**
     * When a color is touched its background color is changed
     */
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {

        int tempBgColor;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tempBgColor = getResources().getColor(R.color.selected_item);
                switch (v.getId()) {
                    case R.id.black:
                        createSelectCell(v, ProjectConstants.COLOR_BLACK, tempBgColor);
                        break;
                    case R.id.blue:
                        createSelectCell(v, ProjectConstants.COLOR_BLUE, tempBgColor);
                        break;
                    case R.id.green:
                        createSelectCell(v, ProjectConstants.COLOR_GREEN, tempBgColor);
                        break;
                    case R.id.yellow:
                        createSelectCell(v, ProjectConstants.COLOR_YELLOW, tempBgColor);
                        break;
                    case R.id.red:
                        createSelectCell(v, ProjectConstants.COLOR_RED, tempBgColor);
                        break;
                    case R.id.gray:
                        createSelectCell(v, ProjectConstants.COLOR_GRAY, tempBgColor);
                        break;
                    case R.id.purple:
                        createSelectCell(v, ProjectConstants.COLOR_PURPLE, tempBgColor);
                        break;
                    case R.id.orange:
                        createSelectCell(v, ProjectConstants.COLOR_ORANGE, tempBgColor);
                        break;
                    case R.id.brown:
                        createSelectCell(v, ProjectConstants.COLOR_BROWN, tempBgColor);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                tempBgColor = Color.WHITE;
                switch (v.getId()) {
                    case R.id.black:
                        createSelectCell(v, ProjectConstants.COLOR_BLACK, tempBgColor);
                        break;
                    case R.id.blue:
                        createSelectCell(v, ProjectConstants.COLOR_BLUE, tempBgColor);
                        break;
                    case R.id.green:
                        createSelectCell(v, ProjectConstants.COLOR_GREEN, tempBgColor);
                        break;
                    case R.id.yellow:
                        createSelectCell(v, ProjectConstants.COLOR_YELLOW, tempBgColor);
                        break;
                    case R.id.red:
                        createSelectCell(v, ProjectConstants.COLOR_RED, tempBgColor);
                        break;
                    case R.id.gray:
                        createSelectCell(v, ProjectConstants.COLOR_GRAY, tempBgColor);
                        break;
                    case R.id.purple:
                        createSelectCell(v, ProjectConstants.COLOR_PURPLE, tempBgColor);
                        break;
                    case R.id.orange:
                        createSelectCell(v, ProjectConstants.COLOR_ORANGE, tempBgColor);
                        break;
                    case R.id.brown:
                        createSelectCell(v, ProjectConstants.COLOR_BROWN, tempBgColor);
                        break;
                }
                break;
        }
        return false;
    }

    /**
     * Gets the intent extras
     */
    public void getIntentExtras() {
        Intent mIntent = getIntent();
        shape = mIntent.getIntExtra("shapeID", 0);
        DrawWidth = (int) mIntent.getFloatExtra("width", 6);
        fill = mIntent.getBooleanExtra("fill", false);
    }

    /**
     * Sets the listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setListeners() {
        ImageView black = findViewById(R.id.black);
        ImageView blue = findViewById(R.id.blue);
        ImageView green = findViewById(R.id.green);
        ImageView yellow = findViewById(R.id.yellow);
        ImageView red = findViewById(R.id.red);
        ImageView gray = findViewById(R.id.gray);
        ImageView purple = findViewById(R.id.purple);
        ImageView orange = findViewById(R.id.orange);
        ImageView brown = findViewById(R.id.brown);
        Button btnCancelColor = findViewById(R.id.btnCancelColor);

        black.setOnClickListener(this);
        blue.setOnClickListener(this);
        green.setOnClickListener(this);
        yellow.setOnClickListener(this);
        red.setOnClickListener(this);
        gray.setOnClickListener(this);
        purple.setOnClickListener(this);
        orange.setOnClickListener(this);
        brown.setOnClickListener(this);
        btnCancelColor.setOnClickListener(this);

        black.setOnTouchListener(this);
        blue.setOnTouchListener(this);
        green.setOnTouchListener(this);
        yellow.setOnTouchListener(this);
        red.setOnTouchListener(this);
        gray.setOnTouchListener(this);
        purple.setOnTouchListener(this);
        orange.setOnTouchListener(this);
        brown.setOnTouchListener(this);
    }
}

