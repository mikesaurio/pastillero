package com.wheel;

import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.mikesaurio.pastillero.R;

public class SlotMachineActivity extends Activity {
	
	
	final String cities[] = new String[] { "Item 1",
			"Item 2", "Item 3", "Item 4",
			"Item 5", "Item 6", "Item 7",
			"Item 8", " Item 9", "Item 10" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.slot_machine_layout);

		

		initWheel(R.id.slot_1, cities);

		
	}

	Handler handlar = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			mixWheel(R.id.slot_1);
		}
	};

	class UpdateTimeTask extends TimerTask {
		public void run() {
			handlar.removeCallbacks(this);
			handlar.sendEmptyMessage(0);
		}
	}

	// Wheel scrolled flag
	private boolean wheelScrolled = false;

	// Wheel scrolled listener
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			wheelScrolled = true;
		}

		public void onScrollingFinished(WheelView wheel) {
			wheelScrolled = false;
			// updateStatus();
			
			Toast.makeText(getApplicationContext(), cities[wheel.getCurrentItem()], Toast.LENGTH_LONG).show();
		}
	};

	// Wheel changed listener
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!wheelScrolled) {
				// updateStatus();
			}
		}
	};

	/**
	 * Initializes wheel
	 * 
	 * @param id
	 *            the wheel widget Id
	 */
	private void initWheel(int id, String cities[]) {

		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities);

		WheelView wheel = getWheel(id);
		wheel.setViewAdapter(adapter);
		wheel.setCurrentItem((int) (Math.random() * 10));
		wheel.setVisibleItems(7);

		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(true);
		wheel.setEnabled(true);
	}

	/**
	 * Returns wheel by Id
	 * 
	 * @param id
	 *            the wheel Id
	 * @return the wheel with passed Id
	 */
	private WheelView getWheel(int id) {
		return (WheelView) findViewById(id);
	}

	/**
	 * Mixes wheel
	 * 
	 * @param id
	 *            the wheel id
	 */
	private void mixWheel(int id) {
		WheelView wheel = getWheel(id);
		wheel.scroll(-350 + (int) (Math.random() * 50), 2000);

	}

}
