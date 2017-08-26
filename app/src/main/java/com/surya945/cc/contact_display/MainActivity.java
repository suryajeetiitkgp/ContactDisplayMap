package com.surya945.cc.contact_display;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    HandlerClass handlerClass;
    public LayoutInflater layoutInflater;
    public LinearLayout myRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handlerClass=new HandlerClass();
        layoutInflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myRoot=(LinearLayout)findViewById(R.id.linearLayoutContact);
        GetContactDetails();

    }

    public void GetContactDetails(){
        final List<Contact> htmlResponces =new ArrayList<>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt";
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);


                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null)
                    {
                        String[] data=line.split(" ");
                        Contact contact=new Contact(data[0],data[1],data[2]+" "+data[3]);
                        handlerClass.obtainMessage(1,contact).sendToTarget();
                        htmlResponces.add(contact);
                    }
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    class HandlerClass extends Handler {
        HandlerClass() {
        }

        public void handleMessage(Message msg) {
            Contact contact=(Contact ) msg.obj;
            switch (msg.what) {
                case 1:
                    View v = layoutInflater.inflate(R.layout.layout_contact, null);
                    TextView textViewName =(TextView)v.findViewById(R.id.textViewName);
                    TextView textViewEmail =(TextView)v.findViewById(R.id.textViewEmail);
                    TextView textViewLocation =(TextView)v.findViewById(R.id.textViewLocation);

                    textViewName.setText(contact.name);
                    textViewEmail.setText(contact.email);
                    textViewLocation.setText(contact.location);
                    myRoot.addView(v);
                    return;
                default:
                    return;
            }
        }
    }
    public void OnClick(View view){
        TextView textViewName=(TextView)view.findViewById(R.id.textViewName);
        TextView textViewLocation=(TextView)view.findViewById(R.id.textViewLocation);
        Intent intent=new Intent(MainActivity.this,MapsActivity.class);
        intent.putExtra("name",textViewName.getText().toString());
        intent.putExtra("location",textViewLocation.getText().toString());
        startActivity(intent);
    }

}
