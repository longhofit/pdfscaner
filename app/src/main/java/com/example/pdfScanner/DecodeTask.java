package com.example.pdfScanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DecodeTask extends AsyncTask<String, Void, Bitmap> {

    private static int MaxTextureSize = 2048; /* True for most devices. */

    public ImageView v;

    public DecodeTask(ImageView iv) {
        v = iv;
    }

    protected Bitmap doInBackground(String... params) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPurgeable = true;
        opt.inPreferQualityOverSpeed = false;
        opt.inSampleSize = 0;

        Bitmap bitmap = null;
        if(isCancelled()) {
            return bitmap;
        }

        opt.inJustDecodeBounds = true;
        do {
            opt.inSampleSize++;
            BitmapFactory.decodeFile(params[0], opt);
        } while(opt.outHeight > MaxTextureSize || opt.outWidth > MaxTextureSize);
        opt.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(params[0], opt);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(v != null) {
            v.setImageBitmap(result);
        }
    }

}
