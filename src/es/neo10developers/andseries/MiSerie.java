package es.neo10developers.andseries;

public class MiSerie {
	private String idSerie;
	private int temporada;
	private int capitulo;
	
	public MiSerie() {
		super();
		idSerie = "";
		temporada = 0;	
		capitulo = 0;
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
}
