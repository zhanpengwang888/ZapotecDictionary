package edu.haverford.cs.zapotecdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            onViewStateRestored(savedInstanceState);
        }
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.about_page, container, false);
        //TextView documentation = view.findViewById(R.id.documentation);
        return view;
    }
}
