package pt.menuguru.menuguru2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Json_parser.JSONParser;
import Utils.Globals;
import Utils.User;


public class LoginNormal extends Activity {

    // para o loading
    private ProgressDialog progressDialog;

    // para os dados do utiliazador
    public User utilizador;

    public String erroLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_normal);

        Button recuperarSenha = (Button) findViewById(R.id.button_recuperar_senha);
        recuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // faz coisas aqui :)

            }
        });


        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            finish();
            return true;
        }

        if (id == R.id.action_login_normal)
        {

            Log.v("cenas","fazer login");
            new WebserviceLoginNormal(this).execute();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    // para o webservice de fazer login normal
    // http://menuguru.pt/menuguru2/json_recuperar_password.php

    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class WebserviceLoginNormal extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_login_normal.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private LoginNormal delegate;

        public WebserviceLoginNormal (LoginNormal delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginNormal.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JSONParser jParser = new JSONParser();

                // get json string from url
                // tenho de criar um jsonobject e adicionar la as cenas
                JSONObject dict = new JSONObject();
                JSONObject jsonObj = new JSONObject();


                EditText textUser = (EditText)delegate.findViewById(R.id.text_user);
                EditText textSenha = (EditText)delegate.findViewById(R.id.text_senha);


                dict.put("email",textUser.getText());
                dict.put("pass",textSenha.getText());


                String jsonString = jParser.getJSONFromUrl(yourJsonStringUrl,dict);

                // try parse the string to a JSON object
                try {
                    Log.v("Ver Json ", "Ele retorna do facebook login" + jsonString);
                    jsonObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
                // get the array of users

                dataJsonArr = jsonObj.getJSONObject("res").getJSONArray("favoritos");

                if (!jsonObj.getJSONObject("res").getString("mensagem").equalsIgnoreCase("TM"))
                {
                     erroLogin = (jsonObj.getJSONObject("res").getString("mensagem"));

                }
                {
                    utilizador = new User();

                    // tenho de ir buscar tudo aqui
                    utilizador.setUserid(jsonObj.getJSONObject("res").getString("id_user"));
                    utilizador.setPnome(jsonObj.getJSONObject("res").getString("nome"));
                    utilizador.setSnome(jsonObj.getJSONObject("res").getString("sobrenome"));
                    utilizador.setPass(jsonObj.getJSONObject("res").getString("senha"));
                    utilizador.setPontos(jsonObj.getJSONObject("res").getString("pontos"));
                    utilizador.setTelefone_user(jsonObj.getJSONObject("res").getString("telefone"));
                    utilizador.setPedido(jsonObj.getJSONObject("res").getString("pedido"));
                    utilizador.setCidade(jsonObj.getJSONObject("res").getString("cidade"));


                    List<String> restaurantesFavs = new ArrayList<>();

                    for (int i = 0; i < dataJsonArr.length(); i++) {

                        JSONObject c = dataJsonArr.getJSONObject(i);

                        String info = c.getString("id_rest");

                        restaurantesFavs.add(info);

                    }


                    utilizador.setRestaurantesFav(restaurantesFavs);

                    Globals.getInstance().setUser(utilizador);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){  progressDialog.dismiss();delegate.asyncCompleteLogin(true);  }

    }

    public void asyncCompleteLogin(Boolean success)
    {

        if (erroLogin != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage(erroLogin)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
        {

            finish();

        }

    }


}
