package fr.visufo.titouan.tripodoverlay;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        checkDrawOverlayPermission();

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if ( myBluetooth==null ) {
            Toast.makeText(getApplicationContext(), "Le Bluetooth n'est pas disponible sur cet appareil", Toast.LENGTH_LONG).show();
            //finish();
        } else if ( !myBluetooth.isEnabled() ) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
            if (!Settings.canDrawOverlays(this)) {
                checkDrawOverlayPermission();
            }
        }









        RelativeLayout button3 = (RelativeLayout ) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchJoystick();
                finish();
            }
        });
        RelativeLayout button1 = (RelativeLayout ) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        RelativeLayout button2 = (RelativeLayout ) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), WifiOverlay.class);
                startActivity(intent);
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

                Intent intent = new Intent(this, JoystickActivity.class);
                startActivity(intent);

            }
            else {

                //Si non, faire apparaître la fenêtre pour accepter la permission.
                checkDrawOverlayPermission();
            }
        }

    }







}
