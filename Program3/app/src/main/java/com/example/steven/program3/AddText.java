package com.example.steven.program3;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.content.Intent;

public class AddText extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{


    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    static final public String MYPREFS = "myprefs";
    static final public String PREF_URL = "restore_url";
    static final public String WEBPAGE_NOTHING = "about:blank";
    static final public String MY_WEBPAGE = "https://users.soe.ucsc.edu/~dustinadams/CMPS121/assignment3/www/index.html";
    static final public String LOG_TAG = "webview_example";

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        myWebView = (WebView) findViewById(R.id.webView1);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Binds the Javascript interface
        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        myWebView.loadUrl(MY_WEBPAGE);
        if(!checkPermission()){
            requestPermission();
        }
    }

    public class JavaScriptInterface {
        Context mContext; // Having the context is useful for lots of things,
        // like accessing preferences.
        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void record() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddText.this, "Recording",
                            Toast.LENGTH_LONG).show();
                    if (checkPermission()) {
                        String fileName = "";
                        try {
                            File f = new File(getFilesDir(), "file.ser");
                            BufferedReader br = new BufferedReader(new FileReader(f));

                            String length = "";
                            length = br.readLine();
                            int num = Integer.valueOf(length) + 1;
                            fileName = String.valueOf(num - 1);
                            br.close();

                        } catch (Exception e) {
                            try {
                                File f = new File(getFilesDir(), "file.ser");
                                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                                fileName = "1";
                                bw.write("1");
                                bw.close();
                            } catch (IOException ef) {
                                ef.printStackTrace();
                            }
                        }

                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + ".3gp";

                        System.out.println(AudioSavePathInDevice);
                        MediaRecorderReady();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        requestPermission();
                    }
                }
            });
        }

        @JavascriptInterface
        public void stop() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddText.this, "Stopping",
                            Toast.LENGTH_LONG).show();
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        try {
                            File f = new File(getFilesDir(), "file.ser");
                            BufferedReader br = new BufferedReader(new FileReader(f));

                            String length = br.readLine();
                            br.close();
                            int num;
                            if (length == null) {
                                num = 1;
                            } else {
                                num = Integer.valueOf(length) +1;
                            }

                            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                            bw.write(String.valueOf(num));
                            bw.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void play() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddText.this, "Playing",
                            Toast.LENGTH_LONG).show();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(AudioSavePathInDevice);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            });

        }

        @JavascriptInterface
        public void stoprec() {
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddText.this, "Stopping recording",
                            Toast.LENGTH_LONG).show();
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        MediaRecorderReady();
                    }
                }
            });
        }
        @JavascriptInterface
        public void exit(){
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Intent i = new Intent(AddText.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }

    }

    // initialize recorder object
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    // permissions from user
    private void requestPermission() {
        ActivityCompat.requestPermissions(AddText.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    // callback method
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AddText.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddText.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}