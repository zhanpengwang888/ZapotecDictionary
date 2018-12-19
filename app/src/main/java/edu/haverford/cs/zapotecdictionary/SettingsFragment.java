package edu.haverford.cs.zapotecdictionary;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    protected static Switch[] switchArr = new Switch[4];
    protected static Switch wifiSwitch;
    private CompoundButton.OnCheckedChangeListener cb;
    private CompoundButton lastClicked;
    protected static boolean wifi_only = true;
    protected static MainActivity mainActivity;


    public SettingsFragment () {
        super();
        lastClicked = null;
    }

    public void setmActivity(MainActivity mActivity) {
        mainActivity = mActivity;
    }

    public boolean getWifiOnly() {
        return wifi_only;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.settings, container, false);
        switchArr[0] = view.findViewById(R.id.update_content_min_audio_pic);
        switchArr[1] = view.findViewById(R.id.update_content_only);
        switchArr[2] = view.findViewById(R.id.update_content_min_audio);
        switchArr[3] = view.findViewById(R.id.update_full);
        //switchArr[4] = view.findViewById(R.id.wifi);
        wifiSwitch = view.findViewById(R.id.wifi);
        wifiSwitch.setChecked(true);
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == false && wifi_only == true) {
                    Toast.makeText(mainActivity, "Please restart the app for downloading data. ", Toast.LENGTH_LONG*3).show();
                } else {
                    Toast.makeText(mainActivity, "For future update, please turn off 'wifi-only' in 'Settings' and use mobile data, or connect wifi for downloading", Toast.LENGTH_LONG*3).show();
                }
                wifi_only = b;
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
        wifiSwitch.setChecked(sp.getBoolean("wifi", true));
        if(sp != null) {
            for(int i = 0; i < switchArr.length; i++) {
                switchArr[i].setChecked(sp.getBoolean("#"+Integer.toString(i), false));
            }
        }
        if(savedInstanceState != null) {
            for(int i = 0; i < switchArr.length; i++) {
                switchArr[i].setChecked(savedInstanceState.getBoolean(Integer.toString(i)));
            }
            wifiSwitch.setChecked(savedInstanceState.getBoolean("wifi"));
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
        outState.putBoolean("wifi", wifiSwitch.isChecked());
        //outState.putBoolean("4", switchArr[4].isChecked());
    }



}
