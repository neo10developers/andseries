package es.neo10developers.andseries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import es.neo10developers.andseries.R;

public class GestionBBDD extends SQLiteOpenHelper{
	private Context ctx;
	
	private String creaTablaUsuario = "CREATE TABLE IF NOT EXISTS [Usuario] ([Usuario] TEXT  UNIQUE NOT NULL PRIMARY KEY,[Pass] TEXT  NULL)";
	private String creaTablaSeries = "CREATE TABLE IF NOT EXISTS [Series] ([idSerie] TEXT  UNIQUE NOT NULL PRIMARY KEY,[Titulo] TEXT  NULL,[Sinopsis] TEXT  NULL)";
	private String creaTablaMisSeries = "CREATE TABLE IF NOT EXISTS [Mis_series] ([idSerie] TEXT  UNIQUE NOT NULL PRIMARY KEY,[Temporada] INTEGER  NULL,[Capitulo] INTEGER  NULL)";	
	private String creaTablaOperacionesPendientes = "CREATE TABLE IF NOT EXISTS [Operaciones_pendientes] ([codigoOperacion] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[tipoOperacion] TEXT  NULL,[idSerie] TEXT  NULL,[Temporada] INTEGER  NULL,[Capitulo] INTEGER  NULL)";	
	
	private String eliminaTablaUsuario = "DROP TABLE IF EXISTS Usuario";
	private String eliminaTablaMisSeries = "DROP TABLE IF EXISTS Mis_series";
	private String eliminaTablaOperacionesPendientes = "DROP TABLE IF EXISTS Operaciones_Pendientes";	
	
	public GestionBBDD(Context context) {
		super(context, "andseries.db", null, 1);
		ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(creaTablaUsuario);
		db.execSQL(creaTablaSeries);
		db.execSQL(creaTablaMisSeries);
		db.execSQL(creaTablaOperacionesPendientes);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior, int nuevaVersion) {
		//En caso de actualizar la versión de la BBDD en una instalación anterior, se llama a este método.		
	}	
	
	//TABLA SERIES
	public void inicializaTablaSeries() {
		ArrayList<Serie> series = leeFicheroSeries();
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL("DELETE FROM series");
		
		String sql = "";
		
		db.execSQL("BEGIN");
		for(Serie ser:series){
			try {
				sql = "INSERT INTO series VALUES ('" + ser.getIdSerie() + "', '"+ ser.getTitulo() + "', '')";				
				
				db.execSQL(sql);
			} catch (SQLException e) {
			}
		}
		db.execSQL("COMMIT");
		
		db.close();
	}
	
