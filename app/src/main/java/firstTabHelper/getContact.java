package firstTabHelper;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.content.Context;

import com.example.q.week2.R;

import java.io.FileInputStream;
import java.util.ArrayList;

import Contact.Person;

public class getContact {
    public getContact() {
    }

    public ArrayList<Person> collect(Context context) {
        Uri muri = ContactsContract.Contacts.CONTENT_URI;
        Uri curi = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri euri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        Uri auri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        Uri nuri = ContactsContract.Data.CONTENT_URI;

        Person person;
        base64Converter base = new base64Converter();
        String photo;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        String[] mainProjection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        String[] contactProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID
        };

        String[] emailProjection = new String[]{
                ContactsContract.CommonDataKinds.Email.DATA
        };

        String[] addressProjection = new String[]{
                ContactsContract.CommonDataKinds.StructuredPostal.DATA,
        };

        ArrayList<Person> contactlist = new ArrayList<>();
        Cursor mainCursor = context.getContentResolver().query(muri, mainProjection, null, null, sortOrder);

        while (mainCursor.moveToNext()) {
            person = new Person();
            person.setId(mainCursor.getString(0));
            person.setName(mainCursor.getString(1));

            Cursor contactCursor = context.getContentResolver().query(curi, contactProjection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + person.getId(), null, sortOrder);

            while (contactCursor.moveToNext()) {
                String phone = contactCursor.getString(0);
                person.setPhone(phone);
               //Log.d("phonephone", phone);

                Long photo_id = contactCursor.getLong(1);
                if(photo_id==0){
                    Log.d("photono", "");
                    person.setPhoto("None");
                }
                else {
                    photo = base.getStringFromBitmap(getImage(photo_id, context));
                    person.setPhoto(photo);
                    Log.d("photoyes", person.getName());
                }
            }
            contactCursor.close();

            Cursor emailCursor = context.getContentResolver().query(euri, emailProjection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + person.getId(), null, sortOrder);

            while (emailCursor.moveToNext()) {
                String email = emailCursor.getString(0);
                person.setEmail(email);
                Log.d("loops", "email");
            }
            emailCursor.close();

            Cursor addressCursor = context.getContentResolver().query(auri, addressProjection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + person.getId(), null, sortOrder);

            while (addressCursor.moveToNext()) {
                String address = addressCursor.getString(0);
                person.setAddress(address);
                Log.d("loops", "address");
            }
            addressCursor.close();

            contactlist.add(person);
            Log.d("loops", "main");

            try {
                FileInputStream fis = context.openFileInput(person.getName() + ".txt");
                byte[] data = new byte[fis.available()];
                while (fis.read(data) != -1) {
                    ;
                }
                fis.close();
                String bool = new String(data);
                if (bool.equals("true")) {
                    person.setFavorite(true);
                } else {
                    person.setFavorite(false);
                }
            } catch (Exception e) {
                ;
            }
        }
        mainCursor.close();

        Log.d("passs", "sa");
        return contactlist;
    }

    private Bitmap getImage(long imageDataRow, Context context) {
        Cursor c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[]{
                Long.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.profile);
        }
    }
}
