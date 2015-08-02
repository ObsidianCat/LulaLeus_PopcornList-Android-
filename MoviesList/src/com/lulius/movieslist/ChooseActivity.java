package com.lulius.movieslist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChooseActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		
		Button btnCreate = (Button) findViewById(R.id.button1CreateMovie);
		Button btnAdd = (Button) findViewById(R.id.button2FindMovie);
		btnCreate.setOnClickListener(this);
		btnAdd.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting_menu, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
		case  R.id.settings_back_home:
			finish();
			break;
		default:
//	        return super.onOptionsItemSelected(item);
			break;
		}
        return true;
    }


	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.button1CreateMovie:
			intent = new Intent(this, AddMovieActivity.class);
			startActivity(intent);
			break;
		case R.id.button2FindMovie:
			intent = new Intent(this, SearchListActivity.class);
			startActivity(intent);
			break;
		}
	}//end of on click
}
