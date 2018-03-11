package com.edwinbustamante.gruposcochalos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwinbustamante.gruposcochalos.Objetos.GrupoMusical;

import java.util.List;

/**
 * Created by Admin on 22/2/2018.
 */

public class RecyclerViewAdaptadorPrincipal extends RecyclerView.Adapter<RecyclerViewAdaptadorPrincipal.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView fotoPortada;
        private TextView nombreGrupo, tipoMusica;

        public ViewHolder(View itemView) {
            super(itemView);
            fotoPortada = (ImageView) itemView.findViewById(R.id.fotoPortada);
            nombreGrupo = (TextView) itemView.findViewById(R.id.textNombreGrupo);
            tipoMusica = (TextView) itemView.findViewById(R.id.textTipoDeMusica);

        }
    }

    public List<GrupoMusical> gruposMusicalesLista;//lista de todos los grupos

    public RecyclerViewAdaptadorPrincipal(List<GrupoMusical> gruposMusicalLista) {
        this.gruposMusicalesLista = gruposMusicalLista;
    }

    //encargado de inflar un nuevo item para la lista
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // aqui es donde se esta haciendo llamado a  la tarjeta que vamos a  cargar
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_grupo, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //modificaciones del cntenido de cada item
        holder.fotoPortada.setImageResource(gruposMusicalesLista.get(position).getUrlFotoPortada());
        holder.nombreGrupo.setText(gruposMusicalesLista.get(position).getNombreGrupoMusical());
        holder.tipoMusica.setText(gruposMusicalesLista.get(position).getTipoDeMusica());

    }

    @Override
    public int getItemCount() {
         return gruposMusicalesLista.size();
    }
}
