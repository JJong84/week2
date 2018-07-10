package firstTabHelper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Contact.Person;

/**
 * AsyncTask를 이용하여 REST GET콜을 통한 JSON을 얻어오는 클래스.
 */
public class GetPerson {
    private ArrayList<Person> downloadList;
    private String person;

    public void getPerson() {
        // inner class로 구현한 GetTask 객체를 통해 REST API콜
        new GetTask().execute("http://52.231.68.146:8080/api/contacts");
    }

    // AsyncTask를 inner class로 구현
    private class GetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                person = GET(params[0]);
                Log.d("REST GET1", "The person is : " + person);
                return person;
            } catch (IOException e) {
                return "Unable to retreive data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONHelper jsonhelper = new JSONHelper();
            Log.d("REST GET10",  s);
            person = s;
            Log.d("REST GET103",  person);
            try {
                JSONArray json = new JSONArray(person);
                downloadList = jsonhelper.parser(json);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String GET(String myurl) throws IOException {
        InputStream inputStream = null;
        String returnString = "";
        JSONHelper jsonhelper = new JSONHelper();

        int length = 500;


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("REST GET3", "The response is : " + response);
            inputStream = conn.getInputStream();
            // convert inputStream into json
            returnString = convertInputStreamToString(inputStream, length);
        } catch (Exception e) {
            Log.e("REST GET4", "Error : " + e.getMessage());
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return returnString;
    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

    public ArrayList<Person> getDownloadList() {
        return downloadList;
    }
}
