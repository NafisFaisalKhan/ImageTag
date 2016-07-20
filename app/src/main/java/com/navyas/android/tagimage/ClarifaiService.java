package com.navyas.android.tagimage;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClarifaiService extends IntentService {

    private static String[] tagName = new String[20];
    private static Cursor c;
    private static int flag;
    private static String compare = "ipsum";
    private ProgressDialog mProgress;

    private boolean mRunning;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mRunning = false;

    }


    public ClarifaiService(){
        super(ClarifaiService.class.getName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        List<File> files = new ArrayList<>();
        for (String attr: MainActivity.string) {
            File file = new File(attr);
            files.add(file);
            Log.e("Tag", attr);
        }

        ClarifaiClient client = new ClarifaiClient(MainActivity.ClientId, MainActivity.ClientSecret);
        ClarifaiDbHelper mDbHelper = new ClarifaiDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //db.delete(ClarifaiContract.DataEntry.TABLE_NAME, null, null);
        ContentValues values = new ContentValues();

        int i;
        for (File file: files) {
            if(MainActivity.stop == 1) break;
            flag = 0;
            try {
                String projection[] = {ClarifaiContract.DataEntry._ID,
                        ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION,
                };

                c = db.query(ClarifaiContract.DataEntry.TABLE_NAME, projection, null, null, null, null, null);
            } catch (Exception e) {}

            try {
                c.moveToFirst();
                compare = c.getString(c.getColumnIndex(ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION));
                if (compare.equals(Uri.fromFile(file).toString()))
                    flag = 1;
                while (c.moveToNext()){
                    compare = c.getString(c.getColumnIndex(ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION));
                    if (compare.equals(Uri.fromFile(file).toString()))
                        flag = 1;
                    Log.e("TAG", "Running");

                }
            }
            catch (Exception e){}

            if (flag == 0) {
                i = 0;
                try {
                    List<RecognitionResult> results = client.recognize(new RecognitionRequest(file));

                    for (Tag tag : results.get(0).getTags()) {
                        System.out.println(i + ": " + tag.getName() + ": " + tag.getProbability());
                        tagName[i] = tag.getName();
                        i++;
                    }

                    Uri uri = Uri.fromFile(file);
                    values.put(ClarifaiContract.DataEntry.COLUMN_IMAGE_LOCATION, uri.toString());
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG1, tagName[0]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG2, tagName[1]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG3, tagName[2]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG4, tagName[3]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG5, tagName[4]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG6, tagName[5]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG7, tagName[6]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG8, tagName[7]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG9, tagName[8]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG10, tagName[9]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG11, tagName[10]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG12, tagName[11]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG13, tagName[12]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG14, tagName[13]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG15, tagName[14]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG16, tagName[15]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG17, tagName[16]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG18, tagName[17]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG19, tagName[18]);
                    values.put(ClarifaiContract.DataEntry.COLUMN_TAG20, tagName[19]);

                    db.insert(ClarifaiContract.DataEntry.TABLE_NAME, null, values);

                }
                catch (Exception e){}
            }


        }
        Log.e("Tag", "Service complete");
            MainActivity.mProgress.setProgress(100);
            MainActivity.mProgress.dismiss();





        }



    }

