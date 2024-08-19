package org.coiffier.bridgette;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainPlayer extends AppCompatActivity {

    //SharedPreferences sharedData;
    TextView mMessage;
    ImageView mIconReturn;
    WebView mViewPlayer;
    WebAppInterface myWebAppInterface;
    String w;   // largeur écran
    // variables passées en argument de l'activité
    String token;
    String userId;
    String urlApi;
    // version application
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    //String info = "Code "+versionCode + ", Nom "+versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // affichage
        setContentView(R.layout.activity_main_player);
        mViewPlayer = findViewById(R.id.viewPlayer);
        mIconReturn = findViewById(R.id.home);
        mMessage = findViewById(R.id.messagePlayer);

        // récupération des paramètres passés en argument
        Intent i = getIntent();
        userId = i.getStringExtra("userId");
        urlApi = i.getStringExtra("urlApi");
        token  = i.getStringExtra("token");
        setTitle(i.getStringExtra("Club"));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        w = String.valueOf(width /4);

        mViewPlayer.getSettings().setJavaScriptEnabled(true);
        myWebAppInterface = new MainPlayer.WebAppInterface(this);
        mViewPlayer.addJavascriptInterface(myWebAppInterface, "Android");

        mIconReturn.setOnClickListener(v -> {
            // The user just clicked
            mMessage.setText("");
            mIconReturn.setVisibility(View.INVISIBLE);
            mIconReturn.setEnabled(false);
            myWebAppInterface.processFirst("bridgette");
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainPlayer", "onStart");
        mIconReturn.setVisibility(View.INVISIBLE);
        mIconReturn.setEnabled(false);
        mMessage.setText("");
        myWebAppInterface.processFirst("bridgette");
    }

    // Fetch the stored data in onResume()
    // Because this is what will be called when the app opens again
    @Override
    protected void onResume() {
        super.onResume();
        //mMessage.setText("onResume");
        Log.e("MainPlayer", "onResume");
    }

    // Store the data in the SharedPreference in the onPause() method
    // When the user closes the application onPause() will be called and data will be stored
    @Override
    protected void onPause() {
        super.onPause();
        //mMessage.setText("onPause");
        Log.e("MainPlayer", "onPause");
        // write all the data entered by the user in SharedPreference and apply
		// todo
    }
    @Override
    protected void onStop() {
        super.onStop();
        //mViewPlayer.clearCache(true);
        Log.e("MainPlayer", "onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bridgette, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.apropos:
                mIconReturn.setVisibility(View.VISIBLE);
                mIconReturn.setEnabled(true);
                mViewPlayer.loadUrl(urlApi + "apk_apropos.php");
                return true;
            case R.id.types_tournois:
                mMessage.setText("");
                mIconReturn.setVisibility(View.VISIBLE);
                mIconReturn.setEnabled(true);
                mViewPlayer.loadUrl(urlApi + "apk_typestournois.php");
                return true;
            case R.id.doc:
                mMessage.setText("");
                mIconReturn.setVisibility(View.VISIBLE);
                mIconReturn.setEnabled(true);
                mViewPlayer.loadUrl(urlApi + "guide.htm");
                return true;
            case R.id.home:
                mMessage.setText("");
                mIconReturn.setVisibility(View.INVISIBLE);
                mIconReturn.setEnabled(false);
                myWebAppInterface.processFirst("bridgette");
                return true;
            case R.id.disconnect:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class WebAppInterface {
        Context mContext;
        String Url;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void processNext(String jsonParms){
           Intent PlayTournoi = new Intent(mContext, PlayTournoi.class);
            try {
                JSONObject parms = new JSONObject(jsonParms);
                String next = parms.getString("next");
                switch (next) {
                    case "bridge60":    // retour page d'accueil
                        Url = urlApi + "apk_bridgette.php/" + "?userid="+userId +
                                "&apk_code="+versionCode + "&apk_name="+versionName;
                        break;

                    case "bridge20":
                        PlayTournoi.putExtra("first", "bridge20");
                        PlayTournoi.putExtra("urlApi", urlApi);
                        PlayTournoi.putExtra("token", token);
                        startActivity(PlayTournoi);
                        Url = "";
                        break;
                    case "bridge25":
                        PlayTournoi.putExtra("first", "bridge25");
                        PlayTournoi.putExtra("urlApi", urlApi);
                        PlayTournoi.putExtra("token", token);
                        PlayTournoi.putExtra("userid", userId);
                        startActivity(PlayTournoi);
                        Url = "";
                        break;
                    case "bridge59":
                        PlayTournoi.putExtra("first", "bridge59");
                        PlayTournoi.putExtra("urlApi", urlApi);
                        PlayTournoi.putExtra("token", token);
                        startActivity(PlayTournoi);
                        Url = "";
                        break;

                    case "howell62":
                        PlayTournoi.putExtra("first", "howell62");
                        PlayTournoi.putExtra("urlApi", urlApi);
                        PlayTournoi.putExtra("token", token);
                        PlayTournoi.putExtra("idtournoi", parms.getString("idtournoi"));
                        PlayTournoi.putExtra("paire", parms.getString("paire"));
                        PlayTournoi.putExtra("ligne", parms.getString("ligne"));
                        startActivity(PlayTournoi);
                        Url = "";
                        break;

                    case "mitch62":		// { next:"mitch62", idtournoi:idtournoi, table:numtable };
                        PlayTournoi.putExtra("first", "mitch62");
                        PlayTournoi.putExtra("urlApi", urlApi);
                        PlayTournoi.putExtra("token", token);
                        PlayTournoi.putExtra("idtournoi", parms.getString("idtournoi"));
                        PlayTournoi.putExtra("paire", parms.getString("paire"));
                        PlayTournoi.putExtra("ligne", parms.getString("ligne"));
                        startActivity(PlayTournoi);
                        Url = "";
                        break;

                    case "bridge66":	// { next:"bridge66", idtournoi:idtournoi, w:window.innerWidth };
                        PlayTournoi.putExtra("first", "bridge66");
                        PlayTournoi.putExtra("urlApi", urlApi);
                        PlayTournoi.putExtra("token", token);
                        PlayTournoi.putExtra("idtournoi", parms.getString("idtournoi"));
                        startActivity(PlayTournoi);
                        Url = "";
                        break;

                    default:
                        Toast.makeText(mContext, "A implémenter: "+next, Toast.LENGTH_SHORT).show();
                        Url = urlApi +  "apk_signalbug.php/?bug=MainPlayer_next_" + next;
                }
                if (!Url.isEmpty()) {
                    Log.e("MainPlayer", "next Url: "+Url);
                    mViewPlayer.post(() -> {
                        Url = Url + "&token="+token;
                        mViewPlayer.loadUrl(Url);
                    });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public void processFirst(String first) {
            switch (first) {
                case "bridgette":
                    Url = urlApi + "apk_bridgette.php/" + "?userid="+userId +
                    "&apk_code="+versionCode + "&apk_name="+versionName;
                    break;
                default:
                    Toast.makeText(mContext, "Inconnu: "+first, Toast.LENGTH_SHORT).show();
                    Url = urlApi +  "apk_signalbug.php/?bug=MainPlayer_first" + first;
            }
            Log.e("MainPlayer", "first Url: "+Url);
            mViewPlayer.post(() -> {
                Url = Url + "&token="+token;
                mViewPlayer.loadUrl(Url);
            });
        }
    }
}