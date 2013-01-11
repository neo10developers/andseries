package es.neo10developers.andseries;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import es.neo10developers.andseries.R;

public class Adaptador_buscador_series extends BaseAdapter {
    private final ArrayList<Serie> lista;
    private LayoutInflater inflater;

    public Adaptador_buscador_series(Context context, ArrayList<Serie> lista) {
          super();
          this.lista = lista;
          this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder{
    	TextView lbl_titulo;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {          
    	ViewHolder holder;
    	
        if(convertView==null){
        	holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.elemento_lista_buscador, null);
        	
            holder.lbl_titulo = (TextView) convertView.findViewById(R.id.lbl_titulo);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.lbl_titulo.setText(lista.get(position).getTitulo());

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
