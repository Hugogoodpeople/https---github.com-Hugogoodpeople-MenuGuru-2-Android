package Utils;

import java.io.Serializable;

/**
 * Created by hugocosta on 13/02/15.
 */
public class ObjectInfoRest implements Serializable
{

    public String titulo;
    public String conteudo;
    public String subconteudo;

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getConteudo()
    {
        return conteudo;
    }

    public void setConteudo(String conteudo)
    {
        this.conteudo = conteudo;
    }

    public String getSubconteudo() {
        return subconteudo;
    }

    public void setSubconteudo(String subconteudo) {
        this.subconteudo = subconteudo;
    }
}
