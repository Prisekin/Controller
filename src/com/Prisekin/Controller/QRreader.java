package com.Prisekin.Controller;
/**
 * Created with IntelliJ IDEA.
 * User: prisekin
 * Date: 03.12.13
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.os.Process;

import com.Prisekin.Controller.ShowMap.EventInfo;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QRreader extends Activity implements
Listener,ErrorListener{
	boolean close_app=false; RequestQueue queue;
	ArrayList<Participants> participants; Gson gson;
@Override public void onCreate(Bundle bund){
    super.onCreate(bund);
    setContentView(R.layout.qr_reader);
    gson=new Gson();
    participants=new ArrayList<Participants>();
    queue=Volley.newRequestQueue(this);
    JsonArrayRequest jreq=new JsonArrayRequest(
    "http://face-control.dev.sibext.com/participants.json?event="+
    getIntent().getStringExtra("id"),
    this,this);
    queue.add(jreq);
}
@Override
public void onResponse(Object resp_obj){
	JSONArray json_resp=(JSONArray)resp_obj;
	for(int i=0;i<json_resp.length();i++){
		try{participants.add(gson.fromJson(json_resp.getString(i),Participants.class));}
		catch(Exception e){}
		Log.e("Name: ",participants.get(i).code);
	}
}
@Override
public void onErrorResponse(VolleyError error){
	Log.e("Volley eroor: ",error.toString());
}

@Override public void onActivityResult(int req,int res,Intent intent){
	final IntentResult scan_res=IntentIntegrator.parseActivityResult(req,res,intent);
	if((scan_res!=null)&&(scan_res.getContents()!=null)&&
		(participants.size()!=0))
			{
			String qr_code=scan_res.getContents();
			boolean status=false; int coincidence=0;
			for(int k=0;k<participants.size();k++){
				if(qr_code.equals(participants.get(k).code)){
					status=true; coincidence=k;}
			}
		 if(status){
			 Log.e("User code:","This is "+participants.get(coincidence).name);
			 Intent photo_camera=new Intent(this,PhotoCamera.class);
			 photo_camera.putExtra("id",participants.get(coincidence).id);
			 photo_camera.putExtra("name",participants.get(coincidence).name);
			startActivity(photo_camera); 
		 } else{
		 AlertDialog.Builder builder=new AlertDialog.Builder(this);
		 builder.setTitle("Неправильный QR-код:").
		 setMessage("Код не совпадает ни с одним из кодов участников мероприятия.").
		 setPositiveButton("Повтор",null).show();
		 }
	}
}
@Override public void onDestroy(){
	super.onDestroy();
	if(close_app){Process.killProcess(Process.myPid());}
}
public void ButtonClicked(View v){
switch(v.getId()){
case R.id.scan:
	IntentIntegrator scanner=new IntentIntegrator(this);
	scanner.initiateScan();
	break;
case R.id.exit: close_app=true; finish(); break;
  }	
 }
class Participants{
@SerializedName("id") int id;
@SerializedName("name") String name;
@SerializedName("code") String code;
@SerializedName("event_id") String event_id;
@SerializedName("url") String url;
}

}
