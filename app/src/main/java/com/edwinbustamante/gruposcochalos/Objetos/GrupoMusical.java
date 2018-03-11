package com.edwinbustamante.gruposcochalos.Objetos;

/**
 * Created by Admin on 22/2/2018.
 */

public class GrupoMusical {
    private String nombreGrupoMusical;
    private int urlFotoPortada;
    private String TipoDeMusica;

    public GrupoMusical() {
    }

    public GrupoMusical(int urlFotoPortada, String nombreGrupoMusical, String tipoDeMusica) {
        this.nombreGrupoMusical = nombreGrupoMusical;
        this.urlFotoPortada = urlFotoPortada;
        TipoDeMusica = tipoDeMusica;
    }

    public String getNombreGrupoMusical() {
        return nombreGrupoMusical;
    }

    public void setNombreGrupoMusical(String nombreGrupoMusical) {
        this.nombreGrupoMusical = nombreGrupoMusical;
    }

    public int getUrlFotoPortada() {
        return urlFotoPortada;
    }

    public void setUrlFotoPortada(int urlFotoPortada) {
        this.urlFotoPortada = urlFotoPortada;
    }

    public String getTipoDeMusica() {
        return TipoDeMusica;
    }

    public void setTipoDeMusica(String tipoDeMusica) {
        TipoDeMusica = tipoDeMusica;
    }
}
