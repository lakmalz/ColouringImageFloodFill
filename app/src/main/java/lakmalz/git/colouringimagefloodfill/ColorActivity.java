package lakmalz.git.colouringimagefloodfill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
    @BindView(R.id.ivImage)
    TouchImageView ivImage;

    private int mSelectedColor = Color.RED;
    private Bitmap currentBitmap;
    private Bitmap originalBitmap;
    private int currentX, currentY, currentTolerance = 40;

    static {
        System.loadLibrary("jnibitmap");
    }

    public native void constructor();

    public static native void floodFill(Bitmap bitmap, int x, int y, int fillColor, int targetColor, int tolerance);

    public static native void redo();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.four, o);
        ivImage.setImageBitmap(originalBitmap);
        currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        constructor();
        ivImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentX = (int) event.getX();
                        currentY = (int) event.getY();

                        PointF point = ivImage.transformCoordTouchToBitmap(event.getX(), event.getY(), true);
                        currentX = (int)point.x/4;
                        currentY = (int)point.y/4;

                        floodFill(currentBitmap, currentX, currentY, mSelectedColor, Color.BLACK, 50);
                        ivImage.setImageBitmap(currentBitmap);
                        break;
                }
                return true;
            }
        });
    }

    /*final float[] getPointerCoords(ImageView view, MotionEvent e)
    {
        final int index = e.getActionIndex();
        float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        view.getImageMatrix().invert(matrix);
        matrix.postTranslate(view.getScrollX(), view.getScrollY());
        matrix.mapPoints(coords);

        return coords;
    }*/

    @OnClick(R.id.btn_select_color)
    public void onClickTest() {
        //Toast.makeText(this, MainActivity.getTestString(), Toast.LENGTH_LONG).show();
        mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);
        int[] mColors = getResources().getIntArray(R.array.default_rainbow);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors,
                mSelectedColor,
                5, // Number of columns
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;
            }

        });

        dialog.show(getFragmentManager(), "color_dialog_test");
    }

}
