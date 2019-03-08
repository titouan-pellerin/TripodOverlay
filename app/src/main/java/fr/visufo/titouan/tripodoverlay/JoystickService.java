package fr.visufo.titouan.tripodoverlay;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickService extends Service implements View.OnTouchListener {

    String address = "98:D3:31:20:9B:30";
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = JoystickService.class.getSimpleName();

    private WindowManager windowManager;
    private Context mContext;
    private View floatyView;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {


        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mContext = this;

        addOverlayView();
        new ConnectBT().execute();

    }

    private void addOverlayView() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        /*int width = (int) (metrics.widthPixels *0.25f);
        int height = (int) (metrics.heightPixels * 0.25f);*/
        //Beaucoup de problèmes rencontrés dans cette partie
        final WindowManager.LayoutParams params=
                new WindowManager.LayoutParams(
                //width,
                // height,

                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                (Build.VERSION.SDK_INT <= 25) ? WindowManager.LayoutParams.TYPE_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                //WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, // Not displaying keyboard on bg activity's EditText
                //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //Not work with EditText on keyboard
                PixelFormat.TRANSLUCENT);


        params.gravity = Gravity.TOP | Gravity.LEFT;
        //params.x = 0;
        //params.y = 100;
        /*final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                        0,
                        PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = 0;
        params.y = 0;*/

        FrameLayout interceptorLayout = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                        Log.v(TAG, "Appui sur le bouton retour");
                        onDestroy();
                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                       return true;
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event);
            }
        };


        floatyView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_main, interceptorLayout);

        floatyView.setOnTouchListener(this);
        final JoystickView joystick = (JoystickView) floatyView.findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (strength > 15 && angle >= 0 && angle <= 180) {
                    sendSignal(strength);
                    //Log.v(TAG, ""+strength);
                } else if (strength > 15 && angle > 180 && angle <= 360) {
                    int strength2 = strength+100;
                    sendSignal(strength2);
                    Log.v(TAG, ""+ strength2);
                    //Log.v(TAG, "" + strength*-1);
                } else if (strength <=15) {
                    /*sendSignal(0);
                    sendSignal(0);
                    sendSignal(0);*/
                    sendSignal(0);
                    //Log.v(TAG, "0");

                }
                //Log.v(TAG,"Force: "+strength);
            }
        });
        /*Button btn = (Button) floatyView.findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Action après avoir appuyé sur le bouton", Toast.LENGTH_SHORT).show();

                //onDestroy();
            }
        });*/

        windowManager.addView(floatyView, params);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (floatyView != null) {
            windowManager.removeView(floatyView);
            //System.exit(0);


            floatyView = null;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        Log.v(TAG, "onTouch...");

       //onDestroy();

        return true;
    }



    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
/*
        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(JoystickService.this, "Connecting...", "Please Wait!!!");
        }*/

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice btDevice = myBluetooth.getRemoteDevice(address);
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("La connexion a échouée");
                //finish();
            } else {
                msg("Connecté au trépied");
                isBtConnected = true;
            }

            //progress.dismiss();
        }
    }
    private void sendSignal ( int number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number/*.toString().getBytes()*/);
            } catch (IOException e) {
                msg("Erreur de transmission");

            }
        }
    }
    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }




}
