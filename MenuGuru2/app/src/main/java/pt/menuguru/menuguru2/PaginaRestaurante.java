package pt.menuguru.menuguru2;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Json_parser.JSONParser;
import Utils.ImageLoader;
import Utils.Menu_Especial;
import Utils.ObjectInfoRest;
import Utils.Restaurante;

// para a parte dos webservices



public class PaginaRestaurante extends Activity
{

    public Restaurante restaurante;
    public ObjectInfoRest[] array_info =  null;
    public String[]         array_fotos = null;
    public Menu_Especial[]  array_especiais = null;

    // para o loading
    private ProgressDialog progressDialog;

    // para poder carregar as imagens da net
    public ImageLoader imageLoader;
    public String latitude;
    public String longitude;


    // para a tabela do info
    private ListView listViewInfo;
    private static AdapterInfo adapterInfo;

    // para a tabela especiais
    private ListView listViewEspeciais;
    private static AdapterEspeciais adapterEspeciais;

    public Boolean toogleInfo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_restaurante);

        // para ter o botao de back
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        Restaurante rest = (Restaurante) i.getSerializableExtra("restaurante");

        TextView textView = (TextView)findViewById(R.id.rest_nome);
        textView.setText(rest.nome);

        TextView textView1 = (TextView)findViewById(R.id.rest_votos);
        textView1.setText("("+rest.votacoes+")");

        TextView textView2 = (TextView)findViewById(R.id.rest_cidade);
        textView2.setText(rest.cidade);

        TextView textView3 = (TextView)findViewById(R.id.rest_cozinhas);
        textView3.setText(rest.cosinhas);

        TextView textView4 = (TextView)findViewById(R.id.rest_cidade);
        textView4.setText(rest.cidade);

        TextView textView5 = (TextView)findViewById(R.id.rest_morada);
        textView5.setText(rest.morada);

        RatingBar rating = (RatingBar)findViewById(R.id.rest_estrelas);
        rating.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        rating.setFocusable(false);
        rating.setRating(Float.parseFloat(rest.mediarating));

        imageLoader = new ImageLoader(this);

        ImageView icon=(ImageView)findViewById(R.id.rest_imagem);
        imageLoader.DisplayImage("http://menuguru.pt/"+rest.getUrlImagem(), icon);

        latitude = rest.latitude;
        longitude = rest.longitude;

        // já devia ter feito isto antes
        restaurante = rest;

        new WebserviceInfo(this).execute();
        new webserviceFotos(this).execute();
        new webserviceMenusEspeciais(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pagina_restaurante, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class WebserviceInfo extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_info_restaurante.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private PaginaRestaurante delegate;

        public WebserviceInfo (PaginaRestaurante delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PaginaRestaurante.this);
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

                dict.put("lang","pt");
                dict.put("id_rest",restaurante.db_id);


                String jsonString = jParser.getJSONFromUrl(yourJsonStringUrl,dict);

                // try parse the string to a JSON object
                try {
                    Log.v("Ver Json ", "Ele retorna isto" + jsonString);
                    jsonObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
                // get the array of users

                dataJsonArr = jsonObj.getJSONArray("res");
                // loop through all users


                array_info = new ObjectInfoRest[dataJsonArr.length()];
                for (int i = 0; i < dataJsonArr.length(); i++) {

                    JSONObject c = dataJsonArr.getJSONObject(i);

                    ObjectInfoRest info = new ObjectInfoRest();
                    info.titulo     = c.getString("titulo");
                    info.conteudo   = c.getString("conteudo");
                    info.subconteudo= c.getString("subconteudo");

                    array_info[i] = info;

                }

                //  Log.v("sdffgddvsdsv","objecto especial = "+ jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){  progressDialog.dismiss();delegate.asyncCompleteInfo(true);  }

    }


    public void asyncCompleteInfo(Boolean complete)
    {
        // tenho de preencher os dados da tabela info aqui :p

        // para defenir o layout das celulas
        listViewInfo = (ListView) findViewById(R.id.lista_info);


        // para defenir quando a lista esta vazia
        // listViewInfo.setEmptyView(findViewById(R.id.empty_list_main));

        adapterInfo = new AdapterInfo(this, R.layout.row_info, array_info);
        // Assign adapter to ListView
        listViewInfo.setAdapter(adapterInfo);



        // para  aparte do botao de ver mais
        final Button button = (Button) findViewById(R.id.button_info);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //ViewGroup.LayoutParams lp = listViewInfo.getLayoutParams();
                if (!toogleInfo)
                {
                    //setListViewHeightBasedOnChildren((ListView)listViewInfo); // este metudo vai ser preciso mas para as outras tabelas
                    ResizeAnimation resizeAnimation =
                            new ResizeAnimation(listViewInfo, getListViewHeightBasedOnChildren(listViewInfo));
                    resizeAnimation.setDuration(600);
                    listViewInfo.startAnimation(resizeAnimation);



                    button.setText("Fechar");
                }else
                {
                    //lp.height = 200;
                    ResizeAnimation resizeAnimation = new ResizeAnimation(listViewInfo, 200);
                    resizeAnimation.setDuration(600);
                    listViewInfo.startAnimation(resizeAnimation);
                    button.setText("Ver mais");
                }
                // listViewInfo.setLayoutParams(lp);
                toogleInfo = !toogleInfo;
            }
        });

    }

    public class ResizeAnimation extends Animation {
        final int startHeight;
        final int targetHeight;
        View view;

        public ResizeAnimation(View view, int targetHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
            startHeight = view.getHeight();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }


    // como a lista esta dentro de uma scroll view ela tem um bug de nao esticar correctamente
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();


        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if(listItem != null){
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    // como a lista esta dentro de uma scroll view ela tem um bug de nao esticar correctamente
    public static int getListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();


        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if(listItem != null){
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.setLayoutParams(params);
        //listView.requestLayout();

        return params.height;
    }






    public class AdapterInfo extends ArrayAdapter<ObjectInfoRest> {

        Context myContext;
        public ImageLoader imageLoader;

        public AdapterInfo(Context context, int textViewResourceId,
                             ObjectInfoRest[] objects) {
            super(context, textViewResourceId, objects);
            imageLoader=new ImageLoader(context);
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater =(LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View row = null;
            View row = inflater.inflate(R.layout.row_info, parent, false);

            TextView label1=(TextView)row.findViewById(R.id.info_titulo);
            label1.setText(array_info[position].titulo);

            TextView label2=(TextView)row.findViewById(R.id.info_conteudo);
            label2.setText(array_info[position].conteudo);

            /* não tem nada disto no webservice por isso tbm n precisa de ter aqui
            TextView label3=(TextView)row.findViewById(R.id.info_subconteudo);
            label3.setText(array_info[position].subconteudo);
            */

            return row;
        }

    }



    // para ir buscar as imagens ao restaurante
    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class webserviceFotos extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_galeria_fotos_restaurante.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private PaginaRestaurante delegate;

        public webserviceFotos (PaginaRestaurante delegate){
            this.delegate = delegate;
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


                dict.put("id_rest",restaurante.db_id);


                String jsonString = jParser.getJSONFromUrl(yourJsonStringUrl,dict);

                // try parse the string to a JSON object
                try {
                    Log.v("Ver Json ", "Ele retorna isto" + jsonString);
                    jsonObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
                // get the array of users

                dataJsonArr = jsonObj.getJSONArray("res");
                // loop through all users


                array_fotos = new String[dataJsonArr.length()];
                for (int i = 0; i < dataJsonArr.length(); i++) {

                    JSONObject c = dataJsonArr.getJSONObject(i);



                    array_fotos[i] = c.getString("foto_url");

                }

                //  Log.v("sdffgddvsdsv","objecto especial = "+ jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){  /*progressDialog.dismiss();*/delegate.asyncCompleteFotos(true);  }

    }

    public void asyncCompleteFotos(boolean success)
    {
        ImageView icon1=(ImageView)findViewById(R.id.gal_foto1);
        ImageView icon2=(ImageView)findViewById(R.id.gal_foto2);
        ImageView icon3=(ImageView)findViewById(R.id.gal_foto3);
        ImageView icon4=(ImageView)findViewById(R.id.gal_foto4);
        ImageView icon5=(ImageView)findViewById(R.id.gal_foto5);

        if (array_fotos.length > 0)
            imageLoader.DisplayImage("http://menuguru.pt/"+array_fotos[0], icon1);
        if (array_fotos.length > 1)
            imageLoader.DisplayImage("http://menuguru.pt/"+array_fotos[1], icon2);
        if (array_fotos.length > 2)
            imageLoader.DisplayImage("http://menuguru.pt/"+array_fotos[2], icon3);
        if (array_fotos.length > 3)
            imageLoader.DisplayImage("http://menuguru.pt/"+array_fotos[3], icon4);
        if (array_fotos.length > 4)
            imageLoader.DisplayImage("http://menuguru.pt/"+array_fotos[4], icon5);
    }




    // os menus especiais pertencentes a este restaurante
    // parte onde se realiza o pedido e a receção dos webservices
    // you can make this class as another java file so it will be separated from your main activity.
    public class webserviceMenusEspeciais extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";


        String yourJsonStringUrl = "http://menuguru.pt/menuguru2/json_menus_especiais_restaurante.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        private PaginaRestaurante delegate;

        public webserviceMenusEspeciais (PaginaRestaurante delegate){
            this.delegate = delegate;
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


                dict.put("id_rest",restaurante.db_id);


                String jsonString = jParser.getJSONFromUrl(yourJsonStringUrl,dict);

                // try parse the string to a JSON object
                try {
                    Log.v("Ver Json ", "Ele retorna isto" + jsonString);
                    jsonObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing data " + e.toString());
                }
                // get the array of users

                dataJsonArr = jsonObj.getJSONArray("res");
                // loop through all users


                array_especiais = new Menu_Especial[dataJsonArr.length()];
                for (int i = 0; i < dataJsonArr.length(); i++)
                {

                    JSONObject c = dataJsonArr.getJSONObject(i);

                    Menu_Especial especial = new Menu_Especial();
                    especial.setId(c.getString("id"));
                    especial.setNome(c.getString("nome"));
                    especial.setImagem(c.getString("imagem"));
                    especial.setPreco_actual(c.getString("preco_atual"));
                    especial.setPreco_ant(c.getString("preco_antigo"));
                    especial.setDescricao(c.getString("descricao"));
                    especial.setDestaque(c.getString("destaque"));
                    especial.setDataActual(c.getString("data_inicio"));
                    especial.setDatafinal(c.getString("data_fim"));
                    especial.setDestaque(c.getString("destaque"));
                    especial.setDesconto(c.getString("desconto"));
                    especial.setAcerca(c.getString("acerca"));
                    especial.setCondicoes(c.getString("condicoes"));
                    especial.setDescricao2(c.getString("descricao2"));
                    especial.setDescricao(c.getString("descricao"));
                    especial.setTipo(c.getString("tipo"));

                    array_especiais[i] = especial;
                }

                //  Log.v("sdffgddvsdsv","objecto especial = "+ jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg){ delegate.asyncCompleteEspeciais(true);  }

    }

    public void asyncCompleteEspeciais(boolean success)
    {

        // tenho de instanciar aqui o adapter e a lista para visualizar os menus especiais

        // tenho de preencher os dados da tabela info aqui :p

        // para defenir o layout das celulas
        listViewEspeciais = (ListView) findViewById(R.id.lista_especiais);


        // para defenir quando a lista esta vazia
        // listViewInfo.setEmptyView(findViewById(R.id.empty_list_main));

        adapterEspeciais = new AdapterEspeciais(this, R.layout.row_menu_especial, array_especiais);
        // Assign adapter to ListView
        listViewEspeciais.setAdapter(adapterEspeciais);

        setListViewHeightBasedOnChildren(listViewEspeciais);
    }


    public class AdapterEspeciais extends ArrayAdapter<Menu_Especial> {

        Context myContext;
        public ImageLoader imageLoader;

        public AdapterEspeciais(Context context, int textViewResourceId, Menu_Especial[] objects)
        {
            super(context, textViewResourceId, objects);
            imageLoader = new ImageLoader(context);
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater =(LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View row = null;
            View row = inflater.inflate(R.layout.row_menu_especial, parent, false);

            TextView label1 = (TextView)row.findViewById(R.id.especial_titulo);
            label1.setText(array_especiais[position].getNome());

            TextView label2 = (TextView)row.findViewById(R.id.especial_preco_antigo);
            label2.setPaintFlags(label2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            label2.setText(array_especiais[position].getPreco_ant());

            TextView label3 = (TextView)row.findViewById(R.id.especial_preco_novo);
            label3.setText(array_especiais[position].getPreco_actual());

            TextView label4=(TextView)row.findViewById(R.id.especial_desconto);
            label4.setText(array_especiais[position].getDescricao());

            ImageView icon=(ImageView)row.findViewById(R.id.especial_imagem);
            imageLoader.DisplayImage("http://menuguru.pt/"+(array_especiais[position].getImagem()), icon);

            // tenho de adicionar um botao que ao ser clicado tem de abrir o cenas de fazer reservas


            return row;
        }

    }


}