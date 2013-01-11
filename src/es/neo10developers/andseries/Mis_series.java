package es.neo10developers.andseries;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Mis_series extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener{
	private ListView lista_mis_series;
	private ArrayList<MiSerie> misSeries;
	private LinearLayout linearLayout_sin_series;
	private Serie serie;
    private LinearLayout linearLayout_recuperando_datos;
    protected InitTask iniciarTarea;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mis_series, null);
	}
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        
        lista_mis_series = (ListView) getView().findViewById(R.id.lista_mis_series);
        linearLayout_sin_series = (LinearLayout) getView().findViewById(R.id.linearLayout_sin_series);
        linearLayout_recuperando_datos = (LinearLayout) getView().findViewById(R.id.linearLayout_recuperando_datos);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        lista_mis_series.setOnItemClickListener(this);
        lista_mis_series.setOnItemLongClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//Llenamos la lista
		llenaLista();		
	}
	
	private void llenaLista(){
		GestionBBDD ges = new GestionBBDD(getActivity());		
		misSeries = new ArrayList<MiSerie>();
		
		if(Utilidades.laRedEstaDisponible(getActivity())){
			int operacionesPendientes = ges.numeroOperacionesPendientes();
			if(operacionesPendientes > 0){
				//Aquí hay que reintentar las operaciones pendientes
				OperacionPendiente op = new OperacionPendiente(getActivity());
				
				if (op.reintentarOperacionesPendientes(ges.leeOperacionesPendientes())){
					ges.eliminarOperacionesPendientes();
					
					iniciarTarea = new InitTask();
			    	iniciarTarea.execute(getActivity());
				}else{
					misSeries = ges.leeMisSeries();
					refrescarLista();
				}	
			}else{
				if(ges.totalMisSeries() > 0){
					misSeries = ges.leeMisSeries();
					refrescarLista();
				}else{
					iniciarTarea = new InitTask();
			    	iniciarTarea.execute(getActivity());
				}								
			}
		}else{
			if(ges.totalMisSeries() > 0){
				misSeries = ges.leeMisSeries();
				refrescarLista();
			}else{
				//Aquí hay que avisar al usuario que es la primera carga y que no tiene conexión.
				misSeries = ges.leeMisSeries();
				refrescarLista();
			}
		}
	}
	
	private void recuperarDatos(){
		misSeries = Sincronizador.leeSeriesServicioWeb(getActivity());
		actualizaSeriesEnLocal();
	}
	
	private void refrescarLista(){
				
		lista_mis_series.setAdapter(new Adaptador_mis_series(getActivity(), misSeries));
		
		if(misSeries.size() == 0){
			lista_mis_series.setVisibility(View.GONE);
			linearLayout_sin_series.setVisibility(View.VISIBLE);
		}else{
			linearLayout_sin_series.setVisibility(View.GONE);
			lista_mis_series.setVisibility(View.VISIBLE);			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(Utilidades.laRedEstaDisponible(getActivity())){
			Intent i = new Intent(getActivity(), Gestionar_mi_serie.class);
			i.putExtra("idSerie", misSeries.get(position).getIdSerie());
			getActivity().startActivity(i);
		}else{
			Toast.makeText(getActivity(), getString(R.string.sin_conexion), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		leerSerieSeleccionada(misSeries.get(position).getIdSerie());
		seleccionaOpcionesSerie(misSeries.get(position));
		
		//Nota: Devuelve true si se consume la pulsación
		//Esto es imprescindible si queremos manejar eventos
		//onclick y onlongclick simultáneamente
		return true;
	}
	
	private void seleccionaOpcionesSerie(final MiSerie miSer){
    	final CharSequence[] items = {getString(R.string.recomendar), getString(R.string.eliminar), getString(R.string.volver)};

    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(serie.getTitulo());

    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {    	            	       
    	        switch(item){
    	        
    	        case 0:
    	        	recomendarSerie(miSer);
    	        	break;
    	        
    	        case 1:
    	        	confirmarEliminarSerie(miSer);
    	        	break;
    	        	
    	        case 2:
    	        	dialog.dismiss();
    	        	break;   	        	
    	        	
    	        default:
    	        	Toast.makeText(getActivity(), getString(R.string.nada_seleccionado), Toast.LENGTH_LONG).show();
    	        	break;
    	        	
    	        }   	        
    	    }
    	});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
	
	private void confirmarEliminarSerie(final MiSerie miSer){				
		//Mostramos el diálogo de confirmación
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setMessage(getString(R.string.confirma_eliminar) + " " + serie.getTitulo() + " " + getString(R.string.de_su_lista))
	       .setCancelable(false)
	       .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   eliminarSerie(miSer.getIdSerie());
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
	
	private void eliminarSerie(String id){
		GestionBBDD ges = new GestionBBDD(getActivity());
		ges.eliminarMiSerie(id);
		
		Toast.makeText(getActivity(), getString(R.string.serie_eliminada), Toast.LENGTH_LONG).show();
		
		if(Utilidades.laRedEstaDisponible(getActivity())){
			if(!Sincronizador.eliminarSerie(getActivity(), id).equals("OK")){
				Log.d("Ficha_serie", "La operación queda pendiente");
				
				dejarComoPendiente(id);
			}else{
				Log.d("Ficha_serie", "Operación realizada");
			}
		}else{
			Log.d("Ficha_serie", "La operación queda pendiente");
			
			dejarComoPendiente(id);
		}
		
		//Llenamos la lista
		llenaLista();
		refrescarLista();
	}
	
	private void dejarComoPendiente(String id){
		GestionBBDD ges = new GestionBBDD(getActivity());
		
		OperacionPendiente op = new OperacionPendiente(getActivity());
		
		op.setTipoOperacion("E");
		op.setIdSerie(id);
		op.setTemporada(0);
		op.setCapitulo(0);
		
		ges.insertarOperacionPendiente(op);
	}
	
	private void recomendarSerie(MiSerie miSer){		
		Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.asunto_recomendacion));
        
        String cuerpo = getString(R.string.cuerpo_recomendacion)+":\n\n" + serie.getTitulo()+ "\n\n" + serie.getSinopsis();
        intent.putExtra(Intent.EXTRA_TEXT, cuerpo);
        startActivity(intent);
	}
	
	private void leerSerieSeleccionada(String id){
		GestionBBDD ges = new GestionBBDD(getActivity());
		serie = ges.leeSeriePorId(id);		
	}
	
	private void actualizaSeriesEnLocal(){
		//Eliminamos todas las series y guardamos las recuperadas por el servicio web
		GestionBBDD ges = new GestionBBDD(getActivity());
		ges.eliminarMisSeries();
		
		LeerSerieServicioWeb lee = new LeerSerieServicioWeb();
		serie = new Serie();
		
		//Insertamos cada serie y descargamos cada banner
		for(MiSerie miser:misSeries){
			ges.insertaSerie(miser);
			Utilidades.descargaBanner(getActivity(), miser.getIdSerie());
						
			serie = lee.datosSerie(ges.leeTituloSeriePorId(miser.getIdSerie()));
			ges.actualizarSerie(serie);
		}
	}
	
	/* Para versiones más avanzadas de la aplicación se podrán crear alertas para que avise 
	 * de la fecha de emisión de una serie. El problema es que hay que utilizar el API 9 de Android
	 * y eso dejaría casi al 30% de dispositivos Android sin poder utilizar la aplicación.
	 * 
	 * private void crearAlerta(){
		Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
		i.putExtra(AlarmClock.EXTRA_MESSAGE, "Serie"); 
		i.putExtra(AlarmClock.EXTRA_HOUR, 21);
		i.putExtra(AlarmClock.EXTRA_MINUTES, 30);
		startActivity(i);
	}*/
	
	/**
	 * Tarea asíncrona para recuperar los datos del servidor y mostrar el loading
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			linearLayout_sin_series.setVisibility(View.GONE);
			lista_mis_series.setVisibility(View.GONE);
			linearLayout_recuperando_datos.setVisibility(View.VISIBLE);			
		}

		@Override
		protected String doInBackground(Context... params) {
			recuperarDatos();
			
			return "TERMINADO!";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			linearLayout_recuperando_datos.setVisibility(View.GONE);
			lista_mis_series.setVisibility(View.VISIBLE);
			
			refrescarLista();
		}
	}
}
