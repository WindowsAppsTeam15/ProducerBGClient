package team15.producerbgclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProducerDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String PRODUCERS_TABLE_NAME = "cachedProducers";
    private static final String PRODUCERS_TABLE_CREATE =
            "CREATE TABLE " + PRODUCERS_TABLE_NAME + " (" +
                    "_id TEXT, " +
                    "name TEXT, " +
                    "type TEXT, " +
                    "logo BLOB);";

    ProducerDbHelper(Context context) {
        super(context, "producerBg", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PRODUCERS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public List<ContractProducer> getProducers(String name) {
        List<ContractProducer> producersList = new ArrayList<ContractProducer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + PRODUCERS_TABLE_NAME;
        if(name == null || name == "") {
            selectQuery = "SELECT  * FROM " + PRODUCERS_TABLE_NAME;
        } else {
            selectQuery = "SELECT  * FROM " + PRODUCERS_TABLE_NAME;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContractProducer contact = new ContractProducer();
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setType(cursor.getString(2));
                contact.setLogo(cursor.getBlob(3));

                // Adding contact to list
                producersList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return producersList;
    }

    public boolean emptyProducersDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ PRODUCERS_TABLE_NAME);
        db.close();
        return true;
    }

    public boolean fillProducerDb(String producersStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        JSONArray json = null;
        try {
            json = new JSONArray(producersStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < json.length(); i++) {
            JSONObject producerJson = null;
            try {
                producerJson = json.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray logo = null;
            try {
                logo = producerJson.getJSONArray("logo");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            byte[] imgByteArr = null;
            if (logo != null) {
                imgByteArr = new byte[logo.length()];
                for (int j = 0; j < logo.length(); j++) {
                    try {
                        imgByteArr[j] = (byte) logo.getInt(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            String id = null;
            try {
                id = producerJson.getString("_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String name = null;
            try {
                name = producerJson.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String type = null;
            try {
                type = producerJson.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            contentValues.put("name", name);
            contentValues.put("_id", id);
            contentValues.put("logo", imgByteArr);
            contentValues.put("type", type);
            db.insert(PRODUCERS_TABLE_NAME, null, contentValues);
        }
        db.close();

        return true;
    }
}