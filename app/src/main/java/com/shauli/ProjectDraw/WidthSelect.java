package com.shauli.ProjectDraw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author Shauli Bracha
 * This class gets in a bundle the current shape, color and fill state of the current brush
 * and returns a width which is selected here
 */

public class WidthSelect extends AppCompatActivity implements OnClickListener, OnTouchListener {

    private GraphicsView gv = null;
    private int color;
    private int shape;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.width_select);

        getIntentExtras();
        setListeners();
        createGrid();
    }

    /**
     * Creates the grid for choosing the width
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
         * for each width creates a bitmap using the GraphicsView class and sets it as an ImageView
         */
        for (int i = 1; i < 4; i++) {
            gv = new GraphicsView(this);

            gv.setDrawWidth(i * 6);
            gv.setColor(color);
            gv.setShapeID(shape);
            gv.setStyle(Style.STROKE);
            gv.drawShape(width / 2, width / 2);
            gv.invalidate();

            switch (i) {
                case ProjectConstants.WIDTH_NORMAL / 6:
                    ((ImageView) findViewById(R.id.widthNormal)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.WIDTH_WIDE / 6:
                    ((ImageView) findViewById(R.id.widthWide)).setImageBitmap(gv.getBitmap(width, width));
                    break;
                case ProjectConstants.WIDTH_EXTRA_WIDE / 6:
                    ((ImageView) findViewById(R.id.widthExtraWide)).setImageBitmap(gv.getBitmap(width, width));
                    break;
            }
        }
    }

    /**
     * When the width is selected returns its ID in an intent to the main activity
     */
    public void onClick(View v) {

        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.widthNormal:
                bundle.putInt("width", ProjectConstants.WIDTH_NORMAL / 6);
                break;
            case R.id.widthWide:
                bundle.putInt("width", ProjectConstants.WIDTH_WIDE / 6);
                break;
            case R.id.widthExtraWide:
                bundle.putInt("width", ProjectConstants.WIDTH_EXTRA_WIDE / 6);
                break;
            case R.id.btnCancelWidth:
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                break;
        }
        resultIntent.putExtra("bundle", bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * Draws a selected color with a different background color
     */
    public void createSelectCell(View v, int drawWidth, int bgColor) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / 3;

        gv = new GraphicsView(this);

        gv.setBgColor(bgColor);
        gv.setDrawWidth(drawWidth);
        gv.setColor(color);
        gv.setShapeID(shape);
        gv.setStyle(Style.STROKE);
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
                    case R.id.widthNormal:
                        createSelectCell(v, ProjectConstants.WIDTH_NORMAL, tempBgColor);
                        break;
                    case R.id.widthWide:
                        createSelectCell(v, ProjectConstants.WIDTH_WIDE, tempBgColor);
                        break;
                    case R.id.widthExtraWide:
                        createSelectCell(v, ProjectConstants.WIDTH_EXTRA_WIDE, tempBgColor);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                tempBgColor = Color.WHITE;
                switch (v.getId()) {
                    case R.id.widthNormal:
                        createSelectCell(v, ProjectConstants.WIDTH_NORMAL, tempBgColor);
                        break;
                    case R.id.widthWide:
                        createSelectCell(v, ProjectConstants.WIDTH_WIDE, tempBgColor);
                        break;
                    case R.id.widthExtraWide:
                        createSelectCell(v, ProjectConstants.WIDTH_EXTRA_WIDE, tempBgColor);
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
        color = mIntent.getIntExtra("color", 0);
    }

    /**
     * Sets the listeners
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setListeners() {
        ImageView widthNormal = findViewById(R.id.widthNormal);
        ImageView widthWide = findViewById(R.id.widthWide);
        ImageView widthExtraWide = findViewById(R.id.widthExtraWide);
        Button btnCancelWidth = findViewById(R.id.btnCancelWidth);

        widthNormal.setOnClickListener(this);
        widthWide.setOnClickListener(this);
        widthExtraWide.setOnClickListener(this);
        btnCancelWidth.setOnClickListener(this);

        widthNormal.setOnTouchListener(this);
        widthWide.setOnTouchListener(this);
        widthExtraWide.setOnTouchListener(this);
    }
}
