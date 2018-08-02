package lakmalz.git.colouringimagefloodfill;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Show captured image and allow user to delete or save it.
 *
 * @author Dhaval Patel
 * @version 1.0
 * @since 02 August 2018
 */
public class ScreenshotDialog extends DialogFragment {

    private static final String FILE_PATH = "extras.file_path";

    @BindView(R.id.img_screenshot)
    AppCompatImageView imgScreenshot;

    private String mFilePath;

    public static ScreenshotDialog newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(FILE_PATH, path);

        ScreenshotDialog fragment = new ScreenshotDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_screenshot, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    private void initUI() {
        mFilePath = getArguments().getString(FILE_PATH);
        Picasso.get().load(new File(mFilePath)).into(imgScreenshot);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void show(FragmentManager manager) {
        show(manager, ScreenshotDialog.class.getSimpleName());
    }

    @OnClick({R.id.action_delete, R.id.action_save})
    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.action_delete:
                new File(mFilePath).delete();
                break;
            case R.id.action_save:
                Toast.makeText(getActivity(), "Image saved!", Toast.LENGTH_SHORT).show();
                break;
        }
        dismiss();
    }
}
