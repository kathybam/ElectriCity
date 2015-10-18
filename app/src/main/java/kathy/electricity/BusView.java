package kathy.electricity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

    private int mX = -1;
    private int mY = 0;
    private int mSmileySize = 50;
    private int mSeatWidth = 80;
    private int mSeatHeight = 63;
    private int mSeatOffsetX = 233;
    private int mSeatOffsetY = 300;

    private float mScaleFactor = 1.f;
    private Map mSeating;

    private Toast mToast;

    public BusView (Context c, AttributeSet attrs) {
        super(c, attrs);

        mToast = null;
        this.mSeating = new HashMap();

        this.setBackgroundResource(R.drawable.busview);
        mSmiley = getResources().getDrawable(R.drawable.smiley);

        String urlString = "http://www.hemma.org/rebusarna/rebusar.php";
        CallAPI server = new CallAPI();
        server.responses = this;
        server.execute(urlString);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.setBounds(0,0,960,1440);
        mSmiley.setBounds(0, 0, mSmileySize, mSmileySize);
        canvas.save();
        canvas.translate(mSeatOffsetX - mSmileySize/2, mSeatOffsetY - mSmileySize/2);
        for(int j=0; j < 5; j++) {
            for (int i = 0; i < 8; i++) {
                canvas.translate(0, mSeatHeight);
                if ((mX == j && mY == i) || mSeating.containsKey(new Integer(j+i*5).toString()))
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

        if (mToast != null) {
            mToast.cancel();
        }
        String pos = new Integer(x+y*5).toString();
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
        try {

            JSONArray jArray = (new JSONObject(output)).getJSONArray("seating");
            for (int i=0; i< jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String pos = jObject.getString("pos");
                String id = jObject.getString("id");
                mSeating.put(pos, id);
                invalidate();
            }
        } catch (JSONException e) {
            Log.d("", e.getMessage());
        }
    }

    private class CallAPI extends AsyncTask<String, String, String> {
        public AsyncResponse responses;

        @Override
        protected String doInBackground(String... params) {
            String urlString=params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
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
            responses.processFinish(result);
        }

    } // end CallAPI

}
