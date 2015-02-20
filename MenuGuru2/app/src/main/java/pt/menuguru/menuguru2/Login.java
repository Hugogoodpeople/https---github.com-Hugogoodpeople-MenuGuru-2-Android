package pt.menuguru.menuguru2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Json_parser.JSONParser;
import Utils.Globals;
import Utils.ObjectInfoRest;
import Utils.User;


public class Login extends Activity {

    // para o loading
    private ProgressDialog progressDialog;

    // para os dados do utiliazador
    public User utilizador;


    // cenas facebook necessários para salvar o login
    private static final String TAG = "MainFragment";
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Add code to print out the key hash
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "pt.menuguru.menuguru2",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        Button loginNormal = (Button) findViewById(R.id.login_normal);
        loginNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               abrirNormal();
            }
        });
    }

    public void abrirNormal()
    {
        Intent myIntent = new Intent(this, LoginNormal.class);
        startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

            // aqui já tenho o login do utilizador

            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response)
                {
                    if (user != null)
                    {
                        Log.v("1","user.getName : " + user.getName());
                        Log.d("2","user.getLastName : " + user.getLastName());
                        Log.d("2","user.getFirstName : " + user.getFirstName());
                        Log.d("3","user.getId : " + user.getId());
                        String email = user.getProperty("email").toString();
                        Log.d("4","user.email : " + email);

                        // tenho de chamar o webservice aqui para ir buscar as informações caso já existam na base de dados
                        // mas primeiro tenho de preencher os dados do utilizador
                        utilizador = new User();
                        utilizador.setEmail(email);
                        utilizador.setPnome(user.getFirstName());
                        utilizador.setSnome(user.getLastName());
                        utilizador.setId_face(user.getId());

                        chamarWSLogin();

                    }
                }
            }).executeAsync();


        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    public void chamarWSLogin()
    {
        new WebserviceLoginFacebook(this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        User utilizador = Globals.getInstance().getUser();
        if(utilizador != null)
        {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }


    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class WebserviceLoginFacebook extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_login_facebook.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private Login delegate;

        public WebserviceLoginFacebook (Login delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this);
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

                //dict.put("lang","pt");

                dict.put("face_id",utilizador.id_face);
                dict.put("mail",utilizador.email);
                dict.put("name",utilizador.pnome);
                dict.put("last_name",utilizador.snome);
                dict.put("versao","facebook Android");




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
                // loop through all users


                // tenho de ir buscar tudo aqui
                utilizador.setUserid(jsonObj.getJSONObject("res").getString("id_user"));
                utilizador.setPnome(jsonObj.getJSONObject("res").getString("nome"));
                utilizador.setSnome(jsonObj.getJSONObject("res").getString("sobrenome"));
                utilizador.setPass(jsonObj.getJSONObject("res").getString("senha"));
                utilizador.setPontos(jsonObj.getJSONObject("res").getString("pontos"));
                utilizador.setTelefone_user(jsonObj.getJSONObject("res").getString("telefone"));
                utilizador.setPedido(jsonObj.getJSONObject("res").getString("pedido"));
                utilizador.setCidade(jsonObj.getJSONObject("res").getString("cidade"));


                List<String> restaurantesFavs =  new ArrayList<>();

                for (int i = 0; i < dataJsonArr.length(); i++) {

                    JSONObject c = dataJsonArr.getJSONObject(i);

                    String info     = c.getString("id_rest");

                    restaurantesFavs.add(info);

                }

                utilizador.setRestaurantesFav(restaurantesFavs);

                Globals.getInstance().setUser(utilizador);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){  progressDialog.dismiss();delegate.asyncCompleteInfo(true);  }

    }

    public void asyncCompleteInfo(Boolean success)
    {
        // faz coisas aqui com o login acabado de realizar
        finish();
    }


}
