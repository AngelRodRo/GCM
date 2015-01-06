package com.example.usuario.gcm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

public class RegisterActivity extends Activity {

	Button btnGCMRegister;
	Button btnAppShare;
    Button btnmensaje;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		context = getApplicationContext();

		btnGCMRegister = (Button) findViewById(R.id.btnGCMRegister);
		btnGCMRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					regId = registerGCM();
					Log.d("RegisterActivity", "GCM RegId: " + regId);
				} else {
					Toast.makeText(getApplicationContext(),
							"Already Registered with GCM Server!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		btnAppShare = (Button) findViewById(R.id.btnAppShare);
		btnAppShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					Toast.makeText(getApplicationContext(), "RegId is empty!",
							Toast.LENGTH_LONG).show();
				} else {
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					i.putExtra("regId", regId);
					Log.d("RegisterActivity",
							"onClick of Share: Before starting main activity.");
					startActivity(i);
					finish();
					Log.d("RegisterActivity", "onClick of Share: After finish.");
				}
			}
		});

        /*btnmensaje = (Button) findViewById(R.id.btnMensaje);


        //Enviar un mensaje nuevo al servidor
        btnmensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> values = new ArrayList<String>();
                values.add("user1");


                String URL = "https://aqueous-escarpment-1930.herokuapp.com/send";

                //JSON PRINCIPAL (MENSAJE)
                JSONObject princjs = new JSONObject();


                //Partes del JSON
                JSONObject androidjs = new JSONObject();
                JSONObject datajs = new JSONObject();
                JSONObject iosjs = new JSONObject();

                try {
                    datajs.put("messaje", "Este es mi mensaje :D");

                    androidjs.put("collapseKey", "optional");
                    androidjs.put("data", datajs);

                    iosjs.put("badge", 0);
                    iosjs.put("alert", "Your message here");
                    iosjs.put("sound", "soundName");


                    princjs.put("users", values);
                    princjs.put("android", androidjs);
                    princjs.put("ios", iosjs);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error en el mensaje JSON", Toast.LENGTH_LONG).show();
                }

                //String result = "";
                //Map<String, String> paramsMap = new HashMap<String, String>();
                //paramsMap.put("regId", regId);

                java.net.URL serverUrl = null;
                try {
                    serverUrl = new URL(URL);
                } catch (MalformedURLException e) {
                    Log.e("AppUtil", "URL Connection Error: "
                            + URL, e);
                    //result = "Invalid URL: " + URL;
                }

                //StringBuilder postBody = new StringBuilder();
                //Iterator<Entry<String, String>> iterator = paramsMap.entrySet()
                //		.iterator();

                //while (iterator.hasNext()) {
                //	Entry<String, String> param = iterator.next();
                //	postBody.append(param.getKey()).append('=')
                //			.append(param.getValue());
                //	if (iterator.hasNext()) {
                //		postBody.append('&');
                //	}
                //}
                //String body = postBody.toString();
                byte[] bytes = princjs.toString().getBytes();
                //byte[] bytes =  json.toString().getBytes("UTF-8");

                //byte[] bytes = body.getBytes();
                HttpURLConnection httpCon = null;
                try {
                    httpCon = (HttpURLConnection) serverUrl.openConnection();
                    httpCon.setDoOutput(true);
                    httpCon.setUseCaches(false);
                    httpCon.setFixedLengthStreamingMode(bytes.length);
                    httpCon.setRequestMethod("POST");
                    httpCon.setRequestProperty("Content-Type", "application/json");
                    httpCon.connect();

                    OutputStream out = httpCon.getOutputStream();
                    out.write(bytes);
                    out.close();

                    int status = httpCon.getResponseCode();
                    if (status == httpCon.HTTP_OK) {
                        Toast.makeText(getApplicationContext(), "Se envio los datos correctamente", Toast.LENGTH_LONG).show();
                        //result = "RegId shared with Application Server. RegId: "
                        //       + regId;
                    } else {
                        Toast.makeText(getApplicationContext(), "Post Failure." + " Status: " + status, Toast.LENGTH_LONG).show();

                        //result = "Post Failure." + " Status: " + status;
                    }
                }catch (IOException ex)
                {
                    Toast.makeText(getApplicationContext(), " POST FAILURE ", Toast.LENGTH_LONG).show();

                } finally {
                    if (httpCon != null) {
                        httpCon.disconnect();
                    }
                }



                //return result;

            }

        });*/
	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}


}
