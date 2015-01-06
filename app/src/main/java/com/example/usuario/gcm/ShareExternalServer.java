package com.example.usuario.gcm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShareExternalServer {

	public String shareRegIdWithAppServer(final Context context,
			final String regId) {

        JSONArray values = new JSONArray();
        values.put("user1");


        //JSONObject json = new JSONObject();

/*        try {
            json.put("user","user1");
            json.put("type","android");
            json.put("token",regId);

        }catch (Exception e)
        {}*/

        //JSON PRINCIPAL (MENSAJE)
        JSONObject princjs = new JSONObject();


        //Partes del JSON
        JSONObject androidjs = new JSONObject();
        JSONObject datajs = new JSONObject();
        JSONObject iosjs = new JSONObject();

        try {
            datajs.put("message", "Este es mi mensaje :D");

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

        }



        String result = "";
		//Map<String, String> paramsMap = new HashMap<String, String>();
		//paramsMap.put("regId", regId);
		try {
			URL serverUrl = null;
			try {
				serverUrl = new URL(Config.APP_SERVER_URL);
			} catch (MalformedURLException e) {
				Log.e("AppUtil", "URL Connection Error: "
						+ Config.APP_SERVER_URL, e);
				result = "Invalid URL: " + Config.APP_SERVER_URL;
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
            byte[] bytes =  princjs.toString().getBytes();
//			byte[] bytes =  json.toString().getBytes("UTF-8");

			//byte[] bytes = body.getBytes();
			HttpURLConnection httpCon = null;
			try {
				httpCon = (HttpURLConnection) serverUrl.openConnection();
				httpCon.setDoOutput(true);
				httpCon.setUseCaches(false);
				httpCon.setFixedLengthStreamingMode(bytes.length);
				httpCon.setRequestMethod("POST");
				httpCon.setRequestProperty("Content-Type","application/json");
                httpCon.connect();

                OutputStream out = httpCon.getOutputStream();
				out.write(bytes);
				out.close();

				int status = httpCon.getResponseCode();
				if (status == httpCon.HTTP_OK) {
					result = "RegId shared with Application Server. RegId: "
							+ regId;
				} else {
					result = "Post Failure." + " Status: " + status;
				}
			} finally {
				if (httpCon != null) {
					httpCon.disconnect();
				}
			}

		} catch (IOException e) {
			result = "Post Failure. Error in sharing with App Server.";
			Log.e("AppUtil", "Error in sharing with App Server: " + e);
		}
		return result;
	}
}
