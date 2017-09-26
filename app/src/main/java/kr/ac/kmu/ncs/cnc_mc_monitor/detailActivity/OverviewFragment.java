package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.DbHelper;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.MachineDataSet;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class OverviewFragment extends Fragment {
    /**
     * Singleton Pattern
     */
    private static OverviewFragment instance;

    private String machineID;

    private String ID;
    private String lubricant_machine;
    private String lubricant_saw;
    private String pressure_air_main;
    private String pressure_oil_hydraulic;
    private String servo_cut;
    private String servo_transfer;
    private String spindle;
    private String safety_door;
    private String depletion;
    private String workload;
    private int current;
    private int total;

    private OverviewFragment(){
    }

    public static OverviewFragment getInstance(){
        if(instance == null)
            instance = new OverviewFragment();
        return instance;
    }

    /**
     * Fragment routine
     */
    private DbHelper helper;
    private IntentFilter filter;
    private ArrayList<MachineDataSet> mListMachineDataSet;

    private View view;
    private TextView tv_ID;
    private TextView tv_lubricant_machine;
    private TextView tv_lubricant_saw;
    private TextView tv_pressure_air_main;
    private TextView tv_pressure_oil_hydraulic;
    private TextView tv_servo_cut;
    private TextView tv_servo_transfer;
    private TextView tv_spindle;
    private TextView tv_safety_door;
    private TextView tv_depletion;
    private TextView tv_workload;
    private TextView tv_timestamp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        filter = new IntentFilter();
        filter.addAction("renewed_data");
        filter.addAction("failed_to_renew_data");
        getActivity().registerReceiver(receiver, filter);
        getDataFromIntent();

        helper = new DbHelper(getContext());

        view = inflater.inflate(R.layout.overview_fragment, container, false);

        tv_ID = (TextView) view.findViewById(R.id.tv_ID);
        tv_lubricant_machine = (TextView) view.findViewById(R.id.tv_lubricant_machine);
        tv_lubricant_saw = (TextView) view.findViewById(R.id.tv_lubricant_saw);
        tv_pressure_air_main = (TextView) view.findViewById(R.id.tv_pressure_air_main);
        tv_pressure_oil_hydraulic = (TextView) view.findViewById(R.id.tv_pressure_oil_hydraulic);
        tv_servo_cut = (TextView) view.findViewById(R.id.tv_servo_cut);
        tv_servo_transfer = (TextView) view.findViewById(R.id.tv_servo_transfer);
        tv_spindle = (TextView) view.findViewById(R.id.tv_spindle);
        tv_safety_door = (TextView) view.findViewById(R.id.tv_safety_door);
        tv_depletion = (TextView) view.findViewById(R.id.tv_depletion);
        tv_workload = (TextView) view.findViewById(R.id.tv_workload);
        tv_timestamp = (TextView) view.findViewById(R.id.tv_timestamp);

        return view;
    }

    public void onBackPressed() {
        getActivity().unregisterReceiver(receiver);
        super.getActivity().onBackPressed();
    }

    private void getDataFromIntent(){
        Intent intent = getActivity().getIntent();
        machineID = intent.getStringExtra(Constants.INTENT_KEY_MACHINE_ID);
    }

    protected void renew_data() {
        for(int i = 0 ; i < mListMachineDataSet.size() ; i++) {
            if(Integer.toString(mListMachineDataSet.get(i).getId()).equals(machineID)) {
                tv_ID.setText("" + mListMachineDataSet.get(i).getId());
                tv_lubricant_machine.setText("" + mListMachineDataSet.get(i).getLubricant_machine());
                tv_lubricant_saw.setText("" + mListMachineDataSet.get(i).getLubricant_saw());
                tv_pressure_air_main.setText("" + mListMachineDataSet.get(i).getPressure_air_main());
                tv_pressure_oil_hydraulic.setText("" + mListMachineDataSet.get(i).getPressure_oil_hydraulic());
                tv_servo_cut.setText("" + mListMachineDataSet.get(i).getServo_cut());
                tv_servo_transfer.setText("" + mListMachineDataSet.get(i).getServo_transfer());
                tv_spindle.setText("" + mListMachineDataSet.get(i).getSpindle());
                tv_safety_door.setText("" + mListMachineDataSet.get(i).getSafety_door());
                tv_depletion.setText("" + mListMachineDataSet.get(i).getDepletion());

                current = (int) (mListMachineDataSet.get(i).getWorkload() & 0b1111111111111111);
                total = (int) (mListMachineDataSet.get(i).getWorkload() >> 16);

                tv_workload.setText(current + "/" + total);

                long timestamp = mListMachineDataSet.get(i).getTimestamp();

                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat time = new SimpleDateFormat("a hh:mm:ss");

                tv_timestamp.setText(date.format(timestamp*1000L) + " " + time.format(timestamp*1000L));

                Log.d("데이터 확인", "" + mListMachineDataSet.get(i).getId());
                Log.d("데이터 확인", "" + mListMachineDataSet.get(i).getLubricant_machine());
                Log.d("데이터 확인", "" + mListMachineDataSet.get(i).getPressure_air_main());
                Log.d("데이터 확인", current + "/" + total);
            }
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(true == intent.getBooleanExtra("result", false)) {
                Log.d("리시버", "리시버");
                mListMachineDataSet = (ArrayList<MachineDataSet>) intent.getSerializableExtra("machineData");
                renew_data();
            }

            else {
                onBackPressed();
            }
        }
    };
}
