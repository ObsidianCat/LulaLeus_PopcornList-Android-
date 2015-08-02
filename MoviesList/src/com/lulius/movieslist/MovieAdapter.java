package com.lulius.movieslist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MovieAdapter extends AbstractMovieAdapter {
	
	
    public MovieAdapter(Context context, List<Movie> movies) {
		super(context, movies);
	}   

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        View view = vi.inflate(R.layout.list_item_movie, null);
	
        Movie movie = movies.get(position);
        if(movie.isWatched()){
            view.setBackgroundResource(R.color.blue_baby);
        }
        else {
        	TextView tvIsWatched = (TextView) view.findViewById(R.id.textViewisWatchedItem);
			tvIsWatched.setVisibility(View.INVISIBLE);  	
        }
        
        TextView tvTitle = (TextView) view.findViewById(R.id.textViewItemMovieTitle);
        tvTitle.setText(movie.getTitle());
        
        TextView tvYear = (TextView) view.findViewById(R.id.textViewItemMovieYear);
        int movieYear = movie.getYear();
        tvYear.setText(Integer.toString(movieYear));

        setMoviePoster(view, movie);
      
        return view;
	}

	@Override
	int getImagePosterId() {
		return R.id.imageViewItemMoviePoster;
	}	
}
