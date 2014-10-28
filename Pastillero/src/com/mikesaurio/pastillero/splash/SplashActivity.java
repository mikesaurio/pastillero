package com.mikesaurio.pastillero.splash;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mikesaurio.pastillero.PastilleroActivity;
import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.utilerias.Utilerias;

/**
 * Splash inicial
 * @author mikesaurio
 *
 */
public class SplashActivity extends Activity {

	private static final long SPLASH_SCREEN_DELAY = 2000; //tiempo que dura el splash

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		FrameLayout frame_splash = (FrameLayout) findViewById(R.id.frame_splash);

		Point p = Utilerias.getTamanoPantalla(SplashActivity.this); //tama–o de pantalla
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(p.x / 2, p.y / 3);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		frame_splash.setLayoutParams(lp);
		
		
			init(PastilleroActivity.class);

	}

	/**
	 * hilo que al terminar el splash inicia una actividad
	 * @param clase
	 */
	public void init(final Class<?> clase) {

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(new Intent().setClass(SplashActivity.this, clase));
				finish();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, SPLASH_SCREEN_DELAY);
	}

	@Override
	public void onBackPressed() {
	}
	
	
	

}