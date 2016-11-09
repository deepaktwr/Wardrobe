package proj.me.wardrobe.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import proj.me.wardrobe.MainActivity;
import proj.me.wardrobe.R;
import proj.me.wardrobe.helper.Utils;

/**
 * Created by deepak on 9/11/16.
 */
public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Want to try new pairs?");
        builder.setContentText("We have new pair of cloths for you!!");
        builder.setSmallIcon(R.mipmap.wardrobe);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.wardrobe));
        builder.setAutoCancel(true);
        builder.setContentInfo("Wardrobe");
        builder.setTicker("Wardrobe");
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
        builder.setContentIntent(PendingIntent.getActivity(context, Utils.REQUEST_CODE + 1, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(Utils.REQUEST_CODE, builder.build());

        //fire next
        Intent nextIntent = new Intent(Utils.NOTIFICATION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Utils.REQUEST_CODE, nextIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 24 * 60 * 60 * 1000, pendingIntent);
    }
}
