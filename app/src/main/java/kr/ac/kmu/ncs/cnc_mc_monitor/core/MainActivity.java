package kr.ac.kmu.ncs.cnc_mc_monitor.core;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;

public class MainActivity extends AppCompatActivity {
    private EditText edtAddress;
    private Button btnConnect;
    private HTTPConnectionTask task;
    private HttpURLConnection conn;
    private String fcm_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prf = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        fcm_token = prf.getString("fcm_token", "");

        Log.d("fcm_token", fcm_token);

        init();
    }


    class HTTPConnectionTask extends AsyncTask<String ,Integer ,Integer> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("서버에 연결중입니다.");
            asyncDialog.show();
        }

        @Override
        protected Integer doInBackground(String... value) {
            Boolean result = request();

            if(result != true) {
                publishProgress(0);
                return null;
            }

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            publishProgress(1);
            startActivity(intent);

            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if(value[0]==0)
                Toast.makeText(getApplicationContext(), "네트워크 연결을 확인하세요.",Toast.LENGTH_SHORT).show();
            asyncDialog.dismiss();
            btnConnect.setEnabled(true);
        }

        public Boolean request() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/establish");
                conn = (HttpURLConnection)url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

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

                        JSONObject responseJSON = new JSONObject(response);

                        result = (Boolean) responseJSON.get("type");

                        if(result==true) {
                            Constants.ORGINIZATION = (String) responseJSON.get("data");
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
    }
    private void init(){
                this.edtAddress = (EditText)findViewById(R.id.edt_address);
                this.btnConnect = (Button)findViewById(R.id.btn_connect);
                SharedPreferences prf = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
                this.edtAddress.setText(prf.getString("IP", ""));

                this.btnConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if((edtAddress.getText().toString()).equals("")) {
                            Toast.makeText(getApplicationContext(), "서버의 주소를 입력하세요",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Constants.SERVERADDRESS = new String(edtAddress.getText().toString());

                        task = new HTTPConnectionTask();
                        task.execute();

                        btnConnect.setEnabled(false);
                    }});
        btnConnect.callOnClick();
    }
}