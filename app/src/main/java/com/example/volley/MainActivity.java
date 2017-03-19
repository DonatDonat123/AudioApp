package com.example.volley;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.time.StopWatch;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DD";

    Button button;
    TextView textView, dd_text;
    String serverurl ="http://dennisdemenis.pythonanywhere.com/";
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.bn);
        dd_text = (TextView)findViewById(R.id.dd_text);
        textView = (TextView)findViewById(R.id.text);
        builder = new AlertDialog.Builder(MainActivity.this);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
        Network network = new BasicNetwork(new HurlStack());

        button.setOnClickListener(new View.OnClickListener() {
            MediaPlayer mediaPlayer = new MediaPlayer();
            StopWatch stopwatch = new StopWatch();
            String song = "";
            double millis = 0.0;
            int counter = 1;

            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    stopwatch.reset();

                    Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/raw/sound" + counter);
                    String message1 = myUri.toString();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        Log.i(TAG, "trying to setDataSource");
                        mediaPlayer.setDataSource(getApplicationContext(), myUri);
                        Log.i(TAG, "URI = " + message1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.i(TAG, "trying to prepare");
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "trying to start");
                    mediaPlayer.start();
                    stopwatch.start();
                    button.setText("STOP");
                }
                else{

                    mediaPlayer.stop();
                    stopwatch.stop(); // optional
                    counter++;
                    button.setText("Play next one");
                    song = String.format("Song %2d / 10", counter);
                    dd_text.setText(song);
                    //millis = stopwatch.getNanoTime();
                    millis = stopwatch.getTime();
                    textView.setText("that took: " + stopwatch);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverurl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                builder.setTitle("Server Response");
                                builder.setMessage("Response :" + response);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //textView.setText("POSITIVE BUTTON");

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                //alertDialog.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                                textView.setText("Sth went wrong");
                                error.printStackTrace();
                                //requestQueue.stop();

                            }
                        }
                )

                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("durations", Double.toString(millis));
                        params.put("song_nr", Integer.toString(counter-1));

                        return params;
                    }
                };
                MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
            }}
        });
    }
}

