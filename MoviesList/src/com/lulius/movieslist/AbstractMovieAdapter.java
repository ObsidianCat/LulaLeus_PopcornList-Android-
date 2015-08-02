package com.lulius.movieslist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public abstract class AbstractMovieAdapter extends ArrayAdapter<Movie> implements Downloader{

	Context context;
	List<Movie> movies;
	
	public AbstractMovieAdapter(Context context, List<Movie> movies) {
		super(context, android.R.id.content, movies);
		this.context = context;
		this.movies = movies;
	}
	
	/**
	 * Updates image view with the poster of the movie, if exists
	 * @param view
	 * @param movie
	 */
	protected void setMoviePoster(View view, Movie movie) {
		ImageView imgPoster = (ImageView) view.findViewById(getImagePosterId());
		if (movie.getImageBitmap() != null) {
		  
	      InputStream is = null;
	      try {
	      	is = new ByteArrayInputStream(movie.getImageBitmap());
	  		Bitmap bm = BitmapFactory.decodeStream(is);
	  		imgPoster.setImageBitmap(bm);	
	      }
	      finally {
	      	if (is != null) {
	      		try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	      	}
	      }
        }
		else if (movie.getImageUrl() != null) {
			new DownloadImageTask().execute(movie.getImageUrl(), this, imgPoster);
		}
	}
	
	@Override
	/**
	 * Sets movie poster, or default poster if no poster was received
	 */
	public void afterDownload(Bitmap b, ImageView v) {
		if (b != null) {
			v.setImageBitmap(b);
					
		}
		// No poster, set default one
		else {
			Drawable myDrawable = context.getResources().getDrawable(R.drawable.no_image);
			v.setImageDrawable(myDrawable);	
		}
	}

	@Override
	/**
	 * Not implemented
	 */
	public void afterDownload(Bitmap b) {
		return;		
	}

	@Override
	/**
	 * Not implemented
	 */
	public void afterDownload(Bitmap b, ImageView v, ProgressDialog progress) {
		return;
	}
	
	abstract int getImagePosterId();
}
