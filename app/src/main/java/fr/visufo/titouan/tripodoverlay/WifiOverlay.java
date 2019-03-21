package fr.visufo.titouan.tripodoverlay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class WifiOverlay extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wifi_overlay);

    }
}
