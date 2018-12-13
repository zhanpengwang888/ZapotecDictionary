package edu.haverford.cs.zapotecdictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    protected static final String TAG = "DBHelper";
    protected static final String DATABASE_NAME = "zapotecDictionaryDB.db";
    protected static String DATABASE_PATH = "";
    protected static final String DICTIONARY_TABLE_NAME = "dictionary";
    protected static final String DICTIONARY_COLUMN_OID = "oid";
    protected static final String DICTIONARY_COLUMN_LANG = "lang";
    protected static final String DICTIONARY_COLUMN_IPA = "ipa";
    protected static final String DICTIONARY_COLUMN_GLOSSARY = "gloss";
    protected static final String DICTIONARY_COLUMN_POS = "pos";
    protected static final String DICTIONARY_COLUMN_USAGE_EXAMPLE = "usage_example";
    protected static final String DICTIONARY_COLUMN_DIALECT = "dialect";
    protected static final String DICTIONARY_COLUMN_METADATA = "metadata";
    protected static final String DICTIONARY_COLUMN_AUTHORITY = "authority";
    protected static final String DICTIONARY_COLUMN_AUDIO = "audio";
    protected static final String DICTIONARY_COLUMN_IMAGE = "image";
    protected static final String DICTIONARY_COLUMN_SEMANTIC_IDS = "semantic_ids";
    protected static final String DICTIONARY_COLUMN_CZI = "czi";
    protected static final String DICTIONARY_COLUMN_ES_GLOSS = "es_gloss";
    protected static final int DICTIONARY_DATABASE_QUERY_ERROR = -2;
    private final Context mContext;
    private SQLiteDatabase mDB;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the database upon the first installation of the dictionary app
        sqLiteDatabase.execSQL(
                "CREATE TABLE dictionary " +
                        "(oid INTEGER PRIMARY KEY, lang TEXT, ipa TEXT, gloss TEXT, pos TEXT, usage_example TEXT, dialect TEXT, " +
                        "metadata TEXT, authority TEXT, audio TEXT, image TEXT, semantic_ids TEXT, czi TEXT, es_gloss TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS dictionary");
        onCreate(sqLiteDatabase);
    }

    public ContentValues createContentValues(String oid, String lang, String ipa, String gloss, String pos, String usageExample, String dialect,
                                             String metadata, String authority, String audio, String image, String semantic_ids, String czi, String esGloss) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DICTIONARY_COLUMN_OID, Integer.parseInt(oid));
        contentValues.put(DICTIONARY_COLUMN_LANG, lang);
        contentValues.put(DICTIONARY_COLUMN_IPA, ipa);
        contentValues.put(DICTIONARY_COLUMN_GLOSSARY, gloss);
        contentValues.put(DICTIONARY_COLUMN_POS, pos);
        contentValues.put(DICTIONARY_COLUMN_USAGE_EXAMPLE, usageExample);
        contentValues.put(DICTIONARY_COLUMN_DIALECT, dialect);
        contentValues.put(DICTIONARY_COLUMN_METADATA, metadata);
        contentValues.put(DICTIONARY_COLUMN_AUTHORITY, authority);
        contentValues.put(DICTIONARY_COLUMN_AUDIO, audio);
        contentValues.put(DICTIONARY_COLUMN_IMAGE, image);
        contentValues.put(DICTIONARY_COLUMN_SEMANTIC_IDS, semantic_ids);
        contentValues.put(DICTIONARY_COLUMN_CZI, czi);
        contentValues.put(DICTIONARY_COLUMN_ES_GLOSS, esGloss);
        return contentValues;
    }

    public void insertNewWord(String oid, String lang, String ipa, String gloss, String pos, String usageExample, String dialect,
                                       String metadata, String authority, String audio, String image, String semantic_ids, String czi, String esGloss) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = createContentValues(oid, lang, ipa, gloss, pos, usageExample, dialect,
                metadata, authority, audio, image, semantic_ids, czi, esGloss);
        // if it doesn't exist, insert it into the table, else replace it.
        db.insertWithOnConflict(DICTIONARY_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean updateWord(String oid, String lang, String ipa, String gloss, String pos, String usageExample, String dialect,
                             String metadata, String authority, String audio, String image, String semantic_ids, String czi, String esGloss) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = createContentValues(oid, lang, ipa, gloss, pos, usageExample, dialect,
                metadata, authority, audio, image, semantic_ids, czi, esGloss);
        db.update(DICTIONARY_TABLE_NAME, contentValues, "ID = ?", new String[] {oid});
        return true;
    }

    public Cursor getData(int oid) {
        mDB = this.getReadableDatabase();
        return mDB.rawQuery("SELECT * FROM "+DICTIONARY_TABLE_NAME+" WHERE oid ="+oid+"", null);
    }

    public StringBuilder getInformationFromOID(int oid, String queryRequest) {
        Cursor cur = getData(oid);
        cur.moveToFirst();
        StringBuilder sb = new StringBuilder();
        String tmp = cur.getString(cur.getColumnIndex(queryRequest));
        if (tmp != null) {
            sb.append(tmp);
        }
        cur.close();
        return sb;
    }

    public int getOidOfRandomRow() {
        String randomQueryString = "SELECT * FROM " + DICTIONARY_TABLE_NAME + " ORDER BY RANDOM() LIMIT 1";
        mDB = this.getReadableDatabase();

        Cursor cur = mDB.rawQuery(randomQueryString, null);
        cur.moveToFirst();
        int oidRandom;
        try {
            oidRandom = Integer.parseInt(cur.getString(cur.getColumnIndex(DICTIONARY_COLUMN_OID)));
        } catch (Exception e) {
            oidRandom = DICTIONARY_DATABASE_QUERY_ERROR;
        }
        cur.close();
        return oidRandom;
    }

    public ArrayList<Integer> getOidsForQueryMatchingString(String queryText) {
        ArrayList<Integer> oids = new ArrayList<>();
        //queryText = Arrays.toString(queryText.split(" "));
        String queryString = "SELECT * FROM " + DICTIONARY_TABLE_NAME + " WHERE " +
                DICTIONARY_COLUMN_ES_GLOSS + " LIKE " +"\'%" + queryText + "%\'" + " OR " + DICTIONARY_COLUMN_GLOSSARY
                + " LIKE " + "\'%" + queryText + "%\'" + " OR " + DICTIONARY_COLUMN_LANG + " LIKE " + "\'%" + queryText +
                "%\'";
        mDB = this.getReadableDatabase();
        Cursor cur = mDB.rawQuery(queryString, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            oids.add(Integer.parseInt(cur.getString(cur.getColumnIndex(DICTIONARY_COLUMN_OID))));
            cur.moveToNext();
        }
        cur.close();
        return oids;
    }
}

