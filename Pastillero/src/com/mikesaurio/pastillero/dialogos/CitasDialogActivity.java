package com.mikesaurio.pastillero.dialogos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CalendarView.OnDateChangeListener;

import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.utilerias.Utilerias;
import com.wheel.ArrayWheelAdapter;
import com.wheel.OnWheelChangedListener;
import com.wheel.OnWheelScrollListener;
import com.wheel.WheelView;

/**
 * Actividad que controla el llenado de un nuevo medicamento
 * @author mikesaurio
 *
 */
public  class CitasDialogActivity extends Activity implements OnClickListener {
	
	
	public static final int OK=10;

	public static final int TERMINAR=0;
	public static final int CALENDARIO=1;
	public static final int RELOJ=2;
	
	private int flag_boton=0;
	private Button btn_generico;
	private TextView tv_fecha;
	private String fecha;

	private RelativeLayout rr_calendario, rr_reloj;
	private ImageButton ib_calendar, ib_reloj;
	private CalendarView calendarViewInicio;
	
	private  EditText et_nombre;
	private TextView tv_titulo;
	
	private String reloj_hora= null,calendario_dias= null;
	private WheelView wheel;
	private View view2;

	private boolean editar= false;
	
	
	private TextView tv_reloj;
	
	
	private boolean validador[]= new boolean[]{false,false};//fecha,reloj,hora
	
	
	 String reloj[] = new String[] {"", "05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00"
			,"13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00"
			,"21:00","22:00"};
	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_citas_dialog);

		btn_generico =(Button)findViewById(R.id.citas_btn_generico);
		btn_generico.setOnClickListener(this);
		
		tv_titulo=(TextView)findViewById(R.id.citas_tv_titulo);
		

		rr_calendario = (RelativeLayout) findViewById(R.id.citas_rr_calendar);
		rr_reloj = (RelativeLayout) findViewById(R.id.citas_rr_reloj);


		ib_calendar = (ImageButton) findViewById(R.id.citas_ib_calendar);
		ib_calendar.setOnClickListener(this);

		ib_reloj = (ImageButton) findViewById(R.id.citas_ib_reloj);
		ib_reloj.setOnClickListener(this);


		calendarViewInicio = (CalendarView) findViewById(R.id.citas_calendarViewInicio);


		et_nombre = (EditText) findViewById(R.id.citas_et_nombre);
		et_nombre.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) {
		    	et_nombre.setError(null);//removes error
		    }

		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		    @Override
		    public void afterTextChanged(Editable s) {
		    }
		 });

		tv_fecha=(TextView)findViewById(R.id.citas_tv_fecha);
		tv_reloj=(TextView)findViewById(R.id.citas_tv_reloj);
		
		view2=(View)findViewById(R.id.citas_view_2);

		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			
		   String[] info = extras.getStringArray("info");
		   Utilerias.hideSoftKeyboard(CitasDialogActivity.this, et_nombre);
		   llenarTodo(info);
		   
		}
		
	}
	
	
	/**
	 * 
	 * @param info
	 */
	public void llenarTodo(String[] info) {
		flag_boton=TERMINAR;
		btn_generico.setText(getString(R.string.terminar));
		tv_titulo.setText(getString(R.string.agregar_evento_citas));
		
		rr_reloj.setVisibility(RelativeLayout.VISIBLE);
		rr_calendario.setVisibility(RelativeLayout.VISIBLE);
		et_nombre.setVisibility(EditText.VISIBLE);
		
		validador[0]=true;
		validador[1]=true;
		et_nombre.setText(info[0]);
		
		if(validador[0]){
			calendario_dias=info[1];
			tv_fecha.setVisibility(TextView.VISIBLE);
			tv_fecha.setText(info[1]);
			SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
			try {
				calendarViewInicio.setMinDate((f.parse(info[1]).getTime()));
				calendarViewInicio.setDate ((f.parse(info[1])).getTime(), true, true);
				editar = true;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		if(validador[1]){
			reloj_hora=info[2];
			tv_reloj.setVisibility(TextView.VISIBLE);
			tv_reloj.setText(info[2]+" hrs");
		}
		
		
		view2.setVisibility(View.VISIBLE);
		
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.citas_btn_generico:
			if(flag_boton==TERMINAR){
				validarTodo();
			}else if(flag_boton==CALENDARIO){
					validaCalendario();
			}else if(flag_boton==RELOJ){
				validarReloj();
			}
			break;
		
		
		case R.id.citas_ib_calendar:
			iniciarCalendario();
			break;
		case R.id.citas_ib_reloj:
			iniciarReloj();
			break;

		default:
			break;
		}
		
	}	


public void validarTodo() {
		
		if(!et_nombre.getText().toString().equals("")){
			if(reloj_hora!= null&&calendario_dias!= null){
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("resultado",et_nombre.getText().toString()+"@"+calendario_dias+"@"+reloj_hora);
				setResult(OK,returnIntent);
				finish();
			
			}else{
				Utilerias.hideSoftKeyboard(CitasDialogActivity.this, et_nombre);
				Toast.makeText(CitasDialogActivity.this, getString(R.string.llenar_todo), Toast.LENGTH_LONG).show();
			}
			
		}else{
			et_nombre.setError(getString(R.string.llenar_nombre_citas));
		}
		
	}
	


/***********************************************************RELOJ************************************************************************/	
public void validarReloj() {
	if(reloj_hora!=null&&reloj_hora!=""){
		flag_boton=TERMINAR;
		btn_generico.setText(getString(R.string.terminar));
		tv_titulo.setText(getString(R.string.agregar_evento));
		
		rr_calendario.setVisibility(RelativeLayout.VISIBLE);
		et_nombre.setVisibility(EditText.VISIBLE);
		
		wheel.setVisibility(WheelView.GONE);
		
		tv_reloj.setText(reloj_hora+" hrs");
		validador[1]=true;
		
		if(validador[0]){
			tv_fecha.setVisibility(TextView.VISIBLE);
		}
		if(validador[1]){
			tv_reloj.setVisibility(TextView.VISIBLE);
		}
		
		
		view2.setVisibility(View.VISIBLE);
	}

	
}



public void iniciarReloj() {
	
	//obtenemos las horas que le quedan al dia
	Calendar now = Calendar.getInstance();
	String fechas= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR);

			initWheel(R.id.citas_wheel_reloj, reloj);
	
	
		Utilerias.hideSoftKeyboard(CitasDialogActivity.this,et_nombre);
		flag_boton=RELOJ;
		
		btn_generico.setText(getString(R.string.aceptar));
		tv_titulo.setText(getString(R.string.titulo_tiempo));
		et_nombre.setVisibility(EditText.GONE);
		
		rr_calendario.setVisibility(RelativeLayout.GONE);

		
		tv_reloj.setVisibility(TextView.GONE);
		tv_fecha.setVisibility(TextView.GONE);

		view2.setVisibility(View.GONE);
		
    	et_nombre.setError(null);//removes error
    	et_nombre.clearFocus();
	
}
/***********************************************************TERMINA RELOJ************************************************************************/	




