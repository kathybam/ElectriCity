package kathy.electricity;

import android.os.Bundle;

public class BusStopActivity extends ButtonMenuBase {

    private BusView busView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busstop);

        busView = (BusView) findViewById(R.id.busView);
        busView.mSeatOffsetXPict = 100;
        busView.mSeatOffsetYPict = 0;
        busView.mSeatNrX = 8;
        busView.mSeatNrY = 14;
        busView.mSeatWidthPict = 100;
        busView.mSeatHeightPict = 100;
    }

}
