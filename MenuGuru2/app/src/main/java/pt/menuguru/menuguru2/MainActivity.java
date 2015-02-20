package pt.menuguru.menuguru2;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Json_parser.JSONParser;
import Utils.Globals;
import Utils.ImageLoader;
import Utils.Restaurante;
import Utils.User;


public class MainActivity extends Activity {

    private double latitude;
    private double longitude;

    // para os dados do utiliazador
    public User utilizador;

    Restaurante[] some_array = null;
    private ProgressDialog progressDialog;

    private AbsListView mListView;

    private static MyListAdapter mAdapter;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //executarWebservice();
        incicializarGPS();


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);

        if(Globals.getInstance().getUser() == null) {
            utilizador = new User();
        }else
        {
            utilizador = Globals.getInstance().getUser();
        }


        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    public void incicializarGPS()
    {

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                // Called when a new location is found by the network location provider.
                // aqui so devia de fazer um pedido mas faz vario n sei
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

                Log.v("onStatusChanged","mudou status");
            }

            public void onProviderEnabled(String provider) {

                Log.v("onProviderEnabled","onProviderEnabled");
            }

            public void onProviderDisabled(String provider) {
                Log.v("onProviderDisabled","onProviderEnabled " + provider );
                // tenho de mostrar uma toast a dizer para activar gps
                executarWebservice();
            }
        };


