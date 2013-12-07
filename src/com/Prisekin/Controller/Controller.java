package com.Prisekin.Controller;

import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import org.json.JSONObject;
import org.json.JSONArray;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;


public class Controller extends Activity implements Listener,
ErrorListener{
    /**
     * Called when the activity is first created.
     */
	RequestQueue queue; Gson gson;
	EditText user,passwd; ProgressBar progress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        user=(EditText)findViewById(R.id.username);
        passwd=(EditText)findViewById(R.id.password);
        progress=(ProgressBar)findViewById(R.id.progress);
        queue=Volley.newRequestQueue(this);
    	gson=new Gson();
    }
    public void Authentication(View v){
    	progress.setVisibility(View.VISIBLE);
    	String user_name=user.getText().toString();
    	String password=passwd.getText().toString();
    	LoginData login=new LoginData("vgicebp@gmail.com","AndroControl");	//user_name,password);
    			//"moskvin@sibext.com","test_api");
    	JSONObject jobject=null;
    	try{jobject=new JSONObject(gson.toJson(login));
    	}
    	catch(Exception e){Log.e("JSON error: ",e.toString());}
    	JsonObjectRequest jreq=new JsonObjectRequest(//Method.GET,
    			"http://face-control.dev.sibext.com/login.json",
        		//"http://face-control.dev.sibext.com/users/sign_in.json",
    			//" http://face-control.dev.sibext.com/events.json",
    			//" http://face-control.dev.sibext.com/participants.json",
        		jobject,this,this);
        queue.add(jreq);
//        Log.e("JSON OK: ",jobject.toString());
    }
    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override public boolean onMenuItemSelected(int a,MenuItem item){
                 if(item.getItemId()==R.id.menu_exit){
                     Toast.makeText(this,"Good bye",Toast.LENGTH_LONG).show();
                     finish();};
        return true;
    }
@Override public void onResponse(Object response_obj){
	progress.setVisibility(View.GONE);
	JSONObject json_response=((JSONObject)response_obj);
	Log.e("All OK: ",json_response.toString());
	ServerResponse serv_resp=gson.fromJson(json_response.toString(),ServerResponse.class);
    if(serv_resp.success){
    	Intent show_map=new Intent(this,ShowMap.class);
    	show_map.putExtra("token",serv_resp.token);
    	startActivity(show_map);}
    else{
    	new AlertDialog.Builder(this).
    	setTitle("Ошибка авторизации: ").
    	setMessage(serv_resp.message).
    	show();    	
    }
}
@Override public void onErrorResponse(VolleyError error){
	progress.setVisibility(View.GONE);
	Log.e("Volley Error: ",error.toString());
	new AlertDialog.Builder(this).
	setTitle("Сетевая ошибка: ").
	setMessage(error.toString()).show();	//error.toString()).show();
	//Toast.makeText(this,"Authorization failure: \n"+
	//error.toString(),Toast.LENGTH_LONG).show();
}
class LoginData{
	@SerializedName("user") AuthData auth;
	public LoginData(String email,String passwd){
		this.auth=new AuthData();
		this.auth.password=passwd; this.auth.email=email;
	}
}
class AuthData{
	@SerializedName("email") String email;
	@SerializedName("password") String password;
}
class ServerResponse{
	@SerializedName("success") boolean success;
	@SerializedName("email") String email;
	@SerializedName("token") String token;
	@SerializedName("message") String message;
}
}
