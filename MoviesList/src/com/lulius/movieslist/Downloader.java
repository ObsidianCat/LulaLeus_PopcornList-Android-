package com.lulius.movieslist;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;


/**
 * Each activity that needs to download images asynchronously should implement this interface 
 * @author Lula Leus
 */
public interface Downloader {

	public void afterDownload(Bitmap b);
	
	public void afterDownload(Bitmap b, ImageView v);
	
	public void afterDownload(Bitmap b, ImageView v, ProgressDialog progress);

}
