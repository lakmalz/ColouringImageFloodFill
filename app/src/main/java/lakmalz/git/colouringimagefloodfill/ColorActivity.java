package lakmalz.git.colouringimagefloodfill;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import lakmalz.git.colouringimagefloodfill.util.BitmapUtil;

/**
 * Created by Lakmal Weerasekara
 * Modified by Dhaval Patel
 *
 * @version 2.0
 * @since 29 August 2016
 */
public class ColorActivity extends BaseActivity {

    private static final String TAG = "ColorActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivImage)
    TouchImageView ivImagePaint;
    @BindView(R.id.img_undo)
    AppCompatImageView imgUndo;
    @BindView(R.id.img_redo)
    AppCompatImageView imgRedo;

    private ProgressDialog mProgressDialog;
    private int mSelectedColor = Color.RED;
    private Bitmap currentBitmap;
    private Bitmap originalBitmap;
    private int currentX, currentY;
    private int tolerance = 80;    //190

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar();
        init();

        ivImagePaint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        currentX = (int) event.getX();
                        currentY = (int) event.getY();

                        float devVsImgRatio = ivImagePaint.drawableWidthForDeviceRelated / originalBitmap.getWidth();
                        PointF point = ivImagePaint.transformCoordTouchToBitmap(event.getX(), event.getY(), true);
                        currentX = (int) (point.x / devVsImgRatio);
                        currentY = (int) (point.y / devVsImgRatio);

                        int pixel = currentBitmap.getPixel(currentX, currentY);

                        //then do what you want with the pixel data, e.g
                        int redValue = Color.red(pixel);
                        int blueValue = Color.blue(pixel);
                        int greenValue = Color.green(pixel);

                        int rgb = redValue;
                        rgb = (rgb << 8) + greenValue;
                        rgb = (rgb << 8) + blueValue;

                        String strColor = String.format("#%06X", 0xFFFFFF & rgb);
                        Log.e("Color", "S:" + mSelectedColor + ", C:" + strColor);

                        if ((redValue < 65 && blueValue < 65 && greenValue < 65)) {
                            return true;
                        }

                        Bitmap bitmap = currentBitmap;
                        //floodFill(bitmap, currentX, currentY, mSelectedColor, Color.BLACK, 50);
                        floodFill(bitmap, currentX, currentY, mSelectedColor, Color.BLACK, tolerance);
                        ivImagePaint.setImageBitmap(bitmap);

                        clearUndo();
                        setRedoUndoView();

                        break;
                }
                return true;
            }
        });

        clear();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBarColor(R.color.grey_500);
    }

    private void init() {
        setColorFilter(imgUndo, R.color.grey_400);
        setColorFilter(imgRedo, R.color.grey_400);

        String imageUrl = getIntent().getStringExtra(ImagePickerActivity.EXTRA_IMAGE);
        originalBitmap = BitmapUtil.getBitmapFromAsset(mActivity, imageUrl);
        currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);

        ivImagePaint.setMaxZoom(15);
        ivImagePaint.setImageBitmap(originalBitmap);

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCancelable(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setColorFilter(AppCompatImageView imageView, int color) {
        color = ContextCompat.getColor(mActivity, color);
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @OnClick(R.id.img_pick_color)
    public void onClickTest() {
        mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);
        int[] mColors = getResources().getIntArray(R.array.color_rainbow);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors, mSelectedColor, 5, ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;
            }
        });

        dialog.show(getFragmentManager(), "color_dialog_test");
    }

    @OnClick(R.id.img_undo)
    public void onClickUndo(View view) {
        if (getCount(1)>0) {
            Bitmap bitmap = currentBitmap;
            undo(bitmap, Color.WHITE, Color.BLACK, tolerance);
            ivImagePaint.setImageBitmap(bitmap);
        }

        setRedoUndoView();
    }

    @OnClick(R.id.img_redo)
    public void onClickRedo(View view) {
        if (getCount(2)>0) {
            Bitmap bitmap = currentBitmap;
            redo(bitmap, Color.BLACK, tolerance);
            ivImagePaint.setImageBitmap(bitmap);
        }

        setRedoUndoView();
    }

    private void setRedoUndoView(){
        if (getCount(1)<=0) {
            setColorFilter(imgUndo, R.color.grey_400);
        }else{
            setColorFilter(imgUndo, R.color.cyan_500);
        }

        if (getCount(2)<=0) {
            setColorFilter(imgRedo, R.color.grey_400);
        }else{
            setColorFilter(imgRedo, R.color.cyan_500);
        }

    }

    @OnClick(R.id.img_save)
    public void onClickSave() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        new AsyncTask<Void, Void, File>(){
                            @Override
                            protected void onPreExecute() {
                                mProgressDialog.setMessage("Capturing Screenshot...");
                                mProgressDialog.show();
                                super.onPreExecute();
                            }

                            @Override
                            protected File doInBackground(Void[] objects) {
                                return BitmapUtil.saveBitmapAsImage(currentBitmap);
                            }

                            @Override
                            protected void onPostExecute(File file) {
                                super.onPostExecute(file);
                                mProgressDialog.cancel();
                                if(file!=null){
                                    //Toast.makeText(context, "Image saved!", Toast.LENGTH_SHORT).show();
                                    ScreenshotDialog.newInstance(file.getAbsolutePath()).show(getSupportFragmentManager());
                                }
                            }
                        }.execute();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    }
                })
                .setDeniedMessage("If you reject this permission, you can not save image.\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onDestroy() {
        clear();
        super.onDestroy();
    }

    static {
        System.loadLibrary("jnibitmap");
    }

    public native void floodFill(Bitmap bitmap,
                                 int x,
                                 int y,
                                 int fillColor,
                                 int targetColor,
                                 int tolerance);

    public native void redo(Bitmap bitmap,
                            int targetColor,
                            int tolerance);

    public native void undo(Bitmap bitmap,
                            int fillColor,
                            int targetColor,
                            int tolerance);

    public native void clearUndo();

    public native void clear();

    /*
     * Action Value
     * 1 = redo
     * 2 = undo
     */
    public native int getCount(int action);

}
