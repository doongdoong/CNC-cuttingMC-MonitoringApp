package kr.ac.kmu.ncs.cnc_mc_monitor.db;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class DbUpdateService extends Service {
    private DbHelper helper;
    private HttpURLConnection conn;
    private RefreshTask refreshTask;
    private ArrayList<MachineDataSet> mListMachineDataSet;
    private Bundle bundle;
    private boolean check_temination;
    private SharedPreferences prf;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        this.helper = new DbHelper(getBaseContext());
        mListMachineDataSet = new ArrayList<MachineDataSet>();
        prf = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        refreshTask = new RefreshTask();
        refreshTask.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "DB Service가 중지되었습니다.");
        // 스레드 종료


    }

    class RefreshTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... value) {
            while(true) {
                Boolean result = request_renewed_data();

                Log.d(getClass().getSimpleName(), "roop");
                Log.d(getClass().getSimpleName(), ""+result);

                if (result != true) {
                    publishProgress();
                    broadcastIntent(result);
                    break;
                }

                broadcastIntent(result);

                try{
                    Thread.sleep(Integer.parseInt(prf.getString("rnw_data_interval", "2"))*1000);
                }
                catch (Exception e){
                    Log.d(getClass().getSimpleName(), "sleep 에러");
                }
            }
            return null;
        }

        //publishProgress() 호출 시
        protected void onProgressUpdate(Integer... value) {
            if(check_temination != true)
                Toast.makeText(getApplicationContext(), "데이터 수신에 실패하였습니다.",Toast.LENGTH_SHORT).show();
        }

        //doInBackground에서 return된 값이 넘어옴
        //protected void onPostExecute(Integer result) {
        //}

        public Boolean request_renewed_data() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;
            check_temination = true;

            try {
                URL url = new URL(urlStr + "/data/renewed_data");
                conn = (HttpURLConnection)url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("authorization", Constants.TOKEN);
                    conn.setRequestProperty("Accept", "application/json");

                    String response;
                    int responseCode = conn.getResponseCode();

                    if(responseCode == HttpURLConnection.HTTP_OK) {
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

                        JSONObject responceJSON = new JSONObject(response);

                        result = (Boolean) responceJSON.get("type");

                        //Log.d(getClass().getSimpleName(), "" + result);

                        if(result==true) {
                            String dataString = (String) responceJSON.get("data");
                            JSONArray jarray = new JSONArray(dataString);

                            for(int i=0; i < jarray.length(); i++){
                                JSONObject jObject = jarray.getJSONObject(i);

                                Constants.ID = jObject.getString("id");
                                Constants.LUBRICANT_MACHINE = jObject.getString("lubricant_machine");
                                Constants.LUBRICANT_SAW = jObject.getString("lubricant_saw");
                                Constants.PRESSURE_AIR_MAIN = jObject.getString("pressure_air_main");
                                Constants.PRESSURE_OIL_HYDRAULIC = jObject.getString("pressure_oil_hydraulic");
                                Constants.SERVO_CUT = jObject.getString("servo_cut");
                                Constants.SERVO_TRANSFER = jObject.getString("servo_transfer");
                                Constants.SPINDLE = jObject.getString("spindle");
                                Constants.SAFETY_DOOR = jObject.getString("safety_door");
                                Constants.DEPLETION = jObject.getString("depletion");
                                Constants.TOTAL_WORKLOAD = jObject.getString("total_workload");
                                Constants.CURRENT_WORKLOAD = jObject.getString("current_workload");
                                Constants.TIMESTAMP = jObject.getString("timestamp");

                                helper.insertMachine(Constants.ID, Constants.LUBRICANT_MACHINE, Constants.LUBRICANT_SAW,
                                        Constants.PRESSURE_AIR_MAIN, Constants.PRESSURE_OIL_HYDRAULIC, Constants.SERVO_CUT,
                                        Constants.SERVO_TRANSFER, Constants.SPINDLE, Constants.SAFETY_DOOR, Constants.DEPLETION,
                                        Constants.TOTAL_WORKLOAD, Constants.CURRENT_WORKLOAD, Constants.TIMESTAMP);
                            }

                            check_temination = false;

                        }
                        is.close();
                        conn.disconnect();
                    }
                }
            }
            catch(MalformedURLException ex) {
                ex.printStackTrace();
                result = false;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                result = false;
            }
            return result;
        }

        public void broadcastIntent(Boolean result) {
            Intent intent = new Intent();

            Cursor cursor = helper.getMachineCursor();

            int index = 0;

            if(mListMachineDataSet.size() > 0) {
                if(cursor != null) {
                    if(cursor.moveToFirst()) {
                        do{
                            Log.d("size", ""+mListMachineDataSet.size());

                            mListMachineDataSet.get(index++).update(Integer.parseInt((String)cursor.getString(cursor.getColumnIndex("id"))),
                                    (int) cursor.getInt(cursor.getColumnIndex("lubricant_machine")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("lubricant_saw")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("pressure_air_main")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("pressure_oil_hydraulic")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("servo_cut")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("servo_transfer")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("spindle")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("safety_door")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("depletion")) > 0,
                                    Long.parseLong((String) cursor.getString(cursor.getColumnIndex("total_workload"))),
                                    Long.parseLong((String) cursor.getString(cursor.getColumnIndex("current_workload"))),
                                    Integer.parseInt((String) cursor.getString(cursor.getColumnIndex("timestamp"))));
                        }while(cursor.moveToNext());
                    }
                }
            }
            else {
                Log.d("size==0", "사이즈영");
                if(cursor != null) {
                    if(cursor.moveToFirst()) {
                        int i=0;
                        do{
                            Log.d("size", ""+mListMachineDataSet.size());
                            mListMachineDataSet.add(new MachineDataSet(Integer.parseInt((String)cursor.getString(cursor.getColumnIndex("id"))),
                                    (int) cursor.getInt(cursor.getColumnIndex("lubricant_machine")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("lubricant_saw")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("pressure_air_main")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("pressure_oil_hydraulic")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("servo_cut")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("servo_transfer")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("spindle")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("safety_door")) > 0,
                                    (int) cursor.getInt(cursor.getColumnIndex("depletion")) > 0,
                                    Long.parseLong((String) cursor.getString(cursor.getColumnIndex("total_workload"))),
                                    Long.parseLong((String) cursor.getString(cursor.getColumnIndex("current_workload"))),
                                    Integer.parseInt((String) cursor.getString(cursor.getColumnIndex("timestamp")))));
                            Log.d("id확인", ""+mListMachineDataSet.get(i++).getId());
                        }while(cursor.moveToNext());
                    }
                }
            }

            if(result == true) {
                intent.setAction("renewed_data");
            }
            else {
                intent.setAction("failed_to_renew_data");
            }

            intent.putExtra("machineData", mListMachineDataSet);
            intent.putExtra("result", result);

            sendBroadcast(intent);
            Log.d(""+result, "broadcast");
        }
    }
}

