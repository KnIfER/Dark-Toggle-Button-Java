package com.knziha.plugin101;
import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class PluginFileProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri.parse("content://com.knziha.plugin101/");
	private final static String AUTHORITY = "com.knziha.plugin101";
	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int FileNo1 = 1;
	
	static {
		MATCHER.addURI(AUTHORITY, "data.txt", FileNo1);
	}
	
	@Override
	public boolean onCreate() {
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}
	
	@Override
	public String getType(Uri uri) {
		if (uri.toString().endsWith(".txt")) {
			return "text/plain";
		}
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		CMN.Log("insert---->uri = "+uri);
		CMN.Log("MATCHER.match(uri) =  "+MATCHER.match(uri));
		return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
	
	
	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
		// TODO Auto-generated method stub
		CMN.Log("openAssetFile");
		return super.openAssetFile(uri, mode);
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		//CMN.Log("PluginFileProvider_openFile::", uri);
		File file = new File(getContext().getExternalFilesDir(null), uri.getPath());
		if (file.exists()) {
			//CMN.Log("PluginFileProvider_openFile::exists");
			return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		}
		//CMN.Log("PluginFileProvider_openFile::not exist!!");
		throw new FileNotFoundException(uri.getPath());
	}
	
}