	private ArrayList<Serie> leeFicheroSeries(){
		ArrayList<Serie> series = new ArrayList<Serie>();

		//Para empezar, dejamos un fichero con más de 2000 series en los recursos.
		//Lo ideal sería tener este fichero en una web actualizado y que se descargara cuando hubiera una nueva versión
		InputStream inputStream = ctx.getResources().openRawResource(R.raw.series);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(inputreader);

		String line;

		try {
			while ((line = br.readLine()) != null) {
				Serie serie = new Serie();

				// Partimos por puntos y coma. Para tener en cuenta los blancos, utilizar el parámetro -1
				String[] campos = line.split(";", -1);

				serie.setIdSerie(campos[0]);
				serie.setTitulo(campos[1]);

				series.add(serie);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return series;
	}
	
	public boolean comprobarDatosIniciales(){
    	boolean hayDatos = false;
    	int totalRegistros = 0;
    	
    	SQLiteDatabase db = getReadableDatabase();
    	
    	Cursor cursor = db.rawQuery("SELECT count(*) FROM series", null);
    	
		while (cursor.moveToNext()) {
			totalRegistros = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		if(totalRegistros > 0){
			hayDatos = true;
		}
    	
    	return hayDatos;
    }
	
	public ArrayList<Serie> leeSeries(String busqueda){
		ArrayList<Serie> series = new ArrayList<Serie>();

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT * FROM series";
		
		if(!busqueda.equals("")){
			sql += " WHERE titulo LIKE '%"+ busqueda +"%' ORDER BY titulo";
		}
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {
			Serie serie = new Serie();
			
			serie.setIdSerie(cursor.getString(0));
			serie.setTitulo(cursor.getString(1));
			
			series.add(serie);
		}
		
		cursor.close();
		db.close();
		
		return series;
	}
	
	public String leeTituloSeriePorId(String idSerie){
		String titulo = "";

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT titulo FROM series WHERE idSerie = '"+idSerie+"'";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {
			
			titulo = cursor.getString(0);
		}
		
		cursor.close();
		db.close();
		
		return titulo;
	}
	
	public Serie leeSeriePorId(String idSerie){
		Serie serie = new Serie();

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT * FROM series WHERE idSerie = '"+idSerie+"'";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {
			
			serie.setIdSerie(cursor.getString(0));
			serie.setTitulo(cursor.getString(1));
			serie.setSinopsis(cursor.getString(2));
		}
		
		cursor.close();
		db.close();
		
		return serie;
	}
	
	public void actualizarSerie(Serie ser){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("UPDATE Series SET titulo='"+ser.getTitulo()+"', Sinopsis='"+ser.getSinopsis()+"' WHERE idSerie='"+ser.getIdSerie()+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	//TABLA MIS SERIES
	public void insertaSerie(MiSerie ser) {
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("INSERT INTO Mis_series VALUES ('" + ser.getIdSerie() + "', "+ser.getTemporada()+", "+ser.getCapitulo()+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public ArrayList<MiSerie> leeMisSeries(){
		ArrayList<MiSerie> misSeries = new ArrayList<MiSerie>();

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT * FROM Mis_series, series WHERE Mis_series.idSerie=series.idserie ORDER BY series.titulo";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {
			MiSerie serie = new MiSerie();
			
			serie.setIdSerie(cursor.getString(0));
			serie.setTemporada(cursor.getInt(1));
			serie.setCapitulo(cursor.getInt(2));
			
			misSeries.add(serie);
		}
		
		cursor.close();
		db.close();
		
		return misSeries;
	}
	
	public MiSerie leeMiSerie(String idSerie){
		MiSerie serie = new MiSerie();

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT * FROM Mis_series WHERE idSerie='"+idSerie+"'";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {			
			
			serie.setIdSerie(cursor.getString(0));
			serie.setTemporada(cursor.getInt(1));
			serie.setCapitulo(cursor.getInt(2));
		}
		
		cursor.close();
		db.close();
		
		return serie;
	}
	
	public void modificarMiSerie(MiSerie miSer){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("UPDATE Mis_series SET Temporada="+miSer.getTemporada()+", Capitulo="+miSer.getCapitulo()+" WHERE idSerie='"+miSer.getIdSerie()+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}	
	
	public void eliminarMiSerie(String idSerie){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("DELETE FROM Mis_series WHERE idSerie='"+idSerie+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public void eliminarMisSeries(){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("DELETE FROM Mis_series");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public int totalMisSeries(){
		int total = 0;
		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT COUNT(*) FROM Mis_series";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {			
			total = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		return total;
	}
	
	//TABLA USUARIOS
	public Usuario leeUsuario(){
		Usuario user = new Usuario();

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT * FROM usuario";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {			
			
			user.setUsuario(cursor.getString(0));
			user.setPass(cursor.getString(1));
		}
		
		cursor.close();
		db.close();
		
		return user;
	}
	
	public void insertarUsuario(Usuario user){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("INSERT INTO usuario VALUES ('" + user.getUsuario() + "', '"+user.getPass()+"')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public void eliminarUsuarios(){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("DELETE FROM usuario");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	//TABLA OPERACIONES PENDIENTES
	//Son las operaciones pendientes de realizar en el servidor
	
	/*
	 I --> insertar
	 A --> actualizar
	 E --> eliminar 
	 
	 */	
	
	public ArrayList<OperacionPendiente> leeOperacionesPendientes(){
		ArrayList<OperacionPendiente> operaciones = new ArrayList<OperacionPendiente>();

		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT * FROM Operaciones_Pendientes ORDER BY codigoOperacion";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {
			OperacionPendiente op = new OperacionPendiente(ctx);
			
			op.setCodigoOperacion(cursor.getInt(0));
			op.setTipoOperacion(cursor.getString(1));
			op.setIdSerie(cursor.getString(2));
			op.setTemporada(cursor.getInt(3));
			op.setCapitulo(cursor.getInt(4));
			
			operaciones.add(op);
		}
		
		cursor.close();
		db.close();
		
		return operaciones;
	}
	
	public void insertarOperacionPendiente(OperacionPendiente op){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("INSERT INTO Operaciones_Pendientes (tipoOperacion, idSerie, temporada, capitulo) VALUES ('" + op.getTipoOperacion() + "', '"+op.getIdSerie()+"', "+op.getTemporada()+", "+op.getCapitulo()+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public void eliminarOperacionPendiente(int codigoOperacion){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("DELETE FROM Operaciones_Pendientes WHERE codigoOperacion="+codigoOperacion+"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public void eliminarOperacionesPendientes(){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL("DROP TABLE IF EXISTS Operaciones_Pendientes");
			db.execSQL(creaTablaOperacionesPendientes);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
	
	public int numeroOperacionesPendientes(){
		int operaciones = 0;
		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT COUNT(*) FROM Operaciones_Pendientes";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {			
			operaciones = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		return operaciones;
	}
	
	public int totalSeries(){
		int total = 0;
		SQLiteDatabase db = getReadableDatabase();
    	
		String sql = "SELECT COUNT(*) FROM series";
		
    	Cursor cursor = db.rawQuery(sql, null);
    	
		while (cursor.moveToNext()) {			
			total = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		return total;
	}
	
	public void limpiarDatosUsuario(){
		SQLiteDatabase db = getWritableDatabase();
		
		try {
			db.execSQL(eliminaTablaUsuario);
			db.execSQL(creaTablaUsuario);
			db.execSQL(eliminaTablaMisSeries);
			db.execSQL(creaTablaMisSeries);
			db.execSQL(eliminaTablaOperacionesPendientes);
			db.execSQL(creaTablaOperacionesPendientes);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		db.close();
	}
}