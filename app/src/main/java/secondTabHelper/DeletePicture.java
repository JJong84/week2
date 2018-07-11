package secondTabHelper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import firstTabHelper.DeletePerson;

public class DeletePicture {

    public DeletePicture() {}

    public void deletePicture(){
        // inner class로 구현한 GetTask 객체를 통해 REST API콜
        new DeletePicture.DeleteTask().execute("http://52.231.68.146:8080/api/pictures");
    }

    // AsyncTask를 inner class로 구현
    private class DeleteTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try{
                return DELETE(params[0]);
            }catch (IOException e){
                return "Unable to retreive data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private String DELETE(String myurl) throws IOException{
        InputStream inputStream = null;
        String returnString = "";

        int length = 500;

        try{
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("DELETE");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.connect();

            int response = conn.getResponseCode();
            Log.e("REST DELETE", "The response is : " + response);
        }catch (Exception e){
            Log.e("REST DELETE", "Error : "+e.getMessage());
        }finally {
            if(inputStream != null)
                inputStream.close();
        }
        return returnString;
    }
}
