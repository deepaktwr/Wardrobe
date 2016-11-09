package proj.me.wardrobe.frag.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by deepak on 8/11/16.
 */
public class PagerBean implements Parcelable{
    private int imageId;
    private String imagePath;
    private int paletteColor;

    public PagerBean(){}

    protected PagerBean(Parcel in) {
        imageId = in.readInt();
        imagePath = in.readString();
        paletteColor = in.readInt();
    }

    public static final Creator<PagerBean> CREATOR = new Creator<PagerBean>() {
        @Override
        public PagerBean createFromParcel(Parcel in) {
            return new PagerBean(in);
        }

        @Override
        public PagerBean[] newArray(int size) {
            return new PagerBean[size];
        }
    };

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPaletteColor() {
        return paletteColor;
    }

    public void setPaletteColor(int paletteColor) {
        this.paletteColor = paletteColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imageId);
        parcel.writeString(imagePath);
        parcel.writeInt(paletteColor);
    }
}
