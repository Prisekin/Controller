package com.Prisekin.Controller;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Environment;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;

import org.json.JSONObject;
import android.util.Log;
import android.app.AlertDialog;

public class Preview extends Activity implements
Listener<JSONObject>,ErrorListener{
	ImageView photo; String filename;
	int response_code_value=0;
@Override
public void onCreate(Bundle bund){
	super.onCreate(bund);
	setContentView(R.layout.preview);
	photo=(ImageView)findViewById(R.id.image_preview);
}
@Override
public void onResume(){
	super.onResume();
	filename=Environment.getExternalStorageDirectory().
			getAbsolutePath()+"/control.jpg";
	photo.setImageBitmap(BitmapFactory.decodeFile(filename));
	photo.setRotation(90);
	Toast.makeText(this,"Участник: \n"+getIntent().getStringExtra("name"),Toast.LENGTH_LONG).show();
}
public void MakePhotoAgain(View v){
	finish();
}
public void SendPhoto(View v){
	String file_content="";
	try{
		File file=new File(filename);
		FileInputStream fis=new FileInputStream(file);
		byte[] byte_array=new byte[(int)(file.length())];
		fis.read(byte_array);
		file_content=new String(Base64.encode(byte_array,0),"UTF-8");
	}
	catch(Exception e){	Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}
	PhotoObject photo_obj=new PhotoObject(getIntent().getStringExtra("name"),file_content);
	Gson gson=new Gson();
	String string2send=gson.toJson(photo_obj);
	JSONObject json2send=null;
	try{json2send=new JSONObject(string2send);}
	catch(Exception e){	Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}
	ModifiedRequest jreq=new ModifiedRequest(
			this,Method.PUT,
			"http://face-control.dev.sibext.com/participants/"+
			getIntent().getIntExtra("id",2)+".json",
			json2send,this,this);
	Volley.newRequestQueue(this).add(jreq);
	File out=new File(Environment.getExternalStorageDirectory(),"out.txt");
	try{FileOutputStream fos=new FileOutputStream(out);
	fos.write(string2send.getBytes("UTF-8")); fos.flush(); fos.close();}
	catch(Exception e){	Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show(); }

	
}
@Override
public void onResponse(JSONObject json_obj){
	Log.e("Send OK",json_obj.toString());
	//Toast.makeText(this,json_obj.toString(),Toast.LENGTH_LONG).show();
	new AlertDialog.Builder(this).setTitle("Send OK").
	setMessage(json_obj.toString()).show();
}
@Override 
public void onErrorResponse(VolleyError e){
	/*new AlertDialog.Builder(this).setTitle("Volley result: ").
	setMessage("Response Code: "+response_code_value+" \n").
	setPositiveButton("Далее",new OnClickListener(){
		@Override
		public void onClick(DialogInterface dlg,int a){
			if(response_code_value==204){setResult(PhotoCamera.GO_TO_QR_READER); finish();}
		}
	}).show();*/
	if(response_code_value==204){setResult(PhotoCamera.GO_TO_QR_READER); finish();}
}
class PhotoObject{
	@SerializedName("participant") PhotoInside part;
	public PhotoObject(String name,String photo){
		this.part=new PhotoInside();
		this.part.name=name; this.part.photo=photo;
	}
}
class PhotoInside{
	@SerializedName("name") String name;
	@SerializedName("photo") String photo;
	
}
class ModifiedRequest extends JsonObjectRequest{
	Context context;
	public ModifiedRequest(Context context, int method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
		this.context=context;
	}
@Override
public Response<JSONObject> parseNetworkResponse(NetworkResponse net_resp){
	response_code_value=net_resp.statusCode;
	return super.parseNetworkResponse(net_resp);
}
}
}
