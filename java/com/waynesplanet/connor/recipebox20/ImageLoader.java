package com.waynesplanet.connor.recipebox20;

import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageLoader {
    private MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    // Handler to display images in UI thread
    Handler handler = new Handler();

    public ImageLoader(Context context) { executorService = Executors.newFixedThreadPool(4); }

    private final int tempimage = R.drawable.temp_image;

    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) { imageView.setImageBitmap(bitmap); }
        else {
            queuePhoto(url, imageView);
            imageView.setImageResource(tempimage);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = new File(url);
        Bitmap b = decodeBitmapImageFile(f);
        if (b != null) { return b; }
        return null;
    }


    private Bitmap decodeBitmapImageFile(File file) {
        int height = 480;
        int width = 640;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String abspath = file.getAbsolutePath();
        BitmapFactory.decodeFile(abspath, options);
        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;
        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;
            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio  = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) { options.inSampleSize = heightRatio; }
            else { options.inSampleSize = widthRatio; }
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(abspath,options);

    }

    // Task for  the queue
    private class PhotoToLoad {
        String url;
        ImageView imageView;
        PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad) { this.photoToLoad = photoToLoad; }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad)) { return; }
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad)) { return; }
                BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bmp, photoToLoad);
                final boolean post = handler.post(bitmapDisplayer);
            } catch (Throwable th) { th.printStackTrace(); }
        }
    }

    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return (tag == null || !tag.equals(photoToLoad.url));
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        private Bitmap bitmap;
        PhotoToLoad photoToLoad;

        private BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) { return; }
            if (bitmap != null) { photoToLoad.imageView.setImageBitmap(bitmap); }
            else { photoToLoad.imageView.setImageResource(tempimage); }
        }
    }

    public void clearCache() { memoryCache.clear(); }

}	/*	End of ImageLoader	*/
