package com.destroy.ddopagoihanda;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ImageView addBtn;
    ListView listView;
    ArrayList<StudentVO> data;
    private SimpleCursorAdapter mAdapter;
    private SearchView searchView;
    private double initTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = (ImageView) findViewById(R.id.main_btn);
        addBtn.setOnClickListener(this);

        DBHelper helper = new DBHelper(this, "studentdb", null, DBHelper.DATABASE_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_student order by name", null);

        data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Cursor scoreCursor = db.rawQuery("select score from tb_score "
                    + "where student_id=? order by date desc limit 1", new String[]{cursor.getString(0)});

            data.add(new StudentVO(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    scoreCursor.moveToFirst() ? scoreCursor.getInt(0) : 0));
        }

        listView = (ListView) findViewById(R.id.main_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new MainListAdapter(this, R.layout.main_list_item, data));

        db.close();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_main_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint(getResources().getString(R.string.main_search_hint));
        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromInputMethod(searchView.getWindowToken(), 0);

                searchView.setQuery("", false);
                searchView.setIconified(true);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "name"});

                for (int i = 0; i < data.size(); ++i) {
                    if (data.get(i).name.toLowerCase().startsWith(newText.toLowerCase())) {
                        c.addRow(new Object[]{data.get(i).id, data.get(i).name});
                    }
                }

                mAdapter.changeCursor(c);
                mAdapter.notifyDataSetChanged();

                return false;
            }
        });

        mAdapter = new SimpleCursorAdapter(
                this,
                R.layout.main_search_list_item,
                null,
                new String[]{"name"},
                new int[]{R.id.main_search_item_text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Intent intent = new Intent(MainActivity.this, ReadStudentActivity.class);
                CursorAdapter ca = searchView.getSuggestionsAdapter();
                Cursor cursor = (Cursor) ca.getItem(position);
                intent.putExtra("id", Integer.parseInt(cursor.getString(position)));
                searchView.setQuery("", false);
                searchView.setIconified(true);

                startActivity(intent);

                return false;
            }
        });

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_main_search) {
            Toast.makeText(this, "find!@#!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - initTime > 3000) {
                Toast.makeText(this, getString(R.string.main_back_end), Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            initTime = System.currentTimeMillis();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AddStudentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ReadStudentActivity.class);
        intent.putExtra("id", data.get(position).id);

        startActivity(intent);
    }
}
