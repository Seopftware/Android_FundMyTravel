package seopftware.fundmytravel.function.etc;

import android.support.v7.app.AppCompatActivity;

import seopftware.fundmytravel.function.MyApp;

/**
 * Created by MSI on 2018-02-01.
 */

public class CustomDialog_Activity extends AppCompatActivity {

    public void progressON() {
        MyApp.getInstance().progressON(this, null); // activity, message
    }

    public void progressON(String message) {
        MyApp.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        MyApp.getInstance().progressOFF();
    }
}
