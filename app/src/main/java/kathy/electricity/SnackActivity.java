package kathy.electricity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;


public class SnackActivity extends ButtonMenuBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack);
    }
    /** Called when the user clicks the Tapas text */
    public void sendMessageTapas(View view) {
        // Open the speech window
        startActivity(new Intent(this, TapasActivity.class));
    }

}
