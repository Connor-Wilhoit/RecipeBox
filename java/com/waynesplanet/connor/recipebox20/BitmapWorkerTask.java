package com.waynesplanet.connor.recipebox20;

/*
 * Class to help with the issues surrounding
 * displaying the Bitmap image in
 * GlideViewImageActivity.java
 * 	* implements multi-threading (via extension of AsyncTask)
 * 	* should help handle memory issues
 * 	* should handle activity lifecycle changes issues
 *
 *
 * 	see the following url for details:
 * 	https://stuff.mit.edu/afs/sipb/project/android/docs/training/displaying-bitmaps/process-bitmap.html
 *

	 Class Usage:
	 Example [1] (I think/hope):
	 public void loadBitmap(String imageFilePath, ImageView imageView) {
	 BitmapTaskWorker task = new BitmapWorkerTask(imageView);
	 task.execute(imageFilePath);
	 }

 */
import java.io.File;
//import java.util.Collections;
import java.lang.ref.WeakReference;
//import java.util.Map;
//import java.util.WeakHashMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;

import android.os.Handler;
//import android.content.Context;
import android.os.AsyncTask;
//import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
//import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
//import androidx.core.graphics.drawable.RoundedBitmapDrawable;

import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

	// The following 3 objects are from referencing ImageUtilities.java:
	private static final float PHOTO_BORDER_WIDTH = 3.0f;
	private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	private static final Paint sStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private static final String TAG = BitmapWorkerTask.class.getSimpleName();
	private final int tempimage = R.drawable.temp_image;

	// Handler to display image(s) in UI thread:
	Handler handler = new Handler();

	private String pathToBitmapFile = null;
	private BitmapFactory.Options bitmapOptions;// make this final, if possible
	private final WeakReference<ImageView> imageViewReference;

	public BitmapWorkerTask(ImageView imageView) {
		// Use a WeakReference to ensure the ImageView can be garbage collected:
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	// Decode image in the background:
	@Override
	protected Bitmap doInBackground(String... params) {
		return BitmapFactory.decodeFile(params[0]);
	}

	// Once complete, see if ImageView is still around, and set the bitmap:
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	// Code taken and modified from ImageLoader.java:
	private Bitmap decodeBitmapImageFile(File file) {
		int height = 480;
		int width = 640;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		String abspath = file.getAbsolutePath();
		BitmapFactory.decodeFile(abspath, options);
		int out_height = options.outHeight;
		int out_width = options.outWidth;
		int inSampleSize = 1;

		if (out_height > height || out_width > width) {
			int half_height = out_height / 2;
			int half_width = out_width / 2;
			while ((half_height / inSampleSize) > height && (half_width / inSampleSize) > width) {
				inSampleSize *= 2;
			}
		}
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
		int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				options.inSampleSize = heightRatio;
			} else {
				options.inSampleSize = widthRatio;
			}
		}
		// Do I need this here again? I feel like yes, however it looks odd just being
		// called up above....
		options.inJustDecodeBounds = false;

		// Trying to get the corners rounded for the recipe-images
		// return getRoundedCorners(BitmapFactory.decodeFile(abspath, options));
		return BitmapFactory.decodeFile(abspath, options);
	}

	private static Bitmap scaleAndFrame(Bitmap bitmap, int width, int height) {
		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		final float scale = Math.min((float) width / (float) bitmapWidth, (float) height / (float) bitmapHeight);

		final int scaledWidth = (int) (bitmapWidth * scale);
		final int scaledHeight = (int) (bitmapHeight * scale);

		final Bitmap decored = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
		final Canvas canvas = new Canvas(decored);

		final int offset = (int) (PHOTO_BORDER_WIDTH / 2);

		sStrokePaint.setAntiAlias(false);
		canvas.drawRect(offset, offset, scaledWidth - offset - 1, scaledHeight - offset - 1, sStrokePaint);
		sStrokePaint.setAntiAlias(true);

		return decored;
	}

	// Here is a method to (hopefully) get the rounded corners
	// on the Images that Pichael has suggested:
	public static Bitmap getRoundedCorners(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff4242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
}