package com.example.dell.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.v7.widget.AppCompatDrawableManager.get;

public class MainActivity extends AppCompatActivity {

    int choosanCeleb=0;
    ImageView mImageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    ArrayList<String> celebsUrl = new ArrayList<>();
    ArrayList<String> celebsName = new ArrayList<>();
    String AnswersOptions[] = new String[4];
    int locationOfCorrectAnswer=0;

    public void celebChoosan(View view) {
        if(view.getTag().toString().equals(Integer.toString(choosanCeleb)))
        {
            Toast.makeText(getApplicationContext(),"Correct !",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Incorrect !It was "+celebsName.get(choosanCeleb),Toast.LENGTH_LONG).show();
        }
        questionGenerator();
    }

    public class DownloadImage extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        String result="";
        URL url;
        HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... urls) {
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        mImageView = (ImageView) findViewById(R.id.imageview);
        DownloadTask downloadtask = new DownloadTask();
        String result = null;
        try {
            result = downloadtask.execute("http://www.posh24.se/kandisar").get().toString();//     get() becoz we r grtting data from another thread.
            String[] splitResult = result.split("<div class=\"col-xs-12 col-sm-6 col-md-4\">");

            Pattern p = Pattern.compile("g<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebsUrl.add(m.group(1));
                //System.out.println(m.group(1));
            }
             p = Pattern.compile("alt=\"(.*?)\"");
             m = p.matcher(splitResult[0]);

            while(m.find()){
                celebsName.add(m.group(1));
                //System.out.println(m.group(1));
            }
            //random funtion is gonna applied

            questionGenerator();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void questionGenerator()
    {
        Random randy = new Random();
        choosanCeleb = randy.nextInt(celebsName.size());

        DownloadImage downloadImage = new DownloadImage();
        Bitmap celebsImage;
        try {
            celebsImage = downloadImage.execute(celebsUrl.get(choosanCeleb)).get();
            mImageView.setImageBitmap(celebsImage);

            locationOfCorrectAnswer = randy.nextInt(4);
            int locationOfIncorrectAnswer;
            for(int i=0;i<4;i++)
            {
                if(i==locationOfCorrectAnswer)
                {
                    AnswersOptions[i]=celebsName.get(choosanCeleb);
                }
                else
                {
                    locationOfIncorrectAnswer=randy.nextInt(celebsName.size());
                    if(choosanCeleb == locationOfIncorrectAnswer)
                    {
                        locationOfIncorrectAnswer = randy.nextInt(celebsName.size());
                    }
                    AnswersOptions[i]=celebsName.get(locationOfIncorrectAnswer);
                }
            }
            button1.setText(AnswersOptions[0]);
            button2.setText(AnswersOptions[1]);
            button3.setText(AnswersOptions[2]);
            button4.setText(AnswersOptions[3]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
