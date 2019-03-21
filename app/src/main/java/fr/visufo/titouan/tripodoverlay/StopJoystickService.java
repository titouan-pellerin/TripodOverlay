package fr.visufo.titouan.tripodoverlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopJoystickService extends BroadcastReceiver {
    public static final int REQUEST_CODE = 333;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, JoystickService.class);
        context.stopService(service);

        JoystickActivity mActivity = new JoystickActivity();
        mActivity.killApp();


    }
}
