package firstTabHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.q.week2.R;

import java.util.List;

import Contact.Person;

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Person> ld;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private base64Converter base;

    public ListViewAdapter(List<Person> list, Context context) {
        this.ld = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ld.size();
    }

    @Override
    public Object getItem(int position) {
        return ld.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        base = new base64Converter();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_row, null);

            holder = new ViewHolder();

            holder.pic = convertView.findViewById(R.id.pic);
            holder.name = convertView.findViewById(R.id.name);
            holder.phone = convertView.findViewById(R.id.phone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Person data = ld.get(position);

        holder.name.setText(data.getName());
        holder.phone.setText(phoneHelper.parse(data.getPhone()));
        /*
        Bitmap bm = base.getBitmapFromString(data.getPhoto());


        RoundedBitmapDrawable bd = RoundedBitmapDrawableFactory.create(context.getResources(), bm);
        bd.setCornerRadius(Math.max(bm.getWidth(), bm.getHeight()) / 2.0f);
        bd.setAntiAlias(true);

        holder.pic.setImageDrawable(bd);
        */
        return convertView;
    }

    //listview
    private class ViewHolder {
        public ImageView pic;
        public TextView name;
        public TextView phone;
    }
}