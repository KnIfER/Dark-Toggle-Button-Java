package com.knziha.plugin101;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TesseractPluginTest {
	// https://gist.github.com/alvareztech/6627673
	
	// From assets
	public static Bitmap getBitmapFromAsset(Context context, String strName) {
		AssetManager assetManager = context.getAssets();
		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(strName);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			return null;
		}
		return bitmap;
	}
	
	// From raw
	public static Bitmap getBitmapFromAsset(Context context, int id) {
		InputStream input = context.getResources().openRawResource(id);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(input);
		} catch (Exception e) {
			return null;
		}
		return bitmap;
	}
	
	public static void Test(Context context) {
		TessBaseAPI tess = new TessBaseAPI();

		String dataPath = new File(Environment.getExternalStorageDirectory(), "tesseract").getAbsolutePath();

		tess.init(dataPath, "eng");
		
		tess.setImage(getBitmapFromAsset(context, R.raw.text));
		String text = tess.getUTF8Text();
		
		try {
			Toast.makeText(context, text, 1).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		android.util.Log.d("fatal test", text);
		
		tess.recycle();
	}
}
