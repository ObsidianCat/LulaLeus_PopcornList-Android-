package com.lulius.movieslist;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PopcornListActivity extends ListActivity implements OnClickListener, OnItemClickListener  {
	
	private List<Movie> movies = new ArrayList<Movie>();
	MoviesDataSource datasourse;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films_list);

        datasourse = new MoviesDataSource(this);
        datasourse.open();
        
        movies = datasourse.findAll();
        refreshDisplay();
        
        Button btnAddMovie = (Button) findViewById(R.id.button1Add);
        btnAddMovie.setOnClickListener(this);
        
        ListView listViewMovies = getListView();
        registerForContextMenu(listViewMovies);
        listViewMovies.setOnItemClickListener(this); // click on ITEM in the list

    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	// TODO Auto-generated method stub
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.popcorn_context_menu, menu);
    	
    }
    
    
    //function when I choose something from context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Movie m = movies.get(info.position);
    	switch (item.getItemId()) {
        case R.id.action_editMovie:
    		Long movieId  = m.getId();
    		startEdit(movieId);
            return true;
        case R.id.action_deleteMovie:
        	datasourse.remove(m);
        	movies.remove(m);
        	refreshDisplay();
            return true;
        default:
            return super.onContextItemSelected(item);
		}//end of switch
    }


    private void refreshDisplay() {
		ArrayAdapter<Movie> adapter = new MovieAdapter(this, movies);
		setListAdapter(adapter);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popcorn_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
		case  R.id.action_delete_all_movies:
			datasourse.removeAll();
	        movies = datasourse.findAll();
			refreshDisplay();
        	Toast.makeText(this, R.string.all_removed, Toast.LENGTH_LONG).show();
			break;
		default:
//	        return super.onOptionsItemSelected(item);
			break;
		}
        return true;
    }
    
	protected void onResume(){
		super.onResume();
		datasourse.open();
	}
	
	protected void onPause(){
		super.onPause();
		datasourse.close();
	}	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1Add:
			Intent intent = new Intent(this, ChooseActivity.class);
			startActivity(intent);
			break;
		}		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Movie m = movies.get(position);
		Long movieId  = m.getId();
		startEdit(movieId);		
	}


	private void startEdit(Long movieId) {
		Intent intent = new Intent(this, AddMovieActivity.class);
		intent.putExtra("movieId", movieId);
		startActivity(intent);
		
	}
	
}
