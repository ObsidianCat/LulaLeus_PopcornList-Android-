package com.lulius.movieslist;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchListActivity extends ListActivity implements OnClickListener, OnItemClickListener  {
	public static final String SEARCH_API = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=5jgqapje2acv9ema4avgtdh4&q=";
	public static final String SEARCH_API_PARAMETRS = "&page_limit=50";
	public static final String SEARCH_INITIAL_LIST = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/opening.json?apikey=5jgqapje2acv9ema4avgtdh4&limit=10";
	
	private List<Movie> foundMovies = new ArrayList<Movie>();
	MoviesDataSource datasourse;
	boolean isItFirstSearch;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_list);
		
		isItFirstSearch = true;
        datasourse = new MoviesDataSource(this);
        datasourse.open();
        requestData(SEARCH_INITIAL_LIST);
        
        Button btnSearch = (Button) findViewById(R.id.button2go);
        btnSearch.setOnClickListener(this);
        
        Button buttonCancel = (Button) findViewById(R.id.buttonSearchCancel);
        buttonCancel.setOnClickListener(this);
        
        ListView listViewMovies = getListView();
        listViewMovies.setOnItemClickListener(this); // click on ITEM in the list
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
			Intent intent = new Intent(this, PopcornListActivity.class);
			startActivity(intent);
			break;
		}
        return true;
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Movie m = foundMovies.get(position);
		Intent intent = new Intent(this, AddMovieActivity.class);
		intent.putExtra("foundMovie", true);
		intent.putExtra("Movie", m);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button2go:
			EditText searchEd = (EditText) findViewById(R.id.editText1Search);
			String searchText;
			try {
				searchText = URLEncoder.encode(searchEd.getText().toString(), "UTF-8");
				requestData(SEARCH_API+searchText+SEARCH_API_PARAMETRS);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case R.id.buttonSearchCancel:
			finish();
			break;
		default:
			break;
		}
	}
	
	protected void onResume(){
		super.onResume();
		datasourse.open();
	}
	
	protected void onPause(){
		super.onPause();
		datasourse.close();
	}
	
	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}
	
	class WebSearchTask extends AsyncTask<String, Void, String>{
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SearchListActivity.this);
			dialog.setTitle("Loading");
			dialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String content = HttpManager.getData(params[0]);
			return content;
		}
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
//			
			if (result != null) {
				try {
					JSONObject resultJson = new JSONObject(result);			
					JSONArray listJSON = resultJson.getJSONArray("movies");
					foundMovies.clear();
					for (int i = 0; i < listJSON.length(); i++) {
						
						JSONObject obj = listJSON.getJSONObject(i);
						String title = obj.getString("title");
						String desc = obj.getString("synopsis");
						int year = obj.getInt("year");
						
						JSONObject objImg = obj.getJSONObject("posters");
						String poster = objImg.getString("thumbnail");
						
						Movie movieNow = new Movie(title, desc, poster, year, false);
						foundMovies.add(movieNow);
					}
					
					if(isItFirstSearch == false){
						swapTitle();
					}
					refreshDisplay();
					isItFirstSearch = false;

				} catch (JSONException e) {
					e.printStackTrace();
//					return null;
				}
			}
			else {
				Log.i(this.getClass().getName(), "Search results are empty");
			}
		}//end on post execute

		private void swapTitle() {
			TextView currentTitle = (TextView) findViewById(R.id.textViewSearchTitle);
			currentTitle.setText(R.string.user_search_title);
		}
		
	}//end of async task class

	private void requestData(String uri){
		WebSearchTask task = new WebSearchTask();
		task.execute(uri);
		
	}
    private void refreshDisplay() {
		ArrayAdapter<Movie> adapter = new MovieAdapterSearch(this, foundMovies);
		setListAdapter(adapter);
	}
    
    
}
