package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseEntryImageThumbnailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Bitmap> imageThumbnailBitmaps;
    private OnAddImageClickListener onAddImageClickListener;

    public HouseEntryImageThumbnailsRecyclerAdapter(Context context, ArrayList<Bitmap> imageThumbnailBitmaps, OnAddImageClickListener onAddImageClickListener) {
        this.context = context;
        this.imageThumbnailBitmaps = imageThumbnailBitmaps;
        this.onAddImageClickListener = onAddImageClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View view = inflater.inflate(R.layout.house_images_entry_add_button, parent, false);
                viewHolder = new AddImageButtonViewHolder(view);
                break;
            default:

                view = inflater.inflate(R.layout.house_images_entry_thumbnails_item, parent, false);
                viewHolder = new ImageThumbnailViewHolder(view);

                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                AddImageButtonViewHolder addImageButtonViewHolder = (AddImageButtonViewHolder) viewHolder;
                break;
            default:
                ImageThumbnailViewHolder imageThumbnailViewHolder = (ImageThumbnailViewHolder) viewHolder;
                imageThumbnailViewHolder.thumbnailImageView.setImageBitmap(imageThumbnailBitmaps.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (imageThumbnailBitmaps != null && imageThumbnailBitmaps.size() > 0) {
            return imageThumbnailBitmaps.size() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (getItemCount() - 1))
            return 0;
        else
            return 1;
    }


    public class AddImageButtonViewHolder extends RecyclerView.ViewHolder {
        public ImageButton addImageButton;

        public AddImageButtonViewHolder(View view) {
            super(view);

            addImageButton = (ImageButton)view.findViewById(R.id.house_images_entry_addButton);
            addImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onAddImageClickListener != null) {
                        onAddImageClickListener.onAddImageClick();
                    }
                }
            });
        }
    }

    public class ImageThumbnailViewHolder extends RecyclerView.ViewHolder {

//        public Button removeThumbnailButton;
        private ImageView thumbnailImageView;

        public ImageThumbnailViewHolder(View view) {
            super(view);

//            removeThumbnailButton = (Button)view.findViewById(R.id.city_housesCaption);
            thumbnailImageView = (ImageView)view.findViewById(R.id.house_images_entry_thumbnailImage);
        }
    }

    public interface OnAddImageClickListener {
        public void onAddImageClick();
    }
}
