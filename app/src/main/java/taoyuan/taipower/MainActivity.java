package taoyuan.taipower;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

import taoyuan.taipower.base.AnalogNumberPicker;
import taoyuan.taipower.base.BillComparator;
import taoyuan.taipower.base.ElectricNoViewModel;

public class MainActivity extends AppCompatActivity {

    private int currentViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //檢查user是否已經登入
        if (ParseUser.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            //goAccountPage();
//            goHistoryPage();
            goRecordPage();
        }
    }

    public void changePage(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.buttonAccountManagement:
                goAccountPage();
                break;
            case R.id.buttonElectricUsageHistory:
                goHistoryPage();
                break;
            case R.id.buttonSavingPlan:
                goSavingPlanPage();
                break;
            case R.id.buttonRecordUsage:
                goRecordPage();
                break;
            case R.id.buttonMore:
                goMorePage();
                break;
        }
    }

    private void setBaseView(String title) {
        setContentView(currentViewId);
        invalidateOptionsMenu();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }

    /**
     * 讀取電號資料，讀完後將電號資料傳給listener
     *
     * @param listener
     */
    private void loadElectricNoData(final AfterLoadElectricNoDataListener listener) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "處理中", "請稍候", false, false);

        ParseUser currentUser = ParseUser.getCurrentUser();
        final String userId = currentUser.getObjectId();

        ParseQuery<ParseObject> query = new ParseQuery("ElectricNo");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> electricNoObjects, ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(MainActivity.this, "發生錯誤，請稍候再試", Toast.LENGTH_SHORT).show();
                } else {
                    listener.call(electricNoObjects);
                }
            }
        });
    }

    /**
     * 讀取帳號資料
     */
    private void loadAccountData() {
        loadElectricNoData(new AfterLoadElectricNoDataListener() {
            @Override
            public void call(List<ParseObject> electricNoObjects) {
                TextView textView = (TextView) findViewById(R.id.textViewElectricNoCount);
                textView.setText("本電子帳單帳號內共含" + electricNoObjects.size() + "筆電號");

                final TextView textViewElectricNoAliasName = (TextView) findViewById(R.id.electricNoAliasName);
                final ElectricNoViewModel electricNoViewModel = new ElectricNoViewModel(MainActivity.this, electricNoObjects, new ElectricNoViewModel.ElectricNoSelectedListener() {
                    @Override
                    public void call(ParseObject electricNo) {
                        String aliasName = electricNo.getString("aliasName");
                        if (aliasName == null) {
                            textViewElectricNoAliasName.setText("尚未命名");
                        } else {
                            textViewElectricNoAliasName.setText(aliasName);
                        }
                    }
                });

                textViewElectricNoAliasName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ParseObject parseObject = electricNoViewModel.getCurrentElectricNoObject();
                        final EditText input = new EditText(MainActivity.this);
                        String aliasName = parseObject.getString("aliasName");
                        if (aliasName != null) {
                            input.setText(aliasName);
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("命名電號")
                                .setMessage("Message")
                                .setView(input)
                                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        parseObject.put("aliasName", input.getText().toString());
                                        parseObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                loadAccountData();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                });
            }
        });

    }

    /**
     * 讀取用電紀錄
     */
    private void loadHistoryData() {
        loadElectricNoData(new AfterLoadElectricNoDataListener() {
            @Override
            public void call(List<ParseObject> electricNoObjects) {


                final TextView textViewBestSavingDegree = (TextView) findViewById(R.id.textViewBestSavingDegree);
                final TextView textViewBestSavingDegreeMonth = (TextView) findViewById(R.id.textViewBestSavingDegreeMonth);
                final ElectricNoViewModel electricNoViewModel = new ElectricNoViewModel(MainActivity.this, electricNoObjects, new ElectricNoViewModel.ElectricNoSelectedListener() {
                    @Override
                    public void call(ParseObject electricNo) {
                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "處理中", "請稍候", false, false);
                        ParseQuery<ParseObject> query = new ParseQuery<>("Bill");
                        query.whereEqualTo("electricNoId", electricNo.getObjectId());

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> billObjects, ParseException e) {
                                progressDialog.dismiss();

                                Collections.sort(billObjects, new BillComparator());

                                if (e == null) {
                                    if (!billObjects.isEmpty()) {
                                        ParseObject bestSavingBill = billObjects.get(0);
                                        for (ParseObject bill : billObjects) {
                                            if ((bill.getInt("degreeDiff") - bill.getInt("lastDegreeDiff")) > (bestSavingBill.getInt("degreeDiff") - bestSavingBill.getInt("lastDegreeDiff"))) {
                                                bestSavingBill = bill;
                                            }

                                            System.out.println(bill.getInt("year") + "/" + bill.getInt("month"));
                                        }

                                        //最佳節電紀錄
                                        textViewBestSavingDegree.setText((bestSavingBill.getInt("degreeDiff") - bestSavingBill.getInt("lastDegreeDiff")) + "度");
                                        textViewBestSavingDegreeMonth.setText(bestSavingBill.getInt("year") + "年" + bestSavingBill.getInt("month") + "月");

                                        //累積節電量
                                        //TODO 志強

                                        //累積減少CO2排放
                                        //TODO 志強

                                        int[] textViewMonthIds = new int[]{
                                                R.id.textViewMonth8,
                                                R.id.textViewMonth7,
                                                R.id.textViewMonth6,
                                                R.id.textViewMonth5,
                                                R.id.textViewMonth4,
                                                R.id.textViewMonth3,
                                                R.id.textViewMonth2,
                                                R.id.textViewMonth1
                                        };

                                        int[] textViewYearIds = new int[]{
                                                R.id.textViewYear8,
                                                R.id.textViewYear7,
                                                R.id.textViewYear6,
                                                R.id.textViewYear5,
                                                R.id.textViewYear4,
                                                R.id.textViewYear3,
                                                R.id.textViewYear2,
                                                R.id.textViewYear1
                                        };

                                        for (int i = 0; i < billObjects.size() && i < 8; i++) {
                                            final ParseObject bill = billObjects.get(i);
                                            TextView textViewMonth = (TextView) findViewById(textViewMonthIds[i]);
                                            TextView textViewYear = (TextView) findViewById(textViewYearIds[i]);
                                            final int month = bill.getInt("month");
                                            final int year = bill.getInt("year");
                                            textViewMonth.setText(String.valueOf(month));
                                            if (month == 1 || month == 2) {
                                                textViewYear.setVisibility(View.VISIBLE);
                                                textViewYear.setText(String.valueOf(year));
                                            } else {
                                                textViewYear.setVisibility(View.GONE);
                                            }

                                            textViewMonth.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    double degreeDiff = bill.getDouble("degreeDiff");
                                                    double lastDegreeDiff = bill.getDouble("lastDegreeDiff");
                                                    double price = bill.getInt("price");
                                                    double lastPrice = bill.getInt("lastPrice");
                                                    System.out.println(year + "年" + month + "月" + "被點了");
                                                    System.out.println("本期用電" + degreeDiff + "度/" + price + "元");
                                                    System.out.println("去年同期用電" + lastDegreeDiff + "度/" + lastPrice + "元");

                                                    //TODO 志強
                                                }
                                            });
                                        }


                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, "發生錯誤，請稍候再試", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                });
            }
        });
    }

    private void loadRecordData() {
        loadElectricNoData(new AfterLoadElectricNoDataListener() {
            @Override
            public void call(List<ParseObject> electricNoObjects) {
                final ElectricNoViewModel electricNoViewModel = new ElectricNoViewModel(MainActivity.this, electricNoObjects, null);

                AnalogNumberPicker anp1 = (AnalogNumberPicker) findViewById(R.id.analogNumberPicker1);
                AnalogNumberPicker anp2 = (AnalogNumberPicker) findViewById(R.id.analogNumberPicker2);
                anp2.setClockwise(false);
                AnalogNumberPicker anp3 = (AnalogNumberPicker) findViewById(R.id.analogNumberPicker3);
                AnalogNumberPicker anp4 = (AnalogNumberPicker) findViewById(R.id.analogNumberPicker4);
                anp4.setClockwise(false);
                AnalogNumberPicker anp5 = (AnalogNumberPicker) findViewById(R.id.analogNumberPicker5);
                final EditText et1 = (EditText) findViewById(R.id.editTextDegree1);
                final EditText et2 = (EditText) findViewById(R.id.editTextDegree2);
                final EditText et3 = (EditText) findViewById(R.id.editTextDegree3);
                final EditText et4 = (EditText) findViewById(R.id.editTextDegree4);
                final EditText et5 = (EditText) findViewById(R.id.editTextDegree5);

                anp1.bindToEditText(et1);
                anp2.bindToEditText(et2);
                anp3.bindToEditText(et3);
                anp4.bindToEditText(et4);
                anp5.bindToEditText(et5);

                Button buttonSaveDegree = (Button) findViewById(R.id.buttonSaveDegree);
                buttonSaveDegree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseObject electricNoObject = electricNoViewModel.getCurrentElectricNoObject();
                        String electricNoId = electricNoObject.getObjectId(); //電號
                        //TODO 鳳呈
                    }
                });
            }
        });
    }


    private void goAccountPage() {
        currentViewId = R.layout.activity_1_account;
        setBaseView("電號管理");

        loadAccountData();
    }

    private void goHistoryPage() {
        currentViewId = R.layout.activity_2_history;
        setBaseView("用電紀錄");

        loadHistoryData();
    }


    private void goSavingPlanPage() {
        currentViewId = R.layout.activity_3_saving;
        setBaseView("節電計畫");

        //TODO 博宇
    }

    private void goRecordPage() {
        currentViewId = R.layout.activity_4_record;
        setBaseView("抄表");

        loadRecordData();
    }

    private void goMorePage() {
        currentViewId = R.layout.activity_5_more;
        setBaseView("其他");

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, new String[]{"好康優惠", "常見問題", "關於App", "登出"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        //TODO 薏瑾
                        break;
                    case 1:
                        //TODO 薏瑾
                        break;
                    case 2:
                        //TODO 薏瑾
                        break;
                    case 3:
                        ParseUser.logOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
            }
        });
    }

    interface AfterLoadElectricNoDataListener {
        void call(List<ParseObject> electricNoObjects);
    }
}
