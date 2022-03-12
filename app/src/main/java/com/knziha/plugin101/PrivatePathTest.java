package com.knziha.plugin101;

import android.content.Context;
import android.net.Uri;

import java.io.InputStream;

public class PrivatePathTest {
	public static void Test(Context context) {
		//File dataFile = new File(context.getExternalFilesDir(null), "data.txt");
		try {
			//FileInputStream fint = new FileInputStream(dataFile);
			//FileInputStream fin = context.openFileInput(dataFile.getName());
			//FileInputStream fin = context.openFileInput(dataFile.getName());
			//fin.getFD()
			
			Uri uri = Uri.parse("content://com.knziha.plugin101/data.txt");
			InputStream fin = context.getContentResolver().openInputStream(uri);
			
			byte[] data=new byte[512];
			int len=fin.read(data);
			fin.close();
			CMN.Log("PrivatePathTest::", new String(data, 0, len));
		} catch (Exception e) {
			CMN.Log(e);
		}
	}
}
