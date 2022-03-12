package com.knziha.plugin101;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;


//common
public class CMN {
	public static String Log(Object... o) {
		StringBuilder msg = new StringBuilder(1024);
		for (int i = 0; i < o.length; i++) {
			Object o1 = o[i];
			if (o1 != null) {
				if (o1 instanceof Exception) {
					Exception e = ((Exception) o[i]);
					msg.append(e);
					{
						ByteArrayOutputStream s = new ByteArrayOutputStream();
						PrintStream p = new PrintStream(s);
						e.printStackTrace(p);
						msg.append(s);
						continue;
					}
				}
				if (o1 != null && o1.getClass().isArray()) {
					String classname = o1.getClass().getName();
					if (classname.length() == 2) {
						switch (classname.charAt(1)) {
							case 'B': {
								o1 = msg.append(Arrays.toString((byte[]) o1));
							}
							break;
							case 'D': {
								o1 = msg.append(Arrays.toString((double[]) o1));
							}
							break;
							case 'F': {
								o1 = msg.append(Arrays.toString((float[]) o1));
							}
							break;
							case 'L': {
								o1 = msg.append(Arrays.toString((long[]) o1));
							}
							break;
							case 'I': {
								o1 = msg.append(Arrays.toString((int[]) o1));
							}
							break;
							case 'S': {
								o1 = msg.append(Arrays.toString((short[]) o1));
							}
							break;
						}
					} else {
						o1 = Arrays.toString((Object[]) o1);
					}
				}
			}
			msg.append(o1);
			msg.append("\n");
		}
		String message = msg.toString();
		android.util.Log.d("fatal poison", message);
		return message;
	}
	
	public static long ststrt;
	public static long stst_add;
	
	public static long rt(Object... o) {
		CMN.Log(o);
		return ststrt = System.currentTimeMillis();
	}
	
	public static long pt(Object... args) {
		long ret = System.currentTimeMillis() - ststrt;
		CMN.Log(listToStr(args) + " " + ret);
		return ret;
	}
	
	public static String listToStr(Object... args) {
		String ret = "";
		for (int i = 0; i < args.length; i++) {
			ret += args[i];
		}
		return ret;
	}
	
	public static int id(Object object) {
		return System.identityHashCode(object);
	}
	
	public static long now() {
		return System.currentTimeMillis();
	}
}