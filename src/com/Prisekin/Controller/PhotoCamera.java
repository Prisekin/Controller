package com.Prisekin.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import android.util.Log;

public class PhotoCamera extends Activity implements
SurfaceHolder.Callback,Camera.PictureCallback{
	Camera camera; SurfaceView photo_window;
	SurfaceHolder photo_holder;
	final static int GO_TO_QR_READER=0xf372a983;
@Override
public void onCreate(Bundle bund){
	super.onCreate(bund);
	setContentView(R.layout.camera);
	photo_window=(SurfaceView)findViewById(R.id.camera_preview);
	photo_holder=photo_window.getHolder();
	photo_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	photo_holder.addCallback(this);
}
@Override
public void onResume(){
	super.onResume();
	camera=Camera.open();
	camera.setDisplayOrientation(90);
}
@Override
public void onPause(){
	super.onPause();
	if(camera!=null){camera.stopPreview(); camera.release();}
}
//@Override

@Override
public void onActivityResult(int req,int res,Intent res_intent){
 if(res==GO_TO_QR_READER){finish();}	
}
@Override
public void surfaceCreated(SurfaceHolder holder){
	while(camera==null){}
	try{
		camera.setPreviewDisplay(holder);
		camera.startPreview();
		Log.e("All OK","Surface created");
	}
	catch(Exception e){	android.util.Log.e("Beda",e.toString());}
}
@Override
public void surfaceChanged(SurfaceHolder holder,int a,int b,int c){}
@Override
public void surfaceDestroyed(SurfaceHolder holder){}
public void MakePhoto(View v){
	camera.takePicture(null,null,this);
}
@Override
public void onPictureTaken(byte[] result_jpeg,Camera cam){
	File jpeg_file=new File(Environment.getExternalStorageDirectory(),"control.jpg");
	//FileOutputStream fos=null;
	try{FileOutputStream fos=new FileOutputStream(jpeg_file);
	fos.write(result_jpeg);
	fos.flush(); fos.close();
	Log.e("Picture OK: ",jpeg_file.getAbsolutePath());}
	catch(Exception e){Log.e("Picture",e.toString());}
	finally{
		Intent preview=new Intent(this,Preview.class);
		preview.putExtra("id",getIntent().getIntExtra("id",1));
		preview.putExtra("name",getIntent().getStringExtra("name"));
		startActivityForResult(preview,0);}
}
}
