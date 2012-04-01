package com.googlecode.bangaloretransit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    private static final String DATABASE_NAME = "bang_transit.db";
    private static final String DATABASE_TABLE = "airport";
    
    private static final int DATABASE_VERSION = 1;
    private int no_hops = 0;
    
    // COLUMN NAMES
    public static final String KEY_ID = "_id";
    public static final String KEY_ROUTE = "route";
    public static final String KEY_START = "start";
    public static final String KEY_HOPS = "hops";
    public static final String KEY_TIME_FROM = "from_time";
    public static final String KEY_TIME_TO = "to_time";
    public static final String KEY_130 = "fare_one";
    public static final String KEY_150 = "fare_two";
    public static final String KEY_165 = "fare_three";
    public static final String KEY_180 = "fare_four";
    public static final String KEY_200 = "fare_five";
    public static final String KEY_240 = "fare_six";
    
    // SQL to create table
    private static final String DATABASE_CREATE = 
    	"create table " + DATABASE_TABLE + " (" + 
    	KEY_ID + " integer primary key autoincrement, " + 
    	KEY_ROUTE + " varchar(100), " +
    	KEY_START + " varchar(100)," +
    	KEY_HOPS + " text, " +
    	KEY_TIME_FROM + " text, " +
    	KEY_TIME_TO + " text, " +
    	KEY_130 + " text, " +
    	KEY_150 + " text, " +
    	KEY_165 + " text, " +
    	KEY_180 + " text, " +
    	KEY_200 + " text, " +
    	KEY_240 + " text);";
    
    //variable to hold database instance
    private SQLiteDatabase db;
    // context of the application using database
    private final Context context;
    // Database open/upgrade helper
    private myDbHelper dbHelper;
    
    public DBAdapter(Context _context) {
    	context = _context;
    	dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public DBAdapter open() throws SQLException {
    	try {
    	   db = dbHelper.getWritableDatabase();
    	}
    	catch(SQLiteException ex) {
    	  db  = dbHelper.getReadableDatabase();
    	}
    	return this;
    }
    
    public void close() {
    	db.close();
    }
    
    /*** Airport Related ***/
    
    /* Insert entry to database */
    public long insert(String route, String start, String hops, String from_time, String to_time, 
    		           String fare130, String fare150, String fare165, String fare180, String fare200,
    		           String fare240)
    {
    	ContentValues newValues = new ContentValues();
    	newValues.put(KEY_ROUTE, route);
    	newValues.put(KEY_START, start);
    	newValues.put(KEY_HOPS, hops);
    	newValues.put(KEY_TIME_FROM, from_time);
    	newValues.put(KEY_TIME_TO, to_time);
    	newValues.put(KEY_130, fare130);
    	newValues.put(KEY_150, fare150);
    	newValues.put(KEY_165, fare165);
    	newValues.put(KEY_180, fare180);
    	newValues.put(KEY_200, fare200);
    	newValues.put(KEY_240, fare240);
    	
    	// Insert the row
    	return db.insert(DATABASE_TABLE, null, newValues);
    }
    
    /* get hops */
    public String[] getHops()
    {
    	String[] hops = new String[200];
    	String start;
    	String hopstring;
    	
        Cursor resultCursor = db.query(DATABASE_TABLE, new String[] {KEY_START, KEY_HOPS} , null, null, null, null, null);
        no_hops = 0;
        if(!(resultCursor == null)) {
        	resultCursor.moveToFirst();  
        	while(!(resultCursor.isAfterLast())){
        	    start = resultCursor.getString(0);
        	    addIfNotExists(hops, start);
        	    
        	    hopstring  =  resultCursor.getString(1).trim();
        	    String[] templist = hopstring.split(";");
        	    for(int temp = 0; temp < templist.length; temp++)
        	    {
        	        addIfNotExists(hops, templist[temp].trim());
        	    }
        		resultCursor.moveToNext();
        	}	
        }        
        java.util.Arrays.sort(hops, 0, no_hops);
        resultCursor.close();
        return hops;
    }
    
    /* Get Bus routes */
    /* Data stored as Start To airport */
    public String[] getShuttles(String from, String to, boolean from_airport) {
    	String[] shuttles = new String[50];
    	int no_shuttles = 0;
    	String route;
    	String start;
    	String hopstring;
    	
    	Cursor resultCursor = db.query(DATABASE_TABLE, new String[] {KEY_ROUTE, KEY_START, KEY_HOPS} , 
    			                       null, null, null, null, null);
    	if(!(resultCursor == null)) {
    		resultCursor.moveToFirst();  
        	while(!(resultCursor.isAfterLast())){
        		route = resultCursor.getString(0);
        		start = resultCursor.getString(1);
        		hopstring = resultCursor.getString(2);
        		hopstring = start + ";" + hopstring;
         		String[] hoplist = hopstring.split(";");
                
         		if(isPresentArray(hoplist, from) && isPresentArray(hoplist, to)) {
                	if(isPresentBefore(hoplist, from, to))
                	{
                		String builder = route + ";" + start + " to International Airport";
                		shuttles[no_shuttles] = builder;
                	}
                	else
                	{
                		String builder = route + ";" + "International Airport to " + start;
                		shuttles[no_shuttles] = builder;
                	}
                	no_shuttles = no_shuttles + 1;
                }
                resultCursor.moveToNext();
        	} //end while
    	}
    	resultCursor.close();
    	if(shuttles[0] == null)
    		shuttles[0] = "Sorry!; No shuttles available";
    	return shuttles;
    }
    
    /* is present in Array */
    private boolean isPresentArray(String[] str_array, String ip)
    {
        for(int i = 0; i < str_array.length; i++) {
    		if(str_array[i].equals(ip))
    			return true;
    		if(str_array[i] == null)
    			break;
    	}
    	return false;
    }
    
    /* is first parameter present before second */
    private boolean isPresentBefore(String[] str_arr, String from, String to) {
    	for(int i = 0 ; i < str_arr.length; i++)
    	{
    		if(str_arr[i].equals(from))
    			return true;
    		else if(str_arr[i].equals(to))
    			return false;
    	}
    	return true;
    }
    /* add to string array */
    private void addIfNotExists(String[] addHere, String toAdd )
    {
    	int i = 0;
    	for(i = 0; i < no_hops; i++)
    	{
    		if(addHere[i].equals(toAdd))
    		    return;
    	}
    	addHere[no_hops] = toAdd;
    	no_hops++;
    }
    /*** Airport related ends ***/
    
    /* Flush the DB */
    public boolean flush() {
        return db.delete(DATABASE_TABLE, null , null) > 0;	
    }
    
    // Delete the entry
    public boolean remove(long _rowIndex) {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;	
    }
    
    /*
    //Update a task
    public boolean update(long _rowIndex, String name) {
    	ContentValues newValue = new ContentValues();
    	newValue.put(KEY_NAME, name);
    	return db.update(DATABASE_TABLE, newValue, KEY_ID + "=" + _rowIndex, null) > 0;
    }
    */
    
    
    /*
    // Get all the names 
    public Cursor getAllNames() {
    	return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME}, 
    			        null, null, null, null, null);
    }
    */
    
    // DB helper class
    private static class myDbHelper extends SQLiteOpenHelper {
    	
    	public myDbHelper(Context context, String name, CursorFactory factory, int version) {
    		super(context, name, factory, version);
    	}

		@Override
		public void onCreate(SQLiteDatabase _db) {
		    _db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int arg1, int arg2) {
			//drop the existing table and create the new one
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			//create a new one
			onCreate(_db);
		}
    }
}