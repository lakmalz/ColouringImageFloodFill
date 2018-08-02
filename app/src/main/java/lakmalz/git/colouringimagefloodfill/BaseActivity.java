package lakmalz.git.colouringimagefloodfill;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;

/**
 *
 * Created by Lakmal Weerasekara
 * Modified by Dhaval Patel
 *
 * @version 2.0
 * @since 29 August 2016
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        //Bind View
        ButterKnife.bind(this);

        mActivity = this;
    }

    protected abstract int getLayoutId();

    /**
     * Set StatusBar color
     *
     * @param color Color to set as StatusBar
     */
    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(mActivity, color));
        }
    }
}