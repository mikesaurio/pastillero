package com.mikesaurio.pastillero.dialogos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikesaurio.pastillero.R;
import com.mikesaurio.pastillero.utilerias.Utilerias;
import com.wheel.ArrayWheelAdapter;
import com.wheel.OnWheelChangedListener;
import com.wheel.OnWheelScrollListener;
import com.wheel.WheelView;


public class DatosDialogActivity extends Activity implements OnClickListener {
	
	
	public static final int OK=10;
	
	private AlertDialog customDialog;
	public static final int TERMINAR=0;
	public static final int CALENDARIO=1;
	public static final int RELOJ=2;
	public static final int HORA=3;
	public int flag_boton=0;
	public Button btn_generico;
	public TextView tv_fecha;
	private String fecha_inicial,fecha_final;
	private RelativeLayout rr_calendario, rr_reloj, rr_hora;
	private ImageButton ib_calendar, ib_reloj, ib_hora;
	private CalendarView calendarViewInicio, calendarViewFin;
	private EditText et_nombre;
	private TextView tv_titulo;
	private WheelView wheel_hora;
	private String reloj_hora= null,cada_horas=null,calendario_dias= null;
	private WheelView wheel;
	private View view2,view3;
	
	
	private TextView tv_reloj,tv_hora;
	
	
	private boolean validador[]= new boolean[]{false,false,false};//fecha,reloj,hora
	
	
	
	 String reloj[] = new String[] {"", "1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00","11:00","12:00"
			,"13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00"
			,"21:00","22:00","23:00","24:00"};
	
