package Utils;

/**
 * Created by user on 01/10/14.
 */
public class Horario_Mesa {

    private String id;
    private String hora_inicio;
    private String n_pessoas_h;
    private String dia_id;
    private String n_pessoas_min;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getN_pessoas_min() {
        return n_pessoas_min;
    }

    public void setN_pessoas_min(String n_pessoas_min) {
        this.n_pessoas_min = n_pessoas_min;
    }

    public String getDia_id() {
        return dia_id;
    }

    public void setDia_id(String dia_id) {
        this.dia_id = dia_id;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getN_pessoas_h() {
        return n_pessoas_h;
    }

    public void setN_pessoas_h(String n_pessoas_h) {
        this.n_pessoas_h = n_pessoas_h;
    }
}