// Register the listener with the Location Manager to receive location updates
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
    }





    public void executarWebservice()
    {
        new AsyncTaskParseJson(this).execute();
    }

    public void makeUseOfNewLocation(Location location)
    {
        latitude    = location.getLatitude();
        longitude   = location.getLongitude();

        // para chamar o webservice com a informação pretendida
        executarWebservice();

        locationManager.removeUpdates(locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {

            if (Globals.getInstance().getUser() != null)
            {

                // é aqui que tenho de abrir os restaurantes
                Intent intent = new Intent(this, pagina_pessoal.class);

                startActivity(intent);

            }
            else {

                Intent myIntent = new Intent(this, Login.class);
                startActivity(myIntent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_pertomin_relevancia_noticiavideo.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private MainActivity delegate;

        public AsyncTaskParseJson (MainActivity delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

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

                dict.put("lang","pt");
                dict.put("lat",String.valueOf(latitude));
                dict.put("lon", String.valueOf(longitude));


                String jsonString = jParser.getJSONFromUrl(yourJsonStringUrl,dict);

                // try parse the string to a JSON object
                try
                {
                    Log.v("Ver Json ", "Ele retorna isto" + jsonString);
                    jsonObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
                // get the array of users

                dataJsonArr = jsonObj.getJSONArray("res");
                // loop through all users


                some_array = new Restaurante[dataJsonArr.length()];
                for (int i = 0; i < dataJsonArr.length(); i++) {

                    JSONObject c = dataJsonArr.getJSONObject(i);



                    // Storing each json item in variable
                    String firstname = c.getString("nome");

                    // show the values in our logcat
                    Log.v(TAG, "firstname: " + firstname
                    );


                    Restaurante rest = new Restaurante();
                    rest.setNome(firstname);

                    rest.morada = c.getString("morada");
                    rest.mediarating = c.getString("mediarating");
                    rest.cidade = c.getString("cidade");
                    rest.urlImagem = c.getString("imagem");
                    rest.votacoes = c.getString("votacoes");
                    //rest.morada = c.getString("morada");
                    //rest.precoMedio = c.getString("precomedio");
                    rest.db_id = c.getString("id");


                    rest.latitude = c.getString("lat");
                    rest.longitude = c.getString("lon");
                    rest.mediarating = c.getString("mediarating");
                    rest.precoMedio = c.getString("cifroes");


                    JSONArray cozinhas = c.getJSONArray("cozinhas");

                    for (int z = 0; z < cozinhas.length(); z++) {
                        JSONObject cozinha = cozinhas.getJSONObject(z);
                        if (cozinhas.length() - 1 > z) {
                            rest.cosinhas = rest.cosinhas + cozinha.getString("cozinhas_nome") + ", ";
                        } else {
                            rest.cosinhas = rest.cosinhas + "" + cozinha.getString("cozinhas_nome");
                        }
                    }


                    some_array[i] = rest;

                }

              //  Log.v("sdffgddvsdsv","objecto especial = "+ jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){  progressDialog.dismiss();delegate.asyncComplete(true);  }

    }

    public void asyncComplete(boolean success) {


        // para defenir o layout das celulas
        mListView = (ListView) findViewById(R.id.listaMainActivity);


        // para defenir quando a lista esta vazia
        mListView.setEmptyView(findViewById(R.id.empty_list_main));

        mAdapter = new MyListAdapter(this, R.layout.row_main_cell, some_array);
        // Assign adapter to ListView
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.v("clicado","Restaurante clicado = "+ some_array[i].nome);

                abrirRestaurante(some_array[i]);
            }
        });

    }

    public void abrirRestaurante(Restaurante restaurante)
    {

            // é aqui que tenho de abrir os restaurantes
            Intent intent = new Intent(this, PaginaRestaurante.class);
            intent.putExtra("restaurante", restaurante);

            startActivity(intent);

    }

    public class MyListAdapter extends ArrayAdapter<Restaurante> {

        Context myContext;
        public ImageLoader imageLoader;

        public MyListAdapter(Context context, int textViewResourceId,
                             Restaurante[] objects) {
            super(context, textViewResourceId, objects);
            imageLoader=new ImageLoader(context);
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater =(LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View row = null;
            View row = inflater.inflate(R.layout.row_main_cell, parent, false);

            /*
            if (some_array[position].tipo.equalsIgnoreCase("restaurante"))
            {
            */
                //row=inflater.inflate(R.layout.row_main_cell, parent, false);
            TextView label2=(TextView)row.findViewById(R.id.main_cell_cozinha);
            label2.setText(some_array[position].cosinhas);

            ImageView icon=(ImageView)row.findViewById(R.id.main_cell_img);
            imageLoader.DisplayImage("http://menuguru.pt/"+some_array[position].getUrlImagem(), icon);


            TextView label3=(TextView)row.findViewById(R.id.main_cell_morada);
            label3.setText(some_array[position].morada);


            TextView label=(TextView)row.findViewById(R.id.main_cell_nome_rest);
            label.setText(some_array[position].nome);

            TextView label6=(TextView)row.findViewById(R.id.main_cell_cidade);
            label6.setText(some_array[position].cidade);

            RatingBar rating = (RatingBar)row.findViewById(R.id.main_ratingBar);

            rating.setOnTouchListener(new View.OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            rating.setFocusable(false);

            rating.setRating( Float.parseFloat(some_array[position].mediarating));


            TextView cifroes = (TextView)row.findViewById(R.id.main_cell_cifroes);

            Spannable wordtoSpan = new SpannableString("€ € € € €");
            wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, Integer.parseInt(some_array[position].precoMedio), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            cifroes.setText(wordtoSpan);


            // novas esperiencias
            final Restaurante rest = some_array[position];


            final Button button = (Button)row.findViewById(R.id.add_remove_favs);


            if (Globals.getInstance().getUser() != null)
            if(rest_favorito(rest))
            {
                button.setBackground(getDrawable(R.drawable.icomark));

            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // tenho de verificar se o utilizador tem login
                    // se sim deixo clicar senão tenho de informar que orecisa fazer login
                    // para poder adicionar aos favoritos



                    if (Globals.getInstance().getUser() != null)
                    {
                        // aqui tenho de adicionar este restaurantes aos favoritos


                        if(!rest_favorito(rest))
                        {
                            button.setBackground(getDrawable(R.drawable.icomark));
                            add_remove_favs(rest, true);
                        }
                        else
                        {
                            button.setBackground(getDrawable(R.drawable.icounmark));
                            add_remove_favs(rest, false);
                        }
                    }

                }
            });


            return row;
        }

    }


    public boolean rest_favorito(Restaurante favorito)
    {
        Boolean coiso = false;

        for (String id_rest : Globals.getInstance().getUser().getRestaurantesFav())
        {
            if(favorito.db_id.equals(id_rest))
            {
                coiso = true;
            }

        }

        return coiso;
    }

    public void add_remove_favs(Restaurante rest , boolean add)
    {
        Log.v("favs","clicado para adicionar = " + rest.nome);

        if (!add)
        {
            Globals.getInstance().getUser().getRestaurantesFav().remove(rest.db_id);
            new WebserviceAddRemoveFav(this, rest, "0").execute();
        }
        else
        {
            Globals.getInstance().getUser().getRestaurantesFav().add(rest.db_id);
            new WebserviceAddRemoveFav(this, rest, "1").execute();
        }

    }


    // para o facebook tenho de tomar medidas drasticas

    // cenas facebook necessários para salvar o login
    private static final String TAG = "MainFragment";
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

            // aqui já tenho o login do utilizador

            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Log.v("1", "user.getName : " + user.getName());
                        Log.d("2", "user.getLastName : " + user.getLastName());
                        Log.d("2", "user.getFirstName : " + user.getFirstName());
                        Log.d("3", "user.getId : " + user.getId());
                        String email = user.getProperty("email").toString();
                        Log.d("4", "user.email : " + email);

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
        locationManager.removeUpdates(locationListener);
    }

    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class WebserviceLoginFacebook extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_login_facebook.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private MainActivity delegate;

        public WebserviceLoginFacebook (MainActivity delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

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


                List<String> restaurantesFavs = new ArrayList<>();

                for (int i = 0; i < dataJsonArr.length(); i++)
                {

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
        protected void onPostExecute(String strFromDoInBg){  progressDialog.dismiss();delegate.asyncCompleteLoginFace(true);  }

    }

    public void asyncCompleteLoginFace(Boolean success)
    {
        // faz coisas aqui com o login acabado de realizar

    }


    // webservice para adicionar e remover favoritos associado a um utilizador
    public class WebserviceAddRemoveFav extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_adicionar_remover_favorito.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private MainActivity delegate;
        private Restaurante restaurante;
        private String add;

        public WebserviceAddRemoveFav (MainActivity delegate, Restaurante restaurante, String add){
            this.delegate = delegate;
            this.restaurante = restaurante;
            this.add = add;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // é tao rapido que nem preciso disto para nada
            /*
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            */
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

                dict.put("id_user",utilizador.userid);
                dict.put("id_rest", this.restaurante.db_id);
                dict.put("aux", this.add);



                String jsonString = jParser.getJSONFromUrl(yourJsonStringUrl,dict);

                // try parse the string to a JSON object
                try {
                    Log.v("Ver Json ", "Ele retorna de adicionar favoritos " + jsonString);
                    jsonObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
                // get the array of users

               // depois das cenas estarem feitas nao estou a espera que nada de especial aconteça

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){  progressDialog.dismiss();  }

    }

}
