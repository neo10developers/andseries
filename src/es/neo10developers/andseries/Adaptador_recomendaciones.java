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

public class Adaptador_recomendaciones extends BaseAdapter {
    private final ArrayList<String> lista;
    private LayoutInflater inflater;
    private Context ctx;

    public Adaptador_recomendaciones(Context context, ArrayList<String> lista) {
          super();
          this.lista = lista;
          ctx = context;
          this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder{
    	ImageView imageView_serie_recomendada;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {          
    	ViewHolder holder;
    	
        if(convertView==null){
        	holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.elemento_lista_recomendaciones, null);
        	
            holder.imageView_serie_recomendada = (ImageView) convertView.findViewById(R.id.imageView_serie_recomendada);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        
        String nombreFichero = lista.get(position) + ".jpg";
		
		File f = new File(ctx.getFilesDir() + "/" + nombreFichero);
		if(f.exists()){
			holder.imageView_serie_recomendada.setImageDrawable(Drawable.createFromPath(ctx.getFilesDir() + "/" + nombreFichero));
		}else{
			Drawable logo = ctx.getResources().getDrawable(R.drawable.logo);			
			holder.imageView_serie_recomendada.setImageDrawable(logo);
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
