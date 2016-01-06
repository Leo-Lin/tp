package taoyuan.taipower;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

//        startActivity(new Intent(this, MainActivity.class)
//                        .putExtra("aa", "bb")
//                        .putExtra("cc", "dd")
//        );

//        EditText editText = new EditText(this);

//        ParseObject parseObject = new ParseObject("xxx");
        //整數
        // int i= Integer.parseInt(editText.getText().toString());

        //小數
//        double d = Double.parseDouble(editText.getText().toString());

//        parseObject.put("key", d);


        ParseQuery<ParseObject> query = new ParseQuery<>("Savetip");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject object : objects) {
                    System.out.println(object.getString("name"));

                }
            }
        });

        final ListView listView = (ListView) findViewById(R.id.listView);

        ParseQuery<ParseObject> parseQuery = new ParseQuery("ElectricNo");
        parseQuery.whereEqualTo("userId", "bRkrYL2TeJ");

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {

                ArrayList<String> elecNos = new ArrayList<String>();

                for (ParseObject object : list) {
                    String number = object.getString("number");

                    elecNos.add(number);
                }

                listView.setAdapter(new ArrayAdapter(ExampleActivity.this, android.R.layout.simple_list_item_1, elecNos));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ParseObject parseObject = list.get(position);
                        String userId = parseObject.getString("userId");

                        Toast.makeText(ExampleActivity.this, userId, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
//        getData();
//        saveData();
//        updateData();


    }

    private void updateData() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ElectricNo");
        query.whereEqualTo("number", "003-33333333");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (object != null) {

                    System.out.println("userId=" + object.getString("userId"));
                    System.out.println("電號=" + object.getString("number"));

                    object.put("userId", "00011");
                    object.saveInBackground();

                }
            }
        });
    }


    private void saveData() {

//        ParseObject parseObject = new ParseObject("ElectricNo");
//        parseObject.put("userId", "bRkrYL2TeJ");
//        parseObject.put("number", "002-11133333");
//        parseObject.put("recordDate", new Date());

    }

    private void getData() {
        //        userId bRkrYL2TeJ
        ParseQuery<ParseObject> parseQuery = new ParseQuery("ElectricNo");
        parseQuery.whereEqualTo("userId", "bRkrYL2TeJ");

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                System.out.println("總共有幾筆：" + list.size());

                //for each
                for (ParseObject parseObject : list) {

                    String number = parseObject.getString("number");
                    Date recordDate = parseObject.getDate("recordDate");
                    System.out.println("電號：" + number);
                    System.out.println("抄表日：" + recordDate);

                    getBill(parseObject);


                }

                //傳統for
//                for (int i = 0; i < list.size(); i++) {
//                    ParseObject parseObject = list.get(i);
//
//                }

            }
        });
    }

    /**
     * 用電號資料來找Bill
     */
    private void getBill(ParseObject parseObject) {

        ParseQuery<ParseObject> billQuery = new ParseQuery("Bill");
        billQuery.whereEqualTo("electricNoId", parseObject.getObjectId());

        billQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                System.out.println("帳單數：" + objects.size());

                for (ParseObject bill : objects) {
                    int year = bill.getInt("year");
                    int month = bill.getInt("month");
                    int price = bill.getInt("price");
                    System.out.println(year + "年" + month + "月 帳單" + price + "元");
                }
            }
        });
    }
}