	 String hora[] = new String[] { "","2","4","6","8","12","24","48","72"};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nuevo_evento);

		btn_generico =(Button)findViewById(R.id.evento_btn_generico);
		btn_generico.setOnClickListener(this);
		
		tv_titulo=(TextView)findViewById(R.id.evento_tv_titulo);
		

		rr_calendario = (RelativeLayout) findViewById(R.id.rr_calendar);
		rr_reloj = (RelativeLayout) findViewById(R.id.rr_reloj);
		rr_hora = (RelativeLayout) findViewById(R.id.rr_hora);

		ib_calendar = (ImageButton) findViewById(R.id.evento_ib_calendar);
		ib_calendar.setOnClickListener(this);

		ib_reloj = (ImageButton) findViewById(R.id.evento_ib_reloj);
		ib_reloj.setOnClickListener(this);

		ib_hora = (ImageButton)findViewById(R.id.evento_ib_hora);
		ib_hora.setOnClickListener(this);

		calendarViewInicio = (CalendarView) findViewById(R.id.calendarViewInicio);
		calendarViewFin = (CalendarView) findViewById(R.id.calendarViewFin);

		et_nombre = (EditText) findViewById(R.id.evento_et_nombre);
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

		tv_fecha=(TextView)findViewById(R.id.tv_fecha);
		tv_reloj=(TextView)findViewById(R.id.tv_reloj);
		tv_hora=(TextView)findViewById(R.id.tv_hora);
		
		view2=(View)findViewById(R.id.view_2);
		view3=(View)findViewById(R.id.view_3);
		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
		   String[] info = extras.getStringArray("info");
		   
		   Utilerias.hideSoftKeyboard(DatosDialogActivity.this, et_nombre);
		   llenarTodo(info);
		   
		}
		
		
		
		
	}
	
	
	

	public void llenarTodo(String[] info) {
		flag_boton=TERMINAR;
		btn_generico.setText(getString(R.string.terminar));
		tv_titulo.setText(getString(R.string.agregar_evento));
		
		rr_reloj.setVisibility(RelativeLayout.VISIBLE);
		rr_calendario.setVisibility(RelativeLayout.VISIBLE);
		et_nombre.setVisibility(EditText.VISIBLE);
		
		tv_hora.setText(cada_horas+" hrs");
		validador[0]=true;
		validador[1]=true;
		validador[2]=true;
		et_nombre.setText(info[0]);
		
		if(validador[0]){
			calendario_dias=info[1]+"@"+info[2];
			tv_fecha.setVisibility(TextView.VISIBLE);
			tv_fecha.setText(getString(R.string.fecha_inicio)+" "+info[1]+"\n"+getString(R.string.fecha_fin)+" "+info[2]);
		}
		if(validador[1]){
			reloj_hora=info[3];
			tv_reloj.setVisibility(TextView.VISIBLE);
			tv_reloj.setText(info[3]+" hrs");
		}
		if(validador[2]){
			cada_horas=info[4];
			tv_hora.setVisibility(TextView.VISIBLE);
			tv_hora.setText(info[4]+" hrs");
		}
		
		view2.setVisibility(View.VISIBLE);
		view3.setVisibility(View.VISIBLE);
		
	}




	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.evento_btn_generico:
			if(flag_boton==TERMINAR){
				validarTodo();
				
			}else if(flag_boton==CALENDARIO){
				try {
					validaCalendario();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(flag_boton==HORA){
				validarHora();
				
			}else if(flag_boton==RELOJ){
				validarReloj();
			}
			break;
		
		
		case R.id.evento_ib_calendar:
			iniciarCalendario();
			break;
		case R.id.evento_ib_reloj:
			iniciarReloj();
			break;
		
		case R.id.evento_ib_hora:
			iniciarHora();
			break;
		default:
			break;
		}

	}

	
	public void validarTodo() {
		
		if(!et_nombre.getText().toString().equals("")){
			if(reloj_hora!= null&&cada_horas!=null&&calendario_dias!= null){
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("resultado",et_nombre.getText().toString()+"@"+calendario_dias+"@"+reloj_hora+"@"+cada_horas);
				setResult(OK,returnIntent);
				finish();
			
			}else{
				Utilerias.hideSoftKeyboard(DatosDialogActivity.this, et_nombre);
				Toast.makeText(DatosDialogActivity.this, getString(R.string.llenar_todo), Toast.LENGTH_LONG).show();
			}
			
		}else{
			et_nombre.setError(getString(R.string.llenar_nombre));
		}
		
	}




	public void validarHora() {
		
		if(cada_horas!=null&&cada_horas!=""){
			
			flag_boton=TERMINAR;
			btn_generico.setText(getString(R.string.terminar));
			tv_titulo.setText(getString(R.string.agregar_evento));
			
			rr_reloj.setVisibility(RelativeLayout.VISIBLE);
			rr_calendario.setVisibility(RelativeLayout.VISIBLE);
			et_nombre.setVisibility(EditText.VISIBLE);
			
			wheel.setVisibility(WheelView.GONE);
			
			tv_hora.setText(cada_horas+" hrs");
			validador[2]=true;
			
			if(validador[0]){
				tv_fecha.setVisibility(TextView.VISIBLE);
			}
			if(validador[1]){
				tv_reloj.setVisibility(TextView.VISIBLE);
			}
			if(validador[2]){
				tv_hora.setVisibility(TextView.VISIBLE);
			}
			
			view2.setVisibility(View.VISIBLE);
			view3.setVisibility(View.VISIBLE);
		}
		
	}
	
	
	
	public void iniciarHora() {
		
		
		Utilerias.hideSoftKeyboard(DatosDialogActivity.this,et_nombre);
		flag_boton=HORA;
		
		btn_generico.setText(getString(R.string.aceptar));
		tv_titulo.setText(getString(R.string.titulo_hora));
		et_nombre.setVisibility(EditText.GONE);
		
		rr_reloj.setVisibility(RelativeLayout.GONE);
		rr_calendario.setVisibility(RelativeLayout.GONE);

		initWheel(R.id.evento_wheel_hora, hora);
		
		tv_hora.setVisibility(TextView.GONE);
		tv_reloj.setVisibility(TextView.GONE);
		tv_fecha.setVisibility(TextView.GONE);
		
		view2.setVisibility(View.GONE);
		view3.setVisibility(View.GONE);
		
    	et_nombre.setError(null);//removes error
    	et_nombre.clearFocus();
		
	}



	public void validarReloj() {
		if(reloj_hora!=null&&reloj_hora!=""){
			flag_boton=TERMINAR;
			btn_generico.setText(getString(R.string.terminar));
			tv_titulo.setText(getString(R.string.agregar_evento));
			
			rr_hora.setVisibility(RelativeLayout.VISIBLE);
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
			if(validador[2]){
				tv_hora.setVisibility(TextView.VISIBLE);
			}
			
			view2.setVisibility(View.VISIBLE);
			view3.setVisibility(View.VISIBLE);
		}
	
		
	}



	public void iniciarReloj() {
		
		//obtenemos las horas que le quedan al dia
		Calendar now = Calendar.getInstance();
		String fecha= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR);
		if(fecha_inicial==null||fecha_inicial.equals(fecha)){
			int hora= now.get(Calendar.HOUR_OF_DAY);
			 String reloj2[] = new String[reloj.length-hora];
			 	
			 	reloj2[0]="";
				for(int i=hora+1, j=1;i<reloj.length;i++,j++){
					reloj2[j]=reloj[i]; 
				}
				initWheel(R.id.evento_wheel_reloj, reloj2);
		}else{
			initWheel(R.id.evento_wheel_reloj, reloj);
		}
		
			Utilerias.hideSoftKeyboard(DatosDialogActivity.this,et_nombre);
			flag_boton=RELOJ;
			
			btn_generico.setText(getString(R.string.aceptar));
			tv_titulo.setText(getString(R.string.titulo_tiempo));
			et_nombre.setVisibility(EditText.GONE);
			
			rr_hora.setVisibility(RelativeLayout.GONE);
			rr_calendario.setVisibility(RelativeLayout.GONE);
	
			
			tv_hora.setVisibility(TextView.GONE);
			tv_reloj.setVisibility(TextView.GONE);
			tv_fecha.setVisibility(TextView.GONE);
	
			
			
			
			view2.setVisibility(View.GONE);
			view3.setVisibility(View.GONE);
			
	    	et_nombre.setError(null);//removes error
	    	et_nombre.clearFocus();
		
	}
	
	
	



	/**
	 * Valida al calendario y prepara la vista
	 * @throws ParseException 
	 */
	public void validaCalendario() throws ParseException {
		Calendar now = Calendar.getInstance();
		if(fecha_inicial==null){
			fecha_inicial= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR);
		}
		if(fecha_final==null){
			fecha_final= now.get(Calendar.DAY_OF_MONTH)+"/"+((now.get(Calendar.MONTH))+1)+"/"+now.get(Calendar.YEAR);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
		Date date1 = formatter.parse(fecha_inicial);  
		Date date2 = formatter.parse(fecha_final);  
		if (date1.getTime() > date2.getTime())  
		{  
		Toast.makeText(this, getString(R.string.fecha_incorrecta), Toast.LENGTH_LONG).show();
		}else{ 
				flag_boton=TERMINAR;
				btn_generico.setText(getString(R.string.terminar));
				tv_titulo.setText(getString(R.string.agregar_evento));
				
				rr_reloj.setVisibility(RelativeLayout.VISIBLE);
				rr_hora.setVisibility(RelativeLayout.VISIBLE);
				et_nombre.setVisibility(EditText.VISIBLE);
				calendarViewInicio.setVisibility(CalendarView.GONE);
				calendarViewFin.setVisibility(CalendarView.GONE);
				
				calendario_dias=fecha_inicial+"@"+fecha_final;
				tv_fecha.setText(getString(R.string.fecha_inicio)+" "+fecha_inicial+"\n"+getString(R.string.fecha_fin)+" "+fecha_final);
				
				validador[0]=true;
				
				if(validador[0]){
					tv_fecha.setVisibility(TextView.VISIBLE);
				}
				if(validador[1]){
					tv_reloj.setVisibility(TextView.VISIBLE);
				}
				if(validador[2]){
					tv_hora.setVisibility(TextView.VISIBLE);
				}
		
				view2.setVisibility(View.VISIBLE);
				view3.setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * prepara la vista para que el calendario pueda verse
	 */
	public void iniciarCalendario(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE,Calendar.getInstance().getActualMinimum(Calendar.DATE));
		long date = calendar.getTime().getTime();
		
		
		Utilerias.hideSoftKeyboard(DatosDialogActivity.this,et_nombre);
		flag_boton=CALENDARIO;
		btn_generico.setText(getString(R.string.aceptar));
		tv_titulo.setText(getString(R.string.titulo_calendario));
		
		rr_reloj.setVisibility(RelativeLayout.GONE);
		rr_hora.setVisibility(RelativeLayout.GONE);
		et_nombre.setVisibility(EditText.GONE);
		
		
		

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				Utilerias.getTamanoPantalla(DatosDialogActivity.this).y / 3);

		calendarViewInicio.setVisibility(CalendarView.VISIBLE);
		calendarViewInicio.setLayoutParams(lp);
		calendarViewInicio.setMinDate(date);
		calendarViewInicio.setOnDateChangeListener(new OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                    int dayOfMonth) {
                
            	fecha_inicial= dayOfMonth+"/"+(month+1)+"/"+year;
            }
        });
		calendarViewFin.setVisibility(CalendarView.VISIBLE);
		calendarViewFin.setLayoutParams(lp);
		calendarViewFin.setMinDate(date);
		calendarViewFin.setOnDateChangeListener(new OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                    int dayOfMonth) {
            	
    				fecha_final= dayOfMonth+"/"+(month+1)+"/"+year;
    			
            	
            }
        });
		
		tv_hora.setVisibility(TextView.GONE);
		tv_reloj.setVisibility(TextView.GONE);
		tv_fecha.setVisibility(TextView.GONE);
		
		view2.setVisibility(View.GONE);
		view3.setVisibility(View.GONE);
		
    	et_nombre.setError(null);//removes error
    	et_nombre.clearFocus();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/***********************************************************************************************************************************/	

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
			if(flag_boton==HORA){
				cada_horas= hora[wheel.getCurrentItem()];
				
			}else if(flag_boton==RELOJ){
				reloj_hora= reloj[wheel.getCurrentItem()];
			}
			
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

}
