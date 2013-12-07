package com.Prisekin.Controller;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.view.View;

public class ShowContacts extends Activity
implements OnClickListener{
	TextView phone,email,skype; Intent contacts;
@Override
public void onCreate(Bundle bund){
	super.onCreate(bund);
	setContentView(R.layout.contacts);
	contacts=this.getIntent();
	phone=(TextView)findViewById(R.id.contact_phone);
	email=(TextView)findViewById(R.id.contact_email);
	skype=(TextView)findViewById(R.id.contact_skype);
	phone.setText(contacts.getStringExtra("phone"));
	email.setText(contacts.getStringExtra("email"));
	skype.setText(contacts.getStringExtra("skype"));
	phone.setOnClickListener(this);
	email.setOnClickListener(this);
	skype.setOnClickListener(this);
}
@Override
public void onClick(View v){
switch(v.getId()){
case R.id.contact_phone: Intent call=new Intent(Intent.ACTION_DIAL);
call.setData(Uri.parse("tel:"+phone.getText())); 
startActivity(call); break;
case R.id.contact_email: Intent send=new Intent(Intent.ACTION_SEND);
send.setType("text/plain"); send.putExtra(Intent.EXTRA_EMAIL,new String[] {(String)email.getText()}); 
send.putExtra(Intent.EXTRA_SUBJECT,"Controller");
startActivity(send); break;
}
}
}
