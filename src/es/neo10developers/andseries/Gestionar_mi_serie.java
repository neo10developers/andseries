package es.neo10developers.andseries;

import java.io.File;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Gestionar_mi_serie extends Activity{
	private Button btn_volver;
	private Button btn_guardar;
	private ImageView imageView_banner;
	private TextView lbl_titulo_serie;
	private TextView lbl_sinopsis;
	private Spinner spinner_temporada;
	private Spinner spinner_capitulo;
	private MiSerie miSerie;
	private String idSerie;
	private Serie serie;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestionar_mi_serie);
        
        btn_volver = (Button) findViewById(R.id.btn_volver);
        btn_guardar = (Button) findViewById(R.id.btn_guardar);
        lbl_titulo_serie = (TextView) findViewById(R.id.lbl_titulo_serie);
        lbl_sinopsis = (TextView) findViewById(R.id.lbl_sinopsis);
        imageView_banner = (ImageView) findViewById(R.id.imageView_banner);
        spinner_temporada = (Spinner) findViewById(R.id.spinner_temporada);
        spinner_capitulo = (Spinner) findViewById(R.id.spinner_capitulo);
                
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        idSerie = getIntent().getExtras().getString("idSerie");
        
        btn_volver.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
		}});
        
        btn_guardar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				modificarSerie();
				Toast.makeText(Gestionar_mi_serie.this, serie.getTitulo() + " " + getString(R.string.actualizada), Toast.LENGTH_LONG).show();
				finish();
		}});
        
        inicializaSpinners();
        leerSerie();        
	}
	
	//Leemos los datos de la serie
	private void leerSerie(){
		GestionBBDD ges = new GestionBBDD(this);
		miSerie = ges.leeMiSerie(idSerie);
		serie = ges.leeSeriePorId(idSerie);
		
		if(miSerie != null){
			lbl_titulo_serie.setText(serie.getTitulo());
			lbl_sinopsis.setText(serie.getSinopsis());
			
			
			String nombreFichero = serie.getIdSerie() + ".jpg";
			
			File f = new File(getFilesDir() + "/" + nombreFichero);
			if(f.exists()){
				imageView_banner.setImageDrawable(Drawable.createFromPath(getFilesDir() + "/" + nombreFichero));
			}else{
				Drawable logo = getResources().getDrawable(R.drawable.logo);				
				imageView_banner.setImageDrawable(logo);
			}
			
			if(miSerie.getTemporada() != 0){
				spinner_temporada.setSelection(miSerie.getTemporada() - 1);				
			}
			
			if(miSerie.getCapitulo() != 0){
				spinner_capitulo.setSelection(miSerie.getCapitulo() - 1);				
			}
			
		}else{
			lbl_sinopsis.setText(getString(R.string.serie_no_leida));
		}		
		
	}
	
	//Inicializamos los spinners
	private void inicializaSpinners(){
		ArrayAdapter<CharSequence> adaptador_temporada = ArrayAdapter.createFromResource(this, R.array.temporadas , android.R.layout.simple_spinner_item);
		adaptador_temporada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner_temporada.setAdapter(adaptador_temporada);
		
		ArrayAdapter<CharSequence> adaptador_capitulo = ArrayAdapter.createFromResource( this, R.array.capitulos , android.R.layout.simple_spinner_item);
		adaptador_capitulo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner_capitulo.setAdapter(adaptador_capitulo);
	}		
	
	//Modificamos los cambios
	private void modificarSerie(){
		GestionBBDD ges = new GestionBBDD(this);
		
		int temp = Integer.valueOf((String) spinner_temporada.getItemAtPosition(spinner_temporada.getSelectedItemPosition()));
		int cap = Integer.valueOf((String) spinner_capitulo.getItemAtPosition(spinner_capitulo.getSelectedItemPosition()));
		
		miSerie.setTemporada(temp);		
		miSerie.setCapitulo(cap);
		
		
		ges.modificarMiSerie(miSerie);
		
		
		if(Utilidades.laRedEstaDisponible(this)){
			if(!Sincronizador.actualizarSerie(this, miSerie.getIdSerie(), String.valueOf(miSerie.getTemporada()), String.valueOf(miSerie.getCapitulo())).equals("OK")){
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
		
		op.setTipoOperacion("A");
		op.setIdSerie(miSerie.getIdSerie());
		op.setTemporada(miSerie.getTemporada());
		op.setCapitulo(miSerie.getCapitulo());
		
		ges.insertarOperacionPendiente(op);
	}
}
