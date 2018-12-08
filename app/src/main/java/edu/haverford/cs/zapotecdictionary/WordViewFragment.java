package edu.haverford.cs.zapotecdictionary;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
                    //TODO: get message from db (zhanpeng)
                    String audiofn = db.getInformationFromOID(oid, DBHelper.DICTIONARY_COLUMN_AUDIO).toString();
                    String audiofp = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath() + "/aud/" + audiofn;
                    mp.setDataSource(audiofp);
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        TextView word = view.findViewById(R.id.word_WordView);
        word.setText(db.getInformationFromOID(oid ,DBHelper.DICTIONARY_COLUMN_LANG).toString());
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
        Log.e("mieoid", "+++++++++ " + oid);
    }
}
