package es.neo10developers.andseries;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragment;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Recomendaciones extends SherlockFragment implements OnItemClickListener{
	private ArrayList<String> lista;
	private ListView lista_recomendaciones;
	private LinearLayout linearLayout_cargando;
	protected InitTask iniciarTarea;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recomendaciones, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        
        lista_recomendaciones = (ListView) getView().findViewById(R.id.lista_recomendaciones);
        linearLayout_cargando = (LinearLayout) getView().findViewById(R.id.linearLayout_cargando);
                
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        lista_recomendaciones.setOnItemClickListener(this);
        
        if(Utilidades.laRedEstaDisponible(getActivity())){
        	iniciarTarea = new InitTask();
        	iniciarTarea.execute(getActivity());
        }else{
        	Toast.makeText(getActivity(), getString(R.string.sin_conexion), Toast.LENGTH_LONG).show();
        }
	}
	
	private void leerRecomendaciones(){
		LeerRecomendacionesServicioWeb lee = new LeerRecomendacionesServicioWeb();
		lista = lee.recomendaciones();			
	}
	
	private void descargarImagenes(){
		for(String idSerie:lista){
			Log.d("Recomendaciones", "Descargando " + idSerie);
			Utilidades.descargaBanner(getActivity(), idSerie);			
		}
	}
	
	private void mostrarRecomendaciones(){
		if(lista == null){
			Toast.makeText(getActivity(), getString(R.string.sin_conexion), Toast.LENGTH_LONG).show();
		}else{
			lista_recomendaciones.setAdapter(new Adaptador_recomendaciones(getActivity(), lista));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		GestionBBDD ges = new GestionBBDD(getActivity());
		Serie serie = ges.leeSeriePorId(lista.get(position));
		
		Intent i = new Intent(getActivity(), Ficha_serie.class);
		i.putExtra("titulo", serie.getTitulo());
		i.putExtra("idSerie", serie.getIdSerie());
		
		startActivity(i);
	}
	
	/**
	 * Tarea asíncrona para cargar los datos de recomendaciones y mostrar el loading
	 */
	protected class InitTask extends AsyncTask<Context, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			linearLayout_cargando.setVisibility(View.VISIBLE);			
		}

		@Override
		protected String doInBackground(Context... params) {
			leerRecomendaciones();
			descargarImagenes();
			
			return "TERMINADO!";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mostrarRecomendaciones();
			linearLayout_cargando.setVisibility(View.GONE);
		}
	}
}
