package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class CameraFragment extends Fragment{
    /**
     * Singleton pattern
     */
    private static CameraFragment instance;

    private CameraFragment(){}

    public static CameraFragment getInstance(){
        if(instance == null)
            instance = new CameraFragment();
        return instance;
    }

    /**
     * Fragment routine
     */
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.camera_fragment, container, false);

        return view;
    }
}
