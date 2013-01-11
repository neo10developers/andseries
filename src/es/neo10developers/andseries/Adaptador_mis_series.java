package es.neo10developers.andseries;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class Adaptador_mis_series extends BaseAdapter {
	public LayoutInflater inflater;
    private final ArrayList<MiSerie> lista;
    private Context ctx;

    public Adaptador_mis_series(Context con, ArrayList<MiSerie> lista) {
          super();
          this.lista = lista;
          ctx = con;
          this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder{
    	ImageView imageView_mi_serie;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {          
    	ViewHolder holder;
    	
        if(convertView==null){
        	holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.elemento_lista_mis_series, null);
        	
            holder.imageView_mi_serie = (ImageView) convertView.findViewById(R.id.imageView_mi_serie);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        
        String nombreFichero = lista.get(position).getIdSerie() + ".jpg";
		
		File f = new File(ctx.getFilesDir() + "/" + nombreFichero);
		if(f.exists()){
			holder.imageView_mi_serie.setImageDrawable(Drawable.createFromPath(ctx.getFilesDir() + "/" + nombreFichero));
		}else{
			Drawable logo = ctx.getResources().getDrawable(R.drawable.logo);			
			holder.imageView_mi_serie.setImageDrawable(logo);
		}

        return convertView;
    }

    public int getCount(){
          return lista.size();
    }

    public Object getItem(int pos) {
          return lista.get(pos);
    }

    public long getItemId(int position) {
          return position;
    }
}
