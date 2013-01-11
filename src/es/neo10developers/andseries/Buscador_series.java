package es.neo10developers.andseries;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Buscador_series extends SherlockFragment implements OnItemClickListener{	
	private Button btn_buscar;
	private ListView lista_buscador_series;
	private AutoCompleteTextView autoCompleteTextView1;
	private ArrayList<Serie> series;
	private ArrayList<Serie> series_filtradas;
	private TextView lbl_sin_coincidencias;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.buscador_series, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        
        btn_buscar = (Button) getView().findViewById(R.id.btn_buscar);
        lista_buscador_series = (ListView) getView().findViewById(R.id.lista_buscador_series);
        autoCompleteTextView1 = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextView1);
        lbl_sin_coincidencias = (TextView) getView().findViewById(R.id.lbl_sin_coincidencias);
                
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        lista_buscador_series.setOnItemClickListener(this);      
        
        btn_buscar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ocultarTeclado();
				muestraResultados();
		}});
        
        autoCompleteTextView1.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ocultarTeclado();
				muestraResultados();
			}});
        
        llenaAutoCompletado();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(Utilidades.laRedEstaDisponible(getActivity())){
			GestionBBDD ges = new GestionBBDD(getActivity());
			
			Intent i = new Intent(getActivity(), Ficha_serie.class);
			i.putExtra("titulo", ges.leeTituloSeriePorId(series_filtradas.get(position).getIdSerie()));
			i.putExtra("idSerie", series_filtradas.get(position).getIdSerie());
	          
			getActivity().startActivity(i);
		}else{
			Toast.makeText(getActivity(), getString(R.string.sin_conexion), Toast.LENGTH_LONG).show();
		}
		
	}
	
	private void ocultarTeclado(){
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(autoCompleteTextView1.getWindowToken(), 0);
	}
	
	private void llenaAutoCompletado(){
		GestionBBDD ges = new GestionBBDD(getActivity());		
		series = new ArrayList<Serie>(); 
		series = ges.leeSeries("");
		
		ArrayAdapter<Serie> adapter = new ArrayAdapter<Serie>(getActivity(), android.R.layout.simple_dropdown_item_1line, series);		
		autoCompleteTextView1.setAdapter(adapter);
		
		//Aprovechamos y llenamos la lista
		series_filtradas = series;
		llenaLista();
	}
	
	private void llenaLista(){
		if(series_filtradas.size() > 0){
			lista_buscador_series.setAdapter(new Adaptador_buscador_series(getActivity(), series_filtradas));
			lbl_sin_coincidencias.setVisibility(View.GONE);
			lista_buscador_series.setVisibility(View.VISIBLE);			
		}else{
			lista_buscador_series.setVisibility(View.GONE);
			lbl_sin_coincidencias.setVisibility(View.VISIBLE);
		}
		
		
	}
	
	private void muestraResultados(){
		GestionBBDD ges = new GestionBBDD(getActivity());		
		series_filtradas = new ArrayList<Serie>(); 
		
		String texto = autoCompleteTextView1.getText().toString();
		
		series_filtradas = ges.leeSeries(texto);
		
		llenaLista();
	}
}
