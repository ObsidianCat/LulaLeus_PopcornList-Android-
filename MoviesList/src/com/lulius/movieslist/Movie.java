package com.lulius.movieslist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
	public String title;
	private String description;
	private String image;
	private long id;
	private int year;
	private boolean isWatched;
	private byte[] imageBitmap;

	public boolean isWatched() {
		return isWatched;
	}

	public void setIsWatched(boolean flagIsWatched) {
		this.isWatched = flagIsWatched;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageUrl() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public String toString(){
		return title;
	}

	
	//Parcelable implementation
	
    public Movie() {
    }

    public Movie(Parcel in) {
         id = in.readLong();
         title = in.readString();
         description = in.readString();
         year = in.readInt();
         image = in.readString();
         if (in.readInt() == 1) {
        	 isWatched = true;	 
         }
         else {
        	 isWatched = false;
         }     	
    }

    public Movie(String titleNow, String descriptionNow, String imageUrl,
			int yearNow, boolean isWatchedNow) {
    	setTitle(titleNow);
		setDescription(descriptionNow);
		setImage(imageUrl);
		setTitle(titleNow);
		setYear(yearNow);
		setIsWatched(isWatchedNow);
	}

	@Override
    public int describeContents() {
         return 0;
    }
   
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        
         dest.writeLong(id);
         dest.writeString(title);
         dest.writeString(description);
         dest.writeInt(year);
         dest.writeString(image);
         dest.writeInt(isWatched ? 1 : 0);
    }

    public static final Parcelable.Creator<Movie> CREATOR =
              new Parcelable.Creator<Movie>() {

         @Override
         public Movie createFromParcel(Parcel source) {
              return new Movie(source);
         }

         @Override
         public Movie[] newArray(int size) {
              return new Movie[size];
         }

    };//end of parcelable



	public byte[] getImageBitmap() {
		return imageBitmap;
	}
	
	public void setImageBitmap(byte[] imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public void setImageBitmap(Bitmap imageBitmap) {
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

			byte[] bArray = bos.toByteArray();
			this.imageBitmap = bArray;	
		}
		finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
