package Utils;

/**
 * Created by user on 10/09/14.
 */
public class User {

    public String userid = "0";
    public String id_face = "0";
    public String email = "";
    public String pnome = "";
    public String snome = "";
    public String cidade = "";
    public String telefone_user = "";
    public String data_nasc = "";
    public String pass = "";
    public String tipoconta = "";
    public Boolean news = true;


    public String getId_face() { return id_face;  }

    public void setId_face(String id_face) { this.id_face = id_face; }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPnome() {
        return pnome;
    }

    public void setPnome(String pnome) {
        this.pnome = pnome;
    }

    public String getSnome() {
        return snome;
    }

    public void setSnome(String snome) {
        this.snome = snome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTelefone_user() {
        return telefone_user;
    }

    public void setTelefone_user(String telefone_user) {
        this.telefone_user = telefone_user;
    }

    public String getData_nasc() {
        return data_nasc;
    }

    public void setData_nasc(String data_nasc) {
        this.data_nasc = data_nasc;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTipoconta() {
        return tipoconta;
    }

    public void setTipoconta(String tipoconta) {
        this.tipoconta = tipoconta;
    }

    public Boolean getNews() {
        return news;
    }

    public void setNews(Boolean news) {
        this.news = news;
    }

}
