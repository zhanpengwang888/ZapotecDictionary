package edu.haverford.cs.zapotecdictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ZapotecDictionaryDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "ZapotecDictionaryDBHelper";
    private static final String DATABASE_NAME = "zapotecDictionaryDB.db";
    private static String DATABASE_PATH = "";
    private static final String DICTIONARY_TABLE_NAME = "dictionary";
    private static final String DICTIONARY_COLUMN_OID = "oid";
    private static final String DICTIONARY_COLUMN_LANG = "lang";
    private static final String DICTIONARY_COLUMN_IPA = "ipa";
    private static final String DICTIONARY_COLUMN_GLOSSARY = "gloss";
    private static final String DICTIONARY_COLUMN_POS = "pos";
    private static final String DICTIONARY_COLUMN_USAGE_EXAMPLE = "usage_example";
    private static final String DICTIONARY_COLUMN_DIALECT = "dialect";
    private static final String DICTIONARY_COLUMN_METADATA = "metadata";
    private static final String DICTIONARY_COLUMN_AUTHORITY = "authority";
    private static final String DICTIONARY_COLUMN_AUDIO = "audio";
    private static final String DICTIONARY_COLUMN_IMAGE = "image";
    private static final String DICTIONARY_COLUMN_SEMANTIC_IDS = "semantic_ids";
    private static final String DICTIONARY_COLUMN_CZI = "czi";
    private static final String DICTIONARY_COLUMN_ES_GLOSS = "es_gloss";
    private final Context mContext;
    private SQLiteDatabase mDB;

    public ZapotecDictionaryDBHelper(Context context) {
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

    public String getInformationFromOID(int oid, String queryRequest) {
        Cursor cur = getData(oid);
        cur.moveToFirst();
        StringBuilder sb = new StringBuilder();
        sb.append(cur.getString(cur.getColumnIndex(queryRequest)));
        cur.close();
        return sb.toString();
    }

}

