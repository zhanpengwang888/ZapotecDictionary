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

public class WordViewFragment extends Fragment {

    protected static DBHelper db;
    protected static int oid;

    public WordViewFragment() {
        super();
    }

    public void setDB(DBHelper db) {
        this.db = db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.word_view, container, false);
        ImageButton imb = view.findViewById(R.id.searchWords_voiceE);
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mp = new MediaPlayer();
                try {
                    String audiofn = db.getInformationFromOID(oid, DBHelper.DICTIONARY_COLUMN_AUDIO).toString();
                    String audiofp = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath() + "/dataFolder/tlacochahuaya_content/aud/" + audiofn;
                    mp.setDataSource(audiofp);
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        TextView word = view.findViewById(R.id.word_WordView);
        TextView wordEngDef = view.findViewById(R.id.word_eng_def);
        TextView wordEsDef = view.findViewById(R.id.word_es_def);
        ImageView img = view.findViewById(R.id.word_pic);
        word.setText(db.getInformationFromOID(oid, DBHelper.DICTIONARY_COLUMN_LANG).toString());
        wordEngDef.setText(db.getInformationFromOID(oid, DBHelper.DICTIONARY_COLUMN_GLOSSARY).insert(0, "English: ").toString());
        wordEsDef.setText(db.getInformationFromOID(oid, DBHelper.DICTIONARY_COLUMN_ES_GLOSS).insert(0, "Spanish: ").toString());

        // set picture
        String pic = db.getInformationFromOID(oid, DBHelper.DICTIONARY_COLUMN_IMAGE).toString();
        if(pic != null) {
            String pic_fp = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() + "/dataFolder/tlacochahuaya_content/pix/" + pic;

            Bitmap bMap = BitmapFactory.decodeFile(pic_fp);
            img.setImageBitmap(bMap);
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

    public void set_curId(int newId) {
        oid = newId;
    }
}
