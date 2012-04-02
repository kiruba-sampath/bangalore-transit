package com.googlecode.bangaloretransit;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class shuttleActivity extends Activity {
	/* Variables */
	private ListView shuttle_list;
	private TextView route_text;
	private TextView board_text;
	private TextView time_text;
	
	private DBAdapter airportdb;
	private int itr = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle_view);
        
        /* get view elements */
        route_text = (TextView) findViewById(R.id.route_display);
        board_text = (TextView) findViewById(R.id.board_display);
        time_text = (TextView) findViewById(R.id.time_display);
        shuttle_list = (ListView) findViewById(R.id.shuttle_list);
        
        airportdb = new DBAdapter(getApplicationContext());
        airportdb.open();
        String response = airportdb.getShuttleInfo("BIAS-4", false);
        String[] responselist = response.split("::");
        
        route_text.setText(responselist[itr++]);
        board_text.setText(responselist[itr++]);
        time_text.setText("Starts at : " + responselist[itr++]);
        
        /* List view */
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        
        while(itr < responselist.length) {	
           HashMap<String,String> temp = new HashMap<String,String>();
  	       temp.put("hop", responselist[itr++]);
           temp.put("fare", responselist[itr++]);
           list.add(temp);
        }
        /* temp.clear();
  	    temp.put("hop", "hop");
        temp.put("fare", "fare");
        list.add(temp);
        
        temp.clear();
  	    temp.put("hop", "hop");
        temp.put("fare", "fare");
        list.add(temp); */
        
        SimpleAdapter adapter = new SimpleAdapter(
      		   getBaseContext(),
      		   list,
      		   R.layout.shuttle_custom_row,
      		   new String[] {"hop","fare"},
      		   new int[] {R.id.shuttle_hop,R.id.shuttle_fare});
         
         shuttle_list.setAdapter(adapter);

    }
}