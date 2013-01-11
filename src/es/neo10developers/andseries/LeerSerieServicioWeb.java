package es.neo10developers.andseries;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

public class LeerSerieServicioWeb {
	private String IDIOMA = "es";
	
	public Serie datosSerie(String nombre) {
		try {
			String direccion = "http://www.thetvdb.com/api/GetSeries.php?seriesname="+ URLEncoder.encode(nombre, "UTF-8") + "&language=" + URLEncoder.encode(IDIOMA, "UTF-8");
			
			URL url = new URL(direccion);
			
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
						
			if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				SAXParserFactory fabrica = SAXParserFactory.newInstance();
				SAXParser parser = fabrica.newSAXParser();
				XMLReader lector = parser.getXMLReader();
				ManejadorSerWeb manejadorXML = new ManejadorSerWeb();
				lector.setContentHandler(manejadorXML);
				
				try{
					lector.parse(new InputSource(conexion.getInputStream()));
				} catch (SaxBreakOutException allDone) {
				}
				
				return manejadorXML.getSerie();
			} else {
				Log.e("ANDseries", conexion.getResponseMessage());
				return null;
			}
		} catch (Exception e) {
			Log.e("ANDseries", e.getMessage(), e);
			return null;
		}
	}

	class ManejadorSerWeb extends DefaultHandler {
		private StringBuilder cadena;
		private Serie serie;
		private String idioma;

		public Serie getSerie() {
			return serie;
		}

		@Override
		public void startDocument() throws SAXException {
			cadena = new StringBuilder();
			serie = new Serie();
			idioma = "";
		}

		@Override
		public void characters(char ch[], int comienzo, int longitud) {
			cadena.append(ch, comienzo, longitud);
		}

		@Override
		public void endElement(String uri, String nombreLocal, String nombreCualif) throws SAXException {									
			//Nos aseguramos de que coincida el primer registro
			if(serie.getIdSerie().equals("")){
				String texto = Utilidades.quitaCosasRaras(cadena.toString());
				
				try {
					texto = URLDecoder.decode(texto, "UTF8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}				
				
				if (nombreLocal.equals("language")){
					idioma = texto;
				}
				
				//Si el idioma es el que queremos
				if(idioma.equals(IDIOMA)){
					if (nombreLocal.equals("SeriesName")){
						serie.setTitulo(texto);
					}
					
					if (nombreLocal.equals("Overview")){
						serie.setSinopsis(texto);
					}
					
					if (nombreLocal.equals("id")){
						serie.setIdSerie(texto);
						
						idioma = "";
						throw new SaxBreakOutException();
						//Se provoca esta excepción controlada para que no siga leyendo el XML
					}
				}											
			}
			cadena.setLength(0);
		}
	}
	
	@SuppressWarnings("serial")
	class SaxBreakOutException extends RuntimeException {
	}
}