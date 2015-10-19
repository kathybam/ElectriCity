package kathy.electricity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by patri on 2015-10-18.
 */
public class BusView extends View implements AsyncResponse{
    private Drawable mSmiley;
    private Drawable mDevil;

    private int mX = -1;
    private int mY = 0;
    private int mSmileySize = 50;
    private int mSeatWidthPict = 100;
    private int mSeatHeightPict = 90;
    private int mSeatOffsetXPict = 295;
    private int mSeatOffsetYPict = 420;
    private int mPictWidth = 960;
    private int mPictHeight = 1440;
    private int mSeatWidth;
    private int mSeatHeight;
    private int mSeatOffsetX;
    private int mSeatOffsetY;
    private boolean mDrawDevil = false;
    private String android_id;

    private float mScaleFactor = 1.f;
    private Map<String,String> mSeating;

    private Toast mToast;


    public BusView (Context c, AttributeSet attrs) {
        super(c, attrs);

        mToast = null;
        this.mSeating = new HashMap<>();

        mSmiley = getResources().getDrawable(R.drawable.smiley);
        mDevil = getResources().getDrawable(R.drawable.devil);
        android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String urlString = "http://www.hemma.org/rebusarna/rebusar.php?user_id="+android_id;
        CallAPI server = new CallAPI();
        server.responses = this;
        server.overideSleep = true;
        server.execute(urlString);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mSeatWidth = mSeatWidthPict*canvas.getWidth()/mPictWidth;
        mSeatHeight = mSeatHeightPict*canvas.getHeight()/mPictHeight;
        mSeatOffsetX = mSeatOffsetXPict*canvas.getWidth()/mPictWidth;
        mSeatOffsetY = mSeatOffsetYPict*canvas.getHeight()/mPictHeight;
        if (mDrawDevil) {
            mDevil.setBounds(0, 0, mSmileySize * 2, mSmileySize * 2);
            canvas.save();
            canvas.translate(mSeatOffsetX + 5 * canvas.getWidth() / mPictWidth, mSeatOffsetY - 180 * canvas.getHeight() / mPictHeight);
            mDevil.draw(canvas);
            canvas.restore();
        }

        mSmiley.setBounds(0, 0, mSmileySize, mSmileySize);
        canvas.save();
        canvas.translate(mSeatOffsetX - mSmileySize/2, mSeatOffsetY - mSmileySize/2);
        for(int j=0; j < 5; j++) {
            for (int i = 0; i < 8; i++) {
                canvas.translate(0, mSeatHeight);
                if ((mX == j && mY == i) || mSeating.containsKey(Integer.toString(j+i*5)))
                    mSmiley.draw(canvas);
            }
            canvas.translate(0, -mSeatHeight * 8);
            canvas.translate(mSeatWidth, 0);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (int)((e.getX() - mSeatOffsetX + mSmileySize/2) / mSeatWidth);
        int y = (int)((e.getY() - mSeatOffsetY - mSmileySize/2) / mSeatHeight);

        String pos = Integer.toString(x+y*5);

        if(e.getAction() == MotionEvent.ACTION_UP) {
            if (e.getX() > mSeatOffsetX && e.getX() < (mSeatOffsetX + mSmileySize * 3) && e.getY() < mSeatOffsetY)
                mDrawDevil = !mDrawDevil;

            String urlString;
            if (x >= 0 && y >= 0) {
                urlString = "http://www.hemma.org/rebusarna/rebusar.php?action=\"updateSeating\"&user_id=" + android_id + "&pos=" + pos;
            } else
                urlString = "http://www.hemma.org/rebusarna/rebusar.php?action=\"updateSeating\"&user_id="+android_id+"&pos=-1";

            CallAPI server = new CallAPI();
            server.responses = this;
            server.overideSleep = true;
            server.execute(urlString);
        }

        if (mToast != null) {
            mToast.cancel();
        }
        if(mSeating.containsKey(pos))
        {
            mToast = Toast.makeText(this.getContext(), "Profile for seat " + pos + "\n - Spanska\n - Chalmers", Toast.LENGTH_SHORT);
            mToast.show();
        }

        mX = x;
        mY = y;
        invalidate();
        return true;
    }

    @Override
    public void processFinish(String output) {
        mSeating.clear();
        try {

            JSONArray jArray = (new JSONObject(output)).getJSONArray("seating");
            for (int i=0; i< jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String pos = jObject.getString("pos");
                String id = jObject.getString("id");
                if (!pos.equals("") && !id.equals("")) {
                    mSeating.put(pos, id);
                }
                invalidate();
            }
        } catch (JSONException e) {
            Log.d("", e.getMessage());
        }
        CallAPI server = new CallAPI();
        server.responses = this;
        server.execute("http://www.hemma.org/rebusarna/rebusar.php?user_id="+android_id);
    }

    private class CallAPI extends AsyncTask<String, String, String> {
        public AsyncResponse responses;
        public boolean overideSleep = false;
        @Override
        protected String doInBackground(String... params) {
            String urlString=params[0]; // URL to call

            String resultToDisplay;

            InputStream in;

            if (!overideSleep)
                SystemClock.sleep(1000);

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                resultToDisplay = sb.toString();

            } catch (Exception e ) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            try {
                responses.processFinish(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } // end CallAPI

}
