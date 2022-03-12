package com.knziha.plugin101;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//TesseractPluginTest.Test(MainActivity.this);
		
		final DarkToggleButton btn = findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn.toggle();
			}
		});
		
		File dataFile = new File(getExternalFilesDir(null), "data.txt");
		if(!dataFile.exists()) {
			try {
				FileOutputStream fout = new FileOutputStream(dataFile);
				fout.write("important data!".getBytes());
				fout.close();
			} catch (Exception e) {
				CMN.Log(e);
			}
		}
		
		//PrivatePathTest.Test(this);
	}
}