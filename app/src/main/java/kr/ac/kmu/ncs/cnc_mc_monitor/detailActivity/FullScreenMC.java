package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;

/**
 * Created by kimsiyoung on 2018-01-03.
 */

public class FullScreenMC  extends MediaController{
    private ImageButton fullScreen;
    private String isFullScreen;

    public FullScreenMC(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton (super.getContext());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 30;
        addView(fullScreen, params);

        //fullscreen indicator from intent
        isFullScreen =  ((Activity)getContext()).getIntent().
                getStringExtra("fullScreenInd");

        if("y".equals(isFullScreen)){
            fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
        }else{
            fullScreen.setImageResource(R.drawable.ic_fullscreen);
        }

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();   

                if("y".equals(isFullScreen)){
                    bundle.putString("fullScreenInd", "");
                }else{
                    bundle.putString("fullScreenInd", "y");
                }
                CameraFragment.getInstance().setArguments(bundle);
            }
        });
    }
}
