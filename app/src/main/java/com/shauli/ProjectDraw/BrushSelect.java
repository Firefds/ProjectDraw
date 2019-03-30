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
import android.widget.Switch;


/**
 * @author Shauli Bracha
 * This class gets in a bundle the current color, width and fill state of the current brush
 * and returns a shape which is selected here
 */
public class BrushSelect extends AppCompatActivity implements OnClickListener, OnTouchListener {

    private int color;
    private boolean fill;
    private GraphicsView gv = null;
    private int DrawWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brush_select);

        getIntentExtras();
        setListeners();
        createGrid();
    }

    /**
     * Creates the grid for choosing the shape
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
         * for each ShapeID creates a bitmap using the GraphicsView class and sets it as an ImageView
         */
        for (int i = 1; i < 10; i++) {
            gv = new GraphicsView(this);

            if (fill)
                gv.setStyle(Style.FILL_AND_STROKE);
            else
                gv.setStyle(Style.STROKE);

            gv.setShapeID(i);
            gv.setColor(color);
            gv.setDrawWidth(DrawWidth);
            gv.drawShape(width / 2, width / 2);
            gv.invalidate();

            switch (i) {
                case ProjectConstants.SHAPE_SMALL_CIRCLE:
                    ((ImageView) findViewById(R.id.scircle)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_MEDIUM_CIRCLE:
                    ((ImageView) findViewById(R.id.mcircle)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_LARGE_CIRCLE:
                    ((ImageView) findViewById(R.id.lcircle)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_SMALL_SQUARE:
                    ((ImageView) findViewById(R.id.ssquare)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_MEDIUM_SQUARE:
                    ((ImageView) findViewById(R.id.msquare)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_LARGE_SQUARE:
                    ((ImageView) findViewById(R.id.lsquare)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_SMALL_TRI:
                    ((ImageView) findViewById(R.id.stri)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_MEDIUM_TRI:
                    ((ImageView) findViewById(R.id.mtri)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.SHAPE_LARGE_TRI:
                    ((ImageView) findViewById(R.id.ltri)).setImageBitmap(gv.getBitmap(width, width));
                    break;
            }
        }
    }

    /**
     * When the shape is selected returns its ID in an intent to the main activity
     */
    public void onClick(View v) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("fill", fill);

        switch (v.getId()) {
            case R.id.scircle:
                bundle.putInt("shape", ProjectConstants.SHAPE_SMALL_CIRCLE);
                break;
            case R.id.mcircle:
                bundle.putInt("shape", ProjectConstants.SHAPE_MEDIUM_CIRCLE);
                break;
            case R.id.lcircle:
                bundle.putInt("shape", ProjectConstants.SHAPE_LARGE_CIRCLE);
                break;
            case R.id.ssquare:
                bundle.putInt("shape", ProjectConstants.SHAPE_SMALL_SQUARE);
                break;
            case R.id.msquare:
                bundle.putInt("shape", ProjectConstants.SHAPE_MEDIUM_SQUARE);
                break;
            case R.id.lsquare:
                bundle.putInt("shape", ProjectConstants.SHAPE_LARGE_SQUARE);
                break;
            case R.id.stri:
                bundle.putInt("shape", ProjectConstants.SHAPE_SMALL_TRI);
                break;
            case R.id.mtri:
                bundle.putInt("shape", ProjectConstants.SHAPE_MEDIUM_TRI);
                break;
            case R.id.ltri:
                bundle.putInt("shape", ProjectConstants.SHAPE_LARGE_TRI);
                break;
            case R.id.btnCancelBrush:
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                break;
        }
        resultIntent.putExtra("bundle", bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    /**
     * Draws a selected shape with a different background color
     */
    public void createSelectCell(View v, int shape, int bgColor) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / 3;

        gv = new GraphicsView(this);

        if (fill)
            gv.setStyle(Style.FILL_AND_STROKE);
        else
            gv.setStyle(Style.STROKE);

        gv.setShapeID(shape);
        gv.setColor(color);
        gv.setBgColor(bgColor);
        gv.setDrawWidth(DrawWidth);
        gv.drawShape(width / 2, width / 2);
        gv.invalidate();
        ((ImageView) v).setImageBitmap(gv.getBitmap(width, width));
    }

    /**
     * When a shape is touched its background color is changed
     */
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {

        int tempBgColor;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tempBgColor = getResources().getColor(R.color.selected_item);
                switch (v.getId()) {
                    case R.id.scircle:
                        createSelectCell(v, ProjectConstants.SHAPE_SMALL_CIRCLE, tempBgColor);
                        break;
                    case R.id.mcircle:
                        createSelectCell(v, ProjectConstants.SHAPE_MEDIUM_CIRCLE, tempBgColor);
                        break;
                    case R.id.lcircle:
                        createSelectCell(v, ProjectConstants.SHAPE_LARGE_CIRCLE, tempBgColor);
                        break;
                    case R.id.ssquare:
                        createSelectCell(v, ProjectConstants.SHAPE_SMALL_SQUARE, tempBgColor);
                        break;
                    case R.id.msquare:
                        createSelectCell(v, ProjectConstants.SHAPE_MEDIUM_SQUARE, tempBgColor);
                        break;
                    case R.id.lsquare:
                        createSelectCell(v, ProjectConstants.SHAPE_LARGE_SQUARE, tempBgColor);
                        break;
                    case R.id.stri:
                        createSelectCell(v, ProjectConstants.SHAPE_SMALL_TRI, tempBgColor);
                        break;
                    case R.id.mtri:
                        createSelectCell(v, ProjectConstants.SHAPE_MEDIUM_TRI, tempBgColor);
                        break;
                    case R.id.ltri:
                        createSelectCell(v, ProjectConstants.SHAPE_LARGE_TRI, tempBgColor);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                tempBgColor = Color.WHITE;
                switch (v.getId()) {
                    case R.id.scircle:
                        createSelectCell(v, ProjectConstants.SHAPE_SMALL_CIRCLE, tempBgColor);
                        break;
                    case R.id.mcircle:
                        createSelectCell(v, ProjectConstants.SHAPE_MEDIUM_CIRCLE, tempBgColor);
                        break;
                    case R.id.lcircle:
                        createSelectCell(v, ProjectConstants.SHAPE_LARGE_CIRCLE, tempBgColor);
                        break;
                    case R.id.ssquare:
                        createSelectCell(v, ProjectConstants.SHAPE_SMALL_SQUARE, tempBgColor);
                        break;
                    case R.id.msquare:
                        createSelectCell(v, ProjectConstants.SHAPE_MEDIUM_SQUARE, tempBgColor);
                        break;
                    case R.id.lsquare:
                        createSelectCell(v, ProjectConstants.SHAPE_LARGE_SQUARE, tempBgColor);
                        break;
                    case R.id.stri:
                        createSelectCell(v, ProjectConstants.SHAPE_SMALL_TRI, tempBgColor);
                        break;
                    case R.id.mtri:
                        createSelectCell(v, ProjectConstants.SHAPE_MEDIUM_TRI, tempBgColor);
                        break;
                    case R.id.ltri:
                        createSelectCell(v, ProjectConstants.SHAPE_LARGE_TRI, tempBgColor);
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
        color = mIntent.getIntExtra("colorID", ProjectConstants.COLOR_BLACK);
        fill = mIntent.getBooleanExtra("fill", false);
        DrawWidth = (int) mIntent.getFloatExtra("width", ProjectConstants.WIDTH_NORMAL);
    }

    /**
     * Sets the listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setListeners() {
        ImageView scircle = findViewById(R.id.scircle);
        ImageView mcircle = findViewById(R.id.mcircle);
        ImageView lcircle = findViewById(R.id.lcircle);
        ImageView ssquare = findViewById(R.id.ssquare);
        ImageView msquare = findViewById(R.id.msquare);
        ImageView lsquare = findViewById(R.id.lsquare);
        ImageView stri = findViewById(R.id.stri);
        ImageView mtri = findViewById(R.id.mtri);
        ImageView ltri = findViewById(R.id.ltri);
        Button btnCancelBrush = findViewById(R.id.btnCancelBrush);
        Switch switchFill = findViewById(R.id.switchFill);

        scircle.setOnClickListener(this);
        mcircle.setOnClickListener(this);
        lcircle.setOnClickListener(this);
        ssquare.setOnClickListener(this);
        msquare.setOnClickListener(this);
        lsquare.setOnClickListener(this);
        stri.setOnClickListener(this);
        mtri.setOnClickListener(this);
        ltri.setOnClickListener(this);
        btnCancelBrush.setOnClickListener(this);

        scircle.setOnTouchListener(this);
        mcircle.setOnTouchListener(this);
        lcircle.setOnTouchListener(this);
        ssquare.setOnTouchListener(this);
        msquare.setOnTouchListener(this);
        lsquare.setOnTouchListener(this);
        stri.setOnTouchListener(this);
        mtri.setOnTouchListener(this);
        ltri.setOnTouchListener(this);

        switchFill.setChecked(fill);
        switchFill.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fill = !fill;
            createGrid();
        });
    }
}


