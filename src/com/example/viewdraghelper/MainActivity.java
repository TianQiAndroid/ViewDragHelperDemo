package com.example.viewdraghelper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private MyDrawerLayout mdl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		mdl = (MyDrawerLayout) findViewById(R.id.mdl);
		findViewById(R.id.btn).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
//		findViewById(R.id.iv1).setOnClickListener(this);
//		findViewById(R.id.iv2).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv1:
			Toast.makeText(getApplicationContext(), "cliclk iv1", 0).show();
			break;
		case R.id.iv2:
			Toast.makeText(getApplicationContext(), "cliclk iv2", 0).show();
			break;
		case R.id.btn:
			mdl.open();
		case R.id.btn2:
			mdl.close();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mdl.isMenuOpen()){
			mdl.close();
			return;
		}
		super.onBackPressed();
	}


}
