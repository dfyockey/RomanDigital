package net.diffengine.romandigitalclock;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(ACTION_BOOT_COMPLETED)) {
            Intent kickstart = new Intent(context, TimeDisplayWidget.class);
            kickstart.setAction(TimeDisplayWidget.MINUTE_TICK);
            kickstart.setPackage(context.getPackageName());
            context.sendBroadcast(kickstart);
        }
    }
}
