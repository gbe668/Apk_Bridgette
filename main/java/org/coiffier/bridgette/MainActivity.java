package org.coiffier.bridgette;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    LinearLayout mNewConnection;
    EditText mUrlBridgette;
    EditText mEmailBridgette;
    EditText mPwdBridgette;
    Button mConnexion;
    Button mForgottenPwd;
    TextView mMessage;

    Button mClub1;
    Button mClub2;
    Button mSetNewClub;
    Button mRazConnexions;
    LinearLayout mSectionRazcnx;
    Button mRazClub1;
    Button mRazClub2;
    TextView mVersion;

    // données entrées
    String inputUrl0;   // url entrée
    String userMail0;
    String userPwd0;

    // données mémorisées
    String validCnx1;
    String validCnx2;

    // données de connexion
    String urlApi;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ajout multiclub
        mClub1 = findViewById(R.id.main_club1);
        mClub2 = findViewById(R.id.main_club2);
        mSetNewClub = findViewById(R.id.set_new_cnx);
        mRazConnexions = findViewById(R.id.raz_old_cnx);
        mSectionRazcnx = findViewById(R.id.section_razcnx);
        mRazClub1 = findViewById(R.id.del_club1);
        mRazClub2 = findViewById(R.id.del_club2);

        mNewConnection = findViewById(R.id.enter_newcnx);
        mUrlBridgette = findViewById(R.id.main_urlbridgette);
        mEmailBridgette = findViewById(R.id.main_emailbridgette);
        mPwdBridgette = findViewById(R.id.main_pwdbridgette);
        mConnexion = findViewById(R.id.main_connexion);
        mForgottenPwd = findViewById(R.id.main_forgotten_pwd);
        mMessage = findViewById(R.id.main_message);

        mVersion = findViewById(R.id.msg_version);

        // initialise l'affichage
        mNewConnection.setVisibility(View.GONE);
        mSectionRazcnx.setVisibility(View.GONE);
        mForgottenPwd.setVisibility(View.INVISIBLE);

        // version application
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        mVersion.setText("(C) Bruno, version "+versionName+", code "+versionCode);

        mUrlBridgette.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mMessage.setText("");
                inputUrl0 = s.toString().trim();
            }
        });
        mEmailBridgette.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mMessage.setText("");
                userMail0 = s.toString().trim();
            }
        });
        mPwdBridgette.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mMessage.setText("");
                userPwd0 = s.toString();
            }
            //fermeture du clavier
        });

        mClub1.setOnClickListener(v -> { loadConnectionData1(); });
        mClub2.setOnClickListener(v -> { loadConnectionData2(); });
        mSetNewClub.setOnClickListener(v -> {
            int visi = mNewConnection.getVisibility();
            if (visi == View.GONE ) mNewConnection.setVisibility(View.VISIBLE);
            else mNewConnection.setVisibility(View.GONE);
        });
        mRazConnexions.setOnClickListener(v -> {
            int visi = mSectionRazcnx.getVisibility();
            if (visi == View.GONE ) mSectionRazcnx.setVisibility(View.VISIBLE);
            else mSectionRazcnx.setVisibility(View.GONE);
        });
        mRazClub1.setOnClickListener(v -> { razConnectionData1(); });
        mRazClub2.setOnClickListener(v -> { razConnectionData2(); });
        mConnexion.setOnClickListener(v -> { loadConnectionData0(); });
        mForgottenPwd.setOnClickListener(v -> { changePassword(); });

        // mise à jour appli
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
            }
        });
    }

    // Fetch the stored data in onResume()
    // Because this is what will be called when the app opens again
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);

        // connexions mémorisées ?
        validCnx1 = myData.getString("validCnx1", "0");
        if ( validCnx1.equals("1") ) {
            mClub1.setText( myData.getString("userClub1", "") );
            mRazClub1.setText( "RAZ\n"+myData.getString("userClub1", ""));
            mRazClub1.setVisibility(View.VISIBLE);
        }
        else {
            mClub1.setVisibility(View.GONE);
            mRazClub1.setVisibility(View.GONE);
        }

        validCnx2 = myData.getString("validCnx2", "0");
        if ( validCnx2.equals("1") ) {
            mClub2.setText( myData.getString("userClub2", "") );
            mRazClub2.setText( "RAZ\n"+myData.getString("userClub2", ""));
            mRazClub2.setVisibility(View.VISIBLE);
        }
        else {
            mClub2.setVisibility(View.GONE);
            mRazClub2.setVisibility(View.GONE);
        }

        // Initialise les données au démarrage de l'application
        inputUrl0 = myData.getString("inputUrl", "");
        userMail0 = myData.getString("userMail", "");
        userPwd0  = myData.getString("userPwd", "");
        // valeurs de test: "demobridgette.coiffier.org", "gbe@coiffier.org", "azerty";

        mUrlBridgette.setText(inputUrl0);
        mEmailBridgette.setText(userMail0);
        mPwdBridgette.setText(userPwd0);
    }

    // Store the data in the SharedPreference in the onPause() method
    // When the user closes the application onPause() will be called and data will be stored
    @Override
    protected void onPause() {
        super.onPause();
        //mMessage.setText("onPause");
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = myData.edit();

        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString("inputUrl", inputUrl0);
        myEdit.putString("userMail", userMail0);
        myEdit.putString("userPwd", userPwd0);
        myEdit.apply();
    }

    // function for making a HTTP request using Volley
    private void loadConnectionData0() {
        loadConnectionData( "0", inputUrl0, userMail0, userPwd0 );
    }
    private void loadConnectionData1() {
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);
        validCnx1 = myData.getString("validCnx1", "0");
        if ( validCnx1.equals("1") ) {
            loadConnectionData("1",
                    myData.getString("inputUrl1", ""),
                    myData.getString("userMail1", ""),
                    myData.getString("userPwd1", "") );
        }
        else {
            Toast.makeText(MainActivity.this, "Serveur non défini", Toast.LENGTH_LONG).show();
        }
    }
    private void loadConnectionData2() {
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);
        validCnx2 = myData.getString("validCnx2", "0");
        if ( validCnx2.equals("1") ) {
            loadConnectionData("2",
                    myData.getString("inputUrl2", ""),
                    myData.getString("userMail2", ""),
                    myData.getString("userPwd2", "") );
        }
        else {
            Toast.makeText(MainActivity.this, "Serveur non défini", Toast.LENGTH_LONG).show();
        }
    }
    private void razConnectionData1() {
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = myData.edit();
        myEdit.putString("inputUrl1", "");
        myEdit.putString("userMail1", "");
        myEdit.putString("userPwd1" , "");
        myEdit.putString("userClub1", "");
        myEdit.putString("validCnx1", "0");
        myEdit.apply();
        mClub1.setText( "?" );
        mClub1.setVisibility(View.GONE);
        mRazClub1.setVisibility(View.GONE);
        mSectionRazcnx.setVisibility(View.GONE);
        validCnx1 = "0";
    }
    private void razConnectionData2() {
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = myData.edit();
        myEdit.putString("inputUrl2", "");
        myEdit.putString("userMail2", "");
        myEdit.putString("userPwd2" , "");
        myEdit.putString("userClub2", "");
        myEdit.putString("validCnx2", "0");
        myEdit.apply();
        mClub2.setText( "?" );
        mClub2.setVisibility(View.GONE);
        mRazClub2.setVisibility(View.GONE);
        mSectionRazcnx.setVisibility(View.GONE);
        validCnx1 = "0";
    }
    private void updateConnexionData( String origin, String nomduclub ) {
        SharedPreferences myData = getSharedPreferences("Bridgette",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = myData.edit();
        switch (origin) {
            case "0":
                // test mise à jour si adresses serveurs identiques
                if ( validCnx1.equals("1")
                        && inputUrl0.equals( myData.getString("inputUrl1", "-1") ) ) {
                    myEdit.putString("userMail1", userMail0);
                    myEdit.putString("userPwd1" , userPwd0);
                    myEdit.putString("userClub1", nomduclub);
                    myEdit.apply();
                    mClub1.setText( nomduclub );
                    break;
                }
                else if ( validCnx2.equals("1")
                        && inputUrl0.equals( myData.getString("inputUrl2", "-1") ) ) {
                    myEdit.putString("inputUrl2", inputUrl0);
                    myEdit.putString("userMail2", userMail0);
                    myEdit.putString("userPwd2" , userPwd0);
                    myEdit.putString("userClub2", nomduclub);
                    myEdit.apply();
                    mClub2.setText( nomduclub );
                    break;
                }
                // test emplacement libre pour mémorisation
                if ( validCnx1.equals("0") ) {
                    myEdit.putString("inputUrl1", inputUrl0);
                    myEdit.putString("userMail1", userMail0);
                    myEdit.putString("userPwd1" , userPwd0);
                    myEdit.putString("userClub1", nomduclub);
                    myEdit.putString("validCnx1", "1");
                    myEdit.apply();
                    mClub1.setText( nomduclub );
                    mClub1.setVisibility(View.VISIBLE);
                    mRazClub1.setVisibility(View.VISIBLE);
                    validCnx1 = "1";
                }
                else if ( validCnx2.equals("0") ) {
                    myEdit.putString("inputUrl2", inputUrl0);
                    myEdit.putString("userMail2", userMail0);
                    myEdit.putString("userPwd2" , userPwd0);
                    myEdit.putString("userClub2", nomduclub);
                    myEdit.putString("validCnx2", "1");
                    myEdit.apply();
                    mClub2.setText( nomduclub );
                    mClub2.setVisibility(View.VISIBLE);
                    mRazClub2.setVisibility(View.VISIBLE);
                    validCnx2 = "1";
                }
                break;
            case "1":
                // refresh nom du club, il a pu changer
                myEdit.putString("userClub1", nomduclub);
                myEdit.apply();
                break;
            case "2":
                // refresh nom du club, il a pu changer
                myEdit.putString("userClub2", nomduclub);
                myEdit.apply();
                break;
        }
        mNewConnection.setVisibility(View.GONE);
    }
    private void loadConnectionData( String origin, String url, String mail, String pwd ) {
        Log.e("MainActivity", "loadConnectionData: "+ origin+" url:"+url);
        // getting a new volley request queue for making new requests
        RequestQueue volleyQueue = Volley.newRequestQueue(MainActivity.this);
        // url of the api through which we get data
        String url1 = "https://" + url + "/apkconnect.php";
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                Request.Method.GET, url1, null,
                response1 -> {
                    try {
                        urlApi = response1.getString("server");
                        token  = response1.getString("token");
                        String url2 = urlApi + "apk_loguserin.php/" + "?token="+token +
                                "&mailuser=" + mail + "&password=" + pwd;
                        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(
                                Request.Method.GET, url2, null,
                                // lambda function for handling the case when the HTTP request succeeds
                                response2 -> {
                                    // get the image url from the JSON object
                                    try {
                                        String mLogin = response2.getString("login");
                                        mMessage.setText(response2.getString("message"));
                                        //mMessage.setText(mLogin);
                                        switch (mLogin) {
                                            case "0":   // identifiants corrects
                                                //mForgottenPwd.setVisibility(View.INVISIBLE);
                                                updateConnexionData( origin, response2.getString("club") );
                                                Intent mainplayer = new Intent(this, MainPlayer.class);
                                                mainplayer.putExtra("urlApi", urlApi);
                                                mainplayer.putExtra("token", token);
                                                mainplayer.putExtra("userId", response2.getString("userid"));
                                                mainplayer.putExtra("Club", response2.getString("club"));
                                                startActivity(mainplayer);
                                                break;
                                            case "1":   // "Adresse mail manquante."
                                            case "2":   // "Mot de passe manquant !"
                                            case "3":   // "Adresse mail inconnue !"
                                                mForgottenPwd.setVisibility(View.INVISIBLE);
                                                break;
                                            case "4":   // "Mot de passe incorrect !"
                                                mForgottenPwd.setVisibility(View.VISIBLE);
                                                break;
                                            default:
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                },

                                // lambda function for handling the case when the HTTP request fails
                                error2 -> {
                                    Toast.makeText(MainActivity.this, "apk_loguserin.php", Toast.LENGTH_LONG).show();
                                    // log the error message in the error stream
                                    Log.e("MainActivity", "apk_loguserin.php");
                                }
                        );
                        // add the json request object created above to the Volley request queue
                        volleyQueue.add(jsonObjectRequest2);
                        //verifyLogin();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error1 -> {
                    String txterror = url1 + "\nurl serveur bridgette incorrecte";
                    mMessage.setText(txterror);
                    // make a Toast telling the user that something went wrong
                    Toast.makeText(MainActivity.this, "Some error occurred! Cannot fetch data", Toast.LENGTH_LONG).show();
                    // log the error message in the error stream
                    Log.e("MainActivity", "loadConnectionData: error url serveur du club");
                }
        );
        // add the json request object created above to the Volley request queue
        volleyQueue.add(jsonObjectRequest1);
    }

    private void changePassword() {
        // getting a new volley request queue for making new requests
        RequestQueue volleyQueue = Volley.newRequestQueue(MainActivity.this);
        // url of the api through which we get data
        String url = urlApi + "apk_sendmailpwd.php/" + "?token="+token + "&mailuser="+userMail0;

        mMessage.setText("Envoi d'un mail de réinitialisation du mot de passe.\nNotez que l'envoi de ce mail peut prendre plusieurs secondes.");
        Log.e("MainActivity", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                // lambda function for handling the case when the HTTP request succeeds
                response -> {
                    // get the image url from the JSON object
                    try {
                        String mLogin = response.getString("login");
                        mMessage.setText(response.getString("message"));
                        //mMessage.setText(mLogin);
                        switch (mLogin) {
                            case "0":   // identifiants corrects
                                //myEdit.putString("userMail", mEmail);
                                //myEdit.commit();

                                break;
                            case "1":   // "Adresse mail manquante."
                            case "3":   // "Adresse mail inconnue !"
                            case "5":   // "Erreur: le mail n'est pas parti !!!"
                                //mForgottenPwd.setVisibility(View.VISIBLE);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                // lambda function for handling the case when the HTTP request fails
                error -> {
                    //mMessage.setText("Erreur apk_loguserin.php");
                    // make a Toast telling the user that something went wrong
                    Toast.makeText(MainActivity.this, "apk_sendmailpwd.php", Toast.LENGTH_LONG).show();
                    // log the error message in the error stream
                    Log.e("MainActivity", "apk_sendmailpwd.php");
                }
        );
        // add the json request object created above to the Volley request queue
        volleyQueue.add(jsonObjectRequest);
    }
}