package edu.haverford.cs.zapotecdictionary;
import android.net.wifi.WifiManager;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsFragment extends Fragment {

    protected static Switch[] switchArr = new Switch[4];
    private CompoundButton.OnCheckedChangeListener cb;
    private CompoundButton lastClicked;


    public SettingsFragment () {
        super();
        lastClicked = null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.settings, container, false);
        switchArr[0] = view.findViewById(R.id.update_content_min_audio_pic);
        switchArr[1] = view.findViewById(R.id.update_content_only);
        switchArr[2] = view.findViewById(R.id.update_content_min_audio);
        switchArr[3] = view.findViewById(R.id.update_full);
        //switchArr[4] = view.findViewById(R.id.wifi);
        Switch wifiSwitch = view.findViewById(R.id.wifi);
        wifiSwitch.setChecked(false);
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {

            }
        });

        cb = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(lastClicked == null) {
                    lastClicked = compoundButton;
                } else {
                    lastClicked.setChecked(false);
                    lastClicked = compoundButton;
                }
//                for(int i = 0; i < switchArr.length; i++) {
//                    CheckBox s = switchArr[i];
//                    if(s.getText().toString().equals(compoundButton.getText().toString())) {
//                        //s.setChecked(true);
//                        //s.toggle();
//                        s.setChecked(b);
//
//                    } else {
//                        if(s.isChecked()) {
//                            s.setChecked(false);
//                        }
//                    }
//                }
            }
        };
        for(Switch s : switchArr) {
            s.setOnCheckedChangeListener(cb);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        SharedPreferences sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        if(sp != null) {
            for(int i = 0; i < switchArr.length; i++) {
                switchArr[i].setChecked(sp.getBoolean(Integer.toString(i), false));
            }
        }
        if(savedInstanceState != null) {
            for(int i = 0; i < switchArr.length; i++) {
                switchArr[i].setChecked(savedInstanceState.getBoolean(Integer.toString(i)));
            }
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("0", switchArr[0].isChecked());
        outState.putBoolean("1", switchArr[1].isChecked());
        outState.putBoolean("2", switchArr[2].isChecked());
        outState.putBoolean("3", switchArr[3].isChecked());
        //outState.putBoolean("4", switchArr[4].isChecked());
    }



}
