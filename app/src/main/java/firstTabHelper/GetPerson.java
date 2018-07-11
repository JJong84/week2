package firstTabHelper;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import Contact.Person;

/**
 * AsyncTask를 이용하여 REST GET콜을 통한 JSON을 얻어오는 클래스.
 */
public class GetPerson {
    public GetPerson() {
    }

    private String getString;
    private ArrayList<Person> contactList = null;

    public ArrayList<Person> getPerson() {
        // inner class로 구현한 GetTask 객체를 통해 REST API콜
        Log.d("REST GET", "The response is");
        GetTask gettask = new GetTask();
        try {
            getString = gettask.execute("http://52.231.68.146:8080/api/contacts").get();
            JSONArray json = new JSONArray(getString);
            JSONHelper help = new JSONHelper();

            Log.d("REST GET", Integer.toString(json.length()));
            contactList = help.parser(json);
        } catch (Exception e) {
            Log.d("REST GET", e.getMessage());
        }
        ;
        return contactList;
    }

    // AsyncTask를 inner class로 구현
    private class GetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return GET(params[0]);
            } catch (IOException e) {
                return "Unable to retreive data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ;
        }
    }

    private String GET(String myurl) throws IOException {
        InputStream inputStream = null;
        String returnString = "";

        int length = 400000; //should be big

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("REST GET", "The response is : " + response);
            inputStream = new BufferedInputStream(conn.getInputStream());


            // convert inputStream into json
            returnString = convertInputStreamToString(inputStream);
        } catch (Exception e) {
            Log.d("REST GET", "Error : " + e.getMessage());
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return returnString;
    }

    public String convertInputStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {
        String returnString = "";
        Scanner s = new Scanner(stream);
        while(s.hasNext()){
            returnString += s.nextLine();
        }
        s.close();
        Log.d("REST GET",  returnString);
        return returnString;
        /*
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader rd = new BufferedReader(reader);
        OutputStreamWriter wd = new OutputStreamWriter(returnString);
        BufferedWriter rw = new BufferedWriter(wd);
        try {
            String line = rd.readLine();
            rw.write(line);
            rw.flush();
            rw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnString;
        */
    }
}