package fr.visufo.titouan.tripodoverlay;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Settings.canDrawOverlays(this)) {

            //Lancer le service directement au lancement de l'application si l'utilisateur à déjà autorisé la superposition d'écran.
            launchMainService();
        }
        else {

            //Si non, faire apparaître la fenêtre pour accepter la permission.
            checkDrawOverlayPermission();
        }
    }

    //Fonction pour démarrer le service
    private void launchMainService() {

        Intent svc = new Intent(this, JoystickService.class);

        startService(svc);

        finish();
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
            if (Settings.canDrawOverlays(this)) {

                //Appel de la fonction pour lancer le service.
                launchMainService();
            }
            else {

                Toast.makeText(this, "Pas de superposition d'écran sans accepter la permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
