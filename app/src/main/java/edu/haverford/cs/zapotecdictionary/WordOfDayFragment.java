package edu.haverford.cs.zapotecdictionary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WordOfDayFragment extends Fragment {
    protected static DBHelper mDb;
    protected static int randomOid;

    public WordOfDayFragment() {
        super();
    }

    public void setDB(DBHelper dbHelper){
        this.mDb = dbHelper;
    }

    public void set_curID(int newID){
        this.randomOid = newID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.word_view, container, false);
        ImageButton imb = view.findViewById(R.id.searchWords_voiceE);
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mp = new MediaPlayer();
                try{
                    String audiofn = mDb.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_AUDIO).toString();
                    String audiofp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                            + "/dataFolder/tlacochahuaya_content/aud/" + audiofn;
                    mp.setDataSource(audiofp);
                    mp.prepare();
                    mp.start();
                }catch(Exception e){
                    Toast.makeText(getActivity().getApplicationContext(), "The audio file does not exist.", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
            }
        });
        TextView word = view.findViewById(R.id.word_WordView);
        TextView wordEnglishDef = view.findViewById(R.id.word_eng_def);
        TextView wordEsDef = view.findViewById(R.id.word_es_def);
        ImageView image = view.findViewById(R.id.word_pic);
        word.setText(mDb.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_LANG).toString());
        wordEnglishDef.setText(mDb.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_GLOSSARY).toString());
        wordEsDef.setText(mDb.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_ES_GLOSS).toString());

        // set picture
        String wordOfDay_pic = mDb.getInformationFromOID(randomOid, DBHelper.DICTIONARY_COLUMN_IMAGE).toString();
        if(wordOfDay_pic != null){
            String wordOfDay_pic_fp = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() + "/dataFolder/tlacochahuaya_content/pix/" + wordOfDay_pic;
            Bitmap bMap = BitmapFactory.decodeFile(wordOfDay_pic_fp);
            image.setImageBitmap(bMap);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }
    }
}