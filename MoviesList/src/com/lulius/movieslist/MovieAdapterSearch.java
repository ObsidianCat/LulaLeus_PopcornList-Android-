package com.lulius.movieslist;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class MovieAdapterSearch extends AbstractMovieAdapter implements Downloader{
    
	public MovieAdapterSearch(Context context, List<Movie> movies) {
		super(context, movies);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        View view = vi.inflate(R.layout.list_item_search, null);
	
        Movie movie = movies.get(position);
        if(movie.isWatched()){
            view.setBackgroundResource(R.color.blue_baby);
        }
        
        TextView tvTitle = (TextView) view.findViewById(R.id.textViewItemSearchTitle);
        tvTitle.setText(movie.getTitle());
        
        setMoviePoster(view, movie);
             
        return view;
	}


	@Override
	int getImagePosterId() {
		return R.id.imageViewItemSearchPoster;
	}
	
}
