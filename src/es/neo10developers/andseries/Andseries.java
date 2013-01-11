package es.neo10developers.andseries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Andseries extends Activity {
    private Button btn_salir;
    private Button btn_registro;
    private Button btn_acceder;
    private ImageView imageView_logo;
    protected InitTask iniciarTarea;
    private LinearLayout linearLayout_cargaInicial;
    private LinearLayout linearLayout_acceso;
    private boolean hayDatosIniciales;
    private MediaPlayer mp;
	private EditText txt_usuario;
    private EditText txt_pass;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        btn_acceder = (Button) findViewById(R.id.btn_acceder);
        btn_salir = (Button) findViewById(R.id.btn_salir);
        btn_registro = (Button) findViewById(R.id.btn_registro);
        linearLayout_cargaInicial = (LinearLayout) findViewById(R.id.linearLayout_cargaInicial);
        linearLayout_acceso = (LinearLayout) findViewById(R.id.linearLayout_acceso);
        imageView_logo = (ImageView) findViewById(R.id.imageView_logo);
        txt_usuario = (EditText) findViewById (R.id.txt_usuario);
        txt_pass = (EditText) findViewById (R.id.txt_pass);
        
        btn_salir.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmaSalir();
		}});
        
        btn_acceder.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				acceder();
		}});
        
        btn_registro.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Andseries.this, Registro.class);
		    	startActivity(i);
		}});
        
        imageView_logo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				lanzaAcercaDe();
		}});
                
        comprobarSiEstaLogueado();
    }
        
    private void acceder(){
    	if(comprobacionOk()){
			String usuario = txt_usuario.getText().toString();
			String pass = txt_pass.getText().toString();
						
			String mensaje = Sincronizador.acceder(usuario, pass);
			Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
			
			if(mensaje.contains("Bienvenido")){
				GestionBBDD ges = new GestionBBDD(this);
				
				Usuario user = new Usuario();					
				user.setUsuario(usuario);
				user.setPass(pass);
				
				ges.eliminarUsuarios();
				ges.insertarUsuario(user);
				
				reproduceSonido();
		    	desaparecer();
		    	
		    	compruebaDatosIniciales();
		    	
		    	if(!hayDatosIniciales){
		    		iniciarTarea = new InitTask();
		        	iniciarTarea.execute(this);
		    	}else{
		    		lanzaMenu();
		    	}
			}
		}    	
    }
    
    private void compruebaDatosIniciales(){
    	GestionBBDD ges = new GestionBBDD(this);
    	
    	hayDatosIniciales = ges.comprobarDatosIniciales();
    	
    	if(hayDatosIniciales){    		
    		//Cada vez que cambiemos el fichero de series, actualizar este valor
    		if(ges.totalSeries() != 2619){
    			hayDatosIniciales = false;
    		}    		    		
    	}
    }
    
    private void descargaDatosIniciales(){
    	GestionBBDD ges = new GestionBBDD(this);

    	ges.inicializaTablaSeries();
    }
    
    private void lanzaMenu(){
    	Intent i = new Intent(this, FragmentTabsPager.class);
    	startActivity(i);
    	finish();
    }
    
    private void lanzaAcercaDe(){
    	Intent i = new Intent(Andseries.this, Acerca_de.class);
    	startActivity(i);
    }
    
    private void confirmaSalir(){
		//Mostramos el diálogo de confirmación
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(getString(R.string.confirmar_salir))
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   finish();
    	           }
    	       })
    	       .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();    	
	}
    
    private void reproduceSonido(){
		SharedPreferences pref = getSharedPreferences("es.neo10developers.andseries_preferences", MODE_PRIVATE);

		int son = 0;
		
		try {
			son = Integer.valueOf(pref.getString("sonido_inicio", "0"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		switch(son){
		
		case 0:
			//Ninguno
			break;
		case 1:
			//Sirena de policía
			mp = MediaPlayer.create(this, R.raw.sirena_policia);
			break;
		case 2:
			//Risas y aplausos
			mp = MediaPlayer.create(this, R.raw.risas_aplausos);
			break;
		case 3:
			//Zapping
			mp = MediaPlayer.create(this, R.raw.zapping);
			break;
		default:
			break;						
		}		
		
		if(son > 0){
			mp.start();
		}
    }
    
    private void desaparecer(){
    	Animation animacion = AnimationUtils.loadAnimation(this,R.anim.desaparecer);
    	imageView_logo.startAnimation(animacion);
    }
    
    /**
	 * Tarea asíncrona para cargar los datos iniciales y mostrar el loading
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			linearLayout_acceso.setVisibility(View.GONE);
			linearLayout_cargaInicial.setVisibility(View.VISIBLE);			
		}

		@Override
		protected String doInBackground(Context... params) {
			descargaDatosIniciales();
			
			return "TERMINADO!";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			linearLayout_cargaInicial.setVisibility(View.GONE);
			
			lanzaMenu();
		}
	}
	
	private boolean comprobacionOk(){
    	boolean ok = false;
		
		if(txt_usuario.getText().length() > 0 && txt_pass.getText().length() > 0){ 							
				ok = true;
		}else{
    		Toast.makeText(this, "Por favor, rellene todos los datos de acceso.", Toast.LENGTH_SHORT).show();
    		ok = false;
    	}
		
		return ok;
    }
	
	private void comprobarSiEstaLogueado(){
		GestionBBDD ges = new GestionBBDD(this);
		if(!ges.leeUsuario().getUsuario().equals("")){
			compruebaDatosIniciales();
	    	
	    	if(!hayDatosIniciales){
	    		iniciarTarea = new InitTask();
	        	iniciarTarea.execute(this);
	    	}else{
	    		lanzaMenu();
	    	}
		}
	}
}