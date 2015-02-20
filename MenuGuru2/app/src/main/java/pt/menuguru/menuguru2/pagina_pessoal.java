package pt.menuguru.menuguru2;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;

import Utils.Globals;
import Utils.User;


public class pagina_pessoal extends Activity {

    private ProfilePictureView profilePictureView;
    private TextView userNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_pessoal);

        // Find the user's profile picture custom view
        profilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);

        profilePictureView.setProfileId(Globals.getInstance().getUser().getId_face());

        User utilizador = Globals.getInstance().getUser();

        userNameView = (TextView) findViewById(R.id.user_name);
        userNameView.setText(utilizador.getPnome() + " " + utilizador.getSnome());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pagina_pessoal, menu);
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
        if (id == R.id.log_out) {
            callFacebookLogout();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callFacebookLogout() {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved

                Globals.getInstance().setUser(null);

                finish();
            }
        } else {

            session = new Session(this);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved

            Globals.getInstance().setUser(null);

            finish();

        }

    }
}
