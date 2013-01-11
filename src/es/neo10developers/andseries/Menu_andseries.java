package es.neo10developers.andseries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class Menu_andseries extends Activity{
	private ImageButton imageButton_mis_series;
	private ImageButton imageButton_buscador_series;
	private ImageButton imageButton_recomendaciones;
	private ImageButton imageButton_salir;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        imageButton_mis_series = (ImageButton) findViewById(R.id.imageButton_mis_series);
        imageButton_buscador_series = (ImageButton) findViewById(R.id.imageButton_buscador_series);
        imageButton_recomendaciones = (ImageButton) findViewById(R.id.imageButton_recomendaciones);
        imageButton_salir = (ImageButton) findViewById(R.id.imageButton_salir);
                
        imageButton_salir.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				confirmaSalir();
		}});
        
        imageButton_mis_series.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Menu_andseries.this, Mis_series.class);
				startActivity(i);
		}});
        
        imageButton_buscador_series.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Menu_andseries.this, Buscador_series.class);
				startActivity(i);
		}});
        
        imageButton_recomendaciones.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {				
				if(Utilidades.laRedEstaDisponible(Menu_andseries.this)){
					Intent i = new Intent(Menu_andseries.this, Recomendaciones.class);
					startActivity(i);
		        }else{
		        	Toast.makeText(Menu_andseries.this, getString(R.string.sin_conexion), Toast.LENGTH_LONG).show();
		        }
		}});
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
	
	private void confirmaCerrarSesion(){		
		//Mostramos el diálogo de confirmación
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(getString(R.string.confirmar_cerrar_sesion))
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   GestionBBDD ges = new GestionBBDD(Menu_andseries.this);
    	        	   ges.limpiarDatosUsuario();
    	        	   startActivity(new Intent(Menu_andseries.this, Andseries.class));
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true; /* El menu ya esta visible */
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.opciones:
			lanzaOpciones();
			break;
		case R.id.acerca_de:
			lanzaAcercaDe();
			break;
		case R.id.cerrar_sesion:
			confirmaCerrarSesion();
			break;
		}
		return true; /* Consumimos el item, no se propaga */
	}
	
	private void lanzaAcercaDe(){
    	Intent i = new Intent(Menu_andseries.this, Acerca_de.class);
    	startActivity(i);
    }
	
	private void lanzaOpciones(){
    	Intent i = new Intent(Menu_andseries.this, Opciones.class);
    	startActivity(i);
    }
}
