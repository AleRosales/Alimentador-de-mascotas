package eestn1.rosales.alejandro.alimentador_final;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
    Context contexto;
    String[] idhorafecha;
    int[]imagenes;
    LayoutInflater inflater;
    public ListViewAdapter(Context context, String[] idhorafecha, int[] imagenes) {
        this.contexto=context;
        this.idhorafecha=idhorafecha;
        this.imagenes=imagenes;
    }

    @Override
    public int getCount() {
        return idhorafecha.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txthora;
        ImageView imgImg;

        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_items, parent, false);

        txthora = (TextView) itemView.findViewById(R.id.TVhora);
        imgImg = (ImageView) itemView.findViewById(R.id.imagen);

        txthora.setText(idhorafecha[position]);
        imgImg.setImageResource(imagenes[position]);
        return itemView;
    }
}
