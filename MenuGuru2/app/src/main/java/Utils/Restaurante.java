package Utils;



import java.io.Serializable;

/**
 * Created by hugocosta on 12/08/14.
 */
public class Restaurante  implements Serializable {

    public String nome= " ";
    public String db_id= " ";
    public String latitude= " ";
    public String longitude= " ";
    public String morada= " ";
    public String tipo= " ";
    public String votacoes= "0";
    public String mediarating= "0";
    public String cidade= "0";
    public String cosinhas = "";
    public String hora_minimo_antedencia_mesa = "";

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCosinhas() {
        return cosinhas;
    }

    public void setCosinhas(String cosinhas) {
        this.cosinhas = cosinhas;
    }

    public String telefone = "";

    public String getHora_minimo_antedencia_mesa() {
        return hora_minimo_antedencia_mesa;
    }

    public void setHora_minimo_antedencia_mesa(String hora_minimo_antedencia_mesa) {
        this.hora_minimo_antedencia_mesa = hora_minimo_antedencia_mesa;
    }

    public String getPrecoMedio() {
        return precoMedio;
    }

    public void setPrecoMedio(String precoMedio) {
        this.precoMedio = precoMedio;
    }

    public String precoMedio;

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String urlImagem;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getDb_id() {
        return db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getVotacoes() {
        return votacoes;
    }

    public void setVotacoes(String votacoes) {
        this.votacoes = votacoes;
    }

    public String getMediarating() {
        return mediarating;
    }

    public void setMediarating(String mediarating) {
        this.mediarating = mediarating;
    }

    /*
    @Override
    public String toString()
    {
        return "Person [nome=" + nome + ", db_id=" + db_id + "]";
    }
    */
}


