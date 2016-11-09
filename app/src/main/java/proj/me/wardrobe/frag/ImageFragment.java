package proj.me.wardrobe.frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import proj.me.wardrobe.R;
import proj.me.wardrobe.helper.BitmapHandlerTask;
import proj.me.wardrobe.helper.Utils;

/**
 * Created by deepak on 8/11/16.
 */
public class ImageFragment extends Fragment implements BitmapCallback{

    boolean isRequestCancelled;
    BitmapHandlerTask bitmapHandlerTask;
    ImageView imageView;

    public static ImageFragment newInstance(boolean isTop, String imagePath){
        Bundle bundle = new Bundle();
        bundle.putString("image_path", imagePath);
        bundle.putBoolean("is_top", isTop);
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        imageView = (ImageView) view.findViewById(R.id.cloth);

        LruFragment lruFragment = (LruFragment) getParentFragment();

        isRequestCancelled = false;

        Bitmap imageBitmap = lruFragment.getImageBitmap(bundle.getString("image_path"));
        if(imageBitmap != null){
            imageView.setImageBitmap(imageBitmap);
            return;
        }
        Utils.logMessage("memory reclaimed");

        //will only be executed when cache cleared the image
        imageView.setImageResource(bundle.getBoolean("is_top") ? R.drawable.ic_top : R.drawable.ic_bottom);

        bitmapHandlerTask = new BitmapHandlerTask(this);
        bitmapHandlerTask.execute(bundle.getString("image_path"));
    }

    @Override
    public void onStop() {
        super.onStop();
        //can be done on page changed
        isRequestCancelled = true;
        if(bitmapHandlerTask != null && !bitmapHandlerTask.isCancelled()) bitmapHandlerTask.cancel(isRequestCancelled);
    }

    @Override
    public void bitmapGenerated(Bitmap bitmap, String path) {
        if(getParentFragment() == null || !isAdded()) return;
        LruFragment lruFragment = (LruFragment) getParentFragment();
        if(!isRequestCancelled) imageView.setImageBitmap(bitmap);
        lruFragment.storeImageBitmap(path, bitmap);
    }

    @Override
    public Context getViewContext() {
        return imageView.getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.unbindDrawables(imageView, true, false);
        bitmapHandlerTask = null;
        imageView = null;
    }
}
