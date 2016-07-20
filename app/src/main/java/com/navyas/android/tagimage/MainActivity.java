           package com.navyas.android.tagimage;

           import android.app.ActivityManager;
           import android.app.ProgressDialog;
           import android.content.Context;
           import android.content.Intent;
           import android.database.Cursor;
           import android.database.sqlite.SQLiteDatabase;
           import android.os.Bundle;
           import android.provider.MediaStore;
           import android.support.v7.app.AppCompatActivity;
           import android.view.Menu;
           import android.view.MenuItem;
           import android.view.View;
           import android.widget.Button;
           import android.widget.EditText;
           import android.widget.Toast;

           import java.io.File;
           import java.io.Serializable;
           import java.util.ArrayList;
           import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cursor cursor;
    private static int columnIndex;
    private static final String[] proj = {MediaStore.Images.Media.DATA};
    public static final String ClientId = "id";
    public static final String ClientSecret = "secret";
    private static String[] tagName = new String[20];
    public static List<String> string = new ArrayList<String>();
    private static List<String> grid = new ArrayList<>();
    public static int stop = 0;
    public static ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<File> files = new ArrayList<>();

        cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        string.add(cursor.getString(columnIndex));


        while (cursor.moveToNext()){
            string.add(cursor.getString(columnIndex));
        }

        for (String attr: string) {
            File file = new File(attr);
            files.add(file);
        }

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ClarifaiService.class);
        startService(intent);

        Button btnSearch = (Button) findViewById(R.id.search_button);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    grid.clear();
                    String projection[] = {ClarifaiContract.DataEntry._ID,
                            ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION,
                    };

                    String selection = ClarifaiContract.DataEntry.COLUMN_TAG1 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG2 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG3 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG4 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG5 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG6 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG7 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG8 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG9 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG10 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG11 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG12 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG13 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG14 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG15 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG16 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG17 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG18 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG19 + "=? OR " +
                            ClarifaiContract.DataEntry.COLUMN_TAG20 + "=?";

                    String query = ((EditText) findViewById(R.id.edit_query)).getText().toString().toLowerCase();
                    query = query.trim();
                    String[] selectionArgs = {query, query, query, query, query, query, query, query, query, query
                            , query, query, query, query, query, query, query, query, query, query};
                    Cursor c;
                    String sortOrder =
                            ClarifaiContract.DataEntry._ID + " ASC";
                    ClarifaiDbHelper mDbHelper = new ClarifaiDbHelper(v.getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    c = db.query(ClarifaiContract.DataEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    c.moveToFirst();

                    grid.add(c.getString(c.getColumnIndex(ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION)));
                    while (c.moveToNext()) {
                        grid.add(c.getString(c.getColumnIndex(ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION)));
                    }


                    Intent intent = new Intent(MainActivity.this, GridViewActivity.class);
                    intent.putExtra("grid", (Serializable) grid);

                    startActivity(intent);
                }
                catch (Exception e){
                    Toast.makeText(v.getContext(), "No result found", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            ClarifaiDbHelper mDbHelper = new ClarifaiDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(ClarifaiContract.DataEntry.TABLE_NAME, null, null);
            return true;
        }

        if (id == R.id.action_resync){
            stop = 0;
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ClarifaiService.class);
            startService(intent);

            if(isMyServiceRunning(ClarifaiService.class)) {
                mProgress = new ProgressDialog(this);
                mProgress.setMessage("Loading");
                mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgress.setIndeterminate(true);
                mProgress.setProgress(0);
                mProgress.setCancelable(true);
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
            }
            return true;
        }

        if (id == R.id.action_stop){
            stop = 1;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }





}
