package com.example.user.networkassignment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static String Text="text";
    private TextView textView;
    private Button button;
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.data);
        textView.setMovementMethod(new ScrollingMovementMethod());
        button=(Button)findViewById(R.id.download);
        button1=(Button)findViewById(R.id.reset);
        if(savedInstanceState!=null){
            textView.setText(savedInstanceState.getString(Text,"Content will be displayed here"));
            if(savedInstanceState.getString(Text,"Content will be displayed here").startsWith("Content")){
                button1.setEnabled(false);
                button.setEnabled(true);
            }
            else{
                button1.setEnabled(true);
                button.setEnabled(false);
            }

        }
        else {
            button.setEnabled(true);
            button1.setEnabled(false);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                button1.setEnabled(true);
                textView.setText("");
                URL url = null;
                ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // fetch data
                    try {
                        new DownloadWebpageTask().execute("https://www.iiitd.ac.in/about");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    // display error
                    textView.setText("Cannot get data");
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(true);
                button1.setEnabled(false);
                textView.setText("Content will be displayed here");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Text,textView.getText().toString());
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //textView.setText(result);
            System.out.print(result);
            parseData(result);
        }
        public void parseData(String data){
            Document doc= Jsoup.parse(data);
            //textView.setText(doc.text());
            //textView.append("came");
            Elements elements=doc.getElementsByTag("p");
            //String result="";
            int flag=0;
            for(Element element: elements){
                //System.out.print(element.text());
                if(flag==1){
                    textView.append(element.text()+"\n");
                }
                else if(element.text().startsWith("Indraprastha")){
                    flag=1;
                    textView.append(element.text()+"\n\n");
                }
            }
        }
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();
                String contentAsString = readIt(is);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            String temp="";
            String result="";
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while((temp=bufferedReader.readLine())!=null){
                result=result+temp;
            }
            return result;
        }

    }

}
