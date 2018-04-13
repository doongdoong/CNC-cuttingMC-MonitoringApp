package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.LoginActivity;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.DbHelper;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.MachineDataSet;

import static android.content.Context.MODE_PRIVATE;

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
    private String total_workload;
    private String current_workload;

    private int current;
    private int total;

    public OverviewFragment(){  //private?
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
    private TextView tv_emission_barrel;
    private TextView tv_yield_saw;
    private TextView tv_workload;
    private TextView tv_timestamp;
    private Button btn_poweroff;


    private SharedPreferences prf;
    private HttpURLConnection conn;
    private PowerOffTask powerOffTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        filter = new IntentFilter();
        filter.addAction("renewed_data");
        filter.addAction("failed_to_renew_data");
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
        tv_emission_barrel = (TextView) view.findViewById(R.id.tv_emission_barrel);
        tv_yield_saw = (TextView) view.findViewById(R.id.tv_yield_saw);
        tv_workload = (TextView) view.findViewById(R.id.tv_workload);
        tv_timestamp = (TextView) view.findViewById(R.id.tv_timestamp);
        btn_poweroff = (Button) view.findViewById(R.id.btn_poweroff);

        prf = getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);

        btn_poweroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.btn_poweroff) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    // 제목셋팅
                    alertDialogBuilder.setTitle("장비 전원 종료");

                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("장비 전원을 종료하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("종료",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            // 장비 전원을 종료한다
                                            powerOffTask = new PowerOffTask();
                                            powerOffTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                        }
                                    })
                            .setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            // 다이얼로그를 취소한다
                                            dialog.cancel();
                                        }
                                    });

                    // 다이얼로그 생성
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // 다이얼로그 보여주기
                    alertDialog.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    public void onBackPressed() {
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

                if(mListMachineDataSet.get(i).getLubricant_machine() == true) {
                    tv_lubricant_machine.setText("장비 이상");
                    tv_lubricant_machine.setTextColor(Color.RED);
                }
                else {
                    tv_lubricant_machine.setText("정상 작동");
                    tv_lubricant_machine.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getLubricant_saw() == true) {
                    tv_lubricant_saw.setText("장비 이상");
                    tv_lubricant_saw.setTextColor(Color.RED);
                }
                else {
                    tv_lubricant_saw.setText("정상 작동");
                    tv_lubricant_saw.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getPressure_air_main() == true) {
                    tv_pressure_air_main.setText("장비 이상");
                    tv_pressure_air_main.setTextColor(Color.RED);
                }
                else {
                    tv_pressure_air_main.setText("정상 작동");
                    tv_pressure_air_main.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getPressure_oil_hydraulic() == true) {
                    tv_pressure_oil_hydraulic.setText("장비 이상");
                    tv_pressure_oil_hydraulic.setTextColor(Color.RED);
                }
                else {
                    tv_pressure_oil_hydraulic.setText("정상 작동");
                    tv_pressure_oil_hydraulic.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getServo_cut() == true) {
                    tv_servo_cut.setText("장비 이상");
                    tv_servo_cut.setTextColor(Color.RED);
                }
                else {
                    tv_servo_cut.setText("정상 작동");
                    tv_servo_cut.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getServo_transfer() == true) {
                    tv_servo_transfer.setText("장비 이상");
                    tv_servo_transfer.setTextColor(Color.RED);
                }
                else {
                    tv_servo_transfer.setText("정상 작동");
                    tv_servo_transfer.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getSpindle() == true) {
                    tv_spindle.setText("장비 이상");
                    tv_spindle.setTextColor(Color.RED);
                }
                else {
                    tv_spindle.setText("정상 작동");
                    tv_spindle.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getSafety_door() == true) {
                    tv_safety_door.setText("장비 이상");
                    tv_safety_door.setTextColor(Color.RED);
                }
                else {
                    tv_safety_door.setText("정상 작동");
                    tv_safety_door.setTextColor(Color.parseColor("#000000"));
                }

                if(mListMachineDataSet.get(i).getDepletion() == true) {
                    tv_depletion.setText("장비 이상");
                    tv_depletion.setTextColor(Color.RED);
                }
                else {
                    tv_depletion.setText("정상 작동");
                    tv_depletion.setTextColor(Color.parseColor("#000000"));
                }

                long emission_barrel = mListMachineDataSet.get(i).getEmission_Barrel();
                tv_emission_barrel.setText(emission_barrel+"");

                long yield_saw = mListMachineDataSet.get(i).getYield_Saw();
                tv_yield_saw.setText(yield_saw+"");


                current = (int) (mListMachineDataSet.get(i).getCurrent_Workload());
                total = (int) (mListMachineDataSet.get(i).getTotal_Workload());

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

    class PowerOffTask extends AsyncTask<String, Integer, Integer> {
        protected void onPreExecute() {
        }


        @Override
        protected Integer doInBackground(String... value) {
            Boolean result = poweroff();

            if (result != true) {
                publishProgress(0);
                Log.d(this.getClass().getSimpleName(), "/poweroff 에러");
                return null;
            }

            publishProgress(1);

            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if(value[0]==0)
                Toast.makeText(getContext(), "장비 종료에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "장비를 종료하였습니다.", Toast.LENGTH_SHORT).show();
        }

        public Boolean poweroff() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = prf.getString("IP", "");
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/data/onoff");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("id", machineID);
                    json.put("onoff", 0);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    result = false;
                }

                String body = json.toString();

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("PUT");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("authorization", Constants.TOKEN);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes());
                    os.flush();

                    String response;
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();

                        response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);

                        result = (Boolean) responseJSON.get("type");

                        Log.d("onoff", result + "");


                        is.close();
                        conn.disconnect();
                    }
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                result = false;
            } catch (Exception ex) {
                ex.printStackTrace();
                result = false;
            }

            return result;
        }
    }
}
