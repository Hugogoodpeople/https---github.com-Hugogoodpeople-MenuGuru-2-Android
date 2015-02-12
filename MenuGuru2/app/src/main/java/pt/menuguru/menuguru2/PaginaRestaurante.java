package pt.menuguru.menuguru2;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.Serializable;

import Utils.ImageLoader;
import Utils.Restaurante;


public class PaginaRestaurante extends Activity {

    public ImageLoader imageLoader;

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
        textView1.setText(rest.votacoes);

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
}
