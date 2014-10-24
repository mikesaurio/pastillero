package com.mikesaurio.pastillero.utilerias;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
	
	
}
