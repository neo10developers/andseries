package es.neo10developers.andseries;

import java.util.ArrayList;

import android.content.Context;

public class OperacionPendiente {
	private int codigoOperacion;
	private String tipoOperacion;
	private String idSerie;
	private int temporada;
	private int capitulo;
	private Context ctx;
	
	public OperacionPendiente(Context con) {
		super();
		ctx = con;
		tipoOperacion = "";
		idSerie = "";
		temporada = 0;
		capitulo = 0;		
	}
	
	public int getCodigoOperacion() {
		return codigoOperacion;
	}
	public void setCodigoOperacion(int codigoOperacion) {
		this.codigoOperacion = codigoOperacion;
	}	
	public String getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	public String getIdSerie() {
		return idSerie;
	}
	public void setIdSerie(String idSerie) {
		this.idSerie = idSerie;
	}
	public int getTemporada() {
		return temporada;
	}
	public void setTemporada(int temporada) {
		this.temporada = temporada;
	}
	public int getCapitulo() {
		return capitulo;
	}
	public void setCapitulo(int capitulo) {
		this.capitulo = capitulo;
	}
	
	public boolean reintentarOperacionesPendientes(ArrayList<OperacionPendiente> operaciones){
		boolean reintentoCompletado = true;
		
		GestionBBDD ges = new GestionBBDD(ctx);
		
		String res = "";
		
		for(OperacionPendiente op:operaciones){
			res = "";
		
			if(op.getTipoOperacion().equals("I")){
				res = Sincronizador.insertarSerie(ctx, op.getIdSerie(), String.valueOf(op.getTemporada()), String.valueOf(op.getCapitulo()));
			}
			
			if(op.getTipoOperacion().equals("A")){
				res = Sincronizador.actualizarSerie(ctx, op.getIdSerie(), String.valueOf(op.getTemporada()), String.valueOf(op.getCapitulo()));
			}
	
			if(op.getTipoOperacion().equals("E")){
				res = Sincronizador.eliminarSerie(ctx, op.getIdSerie());
			}
			
			if(!res.equals("OK")){
				reintentoCompletado = false;
			}else{
				ges.eliminarOperacionPendiente(op.getCodigoOperacion());
			}
		}
		
		return reintentoCompletado;
	}
}
