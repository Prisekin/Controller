package com.Prisekin.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.app.AlertDialog;
import android.widget.Toast;
import android.widget.TextView;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import org.json.JSONObject;
import org.json.JSONArray;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;

import java.util.List;
import java.util.ArrayList;

public class ShowMap extends Activity implements
Listener,ErrorListener,OnInfoWindowClickListener{
	GoogleMap map;
	RequestQueue queue; Gson gson; EventInfo event_info; 
	TextView event_title;
	@Override
	public void onCreate(Bundle bund){
		super.onCreate(bund);
		setContentView(R.layout.show_map);
		event_title=(TextView)findViewById(R.id.party_name);
		map=((MapFragment)this.getFragmentManager().findFragmentById(R.id.map_view)).getMap();
		map.setOnInfoWindowClickListener(this);
		gson=new Gson();
		queue=Volley.newRequestQueue(this);
		JsonArrayRequest jreq=new JsonArrayRequest(
		"http://face-control.dev.sibext.com/events.json?token="+
		 getIntent().getStringExtra("token"),this,this);
		queue.add(jreq);
	}
	@Override
	public void onInfoWindowClick(Marker m){
		//new AlertDialog.Builder(this).setTitle("Info Window: ").setMessage("Info Window clicked.").show();
		Intent contacts=new Intent(this,ShowContacts.class);
		contacts.putExtra("phone",event_info.phone);
		contacts.putExtra("email",event_info.email);
		contacts.putExtra("skype",event_info.skype);
		startActivity(contacts);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.map_menu,menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int id,MenuItem item){
		switch(item.getItemId()){
		case R.id.map_menu_map_id:  map.setMapType(GoogleMap.MAP_TYPE_NORMAL); break;
		case R.id.map_menu_sat_id:  map.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
		}
		return true;
	}
@Override
public void onResponse(Object resp_obj){
	JSONArray json_resp=(JSONArray)resp_obj;
	JSONObject json_obj=null;
	try{json_obj=json_resp.getJSONObject(0);}
	catch(Exception e){Log.e("JSON error: ",e.toString());}
	Log.e("JSON OK: ",json_obj.toString());
	/*new AlertDialog.Builder(this).
	setTitle("Event response: ").
	setMessage(json_obj.toString()).show();*/
	event_info=gson.fromJson(json_obj.toString(),EventInfo.class);
	event_title.setText(event_info.title);
	String start_time=event_info.date_start.replace('T',' ').replace('Z',' ');
	//start_time.split(null,start_time.length()-6);
	LatLng place=new LatLng(event_info.latitude,event_info.longitude);
	map.addMarker(new MarkerOptions().position(place).title(start_time+" \n(+Контакты)")).
	showInfoWindow();
	map.moveCamera(CameraUpdateFactory.newLatLngZoom(place,16));
}
@Override
public void onErrorResponse(VolleyError error){
	Log.e("Volley eroor: ",error.toString());
}
public void StartChecking(View v){
	if(event_info!=null){
	 Intent qr_reader=new Intent(this,QRreader.class);
	 qr_reader.putExtra("id",event_info.id);
	 startActivity(qr_reader);
	}
}
class EventInfo{
	@SerializedName("id") String id;
	@SerializedName("title") String title;
	@SerializedName("latitude") float latitude;
	@SerializedName("longitude") float longitude;
	@SerializedName("phone") String phone;
	@SerializedName("email") String email;
	@SerializedName("date_start") String date_start;
	@SerializedName("date_end") String date_end;
	@SerializedName("skype") String skype;
}
}
