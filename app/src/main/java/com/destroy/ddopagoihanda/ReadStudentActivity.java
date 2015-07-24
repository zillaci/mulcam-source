package com.destroy.ddopagoihanda;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ReadStudentActivity extends ActionBarActivity
        implements TabHost.OnTabChangeListener, View.OnClickListener {

    final static int PICTURE_REQUEST = 100;
    File imageFile;
    WebView webView;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = "";
            if (msg.what == 0) {
                message = "network error";
            } else if (msg.what == 1) {
                message = "network offline";
            } else if (msg.what == 2) {
                message = "server error";
            } else if (msg.what == 200) {
                message = "ok~~~";
            }
            Toast t = Toast.makeText(ReadStudentActivity.this, message, Toast.LENGTH_SHORT);
            t.show();
        }
    };
    private ImageView studentImage;
    private TextView nameView;
    private TextView phoneView;
    private TextView emailView;
    private TabHost tabHost;
    private int studentId = 1;
    private TextView addScoreView;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnBack, btnAdd;
    private MyView scoreView;
    private ListView listView;
    private ArrayList<HashMap<String, String>> scoreList;
    private SimpleAdapter sa;
    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                long date = System.currentTimeMillis();
                String score = addScoreView.getText().toString();

                DBHelper helper = new DBHelper(ReadStudentActivity.this, "studentdb", null, DBHelper.DATABASE_VERSION);
                SQLiteDatabase db = helper.getWritableDatabase();

                db.execSQL("insert into tb_score (student_id, date, score) values(?, ?, ?)",
                        new String[]{String.valueOf(studentId), String.valueOf(date), score});
                db.close();

                addScoreView.setText("0");
                tabHost.setCurrentTab(0);
                scoreView.setScore(Integer.valueOf(score));

                HashMap<String, String> map = new HashMap<>();
                map.put("score", score);
                map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(date)));

                scoreList.add(0, map);
                sa.notifyDataSetChanged();
                listView.setSelection(0);
            }

        }
    };
    View.OnClickListener addScoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnAdd) {
                String score = addScoreView.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadStudentActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("Add score");
                builder.setMessage(score + getString(R.string.read_add_score_dialog_message));
                builder.setPositiveButton(getString(R.string.dialog_ok), dialogListener);
                builder.setNegativeButton(getString(R.string.dialog_cancel), null);

                AlertDialog dialog = builder.create();
                dialog.show();

            } else if (v == btnBack) {
                String score = addScoreView.getText().toString();
                if (score.length() == 1) {
                    addScoreView.setText("0");
                } else {
                    addScoreView.setText(score.substring(0, score.length() - 1));
                }
            } else {
                Button btn = (Button) v;
                String text = btn.getText().toString();
                String score = addScoreView.getText().toString();

                if (score.equals("0")) {
                    addScoreView.setText(text);
                } else {
                    String newScore = score + text;
                    int intScore = Integer.parseInt(newScore);

                    if (intScore > 100) {
                        Toast.makeText(ReadStudentActivity.this, getString(R.string.read_add_score_over_score), Toast.LENGTH_SHORT).show();
                    } else {
                        addScoreView.setText(newScore);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == studentImage) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                imageFile = File.createTempFile("student", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));

                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(intent, PICTURE_REQUEST);
            } catch (IOException e) {
                Log.e("mylog", e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_student);

        Intent intent = getIntent();
        studentId = intent.getIntExtra("id", 0);

        initAddScore();
        initData();
        initTab();
        initSpannable();
        initList();
        initWebView();
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.read_score_chart);
        WebSettings settings = webView.getSettings();

        webView.addJavascriptInterface(new JavascriptTest(), "android");
    }

    private void initList() {
        listView = (ListView) findViewById(R.id.read_score_list);
        scoreList = new ArrayList<>();
        DBHelper helper = new DBHelper(this, "studentdb", null, DBHelper.DATABASE_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select score, date from tb_score where student_id = ? order by date desc",
                new String[]{String.valueOf(studentId)});

        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();

            map.put("score", cursor.getString(0));
            Date date = new Date(Long.valueOf(cursor.getString(1)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            map.put("date", sdf.format(date));

            scoreList.add(map);
        }

        db.close();

        sa = new SimpleAdapter(this, scoreList, R.layout.read_list_item, new String[]{"score", "date"},
                new int[]{R.id.read_list_score, R.id.read_list_date});

        listView.setAdapter(sa);
    }

    private void initSpannable() {
        TextView textView = (TextView) findViewById(R.id.spanView);
        URLSpan urlSpan = new URLSpan("") {
            @Override
            public void onClick(View view) {
                Toast.makeText(ReadStudentActivity.this, "more click", Toast.LENGTH_SHORT).show();
            }
        };

        Spannable textSpan = (Spannable) textView.getText();
        String data = textView.getText().toString();
        int pos = data.indexOf("EXID");
        while (pos > -1) {
            textSpan.setSpan(new ForegroundColorSpan(Color.RED), pos, pos + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos = data.indexOf("EXID", pos + 1);
        }

        pos = data.indexOf("more");
        textSpan.setSpan(urlSpan, pos, pos + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView htmlView = (TextView) findViewById(R.id.htmlView);
        htmlView.setText(Html.fromHtml(
                "<font color=blue>HANI</font><img src=myImage />"
                , new MyImageGetter()
                , null
        ));
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals("tab2")) {
            webView.loadUrl("file:///android_asset/test.html");
        }
    }

    private void initTab() {
        tabHost = (TabHost) findViewById(R.id.host);

        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator("SCORE"); //Button
        spec.setContent(R.id.read_score_list);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator("CHART"); //Button
        spec.setContent(R.id.read_score_chart);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tab3");
        spec.setIndicator("ADD"); //Button
        spec.setContent(R.id.read_score_add);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tab1");
        spec.setIndicator("MEMO"); //Button
        spec.setContent(R.id.read_score_memo);
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(this);
    }

    private void initAddScore() {
        btn0 = (Button) findViewById(R.id.key_0);
        btn1 = (Button) findViewById(R.id.key_1);
        btn2 = (Button) findViewById(R.id.key_2);
        btn3 = (Button) findViewById(R.id.key_3);
        btn4 = (Button) findViewById(R.id.key_4);
        btn5 = (Button) findViewById(R.id.key_5);
        btn6 = (Button) findViewById(R.id.key_6);
        btn7 = (Button) findViewById(R.id.key_7);
        btn8 = (Button) findViewById(R.id.key_8);
        btn9 = (Button) findViewById(R.id.key_9);
        btnBack = (Button) findViewById(R.id.key_back);
        btnAdd = (Button) findViewById(R.id.key_add);

        addScoreView = (TextView) findViewById(R.id.key_edit);

        btn0.setOnClickListener(addScoreListener);
        btn1.setOnClickListener(addScoreListener);
        btn2.setOnClickListener(addScoreListener);
        btn3.setOnClickListener(addScoreListener);
        btn4.setOnClickListener(addScoreListener);
        btn5.setOnClickListener(addScoreListener);
        btn6.setOnClickListener(addScoreListener);
        btn7.setOnClickListener(addScoreListener);
        btn8.setOnClickListener(addScoreListener);
        btn9.setOnClickListener(addScoreListener);
        btnBack.setOnClickListener(addScoreListener);
        btnAdd.setOnClickListener(addScoreListener);

    }

    private void initData() {
        studentImage = (ImageView) findViewById(R.id.read_student_img);
        nameView = (TextView) findViewById(R.id.read_name);
        phoneView = (TextView) findViewById(R.id.read_phone);
        emailView = (TextView) findViewById(R.id.read_email);

        DBHelper helper = new DBHelper(this, "studentdb", null, DBHelper.DATABASE_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from tb_student where _id=" + studentId, null);

        if (cursor.getCount() > 1) {
            throw new IllegalStateException(getString(R.string.what_the_fuck));
        }

        cursor.moveToFirst();

        nameView.setText(cursor.getString(1));
        emailView.setText(cursor.getString(2));
        phoneView.setText(cursor.getString(3));
        studentImage.setOnClickListener(this);
        initStudentImage(cursor.getString(4));

        String photo = cursor.getString(4);

        scoreView = (MyView) findViewById(R.id.read_score);
        cursor = db.rawQuery("select score from tb_score where student_id=? order by date desc limit 1",
                new String[]{String.valueOf(cursor.getInt(0))});

        int score = 50;
        while (cursor.moveToNext()) {
            score = cursor.getInt(0);
        }

        scoreView.setScore(score);

        db.close();
    }

    private void initStudentImage(String photo) {
        if (photo == null || photo.equals("")) {
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(photo);
        if (bitmap == null) {
            return;
        }

        studentImage.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent intent = new Intent();
        View rootItem = listView.getChildAt(0);
        TextView tv1 = (TextView) rootItem.findViewById(R.id.read_list_score);
        TextView tv2 = (TextView) rootItem.findViewById(R.id.read_list_date);

        String contentBody = "score:" + tv1.getText().toString() + "-" + tv2.getText().toString();
        switch (id) {
            case R.id.menu_read_sms:
                String phoneNumber = phoneView.getText().toString();

                if (phoneNumber == null || phoneNumber.equals("")) {
                    break;
                }
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", contentBody);

                break;

            case R.id.menu_read_email:
                String email = emailView.getText().toString();
                if (email == null || email.equals("")) {
                    break;
                }
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, "score");
                intent.putExtra(Intent.EXTRA_TEXT, contentBody);

                break;

            case R.id.menu_read_server:
                if (scoreList != null && scoreList.size() > 0) {
                    HashMap<String, String> map = scoreList.get(0);
                    NetworkThread t = new NetworkThread(map.get("score"), map.get("date"));
                    t.start();
                }

                break;
        }

        if (intent.getAction() != null && !intent.getAction().equals("")) {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_REQUEST && resultCode == RESULT_OK) {
            if (imageFile == null) {
                return;
            }

            DBHelper helper = new DBHelper(this, "studentdb", null, DBHelper.DATABASE_VERSION);
            SQLiteDatabase db = helper.getWritableDatabase();

            db.execSQL("update tb_student set photo=? where _id=?",
                    new String[]{imageFile.getAbsolutePath(), String.valueOf(studentId)});

            db.close();

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            studentImage.setImageBitmap(bitmap);
        }
    }

    public enum Tabs {
        SCORE_LIST("scoreList"),
        SCORE_ADD("scoreAdd"),
        SCORE_CHART("scoreChart"),
        SCORE_MEMO("scoreMemo");

        private final String tabName;

        Tabs(String tabName) {
            this.tabName = tabName;
        }

        public String getTabName() {
            return tabName;
        }
    }

    public class JavascriptTest {
        @JavascriptInterface
        public String getWebData() {
            StringBuffer sb = new StringBuffer();
            sb.append("[");

            if (scoreList.size() <= 10) {
                int j = 0;
                for (int i = scoreList.size(); i > 0; --i) {
                    sb.append("[" + j + ".");
                    sb.append(scoreList.get(i - 1).get("score"));
                    sb.append("]");
                    if (i > 1) sb.append(",");
                    j++;
                }
            } else {
                int j = 0;
                for (int i = 10; i > 0; i--) {
                    sb.append("[" + j + ".");
                    sb.append(scoreList.get(i - 1).get("score"));
                    sb.append("]");
                    if (i > 1) sb.append(",");
                    j++;
                }
            }

            sb.append("]");

            return sb.toString();
        }
    }

    class NetworkThread extends Thread {
        String score;
        String date;

        public NetworkThread(String score, String date) {
            this.score = score;
            this.date = date;
        }

        @Override
        public void run() {
            Message message = new Message();
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
                try {
                    URL url = new URL("http://192.168.43.24:8080/myserver.jsp?score=" + score + "&date=" + date);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setConnectTimeout(10000);
                    http.setReadTimeout(10000);
                    http.setDoInput(true);

                    http.connect();

                    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    String line = null;
                    StringBuffer sb = new StringBuffer();
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    String response = sb.toString().trim();
                    if (response != null && response.equals("ok")) {
                        message.what = 200;
                    } else {
                        message.what = 2;//서버 오류
                    }
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = 0;//오류..
                }
            } else {
                message.what = 1;//network 불가상황
            }

            handler.sendMessage(message);

        }
    }

    private class MyImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            if (source.equals("myImage")) {
                Drawable dr = getResources().getDrawable(R.drawable.hani_1);
                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                return dr;
            }
            return null;
        }
    }
}
