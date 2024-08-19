package org.coiffier.bridgette;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayTournoi extends AppCompatActivity {

    TextView mMessage;
	WebView webview;
    ImageView mIconReturn;
    WebAppInterface myWebAppInterface;

    // contexte partagé
    Intent i;
    String token;
    String urlApi;
    String idtournoi = "0";
    String paire = "0";
    String ligne = "??";
    String Url;
    String w;   // largeur écran
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tournoi);
        mMessage = findViewById(R.id.message);
        webview = findViewById(R.id.mitch61);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        w = String.valueOf(width/4);

        // récupération des paramètres passés en argument
        i = getIntent();
        urlApi = i.getStringExtra("urlApi");
        token  = i.getStringExtra("token");
        idtournoi  = i.getStringExtra("idtournoi");
        paire  = i.getStringExtra("paire");
        ligne  = i.getStringExtra("ligne");

        webview.getSettings().setJavaScriptEnabled(true);
        myWebAppInterface = new WebAppInterface(this);
        webview.addJavascriptInterface(myWebAppInterface, "Android");

        Log.e("PlayTournoi", "onCreate");
        myWebAppInterface.processFirst(i.getStringExtra("first"));

        mIconReturn = findViewById(R.id.home);
        mIconReturn.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        //webview.clearCache(false);
        Log.e("PlayTournoi", "onDestroy");
        super.onDestroy();
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface   // must be added for API 17 or higher
        public void processNext(String jsonParms){
            try {
                JSONObject parms = new JSONObject(jsonParms);
                String next = parms.getString("next");
                switch (next) {
                    case "bridge60":    // retour page d'accueil
                        finish();
                        Url = "";
                        break;

                    case "bridge20":
                        Url = urlApi + "apk_bridge20.php/" + "?idtournoi=0" + "&w="+w;
                        break;
                    case "bridge21":
                        Url = urlApi + "apk_bridge21.php/" +
                                "?idtournoi="+parms.getString("idtournoi") + "&w="+w;
                        break;
                    case "bridge25":
                        userId = i.getStringExtra("userid");
                        Url = urlApi + "apk_bridge25.php/" + "?userid="+userId + "&w="+w;
                        break;
                    case "bridge59":    // calcul de la marque
                        Url = urlApi + "apk_bridge59.php/" + "?w="+w;
                        break;

                    case "howell62":
                        Url = urlApi + "apk_howell62.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w;
                        break;
                    case "howell62back":
                        Url = urlApi + "apk_howell62.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w +
                                "&back="+parms.getString("back");
                        break;
                    case "howell63":
                        Url = urlApi + "apk_howell63.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w;
                        break;
                    case "howell64":
                        Url = urlApi + "apk_howell64.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w +
                                "&etui="+parms.getString("etui");
                        break;
                    case "howell65":
                        Url = urlApi + "apk_howell65.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w +
                                "&donne="+parms.getString("donne");
                        break;

                    case "mitch62":
                        Url = urlApi + "apk_mitch62.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w;
                        break;
                    case "mitch62back":
                        Url = urlApi + "apk_mitch62.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w +
                                "&back="+parms.getString("back");
                        break;
                    case "mitch63":
                        Url = urlApi + "apk_mitch63.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w;
                        break;
                    case "mitch64":
                        Url = urlApi + "apk_mitch64.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w+
                                "&etui="+parms.getString("etui");
                        break;
                    case "mitch64diags":
                        Url = urlApi + "apk_mitch64diags.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w +
                                "&donne="+parms.getString("donne");
                        break;
                    case "mitch65":		// { next:"mitch65", idtournoi:idtournoi, table:numNS, donne:donne, w:window.innerWidth };
                        Url = urlApi + "apk_mitch65.php/" + "?idtournoi="+idtournoi +
                                "&paire="+paire + "&ligne="+ligne + "&w="+w +
                                "&donne="+parms.getString("donne");
                        break;
                    case "bridge66":
                        Url = urlApi + "apk_bridge66.php/" + "?idtournoi="+idtournoi + "&w="+w;
                        break;
                    case "bridge67":
                        Url = urlApi + "apk_bridge67.php/" + "?idtournoi="+idtournoi + "&w="+w;
                        break;
                    default:
                        Toast.makeText(mContext, "A implémenter: "+next, Toast.LENGTH_SHORT).show();
                        Url = urlApi + "apk_signalbug.php/?bug=PlayTournoi_next_" + next;
                }
                if (!Url.isEmpty()) {
                    Log.e("PlayTournoi", "next Url: " + Url);
                    webview.post(() -> {
                        Url = Url + "&token=" + token;
                        webview.loadUrl(Url);
                    });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                finish();
            }
        }
        public void processFirst(String first) {
            switch (first) {
                case "mitch62":
                    idtournoi = i.getStringExtra("idtournoi");
                    paire = i.getStringExtra("paire");
                    ligne = i.getStringExtra("ligne");
                    Url = urlApi + "apk_mitch62.php/" + "?idtournoi="+idtournoi +
                            "&paire="+paire + "&ligne="+ligne + "&w="+w;
                    break;
                case "howell62":
                    idtournoi = i.getStringExtra("idtournoi");
                    paire = i.getStringExtra("paire");
                    ligne = i.getStringExtra("ligne");
                    Url = urlApi + "apk_howell62.php/" + "?idtournoi="+idtournoi +
                            "&paire="+paire + "&ligne="+ligne + "&w="+w;
                    break;
                case "bridge66":
                    idtournoi = i.getStringExtra("idtournoi");
                    Url = urlApi + "apk_bridge66.php/" + "?idtournoi="+idtournoi + "&w="+w;
                    break;
                case "bridge20":
                    Url = urlApi + "apk_bridge20.php/" + "?w="+w;
                    break;
                case "bridge25":
                    userId = i.getStringExtra("userid");
                    Url = urlApi + "apk_bridge25.php/" + "?userid="+userId + "&w="+w;
                    break;
                case "bridge59":    // calcul de la marque
                    Url = urlApi + "apk_bridge59.php/" + "?w="+w;
                    break;
                default:
                    Toast.makeText(mContext, "Inconnu: "+first, Toast.LENGTH_SHORT).show();
                    Url = urlApi + "apk_signalbug.php/?bug=PlayTournoi_first_" + first;
            }
            Log.e("PlayTournoi", "first Url: "+Url);
            webview.post(() -> {
                Url = Url + "&token="+token;
                webview.loadUrl(Url);
            });
        }
    }
}