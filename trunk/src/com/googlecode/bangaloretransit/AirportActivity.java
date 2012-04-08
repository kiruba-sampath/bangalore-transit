package com.googlecode.bangaloretransit;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AirportActivity extends Activity {
    
	/* variables */
	private String from_selected = "";
	private String to_selected = "";
	private DBAdapter airportdb;
	private String[] shuttle_info = new String[50];
	private SimpleAdapter adapter;
	private ListView airport_list;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airport);
        
        /* get view elements */
        Spinner from_spinner = (Spinner) findViewById(R.id.spinner_from);
        Spinner to_spinner = (Spinner) findViewById(R.id.spinner_to);
        Button find_button = (Button) findViewById(R.id.airport_find);
        airport_list = (ListView) findViewById(R.id.airport_list); 
        
        airportdb = new DBAdapter(getApplicationContext());
        airportdb.open();
        
        
       /****************** From spinner ***************/
       String[] from_hops = airportdb.getHops();
       ArrayAdapter <CharSequence> from_adapter =
    	   new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
       from_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       from_adapter.add("International Airport");
       for(int gy = 0; gy < from_hops.length; gy++)
       {
    	   if(from_hops[gy] == null) break;
    	   if(!from_hops[gy].equals("International Airport")) {
    	      from_adapter.add(from_hops[gy]);
    	   }
       }
       from_spinner.setAdapter(from_adapter);
       from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	   public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) { 
    		  //Toast.makeText(parent.getContext(), 
    		  //          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
    		  from_selected = parent.getItemAtPosition(pos).toString();
    		  airport_list.setAdapter(null);
    	   } 

    	   public void onNothingSelected(AdapterView<?> adapterView) {
    	        return;
    	   }
	   });
       
       /****************** To spinner **************************/
       String[] to_hops = airportdb.getHops();
       ArrayAdapter <CharSequence> to_adapter =
    	   new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
       to_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       to_adapter.add("International Airport");
       for(int gy = 0; gy < to_hops.length; gy++)
       {
    	   if(to_hops[gy] == null) break;
    	   if(!to_hops[gy].equals("International Airport")) {  
    	      to_adapter.add(to_hops[gy]);
    	   }
       }
       to_spinner.setAdapter(to_adapter);
       to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	   public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
    		  to_selected = parent.getItemAtPosition(pos).toString();
    		  airport_list.setAdapter(null);
    	   } 

    	   public void onNothingSelected(AdapterView<?> adapterView) {
    	        return;
    	   }
	   });
       
       /*******************  Button  ************************************/   
       find_button.setOnClickListener(new View.OnClickListener() {
	   @Override
	   public void onClick(View arg0) {    
            if(from_selected.equals(to_selected)) {
            	Toast.makeText(getBaseContext(), "Get a time machine!", Toast.LENGTH_SHORT).show();
            	airport_list.setAdapter(null);
            }
            else {
            airportdb.open();
            shuttle_info = airportdb.getShuttles(from_selected, to_selected);
            
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
            for(int jk = 0; jk < shuttle_info.length && shuttle_info[jk]!=null; jk++) {
         	   String[] parser = shuttle_info[jk].split(";");
         	   HashMap<String,String> temp = new HashMap<String,String>();
         	   temp.put("route", parser[0]);
               temp.put("board", parser[1]);
               list.add(temp);
            }
            adapter = new SimpleAdapter(
         		   getBaseContext(),
         		   list,
         		   R.layout.airport_custom_row,
         		   new String[] {"route","board"},
         		   new int[] {R.id.number_text,R.id.route_text});
            
            airport_list.setAdapter(adapter);
            airportdb.close();
            }
	    }
	   });
       
       airport_list.setOnItemClickListener(new OnItemClickListener() {
    	   public void onItemClick(AdapterView<?> l, View v,
    		          int position, long id) {
    		    Object o = airport_list.getItemAtPosition(position);
    		    String route = o.toString().replace("{", " ");
    		    route = route.replace("}", "");
    		    String[] routes = route.split("=");
    		    route = routes[2];
    		    if(!route.equals("Sorry!")) {
    		       Intent newActivity = new Intent(getBaseContext(),shuttleActivity.class);
    		       newActivity.putExtra("route", route);
    		       newActivity.putExtra("direction", airportdb.direction_from);
    		       startActivityForResult(newActivity, 1);
    		    }
    	   }
	   });
    }
   
	public void onPause() {
		super.onPause();
		airportdb.close();
	}
	
    public void onStop() {
    	super.onStop();
    	airportdb.close();
    }
    
    public void onDestroy() {
    	super.onDestroy();
    	airportdb.close();
    }
    
    /* overriding the back key */
    public void onBackPressed() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Would you like to rate this app?")
    	       .setCancelable(true)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	               Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.googlecode.bangaloretransit"));
    	               startActivity(marketIntent);
    	               AirportActivity.this.finish();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   AirportActivity.this.finish();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    	
    }
    
}