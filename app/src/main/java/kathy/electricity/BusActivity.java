package kathy.electricity;

import android.os.Bundle;

public class BusActivity extends ButtonMenuBase {

    private BusView busView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        busView = (BusView) findViewById(R.id.busView);
    }

}
