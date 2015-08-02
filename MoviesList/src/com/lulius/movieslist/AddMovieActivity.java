package com.lulius.movieslist;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddMovieActivity extends Activity implements OnClickListener, Downloader {
	private static final int IMAGE_INDEX = 3;
	private static final int DESCRIPTION_INDEX = 2;
	private static final int TITLE_INDEX = 0;
	private static final int YEAR_INDEX = 1;
	MoviesDataSource datasourse;
	boolean isNewMovie = true;
	boolean isFoundMovie = false;
	boolean isMovieFromDB = false;
	boolean isDataValid = true;
	
	ProgressDialog progressDialog;

	String moviePosterUrl;
	Movie movieFromIntent;
	Movie readyMovie;
	private AddMovieActivity activity;

	private class ShowPosterListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
			progressDialog = new ProgressDialog(AddMovieActivity.this);
			progressDialog.show();
			ImageView imageView = (ImageView) findViewById(R.id.imageViewAddMoviePoster);
			
			EditText edPoster = (EditText) findViewById(R.id.editText3PosterLink);
			String moviePosterUrl = edPoster.getText().toString();
			new DownloadImageTask().execute(moviePosterUrl, activity, imageView, progressDialog);	
		}
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_movie);
		
        datasourse = new MoviesDataSource(this);
        datasourse.open();

        activity = this;
        
		Button btnOK = (Button) findViewById(R.id.buttonAddMovieOk);
		Button btnCancel = (Button) findViewById(R.id.button2Cancel);
		Button btnShowPoster = (Button) findViewById(R.id.buttonPosterLink);

		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnShowPoster.setOnClickListener(new ShowPosterListener());
		
		Bundle extras = getIntent().getExtras(); 
		if(extras != null)
		{
			if(extras.getLong("movieId") !=0){
				isNewMovie = false;
				isMovieFromDB = true;
				long movieIdNow = extras.getLong("movieId");
				movieFromIntent = datasourse.findById(movieIdNow);
				setMovieFromIntent(movieFromIntent);
				swapTitle();
			}
			else if(extras.getBoolean("foundMovie") ==true){
				isNewMovie = false;
				isFoundMovie = true;
				movieFromIntent = extras.getParcelable("Movie");
				setMovieFromIntent(movieFromIntent);
			}
		}//end of get extras

		if (isNewMovie==false) {
			moviePosterUrl = movieFromIntent.getImageUrl();
			new DownloadImageTask().execute(moviePosterUrl, this);	
		}

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
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.button2Cancel:
			finish();
			break;
		case R.id.buttonAddMovieOk:
				onAddMovie();
				break;
		}//end of switch
	}//end of onClick

	private void onAddMovie() {
		if(isNewMovie){
			newMovieFromData();
			if(isDataValid){
				datasourse.create(readyMovie);
				backToPopcornList();
			}
			else {
				Toast.makeText(this, "Please fill the required information", Toast.LENGTH_LONG).show();
			}
		}//end isNewMovie
		else {
			updateMovieData();
			if(isDataValid){
				if (isFoundMovie) {
					datasourse.create(movieFromIntent);
					backToPopcornList();
				}//end of found movie
				else if(isMovieFromDB){
					datasourse.update(movieFromIntent);
					backToPopcornList();
				}
			}
			else{
				Toast.makeText(this, "Please fill the required information", Toast.LENGTH_LONG).show();
			}
		}//end of if movie from db or found movie
	}

	private void backToPopcornList() {
		Intent intent = new Intent(this, PopcornListActivity.class);
		startActivity(intent);
		finish();
	}

	private ArrayList<String> gatherDataFromFields(){
		EditText edTitle = (EditText) findViewById(R.id.editText1Title);
		EditText edYear = (EditText) findViewById(R.id.editText1year);
		EditText edDesc = (EditText) findViewById(R.id.editText2Desc);
		EditText edPoster = (EditText) findViewById(R.id.editText3PosterLink);
		
		String titleNow = edTitle.getText().toString();
		String descriptionNow = edDesc.getText().toString();
		String imageNow = edPoster.getText().toString();
		String yearNowS = edYear.getText().toString();
		
		ArrayList<String> gatheredData = new ArrayList<String>();
		gatheredData.add(titleNow);
		gatheredData.add(yearNowS);
		gatheredData.add(descriptionNow);
		gatheredData.add(imageNow);

		return gatheredData;
	}
	
	private void newMovieFromData() {
		ArrayList<String> gatheredData = gatherDataFromFields();

		CheckBox checkBoxIsWatched = (CheckBox) findViewById(R.id.checkBox1);		
		 
		if(fieldsValidation(gatheredData)){
			int yearNow = Integer.parseInt(gatheredData.get(YEAR_INDEX));
			String imageUrl = gatheredData.get(IMAGE_INDEX);
			readyMovie = new Movie(gatheredData.get(TITLE_INDEX), gatheredData.get(DESCRIPTION_INDEX), imageUrl, yearNow, checkBoxIsWatched.isChecked());		
			// Load the image
			new DownloadImageTask().execute(imageUrl, this);
		}

	}//end of gathering data from fields
	
	private Movie updateMovieData() {
		ArrayList<String> gatheredData = gatherDataFromFields();

		CheckBox checkBoxIsWatched = (CheckBox) findViewById(R.id.checkBox1);
		
		
		if(fieldsValidation(gatheredData)){
			int yearNow = Integer.parseInt(gatheredData.get(YEAR_INDEX));
			movieFromIntent.setDescription(gatheredData.get(DESCRIPTION_INDEX));
			movieFromIntent.setTitle(gatheredData.get(TITLE_INDEX));
			movieFromIntent.setYear(yearNow);
			movieFromIntent.setImage(gatheredData.get(IMAGE_INDEX));
			movieFromIntent.setIsWatched(checkBoxIsWatched.isChecked());
		}
		return movieFromIntent;
	}//end of movie update
	
	
	private void setMovieFromIntent(Movie movieFromDB){
		EditText edTitle = (EditText) findViewById(R.id.editText1Title);
		EditText edYear = (EditText) findViewById(R.id.editText1year);
		EditText edDesc = (EditText) findViewById(R.id.editText2Desc);
		EditText edPoster = (EditText) findViewById(R.id.editText3PosterLink);
		CheckBox checkBoxIsWatched = (CheckBox) findViewById(R.id.checkBox1);
		
		int year = movieFromDB.getYear();
		String yearAsString = Integer.toString(year);
		
		// Populate fields
		checkBoxIsWatched.setChecked(movieFromDB.isWatched());				
		edTitle.setText(movieFromDB.getTitle());
		edYear.setText(yearAsString);
		edDesc.setText(movieFromDB.getDescription());
		edPoster.setText(movieFromDB.getImageUrl());
	}
	
	protected void onResume(){
		super.onResume();
		datasourse.open();
	}
	
	protected void onPause(){
		super.onPause();
		datasourse.close();
	}
	
	private void swapTitle() {
		Button currentTitle = (Button) findViewById(R.id.buttonAddMovieOk);
		currentTitle.setText(R.string.update_movie);
	}

	
	@Override
	/**
	 * Populate image bitmap after it has been successfully downloaded
	 */
	public void afterDownload(Bitmap resultBitmap) {
		ImageView imageView = (ImageView) findViewById(R.id.imageViewAddMoviePoster);

		if (resultBitmap == null) {
			
			Toast.makeText(AddMovieActivity.this, "error loading image",
					Toast.LENGTH_LONG).show();
			Drawable myDrawable = getResources().getDrawable(R.drawable.no_image);
			imageView.setImageDrawable(myDrawable);
			
		} else {
			// set the image bitmap:
			imageView.setImageBitmap(resultBitmap);
			if (movieFromIntent != null) {
				movieFromIntent.setImageBitmap(resultBitmap);
			}
			if (readyMovie != null) {
				readyMovie.setImageBitmap(resultBitmap);
			}
		}
	}

	@Override
	/**
	 * Not implemented
	 */
	public void afterDownload(Bitmap b, ImageView v) {
		return;
	}

	
	public boolean fieldsValidation(ArrayList<String> dataStrings){
		ArrayList<String> dataForValidation = new ArrayList<String>();
		dataForValidation.add(dataStrings.get(0));
		dataForValidation.add(dataStrings.get(1));
		dataForValidation.add(dataStrings.get(2));

		for (String data: dataForValidation) {
			if(data == null || data.isEmpty()) { 
				isDataValid = false;
				return isDataValid;
			}
		}
		isDataValid = true;
		return isDataValid;
	}//end of validation method

	@Override
	/**
	 * Handle image download with progress indicator
	 */
	public void afterDownload(Bitmap resultBitmap, ImageView v, ProgressDialog progress) {
		progress.dismiss();
		afterDownload(resultBitmap);
	}
}
