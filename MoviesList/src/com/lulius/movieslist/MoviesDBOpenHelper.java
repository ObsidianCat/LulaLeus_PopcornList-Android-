package com.lulius.movieslist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDBOpenHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "movies.db";
	private static final int DATABASE_VERSION =1;
	
	public static final String TABLE_MOVIES = "myMoviesList";
	public static String COLUMN_ID = "movieId";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_POSTER = "poster";
	public static final String COLUMN_POSTER_BITMAP = "posterBitmap";
	public static final String COLUMN_IS_WATCHED = "isWatched";
	

	private static final String TABLE_CREATE =
			"CREATE TABLE " + TABLE_MOVIES + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_TITLE + " TEXT, " +
			COLUMN_DESC + " TEXT, " +
			COLUMN_YEAR + " NUMERIC, " +
			COLUMN_POSTER + " TEXT, " +
			COLUMN_POSTER_BITMAP + " BLOB, " +
			COLUMN_IS_WATCHED + " NUMERIC " +
			" )";
	
	
	public MoviesDBOpenHelper (Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db){
		db.execSQL(TABLE_CREATE);	
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXIST "+ TABLE_MOVIES);
		onCreate(db);
	}
	
}
