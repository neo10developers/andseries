package es.neo10developers.andseries;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

public class LeerRecomendacionesServicioWeb {	
	public ArrayList<String> recomendaciones() {
		try {
			String direccion = "http://www.thetvdb.com/api/User_Favorites.php?accountid=7FB13C3CF230D455";
			
			URL url = new URL(direccion);
			
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
						
			if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				SAXParserFactory fabrica = SAXParserFactory.newInstance();
				SAXParser parser = fabrica.newSAXParser();
				XMLReader lector = parser.getXMLReader();
				ManejadorSerWeb manejadorXML = new ManejadorSerWeb();
				lector.setContentHandler(manejadorXML);
				lector.parse(new InputSource(conexion.getInputStream()));
				
				return manejadorXML.getRecomendaciones();
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
		private ArrayList<String> lista;

		public ArrayList<String> getRecomendaciones() {
			return lista;
		}

		@Override
		public void startDocument() throws SAXException {
			cadena = new StringBuilder();
			lista = new ArrayList<String>();
		}

		@Override
		public void characters(char ch[], int comienzo, int longitud) {
			cadena.append(ch, comienzo, longitud);
		}

		@Override
		public void endElement(String uri, String nombreLocal, String nombreCualif) throws SAXException {			
			String texto = Utilidades.quitaCosasRaras(cadena.toString());
			
			try {
				texto = URLDecoder.decode(texto, "UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			if (nombreLocal.equals("Series")){
				lista.add(texto);
			}		
			
			cadena.setLength(0);
		}
	}
}