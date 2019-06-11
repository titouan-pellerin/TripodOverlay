package fr.visufo.titouan.tripodoverlay;

import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Accueil extends AppCompatActivity {

    private BluetoothAdapter myBluetooth = null;
    private boolean bluetoothOn = false;
    private LinearLayout button1;
    private LinearLayout button2;
    private LinearLayout button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        checkDrawOverlayPermission();
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth != null){
            if(myBluetooth.isEnabled()) {
            bluetoothOn = true;
            }
        }
        button1 = (LinearLayout) findViewById(R.id.bluetooth);
        button2 = (LinearLayout) findViewById(R.id.wifi);
        button3 = (LinearLayout) findViewById(R.id.joystick);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if ( myBluetooth==null ) {
                Toast.makeText(getApplicationContext(), "Le Bluetooth n'est pas disponible sur cet appareil", Toast.LENGTH_SHORT).show();
            } else if ( !myBluetooth.isEnabled() ) {
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
                bluetoothOn = true;
                if (!Settings.canDrawOverlays(getApplicationContext())) {
                    checkDrawOverlayPermission();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Le bluetooth est déjà activé", Toast.LENGTH_SHORT).show();
                bluetoothOn = true;
            }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWifiSettings();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bluetoothOn && isWifiConnected()) {
                    launchJoystick();
                    finish();
                }
            }
        });
    }
    //Code de la requette envoyée à Android pour autoriser la superposition d'écran. Utile plus tard.
    public final static int REQUEST_CODE = 10101;
    //Ouvrir la fenêtre d'autorisation de superposition d'écran (paramètres Android).
    public void checkDrawOverlayPermission() {
        //On vérifie que l'utilisateur n'a pas encore accepté la superposition d'écran.
        if (!Settings.canDrawOverlays(this)) {
            //Demander à Android d'ouvrir la fenêtre pour accepter la superposition.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }
    //Fonction Android permettant de vérifier si le code reçu est bien le bon, et donc que la permission a bien été acceptée
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            //Double vérification
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Pas de superposition d'écran sans accepter la permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void launchJoystick(){
        if(myBluetooth.isEnabled()){
            if (Settings.canDrawOverlays(this)) {
                startqDslrDashboard();
                Intent intent = new Intent(this, JoystickActivity.class);
                startActivity(intent);

            }
            else {
                //Si non, faire apparaître la fenêtre pour accepter la permission.
                checkDrawOverlayPermission();
            }
        }
    }
    public void openWifiSettings(){
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity( intent);
    }
    public boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
       return mWifi.isConnected();
    }
    public void startqDslrDashboard(){
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("info.qdd", "info.qdd.DslrDashboard");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Vous devez installer l'application qDslrDashboard",Toast.LENGTH_SHORT).show();
        }
    }
}
