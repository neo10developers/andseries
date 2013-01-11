package es.neo10developers.andseries;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utilidades {
	public static boolean versionPro(Context ctx){
		boolean pro = false;
		
		//if (ctx.getPackageName().equals("es.neo10developers.andseries.pro"))
			pro = true;	
		
		return pro;
	}
	
	//Escapa los caracteres especiales
	public static String quitaCosasRaras(String s) {
		s = s.replaceAll("'", "''");
		s = s.replaceAll("%", "");
		s = s.replaceAll("\n", "");
		
		return s;
	}
	
	//Comprobamos si hay una conexión disponible
	public static boolean laRedEstaDisponible(Context context) {
		boolean value = false;
		
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		
		if (info != null && info.isAvailable()) {
			value = true;
		}
		
		return value;
	}
	
	//Descargamos el banner de la serie
	public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) {
            Log.e("Exception", e.getMessage());
            return null;
        }
    }
	
	//Descargar un fichero
	public static boolean downloadFile(Context ctx, String url, String nombreFichero) {
    	boolean descargado = false;
		
		ctx.deleteFile(nombreFichero);
    			
		//Habría que poner una especie de timeout máximo para que no se quede bloqueado
    	try {
    	      URL u = new URL(url);
    	      URLConnection conn = u.openConnection();
    	      int contentLength = conn.getContentLength();    	      
    	      
    	      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
    	      con.setConnectTimeout(10000);
    	      con.setReadTimeout(10000);
    	      
    	      if(con.getResponseCode() != HttpURLConnection.HTTP_OK){
    	    	  return false;
    	      }
    	      
    	      DataInputStream stream = new DataInputStream(u.openStream());
    	      
    	        byte[] buffer = new byte[contentLength];
    	        stream.readFully(buffer);
    	        stream.close();

    	        OutputStream file = ctx.openFileOutput(nombreFichero, Context.MODE_PRIVATE);
    	        DataOutputStream fos = new DataOutputStream(file);
    	        
    	        fos.write(buffer);
    	        fos.flush();
    	        fos.close();
    	        
    	        descargado = true;
    	  } catch(FileNotFoundException e) {
    	      return false; // swallow a 404
    	  } catch (IOException e) {
    	      return false; // swallow a 404
    	  }
    	
    	return descargado;
    }
	
	//MD5
	//Para el PHP: md5(utf8_encode($string));
	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	//Stream a String
	public static String convertStreamToString(InputStream is) throws IOException {
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

	//Descargar banners
	public static boolean descargaBanner(Context ctx, String idSerie){
		String nombreFichero = idSerie + ".jpg";	
		
		String urlGraphical = "http://www.thetvdb.com/banners/graphical/" + idSerie + "-g.jpg";
		String urlText = "http://www.thetvdb.com/banners/text/" + nombreFichero;
		String urlBlank = "http://www.thetvdb.com/banners/blank/" + nombreFichero;
		
		boolean descargado = false;
		
		File f = new File(ctx.getFilesDir() + "/" +nombreFichero);
		if(f.exists()){
			descargado = true;
		}
		
		if(!descargado){
			if(!Utilidades.downloadFile(ctx, urlGraphical, nombreFichero)){
				if(!Utilidades.downloadFile(ctx, urlText, nombreFichero)){
					if(!Utilidades.downloadFile(ctx, urlBlank, nombreFichero)){
						descargado = false;
					}else{
						descargado = true;
					}
				}else{
					descargado = true;
				}
			}else{
				descargado = true;
			}
		}

		return descargado;
	}
}
