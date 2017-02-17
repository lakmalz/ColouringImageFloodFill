package lakmalz.git.colouringimagefloodfill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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
                        //--------------------------

                        //--------------------------

                        BitmapDrawable drawable = (BitmapDrawable) ((TouchImageView) v).getDrawable();
                        Rect imageBounds = new Rect();
                        ivImage.getDrawingRect(imageBounds);
                        // original height and width of the bitmap
                        int intrinsicHeight = drawable.getIntrinsicHeight();
                        int intrinsicWidth = drawable.getIntrinsicWidth();

                        // height and width of the visible (scaled) image
                        int scaledHeight = imageBounds.height();
                        int scaledWidth = imageBounds.width();

                        // Find the ratio of the original image to the scaled image
                        // Should normally be equal unless a disproportionate scaling
                        // (e.g. fitXY) is used.
                        float heightRatio = (float) intrinsicHeight / scaledHeight;
                        float widthRatio = (float) intrinsicWidth / scaledWidth;

                        // do whatever magic to get your touch point
                        // MotionEvent event;

                        // get the distance from the left and top of the image bounds
                        float scaledImageOffsetX = event.getX() - imageBounds.left;
                        float scaledImageOffsetY = event.getY() - imageBounds.top;

                        // scale these distances according to the ratio of your scaling
                        // For example, if the original image is 1.5x the size of the scaled
                        // image, and your offset is (10, 20), your original image offset
                        // values should be (15, 30).
                        int originalImageOffsetX = Math.round(scaledImageOffsetX * widthRatio);
                        int originalImageOffsetY = Math.round(scaledImageOffsetY * heightRatio);

                        //--------------------------
                        Log.e("cxz", "xxx" + currentX + " " + currentY + " " + originalBitmap.getWidth() + " " + originalBitmap.getHeight());
                        currentBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);

                        //-------------
                        int bitW = originalBitmap.getWidth();
                        int bitH = originalBitmap.getHeight();
                        float px = event.getX();
                        float py = event.getY();
                        int h = ivImage.getMeasuredHeight();
                        int w = ivImage.getMeasuredWidth();

                        float pstx = px / w;
                        float psty = py / h;
                        currentX = (int) (bitW * pstx);
                        currentY = (int) (bitH * psty);
                        //---------------
                        /*Matrix m = new Matrix();
                        ivImage.getMatrix().invert(m);
                        //float[] pts = {ivImage.getLas.x, mMapImageView.last.y };
                        float[] pts = {event.getX(), event.getY() };
                        m.mapPoints(pts);
                        //Log.d("Mapped to real image: " + pts[0] + ", " + pts[1]);
                        currentX = (int)pts[0];
                        currentY = (int)pts[1];*/


                        //-------------
                        currentX = (int)TouchImageView.stPointF.x;
                        currentY = (int)TouchImageView.stPointF.y;
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
