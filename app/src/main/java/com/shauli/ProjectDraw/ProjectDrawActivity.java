package com.shauli.ProjectDraw;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.shauli.ProjectDraw.ProjectConstants.MY_CAMERA_REQUEST_CODE;
import static com.shauli.ProjectDraw.ProjectConstants.MY_STORAGE_REQUEST_CODE;
import static com.shauli.ProjectDraw.ProjectConstants.REQUEST_BG_COLOR;
import static com.shauli.ProjectDraw.ProjectConstants.REQUEST_COLOR;
import static com.shauli.ProjectDraw.ProjectConstants.REQUEST_SHAPE;
import static com.shauli.ProjectDraw.ProjectConstants.REQUEST_SOURCE_CAMERA;
import static com.shauli.ProjectDraw.ProjectConstants.REQUEST_SOURCE_GALLERY;
import static com.shauli.ProjectDraw.ProjectConstants.REQUEST_WIDTH;

public class ProjectDrawActivity extends AppCompatActivity
        implements OnClickListener, SensorEventListener {
    private boolean fill;
    private String fileName;
    private GraphicsView gv = null;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private int numOfMoves;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setListeners();
        paintDefault();
        setSensors();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        Button btnWidth = findViewById(R.id.btnWidth);

        //checks if the shape is filled or empty and enables/disables it
        if (!fill) {
            btnWidth.setBackgroundResource(R.drawable.button);
            btnWidth.setEnabled(true);
        } else {
            btnWidth.setEnabled(false);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, accelerometer);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_BG_COLOR:
                if (resultCode == RESULT_OK)
                    gv.setBgColor(setColor((data.getBundleExtra("bundle")).getInt("color")));
                break;
            case REQUEST_COLOR:
                if (resultCode == RESULT_OK)
                    gv.setColor(setColor((data.getBundleExtra("bundle")).getInt("color")));
                break;
            case REQUEST_SHAPE:
                if (resultCode == RESULT_OK) {
                    fill = (data.getBundleExtra("bundle")).getBoolean("fill");
                    setShape((data.getBundleExtra("bundle")).getInt("shape"));
                }
                break;
            case REQUEST_WIDTH:
                if (resultCode == RESULT_OK)
                    setWidth((data.getBundleExtra("bundle")).getInt("width"));
                break;
            case REQUEST_SOURCE_CAMERA:
                if (resultCode == RESULT_OK)
                    setBgImage(BitmapFactory.decodeByteArray(data.getByteArrayExtra("bitmap"), 0, data.getByteArrayExtra("bitmap").length));
                break;
            case REQUEST_SOURCE_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        setBgImage(Media.getBitmap(getContentResolver(), data.getData()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_meu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new:
                newDraw();
                break;
            case R.id.menu_import:
                showImportMenu();
                break;
            case R.id.menu_save:
                showSaveModal();
                break;
            case R.id.menu_upload:
                upload();
                break;
            case R.id.menu_prefs:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_gallery:
                break;
            case R.id.menu_send:
                break;
        }
        return true;
    }

    /**
     * Checks if the picture is saved before exit and prompts the user for exit/showSaveModal/cancel
     */
    @Override
    public void onBackPressed() {
        if (!gv.isSaved()) {
            androidx.appcompat.app.AlertDialog.Builder exitSaveDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
            exitSaveDialog.setMessage(getString(R.string.exitSaveMessege));
            exitSaveDialog.setPositiveButton(getString(R.string.btnSave), (paramDialogInterface, paramInt) -> showSaveModal());

            exitSaveDialog.setNegativeButton(getString(R.string.noSave), (paramDialogInterface, paramInt) -> finish());
            exitSaveDialog.setNeutralButton(getString(R.string.cancel), (dialog, which) -> {

            });
            exitSaveDialog.show();
        } else {
            AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
            exitDialog.setMessage(getString(R.string.exitMessege));
            exitDialog.setPositiveButton(getString(R.string.exit), (paramDialogInterface, paramInt) -> finish());

            exitDialog.setNegativeButton(getString(R.string.stay), (paramDialogInterface, paramInt) -> {

            });
            exitDialog.show();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Calls newDarw() if shake gesture is made
     */
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        if (((curTime - lastUpdate) > 100)) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            if (speed > ProjectConstants.SHAKE_THRESHOLD) {
                numOfMoves++;
                if (numOfMoves == ProjectConstants.MIN_DIRECTION_CHANGE) {
                    numOfMoves = 0;
                    newDraw();
                }
            } else
                numOfMoves = 0;

            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    public void setListeners() {
        gv = findViewById(R.id.graphicsView);
        Button btnColor = findViewById(R.id.btnColor);
        Button btnUndo = findViewById(R.id.btnUndo);
        Button btnWidth = findViewById(R.id.btnWidth);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnUpload = findViewById(R.id.btnUpload);
        Button btnShape = findViewById(R.id.btnShape);

        btnColor.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnWidth.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnShape.setOnClickListener(this);
    }

    /**
     * Sets the Graphics view to the default settings
     */
    public void paintDefault() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Button btnWidth = findViewById(R.id.btnWidth);

        gv.setAA(prefs.getBoolean("aa", false));
        int color = Color.parseColor(prefs.getString("color", "#000000"));
        int shape = Integer.parseInt(Objects.requireNonNull(prefs.getString("shape", "1")));
        int width = Integer.parseInt(Objects.requireNonNull(prefs.getString("width", "1")));

        //Enables/Disables the width button if the chosen shape is filled/empty
        switch (Integer.parseInt(Objects.requireNonNull(prefs.getString("fill", "1")))) {
            case 1:
                gv.setStyle(Style.STROKE);
                btnWidth.setEnabled(true);
                fill = false;
                break;
            case 2:
                gv.setStyle(Style.FILL_AND_STROKE);
                btnWidth.setEnabled(false);
                fill = true;
                break;
        }

        gv.setColor(color);
        setShape(shape);
        setWidth(width);
        gv.setStrokeCap(Cap.SQUARE);
        gv.setSaved(false);
        gv.setUndoState(false);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnColor:
                chooseColor();
                break;
            case R.id.btnUndo:
                gv.undo();
                break;
            case R.id.btnWidth:
                chooseWidth();
                break;
            case R.id.btnSave:
                showSaveModal();
                break;
            case R.id.btnUpload:
                upload();
                break;
            case R.id.btnShape:
                chooseShape();
                break;
        }
    }

    /**
     * Open color chooser activity
     */
    public void chooseColor() {
        Intent intent = new Intent(this, ColorSelect.class);
        intent.putExtra("shapeID", gv.getShapeID());
        intent.putExtra("fill", fill);
        intent.putExtra("width", gv.getDrawWidth());
        startActivityForResult(intent, REQUEST_COLOR);
    }


    /**
     * Open shape chooser activity
     */
    public void chooseShape() {
        Intent intent = new Intent(this, BrushSelect.class);
        intent.putExtra("fill", fill);
        intent.putExtra("width", gv.getDrawWidth());
        intent.putExtra("colorID", gv.getColor());
        startActivityForResult(intent, REQUEST_SHAPE);
    }

    /**
     * Open width chooser activity
     */
    public void chooseWidth() {
        Intent intent = new Intent(this, WidthSelect.class);
        intent.putExtra("shapeID", gv.getShapeID());
        intent.putExtra("color", gv.getColor());
        startActivityForResult(intent, REQUEST_WIDTH);
    }

    /**
     * sets the color of the current paint of the GraphicsView
     */
    public int setColor(int color) {
        int tempColor = 0;
        switch (color) {
            case ProjectConstants.COLOR_BLACK:
                tempColor = Color.BLACK;
                break;
            case ProjectConstants.COLOR_BLUE:
                tempColor = Color.BLUE;
                break;
            case ProjectConstants.COLOR_GREEN:
                tempColor = Color.GREEN;
                break;
            case ProjectConstants.COLOR_YELLOW:
                tempColor = Color.YELLOW;
                break;
            case ProjectConstants.COLOR_RED:
                tempColor = Color.RED;
                break;
            case ProjectConstants.COLOR_GRAY:
                tempColor = Color.GRAY;
                break;
            case ProjectConstants.COLOR_PURPLE:
                tempColor = getResources().getColor(R.color.purple);
                break;
            case ProjectConstants.COLOR_ORANGE:
                tempColor = getResources().getColor(R.color.orange);
                break;
            case ProjectConstants.COLOR_BROWN:
                tempColor = getResources().getColor(R.color.brown);
                break;
            case ProjectConstants.COLOR_WHITE:
                tempColor = Color.WHITE;
                break;
        }
        return tempColor;
    }


    /**
     * sets the shape of the current paint of the GraphicsView
     */
    public void setShape(int shape) {
        if (fill)
            gv.setStyle(Style.FILL_AND_STROKE);
        else
            gv.setStyle(Style.STROKE);
        gv.setShapeID(shape);
    }


    /**
     * sets the width of the current paint of the GraphicsView
     */
    public void setWidth(int width) {
        switch (width) {
            case 1:
                gv.setDrawWidth(ProjectConstants.WIDTH_NORMAL);
                break;
            case 2:
                gv.setDrawWidth(ProjectConstants.WIDTH_WIDE);
                break;
            case 3:
                gv.setDrawWidth(ProjectConstants.WIDTH_EXTRA_WIDE);
                break;
        }
    }

    /**
     * Saves the current bitmap of the GraphicsView into the picture gallery
     */
    public void showSaveModal() {
        View textEntryView = getLayoutInflater().inflate(R.layout.filename_layout, findViewById(android.R.id.content), false);

        AlertDialog.Builder fileNameInput = new AlertDialog.Builder(this);
        fileNameInput.setTitle(getString(R.string.save));
        fileNameInput.setView(textEntryView);
        EditText fileNameEdit = textEntryView.findViewById(R.id.fileName);

        //Checks if the file has already been saved and puts its filename into the EditText
        if (fileName != null)
            fileNameEdit.setText(fileName);

        fileNameInput.setPositiveButton(getString(R.string.save), (paramDialogInterface, paramInt) -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);
            } else {
                fileName = fileNameEdit.getText().toString();
                save();
            }
        });
        fileNameInput.setNegativeButton(getString(R.string.cancel), (paramDialogInterface, paramInt) -> {
        });
        fileNameInput.show();
    }

    /**
     * Scales and sets the background image of the GraphicsView
     */
    public void setBgImage(Bitmap bgBitmap) {
        Bitmap scaledBitmap;
        int width = bgBitmap.getWidth();
        int height = bgBitmap.getHeight();

        //Checks if the image is portrait or landscape
        if (width > height) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            scaledBitmap = Bitmap.createScaledBitmap(bgBitmap, gv.getHeight(), gv.getWidth(), true);
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, gv.getHeight(), gv.getWidth(), matrix, true);
        } else {
            scaledBitmap = Bitmap.createScaledBitmap(bgBitmap, gv.getWidth(), gv.getHeight(), true);
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, gv.getWidth(), gv.getHeight());
        }
        gv.setBgImage(scaledBitmap);
        gv.invalidate();
        bgBitmap.recycle();
        scaledBitmap.recycle();
    }

    /**
     * Clears the GraphicsView and loads default settings
     */
    public void newDraw() {
        //Checks if the GraphicsView is saved and prompts if it isn't
        if (!gv.isSaved()) {
            androidx.appcompat.app.AlertDialog.Builder clearDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
            clearDialog.setMessage(getString(R.string.newMessege));
            clearDialog.setPositiveButton(getString(R.string.btnSave), (paramDialogInterface, paramInt) -> showSaveModal());

            clearDialog.setNegativeButton(getString(R.string.noSave), (paramDialogInterface, paramInt) -> {
                gv.clear();
                paintDefault();
            });
            clearDialog.setNeutralButton(getString(R.string.cancel), (dialog, which) -> {

            });

            clearDialog.show();
        } else {
            fileName = null;
            gv.clear();
            paintDefault();
        }
    }

    /**
     * Shows the image import sources to the user
     */
    public void showImportMenu() {
        String[] items = getResources().getStringArray(R.array.selectSource);
        AlertDialog.Builder fileNameInput = new AlertDialog.Builder(this);
        fileNameInput.setTitle(getString(R.string.selectSource));
        fileNameInput.setItems(items, (dialog, which) -> {
            Intent intent;
            switch (which) {
                case 0:
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                    } else {
                        intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivityForResult(intent, REQUEST_SOURCE_CAMERA);
                    }
                    break;
                case 1:
                    intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.selectViewer)), REQUEST_SOURCE_GALLERY);
                    break;
            }
        });
        fileNameInput.show();
    }

    /**
     * Open width upload activity
     */
    public void upload() {
        Bitmap bitmap = Bitmap.createBitmap(gv.getBitmap());
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra("bitmap", bs.toByteArray());
        bitmap.recycle();
        startActivityForResult(intent, ProjectConstants.REQUEST_UPLOAD);
    }

    /**
     * Set the sensors management
     */
    public void setSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent;
                intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, REQUEST_SOURCE_CAMERA);

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                save();
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void save() {

        //Checks if the filename entered is empty or starts with a space
        if ((fileName.equals("")) || (fileName.charAt(0) == ' ')) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.fileNameEmpty), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(folder);
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            FileOutputStream localFileOutputStream = null;
            File file = new File(myDir, fileName + ".jpg");
            try {
                Bitmap bitmap;
                localFileOutputStream = new FileOutputStream(file);
                bitmap = gv.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                bitmap.recycle();
                Toast toast = Toast.makeText(getApplicationContext(), String.format(getString(R.string.fileNameSaved), fileName), Toast.LENGTH_SHORT);
                toast.show();
                gv.setSaved(true);
                gv.setUndoState(false);

            } catch (FileNotFoundException e) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.saveError), Toast.LENGTH_SHORT);
                toast.show();
                e.printStackTrace();
            } finally {
                if (localFileOutputStream != null)
                    try {
                        localFileOutputStream.flush();
                        localFileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
