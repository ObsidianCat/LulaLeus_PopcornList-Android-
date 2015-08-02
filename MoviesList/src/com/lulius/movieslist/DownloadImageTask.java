package com.lulius.movieslist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

//-- DownloadImageTask:
public class DownloadImageTask extends AsyncTask<Object, Integer, Bitmap> {

	// we'll show a progress dialog while downloading :
	// private ProgressDialog progressDialog;

	private Downloader callback;
	private ImageView imageView;
	private ProgressDialog progressDiaglog;

	
	@Override
	protected Bitmap doInBackground(Object... params) {
		// in the background:

		// get the address from the params:
		String address = (String) params[0];
		if (params.length > 1) {
			callback = (Downloader) params[1];	
		}		
		if (params.length > 2) {
			imageView = (ImageView) params[2];
		}
		if (params.length > 3) {
			progressDiaglog  = (ProgressDialog) params[3];
		}
		

		HttpURLConnection connection = null;
		InputStream stream = null;
		ByteArrayOutputStream outputStream = null;

		// the bitmap will go here:
		Bitmap b = null;

		try {
			// build the URL:
			URL url = new URL(address);
			// open a connection:
			connection = (HttpURLConnection) url.openConnection();

			// check the connection response code:
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// not good..
				return null;
			}

			// the input stream:
			stream = connection.getInputStream();

			// get the length:
			int length = connection.getContentLength();
			// tell the progress dialog the length:
			// a stream to hold the read bytes.
			// (like the StringBuilder we used before)
			outputStream = new ByteArrayOutputStream();

			// a byte buffer for reading the stream in 1024 bytes chunks:
			byte[] buffer = new byte[1024];

			int bytesRead = 0;

			// read the bytes from the stream
			while ((bytesRead = stream.read(buffer, 0, buffer.length)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			// flush the output stream - write all the pending bytes in its
			// internal buffer.
			outputStream.flush();

			// get a byte array out of the outputStream
			// theses are the bitmap bytes
			byte[] imageBytes = outputStream.toByteArray();

			// use the BitmapFactory to convert it to a bitmap
			b = BitmapFactory.decodeByteArray(imageBytes, 0, length);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				// close connection:
				connection.disconnect();
			}
			if (outputStream != null) {
				try {
					// close output stream:
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return b;
	}

	/**
	 * Choose which callback should be called after image is downloaded
	 */
	protected void onPostExecute(Bitmap b) {
		
		if (progressDiaglog != null) {
			callback.afterDownload(b, imageView, progressDiaglog);
		}
		else if (imageView != null) {
			callback.afterDownload(b, imageView);
		}
		else {
			callback.afterDownload(b);	
		}		
	};
}// end of image task
