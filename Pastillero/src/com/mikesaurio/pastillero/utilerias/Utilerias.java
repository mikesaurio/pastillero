package com.mikesaurio.pastillero.utilerias;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mikesaurio.pastillero.PastilleroActivity;
import com.mikesaurio.pastillero.R;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.TypedValue;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RemoteViews;

public class Utilerias {
	


	/**
	 * Hides the soft keyboard
	 */
	public static void hideSoftKeyboard(Activity act,EditText edit) {
		InputMethodManager imm = (InputMethodManager)act.getSystemService(
				Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	/**
	 * obtienes el tama–o de pantalla
	 * 
	 * @param (activity) Activity
	 * @return (Point) .x = width .y = height
	 */
	public static Point getTamanoPantalla(Activity act) {
		Display display = act.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		return (new Point(width, height));
	}
	
	/**
	 * obtienes el tama–o del ActionBar
	 * @param activity
	 * @return
	 */
	public static int getTamanoActionBar(Activity activity){
		TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		   return TypedValue.complexToDimensionPixelSize(tv.data,activity.getResources().getDisplayMetrics());
		}
		return 0;
	}
	
	
	/**
	 * De milisegundos a fecha
	 * @param milliSeconds
	 * @param formatter
	 * @return
	 */
	 public static String getDate(long milliSeconds, SimpleDateFormat formatter)
	    {
	         Calendar calendar = Calendar.getInstance();
	         calendar.setTimeInMillis(milliSeconds);
	         return formatter.format(calendar.getTime());
	    }
	 
	
		
	
	
}
