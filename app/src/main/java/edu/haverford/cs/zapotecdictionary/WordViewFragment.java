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

public class WordViewFragment extends Fragment {

    protected DBHelper db;
    protected static int oid;

    public WordViewFragment() {
        super();
    }

    public void setDB(DBHelper db) {
        this.db = db;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        return inflater.inflate(R.layout.word_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }

        ImageButton imb = getActivity().findViewById(R.id.searchWords_voiceE);
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
    }

    public void set_curId(int newId) {
        oid = newId;
        Log.e("mieoid", "+++++++++ " + oid);
    }
}
