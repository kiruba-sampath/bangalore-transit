package com.googlecode.bangaloretransit;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class shuttleActivity extends Activity {
	/* Variables */
	private ListView shuttle_list;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle_view);
        
        /* get view elements */
        shuttle_list = (ListView) findViewById(R.id.shuttle_list);
        
        /* List view */
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        
        HashMap<String,String> temp = new HashMap<String,String>();
  	    temp.put("hop", "hop");
        temp.put("fare", "fare");
        list.add(temp);
        
        temp.clear();
  	    temp.put("hop", "hop");
        temp.put("fare", "fare");
        list.add(temp);
        
        temp.clear();
  	    temp.put("hop", "hop");
        temp.put("fare", "fare");
        list.add(temp);
        
        SimpleAdapter adapter = new SimpleAdapter(
      		   getBaseContext(),
      		   list,
      		   R.layout.shuttle_custom_row,
      		   new String[] {"hop","fare"},
      		   new int[] {R.id.shuttle_hop,R.id.shuttle_fare});
         
         shuttle_list.setAdapter(adapter);

    }
}