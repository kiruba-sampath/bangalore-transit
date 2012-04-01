package com.googlecode.bangaloretransit;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
       for(int gy = 0; gy < from_hops.length; gy++)
       {
    	   if(from_hops[gy] == null) break;
    	   from_adapter.add(from_hops[gy]);
       }
       from_spinner.setAdapter(from_adapter);
       from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	   public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) { 
    		  //Toast.makeText(parent.getContext(), 
    		  //          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
    		  from_selected = parent.getItemAtPosition(pos).toString();
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
       for(int gy = 0; gy < to_hops.length; gy++)
       {
    	   if(to_hops[gy] == null) break;
    	   to_adapter.add(to_hops[gy]);
       }
       to_spinner.setAdapter(to_adapter);
       to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	   public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
    		  to_selected = parent.getItemAtPosition(pos).toString();
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
            }
            else {
            shuttle_info = airportdb.getShuttles(from_selected, to_selected, true);
            for(int jk = 0; jk < shuttle_info.length && shuttle_info[jk]!=null; jk++) {
            	Log.d("DEBUG", shuttle_info[jk]);
            }
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
            }
	    }
	   });      
    }
    
    
    
}