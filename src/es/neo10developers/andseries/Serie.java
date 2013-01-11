package es.neo10developers.andseries;

public class Serie {
	private String idSerie;
	private String titulo;
	private String sinopsis;
	
	public Serie() {
		super();
		idSerie = "";
		titulo = "";
		sinopsis = "";
	}

	public String getIdSerie() {
		return idSerie;
	}

	public void setIdSerie(String idSerie) {
		this.idSerie = idSerie;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = Utilidades.quitaCosasRaras(titulo);
	}		

	public String getSinopsis() {
		return sinopsis;
	}

	public void setSinopsis(String sinopsis) {
		this.sinopsis = Utilidades.quitaCosasRaras(sinopsis);
	}

	@Override
	public String toString() {
		return this.titulo;
	}
}
