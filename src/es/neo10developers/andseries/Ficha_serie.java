package es.neo10developers.andseries;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Ficha_serie extends Activity{
	private Button btn_volver;
	private Button btn_anyadir;
	private ImageView imageView_banner;
	private TextView lbl_titulo_serie;
	private TextView lbl_sinopsis;
	private String titulo;
	private String idSerie;
	private Serie serie;
	private LinearLayout linearLayout_cargando;
	protected InitTask iniciarTarea;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ficha_serie);
        
        btn_volver = (Button) findViewById(R.id.btn_volver);
        btn_anyadir = (Button) findViewById(R.id.btn_anyadir);
        lbl_titulo_serie = (TextView) findViewById(R.id.lbl_titulo_serie);
        lbl_sinopsis = (TextView) findViewById(R.id.lbl_sinopsis);
        imageView_banner = (ImageView) findViewById(R.id.imageView_banner);
        linearLayout_cargando = (LinearLayout) findViewById(R.id.linearLayout_cargando);
                
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        titulo = getIntent().getExtras().getString("titulo");
        idSerie = getIntent().getExtras().getString("idSerie");
        
        lbl_titulo_serie.setText(titulo);
        
        btn_volver.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
		}});
        
        btn_anyadir.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				insertarSerie();
				
				Toast.makeText(Ficha_serie.this, getString(R.string.se_ha_anyadido) + " " + titulo + " " + getString(R.string.a_mis_series), Toast.LENGTH_LONG).show();
				finish();
		}});
        
        iniciarTarea = new InitTask();
    	iniciarTarea.execute(this);
	}
	
	private void leerSerie(){
		GestionBBDD ges = new GestionBBDD(this);
		serie = ges.leeSeriePorId(idSerie);
		
		if(serie.getSinopsis().equals("")){
			LeerSerieServicioWeb lee = new LeerSerieServicioWeb();
			serie = lee.datosSerie(titulo);
			ges.actualizarSerie(serie);
		}		
	}
	
	private void llenarDatos(){		
		if(serie != null){
			lbl_titulo_serie.setText(titulo);
			lbl_sinopsis.setText(serie.getSinopsis());
			
			if(Utilidades.descargaBanner(this, serie.getIdSerie())){
				imageView_banner.setImageDrawable(Drawable.createFromPath(getFilesDir() + "/" + serie.getIdSerie() + ".jpg"));
			}else{
				Drawable logo = getResources().getDrawable(R.drawable.logo);
				
				imageView_banner.setImageDrawable(logo);
			}
		}else{
			lbl_sinopsis.setText(getString(R.string.serie_no_leida));
		}		
	}
	
	private void insertarSerie(){
		GestionBBDD ges = new GestionBBDD(this);
		
		MiSerie miSer = new MiSerie();
		
		miSer.setIdSerie(serie.getIdSerie());
		
		ges.insertaSerie(miSer);
		
		if(Utilidades.laRedEstaDisponible(this)){
			if(!Sincronizador.insertarSerie(this, serie.getIdSerie(), "1", "1").equals("OK")){
				Log.d("Ficha_serie", "La operación queda pendiente");
				
				dejarComoPendiente();
			}else{
				Log.d("Ficha_serie", "Operación realizada");
			}
		}else{
			Log.d("Ficha_serie", "La operación queda pendiente");
			
			dejarComoPendiente();
		}
	}
	
	private void dejarComoPendiente(){
		GestionBBDD ges = new GestionBBDD(this);
		
		OperacionPendiente op = new OperacionPendiente(this);
		
		op.setTipoOperacion("I");
		op.setIdSerie(serie.getIdSerie());
		op.setTemporada(1);
		op.setCapitulo(1);
		
		ges.insertarOperacionPendiente(op);
	}
	
	/**
	 * Tarea asíncrona para cargar los datos de la serie y mostrar el loading
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			linearLayout_cargando.setVisibility(View.VISIBLE);
			imageView_banner.setVisibility(View.GONE);
			lbl_sinopsis.setVisibility(View.GONE);
			lbl_titulo_serie.setVisibility(View.GONE);
		}

		@Override
		protected String doInBackground(Context... params) {
			leerSerie();
			
			return "TERMINADO!";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			llenarDatos();
			linearLayout_cargando.setVisibility(View.GONE);
			imageView_banner.setVisibility(View.VISIBLE);
			lbl_sinopsis.setVisibility(View.VISIBLE);
			lbl_titulo_serie.setVisibility(View.VISIBLE);
		}
	}
}
