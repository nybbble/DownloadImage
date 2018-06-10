package com.asis.gl.week10;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView txtStatus;
    ImageView imgView;

    String res="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtStatus = (TextView) findViewById(R.id.textView);
        imgView = (ImageView) findViewById(R.id.imageView);
    }

    public void onClickDownload(View view) {
        Log.d("AsyncTask","Onclik is OK");
        try {
            URL uri = new URL("http://www.ybu.edu.tr/muhendislik/bilgisayar/contents/images/3855.jpg");
            DownloaderTask dd = new DownloaderTask();
            Log.d("AsyncTask", "Class created");
            dd.execute(uri);
            Log.d("AsyncTask", "Execute called");
        }
        catch (MalformedURLException e){
            Log.d("AsyncTask","Error: " + e.getMessage());
        }
        catch (Exception e){
            Log.e("AsyncTask", "Error " + e.getMessage());
        }

    }

    public void getJoke(View view) {
        //JsonHelper json = new JsonHelper();
        //json.execute();
        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        processData(result);
                    }
                });



    }
    private void getJoke(){
        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        processData(result);
                    }
                });
    }

    //  {
    //      "type": "success",
    //       "value":
    //              {
    //                  "id": 548,
    //                  "joke": "Product Owners never argue with Chuck Norris after he demonstrates the DropKick feature.",
    //                  "categories": ["nerdy"]
    //              }
    //  }
    private void processData(JsonObject jsonObj){
        JsonObject value = jsonObj.getAsJsonObject("value");
        String joke = value.get("joke").getAsString();
        txtStatus.setText(joke);

        //String joke = res.getAsJsonObject("value")
        //        .get("joke").getAsString();
        //this.res = joke;
    }

    private class DownloaderTask extends AsyncTask<URL,Integer,Bitmap>{

        @Override
        protected void onPreExecute() {
            //main thread
            //super.onPreExecute();
            Log.d("AsyncTask","onPreExecute is working");

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Image downloading...");
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            txtStatus.setText("Download is in progress ...");
            Log.d("AsyncTask","onPreExecute end...");
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            //backgroung thread worker comes here.
            Log.d("AsyncTask","DoInbackgroung is working");
            Bitmap bmp;
            URL url = params[0];
            try {
                int i=0;
                while (i<10) {
                    i++;
                    publishProgress(i * 10);
                    Thread.currentThread().sleep(100);
                }
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream inputS = connection.getInputStream();
                if(inputS!=null) {
                    bmp = BitmapFactory.decodeStream(inputS);
                    return bmp;
                }
            } catch (IOException e) {
                Log.d("AsyncTask","IOError: " + e.getMessage());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //super.onPostExecute(aLong);
            //main UI enters
            if(bitmap == null){
                txtStatus.setText("There was an error");
                return;
            }
            else txtStatus.setText("Download completed");

            progressDialog.hide();
            imgView.setImageBitmap(bitmap);

            /*File newFolder = new File("sdcard/myalbum");

            if(!newFolder.exists()){
                newFolder.mkdir();
            }*/
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            //Main UI enters
            int progress = values[0];
            progressDialog.setProgress(progress);
        }
    }

    private class JsonHelper extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            getJoke();
            return MainActivity.this.res;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            txtStatus.setText(s);
        }
    }
}
