package com.googlecode.bangaloretransit;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AirportActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airport);
        
        DBAdapter airportdb = new DBAdapter(getApplicationContext());
        airportdb.open();
        airportdb.getHops();
        
       
       
       /* List view */
       ListView airport_list = (ListView) findViewById(R.id.airport_list);
       ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
       HashMap<String,String> temp = new HashMap<String,String>();
       temp.put("pen","MONT Blanc");
       temp.put("price", "200.00$");
       list.add(temp);
       HashMap<String,String> temp1 = new HashMap<String,String>();
       temp1.put("pen","Gucci");
       temp1.put("price", "300.00$");
       list.add(temp1);
       HashMap<String,String> temp2 = new HashMap<String,String>();
       temp2.put("pen","Parker");
       temp2.put("price", "400.00$");
       list.add(temp2);
       HashMap<String,String> temp3 = new HashMap<String,String>();
       temp3.put("pen","Sailor");
       temp3.put("price", "500.00$");
       list.add(temp3);
       HashMap<String,String> temp4 = new HashMap<String,String>();
       temp4.put("pen","Porsche Design");
       temp4.put("price", "600.00$");
       list.add(temp4);
       
       SimpleAdapter adapter = new SimpleAdapter(
    		   this,
    		   list,
    		   R.layout.airport_custom_row,
    		   new String[] {"pen","price"},
    		   new int[] {R.id.number_text,R.id.route_text});
       
       airport_list.setAdapter(adapter);
       
       /* parse the XML */
       Resources res = getResources();
       XmlResourceParser xpp = res.getXml(R.xml.airportshuttle);
       
    }
    
    
    
}