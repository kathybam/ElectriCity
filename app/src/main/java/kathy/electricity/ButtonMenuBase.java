package kathy.electricity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * Created by patri on 2015-10-18.
 */
public class ButtonMenuBase extends Activity {
    /** Called when the user clicks the balloon button*/
    public void onSpeechClicked(View view) {
        // Open the speech window
        startActivity(new Intent(this, SnackActivity.class));
    }

    /** Called when the user clicks the balloon button */
    public void onSnackClicked(View view) {
        // Open the speech window
        startActivity(new Intent(this, SnackActivity.class));
    }

    /** Called when the user clicks the bus button */
    public void onBusClicked(View view) {
        // Open the speech window
        startActivity(new Intent(this, BusActivity.class));
    }

}
