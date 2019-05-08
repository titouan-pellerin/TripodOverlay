package fr.visufo.titouan.tripodoverlay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class JoystickActivity extends AppCompatActivity {


    private BluetoothAdapter myBluetooth = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
        /*if (Settings.canDrawOverlays(this)) {

            //Lancer le service directement au lancement de l'application si l'utilisateur à déjà autorisé la superposition d'écran.
            launchMainService();
        }
        else {

            //Si non, faire apparaître la fenêtre pour accepter la permission.
            checkDrawOverlayPermission();
        }*/

        createNotificationChannel();
        createNotification();
        launchMainService();



    }



    //Fonction pour démarrer le service
    private void launchMainService() {

        Intent svc = new Intent(this, JoystickService.class);

        startService(svc);
        /*Intent launchIntent = getPackageManager().getLaunchIntentForPackage("info.qdd");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }*/
        finish();
    }
    private void shutMainService() {

        Intent svc = new Intent(this, JoystickService.class);

        stopService(svc);
        finish();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notif", name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void createNotification(){
        // Create an explicit intent for an Activity in your app
        Intent intentHide = new Intent(this, StopJoystickService.class);
        PendingIntent hide = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intentHide, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notif")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TripoedOverlay")
                .setContentText("Appuyez pour fermer l'application")
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(hide)
                .setOngoing(true)
                .setVibrate(new long[]{0L})
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
    public void killApp(){
        shutMainService();
        onDestroy();
    }



}
