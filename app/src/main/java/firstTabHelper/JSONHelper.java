package firstTabHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Base64;

import com.example.q.week2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import Contact.Person;

public class JSONHelper {

    public JSONObject merger(Person person, Context context) {
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("name", person.getName());
            jsonobject.put("phone", person.getPhone());
            jsonobject.put("email", person.getEmail());
            jsonobject.put("address", person.getAddress());
            //jsonobject.put("profile", person.getPhoto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonobject;
    }

    //json parse
    public ArrayList<Person> parser(JSONArray jarray) {
        ArrayList<Person> contactlist = new ArrayList<Person>();
        Person person;
        try {
            for (int i = 0; i < jarray.length(); i++) {
                person = new Person();
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                person.setName(jObject.getString("name"));
                person.setPhone(jObject.getString("phone"));
                person.setEmail(jObject.getString("email"));
                person.setAddress(jObject.getString("address"));
                person.setPhoto(jObject.getString("profile"));
                contactlist.add(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactlist;
    }
}
