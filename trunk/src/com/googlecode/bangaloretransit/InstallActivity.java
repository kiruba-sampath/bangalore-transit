package com.googlecode.bangaloretransit;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class InstallActivity extends Activity {
	
	private final String airport_url = "http://www.pixelpins.com/android/AirportShuttle.xml";
	private DBAdapter airportdb; 
	private ProgressDialog dialog;
	
	private static final String TAG = "DEBUG";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.install_view);
        TextView xmltest = (TextView) findViewById(R.id.install_text);
       

        dialog = ProgressDialog.show(InstallActivity.this, "", 
                "Loading Routes.. Please wait..", true);
        
        if(!isNetworkAvailable())
        {
        	Toast.makeText(getBaseContext(), "Please connect to Internet..", Toast.LENGTH_LONG).show();
        	finish();
        }
        else {
        /* Thread part  */
        final Handler handler=new Handler();
        final Runnable r = new Runnable()
        {
            public void run() 
            {
               finish();
            }
        };
        
        Thread xml_thread = new Thread() {
        	@Override
            public void run() {
                try {
                	/* Populates airport DB */
                	airportdb =  new DBAdapter(getApplicationContext());
                	/* DB connection */
                    airportdb.open();
                    PopulateAirportDB();
                    Intent AirportIntent = new Intent(getBaseContext(), AirportActivity.class);
                    startActivity(AirportIntent);
                    handler.post(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        xml_thread.start();     
        } 
    }
    
    // override on stop
    public void onStop()
    {
    	super.onStop();
    	dialog.dismiss();
    	airportdb.close();
    }
    
    /* Parse and populate airport routes */
    private boolean PopulateAirportDB(){
    	//HACK
    	airportdb.flush();
    	String xml = getXmlFromUrl(airport_url);
    	Document doc = getDomElement(xml); 
    	NodeList nl = doc.getElementsByTagName("bias");
    	
    	for (int i = 0; i < nl.getLength(); i++) {
            Element item = (Element)nl.item(i);
            String route_number = getValue(item, "number").trim();
            String start = getValue(item, "start").trim();
            String hops = getValue(item, "hops").trim();
            String from_bias = getValue(item, "frombias").trim();
            String to_bias = getValue(item, "fromdestination").trim();
            String fare130 = getValue(item, "fare_130").trim();
            String fare150 = getValue(item, "fare_150").trim();
            String fare165 = getValue(item, "fare_165").trim();
            String fare180 = getValue(item, "fare_180").trim();
            String fare200 = getValue(item, "fare_200").trim();
            String fare240 = getValue(item, "fare_240").trim();
            airportdb.insert(route_number, start, hops,from_bias, to_bias, fare130, fare150, fare165, fare180, fare200, fare240);
        }
    	 return true;
    }
    
    /* get the XML from URL */
    private String getXmlFromUrl(String url) {
        String xml = null;
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
        	Toast.makeText(getBaseContext(), "Problem in connecting to server..", Toast.LENGTH_LONG).show();
        	finish();
            e.printStackTrace();
        } catch (ClientProtocolException e) {
        	Toast.makeText(getBaseContext(), "Problem in connecting to server..", Toast.LENGTH_LONG).show();
        	finish();
            e.printStackTrace();
        } catch (IOException e) {
        	Toast.makeText(getBaseContext(), "Problem in connecting to server..", Toast.LENGTH_LONG).show();
        	finish();
            e.printStackTrace();
        }
        return xml;
    }
    
    /* get DOM element */
    public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
            return doc;
    }
    
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
     
    public final String getElementValue( Node elem ) {
             Node child;
             if( elem != null){
                 if (elem.hasChildNodes()){
                     for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                         if( child.getNodeType() == Node.TEXT_NODE  ){
                             return child.getNodeValue();
                         }
                     }
                 }
             }
             return "";
      } 
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null);
    }
}