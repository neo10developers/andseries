package es.neo10developers.andseries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Sincronizador {
	public static ArrayList<MiSerie> leeSeriesServicioWeb(Context ctx){
		ArrayList<MiSerie> lista = new ArrayList<MiSerie>();				
		
		String usuario = "";
		GestionBBDD ges = new GestionBBDD(ctx);
    	usuario = ges.leeUsuario().getUsuario();
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://andseries.neo10developers.es/gestionSeriesUsuario.php");

        try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
			nameValuePairs.add(new BasicNameValuePair("accion", "leer"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			//200 = OK
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
	
					String result = Utilidades.convertStreamToString(instream);
	
					instream.close();
					
					try {
						lista = parseJSONdataContactos(result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}            
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
		
        return lista;
	}
	
	private static ArrayList<MiSerie> parseJSONdataContactos(String data) throws JSONException {
		ArrayList<MiSerie> lista = new ArrayList<MiSerie>();
		
		JSONObject jsonObj = new JSONObject(data);
		String strData = jsonObj.getString("seriesUsuario");
		JSONArray jsonArray = new JSONArray(strData);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject userObj = jsonArray.getJSONObject(i);
			String userStr = userObj.getString("serieUsuario");
			JSONObject item = new JSONObject(userStr);
			
			MiSerie miSer = new MiSerie();
			
			miSer.setIdSerie(item.getString("idserie"));
			miSer.setTemporada(item.getInt("temporada"));
			miSer.setCapitulo(item.getInt("capitulo"));
			
			lista.add(miSer);
		}
		
		return lista;
	}
	
	public static String registrar(String usuario, String pass){
    	String result = "";		
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://andseries.neo10developers.es/registro.php");

        try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
			nameValuePairs.add(new BasicNameValuePair("pass", Utilidades.md5(pass)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			//200 = OK
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
	
					result = convertStreamToString(instream);
	
					instream.close();
				}
			}            
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
		
		return result;
	}
	
	private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
	
	public static String acceder(String usuario, String pass){
    	String result = "";				
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://andseries.neo10developers.es/login.php");

        try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
			nameValuePairs.add(new BasicNameValuePair("pass", Utilidades.md5(pass)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = null;
			
			try {
				response = httpclient.execute(httppost);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//200 = OK
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
	
					result = Utilidades.convertStreamToString(instream);
	
					instream.close();
				}
			}            
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
		
		return result;
	}
	
	public static String insertarSerie(Context ctx, String idSerie, String temporada, String capitulo){
    	String result = "";
    	String usuario = "";
    	
    	GestionBBDD ges = new GestionBBDD(ctx);
    	usuario = ges.leeUsuario().getUsuario();
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://andseries.neo10developers.es/gestionSeriesUsuario.php");

        try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
			nameValuePairs.add(new BasicNameValuePair("accion", URLEncoder.encode("insertar", "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("idserie", URLEncoder.encode(idSerie, "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("temporada", URLEncoder.encode(temporada, "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("capitulo", URLEncoder.encode(capitulo, "UTF-8")));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			//200 = OK
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
	
					result = convertStreamToString(instream);
	
					instream.close();
				}
			}            
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
		
		return result;
	}
	
	public static String actualizarSerie(Context ctx, String idSerie, String temporada, String capitulo){
    	String result = "";
    	String usuario = "";
    	
    	GestionBBDD ges = new GestionBBDD(ctx);
    	usuario = ges.leeUsuario().getUsuario();
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://andseries.neo10developers.es/gestionSeriesUsuario.php");

        try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
			nameValuePairs.add(new BasicNameValuePair("accion", URLEncoder.encode("actualizar", "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("idserie", URLEncoder.encode(idSerie, "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("temporada", URLEncoder.encode(temporada, "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("capitulo", URLEncoder.encode(capitulo, "UTF-8")));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			//200 = OK
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
	
					result = convertStreamToString(instream);
	
					instream.close();
				}
			}            
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
		
		return result;
	}
	
	public static String eliminarSerie(Context ctx, String idSerie){
    	String result = "";
    	String usuario = "";
    	
    	GestionBBDD ges = new GestionBBDD(ctx);
    	usuario = ges.leeUsuario().getUsuario();
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://andseries.neo10developers.es/gestionSeriesUsuario.php");

        try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
			nameValuePairs.add(new BasicNameValuePair("accion", URLEncoder.encode("eliminar", "UTF-8")));
			nameValuePairs.add(new BasicNameValuePair("idserie", URLEncoder.encode(idSerie, "UTF-8")));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			//200 = OK
			if(response.getStatusLine().getStatusCode() == 200){
				
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
	
					result = convertStreamToString(instream);
	
					instream.close();
				}
			}            
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
		
		return result;
	}
}
