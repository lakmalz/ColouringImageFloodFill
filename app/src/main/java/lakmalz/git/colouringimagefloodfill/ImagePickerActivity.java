package lakmalz.git.colouringimagefloodfill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Image Picker Activity
 * <p>
 * Pick Image from asset folder
 *
 * @author Dhaval Patel
 * @version 1.0
 * @since 13 June 2018
 */
public class ImagePickerActivity extends BaseActivity {

    public static final String EXTRA_IMAGE = "extra.image";

    @BindView(R.id.imageRV)
    RecyclerView mImageGridView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_picker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> imagesList = listAssetFiles("images");
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false);
        mImageGridView.setLayoutManager(layoutManager);

        ImageGridAdapter adapter = new ImageGridAdapter(imagesList);
        mImageGridView.setAdapter(adapter);
    }

    /*
     * List Asset files
     */
    private List<String> listAssetFiles(String path) {
        List<String> imageList = new ArrayList<>();
        try {
            String[] list = getAssets().list(path);
            if (list != null && list.length > 0) {
                for (String file : list) {
                    if(file.startsWith("img"))
                        imageList.add(path + "/" + file);
                }
            }

            Collections.sort(imageList);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return imageList;
    }

    /**
     * Adapter class to Pick Image
     */
    public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.MyViewHolder> {

        private List<String> imageList;
        private LayoutInflater mLayoutInflater;
        private ImageGridAdapter(List<String> objects) {
            this.imageList = objects;
            this.mLayoutInflater = LayoutInflater.from(mActivity);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.imageView)
            ImageView imageViewer;

            private MyViewHolder(View itemView) {
                super(itemView);
                //Bind View
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.imageView)
            public void onImageClick(View view){
                String imgPath = (String) view.getTag();
                //Start Color Activity and pass asset image
                Intent intent = new Intent(mActivity, ColorActivity.class);
                intent.putExtra(EXTRA_IMAGE, imgPath);
                startActivity(intent);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.adapter_image_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int listPosition) {
            String image = imageList.get(listPosition);
            holder.imageViewer.setTag(image);
            //Append Asset file path to asset image id
            Picasso.get().load("file:///android_asset/"+image).into(holder.imageViewer);
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

}
