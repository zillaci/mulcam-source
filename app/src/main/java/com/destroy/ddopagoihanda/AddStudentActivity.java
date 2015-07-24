package com.destroy.ddopagoihanda;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddStudentActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText nameView;
    private EditText emailView;
    private EditText phoneView;
    private EditText memoView;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        nameView = (EditText) findViewById(R.id.add_item_name);
        emailView = (EditText) findViewById(R.id.add_item_email);
        phoneView = (EditText) findViewById(R.id.add_item_phone);
        memoView = (EditText) findViewById(R.id.add_item_memo);

        addBtn = (Button) findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String name = nameView.getText().toString();
        String email = emailView.getText().toString();
        String phone = phoneView.getText().toString();
        String memo = memoView.getText().toString();

        if(name == null || "".equals(name)) {
            Toast.makeText(this, getString(R.string.add_name_null), Toast.LENGTH_SHORT).show();
        }
        else {
            DBHelper helper = new DBHelper(this, "studentdb", null, DBHelper.DATABASE_VERSION);
            SQLiteDatabase db = helper.getWritableDatabase();

            db.execSQL("insert into tb_student (name, email, phone, memo) values(?, ?, ?, ?)",
                    new String[] {name, email, phone, memo});
            db.close();
        }

        finish();
    }
}