/***********************************************************CALENDARIO************************************************************************/		
/**
 * Valida al calendario y prepara la vista
 * @throws ParseException 
 */
public void validaCalendario() {
	Calendar now = Calendar.getInstance();
	
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
	long date1 = 0;
	try {
		if(fecha==null){
			 fecha= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR);
			 date1 = formatter.parse(fecha).getTime()+86400000;
		}else{
			date1 = formatter.parse(fecha).getTime();
		}
		fecha= Utilerias.getDate(date1, formatter);
	} catch (ParseException e) {
		e.printStackTrace();
	}  
	
	
			flag_boton=TERMINAR;
			btn_generico.setText(getString(R.string.terminar));
			tv_titulo.setText(getString(R.string.agregar_evento));
			
			rr_reloj.setVisibility(RelativeLayout.VISIBLE);
			et_nombre.setVisibility(EditText.VISIBLE);

			calendarViewInicio.setVisibility(CalendarView.GONE);
			
			calendario_dias=fecha;
			tv_fecha.setText(getString(R.string.fecha_inicio)+" "+fecha);
			
			validador[0]=true;
			
			if(validador[0]){
				tv_fecha.setVisibility(TextView.VISIBLE);
			}
			if(validador[1]){
				tv_reloj.setVisibility(TextView.VISIBLE);
			}
			
	
			view2.setVisibility(View.VISIBLE);
	
	
}

/**
 * prepara la vista para que el calendario pueda verse
 * @throws ParseException 
 */
public void iniciarCalendario() {
	Calendar now = Calendar.getInstance();
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
	String fechaCel= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR);
	
	long date = 0;
	try {
		date = formatter.parse(fechaCel).getTime()+86400000;
	} catch (ParseException e) {

		e.printStackTrace();
	}
	
	
	Utilerias.hideSoftKeyboard(this,et_nombre);
	flag_boton=CALENDARIO;
	btn_generico.setText(getString(R.string.aceptar));
	tv_titulo.setText(getString(R.string.titulo_calendario));
	
	rr_reloj.setVisibility(RelativeLayout.GONE);
	et_nombre.setVisibility(EditText.GONE);
	
	
	

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			Utilerias.getTamanoPantalla(this).y / 3);

	if(!editar){
		calendarViewInicio.setMinDate(date);
	}	
			
	calendarViewInicio.setVisibility(CalendarView.VISIBLE);
	calendarViewInicio.setLayoutParams(lp);
	calendarViewInicio.setOnDateChangeListener(new OnDateChangeListener() {

        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month,
                int dayOfMonth) {
            
        	fecha= dayOfMonth+"/"+(month+1)+"/"+year;
        }
    });
	
	
	tv_reloj.setVisibility(TextView.GONE);
	tv_fecha.setVisibility(TextView.GONE);
	
	view2.setVisibility(View.GONE);
	
	et_nombre.setError(null);//removes error
	et_nombre.clearFocus();
}


/************************************************************TERMINA CALENDARIO***********************************************************************/	

	
	
	
/*******************************************************************WhEEL****************************************************************/	

	/*Iniciar wheel*/
	
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

					reloj_hora= reloj[wheel.getCurrentItem()];
				
			
			
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

		wheel = getWheel(id);
		wheel.setViewAdapter(adapter);
		wheel.setCurrentItem(0);
		wheel.setVisibleItems(5);
		wheel.setVisibility(WheelView.VISIBLE);
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
	/*Termina WHELL*/
	/************************************************************TERMINA WHEEL***********************************************************************/

	
}
