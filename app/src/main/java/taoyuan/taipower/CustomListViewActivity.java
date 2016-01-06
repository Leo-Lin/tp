package taoyuan.taipower;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class CustomListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_view);

        final ListView listView = (ListView) findViewById(R.id.listView);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Savetip");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                SaveTipAdapter adapter = new SaveTipAdapter(objects);

                listView.setAdapter(adapter);

            }
        });

    }

    class SaveTipAdapter extends BaseAdapter {

        private List<ParseObject> objects;

        public SaveTipAdapter(List<ParseObject> objects) {
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listitem_save_tip, null);
            }

            TextView textViewSaveTipName = (TextView) convertView.findViewById(R.id.textViewSaveTipName);
            TextView textViewSaveTipMethod = (TextView) convertView.findViewById(R.id.textViewSaveTipMethod);

            ParseObject parseObject = objects.get(position);

            textViewSaveTipName.setText(parseObject.getString("name"));
            textViewSaveTipMethod.setText(parseObject.getString("method"));

            return convertView;
        }
    }
}
