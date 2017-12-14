package lakmalz.git.colouringimagefloodfill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import butterknife.BindView;
import butterknife.OnClick;

public class ColorActivity extends BaseActivity {
    private static final String TAG = "ColorActivity";

    @BindView(R.id.ivImage)
    TouchImageView ivImagePaint;

    private int mSelectedColor = Color.RED;
    private Bitmap currentBitmap;
    private Bitmap originalBitmap;
    private int currentX, currentY;
    private Bitmap oldBitmap;
    private ColourFill mColourFill;
    private int tolarance = 190;
/*

    public native void constructor(*/
/*Bitmap bitmap*//*
);

    public static native void floodFill(Bitmap bitmap, int x, int y, int fillColor, int targetColor, int tolerance);

    public static native void redo(Bitmap bitmap, int fillColor, int targetColor, int tolerance);
*/

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        Bitmap bitmap = currentBitmap;
                        //floodFill(bitmap, currentX, currentY, mSelectedColor, Color.BLACK, 50);
                        mColourFill.floodFill(bitmap, currentX, currentY, mSelectedColor, Color.BLACK, tolarance);
                        ivImagePaint.setImageBitmap(bitmap);
                        break;
                }
                return true;
            }
        });
    }

    private void init() {
        mColourFill = new ColourFill();
        ivImagePaint.setMaxZoom(15);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.one, o);
        ivImagePaint.setImageBitmap(originalBitmap);
        currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        oldBitmap = originalBitmap;
    }


    @OnClick(R.id.btn_select_color)
    public void onClickTest() {
        mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);
        int[] mColors = getResources().getIntArray(R.array.default_rainbow);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors,
                mSelectedColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;
            }

        });

        dialog.show(getFragmentManager(), "color_dialog_test");
    }

    @OnClick(R.id.btn_redo)
    public void onClickRedo(View view) {
        Bitmap bitmapo = currentBitmap;
        //redo(bitmapo,Color.WHITE ,Color.BLACK, 50);
        mColourFill.redo(bitmapo, Color.WHITE, Color.BLACK, 0);
        ivImagePaint.setImageBitmap(bitmapo);
    }
/*
    static {
        System.loadLibrary("jnibitmap");
    }*/

}
