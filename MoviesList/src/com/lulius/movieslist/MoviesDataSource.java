package com.lulius.movieslist;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDataSource {

	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	
	private static final String[] allColumns = {
		MoviesDBOpenHelper.COLUMN_ID,
		MoviesDBOpenHelper.COLUMN_TITLE,
		MoviesDBOpenHelper.COLUMN_DESC,
		MoviesDBOpenHelper.COLUMN_YEAR,
		MoviesDBOpenHelper.COLUMN_POSTER,
		MoviesDBOpenHelper.COLUMN_POSTER_BITMAP,
		MoviesDBOpenHelper.COLUMN_IS_WATCHED
	};
	
	public MoviesDataSource(Context context){
		dbhelper = new MoviesDBOpenHelper(context);
	}
	
	public void open(){
		database = dbhelper.getWritableDatabase();
	}
	public void close(){
		dbhelper.close();
	}
	
	public Movie create(Movie movie){
		ContentValues values = populateValues(movie);

		long insertId = database.insert(MoviesDBOpenHelper.TABLE_MOVIES, null, values);
		
		movie.setId(insertId);
		return movie;
	}
	
	public void update(Movie movieUpd){
		// the values to insert:
		ContentValues values = populateValues(movieUpd);

		Long MovieId = movieUpd.getId(); 
		String movieIdString = Long.toString(MovieId);
		String[] args = new String[]{movieIdString};
		
		//insert
		database.updateWithOnConflict(MoviesDBOpenHelper.TABLE_MOVIES, values, MoviesDBOpenHelper.COLUMN_ID + " =?", args,SQLiteDatabase.CONFLICT_REPLACE);
	}
	
	/**
	 * Populate values container with movie data
	 * @param movie
	 * @return populated values
	 */
	private ContentValues populateValues(Movie movie) {
		ContentValues values = new ContentValues();
		values.put(MoviesDBOpenHelper.COLUMN_TITLE, movie.getTitle());
		values.put(MoviesDBOpenHelper.COLUMN_DESC, movie.getDescription());
		values.put(MoviesDBOpenHelper.COLUMN_YEAR, movie.getYear());
		values.put(MoviesDBOpenHelper.COLUMN_POSTER, movie.getImageUrl());
		values.put(MoviesDBOpenHelper.COLUMN_POSTER_BITMAP, movie.getImageBitmap());
		values.put(MoviesDBOpenHelper.COLUMN_IS_WATCHED, movie.isWatched());
		
		return values;
	}

	public void remove(Movie movieDel) {
		String where = MoviesDBOpenHelper.COLUMN_ID + "=?";
		String movieId = Long.toString(movieDel.getId());
		database.delete(MoviesDBOpenHelper.TABLE_MOVIES, where, new String[] {movieId});
	}
	
	public void removeAll() {
		database.delete(MoviesDBOpenHelper.TABLE_MOVIES, null, null);
	}

	
	public List<Movie> findAll(){
		Cursor cursor = database.query(MoviesDBOpenHelper.TABLE_MOVIES, allColumns, null, null, null, null, null);
		List<Movie> movies = cursorToList(cursor);
		return movies;
	}
	
	public Movie findById(Long movieIdNow){
		Cursor cursor = database.query(MoviesDBOpenHelper.TABLE_MOVIES, allColumns, MoviesDBOpenHelper.COLUMN_ID+"=?", new String[]{String.valueOf(movieIdNow)}, null, null, null);

		Movie movie = null; 
		
		//get the data:
		if (cursor.moveToNext()){
			movie = rowToMovie(cursor);
		}
		return movie;
	}//end of find movie
	

	private List<Movie> cursorToList(Cursor cursor){
		List<Movie> movies = new ArrayList<Movie>();
		if(cursor.getCount()>0){
			while(cursor.moveToNext()){
				Movie movie = rowToMovie(cursor);
				
				movies.add(movie);
			}//end of while we have data
			
		}//end of if cursor not null
		return movies;
	}//end of creating list of movies from database

	/**
	 * Converts cursor row to movie object
	 * @param cursor
	 * @return populated movie
	 */
	private Movie rowToMovie(Cursor cursor) {
		long cId = cursor.getLong(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_ID));
		int cYear = cursor.getInt(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_YEAR));
		String cTitle = cursor.getString(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_TITLE));
		String cDescription = cursor.getString(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_DESC));
		String cImage  = cursor.getString(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_POSTER));
		byte[] bitmap = cursor.getBlob(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_POSTER_BITMAP));
		int isWatched = cursor.getInt(cursor.getColumnIndex(MoviesDBOpenHelper.COLUMN_IS_WATCHED));

		Movie movie = new Movie(cTitle, cDescription, cImage, cYear, (isWatched == 1) ? true : false);
		movie.setId(cId);
		if (bitmap != null) {
			movie.setImageBitmap(bitmap);	
		}
		
		return movie;
	}
	
}
