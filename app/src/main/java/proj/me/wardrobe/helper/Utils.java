package proj.me.wardrobe.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import proj.me.wardrobe.exceptions.BitmapException;
import proj.me.wardrobe.frag.bean.PagerBean;

/**
 * Created by deepak on 8/11/16.
 */
public class Utils {
    public static final String NOTIFICATION_ACTION = "wardrobe.action.NOTIFICATION";
    public static final int REQUEST_CODE = 1;
    public static int REQUIRED_WIDTH;
    public static int REQUIRED_HEIGHT;

    private static final String TAG = "Wardrobe";

    public static int getVersionColor(Context context, int colorId){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            return context.getResources().getColor(colorId, context.getTheme());
        else return context.getResources().getColor(colorId);
    }

    public static Drawable getVersionDrawable(Context context, int drawableId){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            return context.getResources().getDrawable(drawableId, context.getTheme());
        else return context.getResources().getDrawable(drawableId);
    }

    public static void unbindDrawables(View view, boolean unbindItself, boolean shouldRecycleBitmaps){
        if(view == null) return;
        if(unbindItself && view.getBackground() != null) view.getBackground().setCallback(null);
        if(view instanceof ImageView && shouldRecycleBitmaps) {
            ImageView imageView = ((ImageView)view);
            if(imageView.getBackground() != null) imageView.getBackground().setCallback(null);
            Drawable drawable = imageView.getDrawable();
            if(drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                if(bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
                bitmap = null;
            }else if(drawable instanceof LayerDrawable){
                Bitmap bitmap = ((BitmapDrawable)((LayerDrawable)drawable).getDrawable(0)).getBitmap();
                if(bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
                bitmap = null;
            }
            imageView.setImageBitmap(null);
            imageView.setImageDrawable(null);
            imageView.setImageResource(0);
        }

        if(view instanceof ViewGroup){
            for(int i = 0;i<((ViewGroup)view).getChildCount();i++) unbindDrawables(((ViewGroup)view).getChildAt(i), true, shouldRecycleBitmaps);
            try{
                ((ViewGroup)view).removeAllViews();
            }catch(UnsupportedOperationException e){}
            if(unbindItself) view.setBackgroundResource(0);
        }
    }

    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
    public static int spToPx(float sp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static void logMessage(String message){
        Log.i(TAG, message);
    }
    public static void logVerbose(String message){
        Log.v(TAG, message);
    }
    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static String formatMessage(String message, Object ... values){
        return String.format(message, values);
    }

    private static int getMixedColor(int ... colors){
        //for 1
        //return same color
        //for 2
        //add colors then right shift them with two to find average
        //for 3
        //add all then right shift by 1 then multiply by 2/3 by shift/add 101010...
        //for 4 add them then right shift 2
        //for 5 add them then divide by 10 then multiply by 2
        //for 6 add them all then divide by 3 then divide by 2
        int finalColor = 0;

        switch(colors.length){
            case 1:
                return colors[0];
            case 2:
                return (colors[0] + colors[1]) >> 1;
            case 3:
                int number = colors[0] + colors[1] + colors[2];
                int resultInt = number >> 1;
                int v = number;int n = 1;

                //calculating leading zeros
                if(v >>> 16 == 0){ n+=16; v <<= 16;}
                if(v >>> 24 == 0){ n+=8; v <<= 8; }
                if(v >>> 28 == 0){ n+=4; v <<= 4; }
                if(v >>> 30 == 0){ n+=2; v <<= 2; }
                n-=v >>> 31;

                int bitLength = 32 - n;
                finalColor = 0;
                int i = 1;
                while(bitLength > 0){
                    //shifting to left as 1, 3, 5.....
                    finalColor += resultInt << i;
                    bitLength--;
                    i+=2;
                }

                bitLength = 32 - n;
                int bits = bitLength * 2;
                int val = 1 << (bits - 1);

                if((val & finalColor) != 0){ finalColor = finalColor >> bits;  return finalColor + 1;}
                else return finalColor >> bits;
            case 4:
                return (colors[0] + colors[1] + colors[2] + colors[3]) >> 2;
            case 5:
                finalColor = colors[0] + colors[1] + colors[2] + colors[3] + colors[4];
                int q = (finalColor >> 1) + (finalColor >> 2);
                q = q + (q >> 4);
                q = q + (q >> 8);
                q = q + (q >> 16);
                q = q >> 3;
                int r = finalColor - (((q << 2) + q) << 1);
                return (q + ((r > 9) ? 1 : 0)) << 1;
            case 6:
                finalColor = getMixedColor(colors[0]+colors[1]+colors[2]+colors[3], colors[4], colors[5]);
                return finalColor >> 1;
        }
        return finalColor;
    }

    public static int getMixedArgbColor(int ... colors) throws IllegalArgumentException{
        if(colors == null) throw new IllegalArgumentException("can not mix empty colors");
        if(colors.length > 6) throw new IllegalArgumentException("only support maximum six color mixing, found : "+colors.length);
        int mixedAlpha = 0;
        int mixedRed = 0;
        int mixedGreen = 0;
        int mixedBlue = 0;
        switch(colors.length){
            case 1:
                mixedAlpha = getMixedColor(colors[0] >>> 24);
                mixedRed = getMixedColor(colors[0] >> 16 & 0xFF);
                mixedGreen = getMixedColor(colors[0] >> 8 & 0xFF);
                mixedBlue = getMixedColor(colors[0] & 0xFF);
                break;
            case 2:
                mixedAlpha = getMixedColor(colors[0] >>> 24, colors[1] >>> 24);
                mixedRed = getMixedColor(colors[0] >> 16 & 0xFF, colors[1] >> 16 & 0xFF);
                mixedGreen = getMixedColor(colors[0] >> 8 & 0xFF, colors[1] >> 8 & 0xFF);
                mixedBlue = getMixedColor(colors[0] & 0xFF, colors[1] & 0xFF);
                break;
            case 3:
                mixedAlpha = getMixedColor(colors[0] >>> 24, colors[1] >>> 24, colors[2] >>> 24);
                mixedRed = getMixedColor(colors[0] >> 16 & 0xFF, colors[1] >> 16 & 0xFF, colors[2] >> 16 & 0xFF);
                mixedGreen = getMixedColor(colors[0] >> 8 & 0xFF, colors[1] >> 8 & 0xFF, colors[2] >> 8 & 0xFF);
                mixedBlue = getMixedColor(colors[0] & 0xFF, colors[1] & 0xFF, colors[2] & 0xFF);
                break;
            case 4:
                mixedAlpha = getMixedColor(colors[0] >>> 24, colors[1] >>> 24, colors[2] >>> 24,
                        colors[3] >>> 24);
                mixedRed = getMixedColor(colors[0] >> 16 & 0xFF, colors[1] >> 16 & 0xFF, colors[2] >> 16 & 0xFF,
                        colors[3] >> 16 & 0xFF);
                mixedGreen = getMixedColor(colors[0] >> 8 & 0xFF, colors[1] >> 8 & 0xFF, colors[2] >> 8 & 0xFF,
                        colors[3] >> 8 & 0xFF);
                mixedBlue = getMixedColor(colors[0] & 0xFF, colors[1] & 0xFF, colors[2] & 0xFF,
                        colors[3] & 0xFF);
                break;
            case 5:
                mixedAlpha = getMixedColor(colors[0] >>> 24, colors[1] >>> 24, colors[2] >>> 24,
                        colors[3] >>> 24, colors[4] >>> 24);
                mixedRed = getMixedColor(colors[0] >> 16 & 0xFF, colors[1] >> 16 & 0xFF, colors[2] >> 16 & 0xFF,
                        colors[3] >> 16 & 0xFF, colors[4] >> 16 & 0xFF);
                mixedGreen = getMixedColor(colors[0] >> 8 & 0xFF, colors[1] >> 8 & 0xFF, colors[2] >> 8 & 0xFF,
                        colors[3] >> 8 & 0xFF, colors[4] >> 8 & 0xFF);
                mixedBlue = getMixedColor(colors[0] & 0xFF, colors[1] & 0xFF, colors[2] & 0xFF,
                        colors[3] & 0xFF, colors[4] & 0xFF);
                break;
            case 6:
                mixedAlpha = getMixedColor(colors[0] >>> 24, colors[1] >>> 24, colors[2] >>> 24,
                        colors[3] >>> 24, colors[4] >>> 24, colors[5] >>> 24);
                mixedRed = getMixedColor(colors[0] >> 16 & 0xFF, colors[1] >> 16 & 0xFF, colors[2] >> 16 & 0xFF,
                        colors[3] >> 16 & 0xFF, colors[4] >> 16 & 0xFF, colors[5] >> 16 & 0xFF);
                mixedGreen = getMixedColor(colors[0] >> 8 & 0xFF, colors[1] >> 8 & 0xFF, colors[2] >> 8 & 0xFF,
                        colors[3] >> 8 & 0xFF, colors[4] >> 8 & 0xFF, colors[5] >> 8 & 0xFF);
                mixedBlue = getMixedColor(colors[0] & 0xFF, colors[1] & 0xFF, colors[2] & 0xFF,
                        colors[3] & 0xFF, colors[4] & 0xFF, colors[5] & 0xFF);
                break;
        }
        return Color.argb(mixedAlpha, mixedRed, mixedGreen, mixedBlue);
    }


    public static int getRandom(int[] numbers, Random twoRandom){
        if(numbers.length < 2) return numbers[0];
        else if(numbers.length == 2) return numbers[twoRandom.nextInt(numbers.length)];
        return numbers[new Random().nextInt(numbers.length)];
    }

    public static int[] getLeastTwoNumbers(int[] favoriteColors){
        //n^2
        int lastDifference = Integer.MAX_VALUE;
        int[] numbers = new int[2];
        for(int i = 0;i< favoriteColors.length;i++){
            if(i == favoriteColors.length - 1) break;
            for(int j = i+1;j<favoriteColors.length;j++){
                if(favoriteColors[i] == favoriteColors[j]){
                    numbers[0] = favoriteColors[i];
                    numbers[1] = favoriteColors[j];
                    return numbers;
                }
                int diff = Math.abs(Math.abs(favoriteColors[i]) - Math.abs(favoriteColors[j]));
                if(diff < lastDifference){
                    lastDifference = diff;
                    numbers[0] = favoriteColors[i];
                    numbers[1] = favoriteColors[j];
                }
            }
        }

        return numbers;
    }

    public static int getClosestNumber(int number, int[] numbers){
        //n
        int lastDifference = Integer.MAX_VALUE;
        int lastNumber = -1;
        for(int num : numbers){
            if(number == num) return num;
            int diff = Math.abs(Math.abs(number) - Math.abs(num));
            if(diff < lastDifference){
                lastDifference = diff;
                lastNumber = num;
            }
        }
        return lastNumber;
    }
}
