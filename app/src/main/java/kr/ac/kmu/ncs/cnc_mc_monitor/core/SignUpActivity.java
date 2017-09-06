package kr.ac.kmu.ncs.cnc_mc_monitor.core;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;

/**
 * Created by kimsiyoung on 2017-08-08.
 */

public class SignUpActivity extends AppCompatActivity {
    private Button btn_OK;
    private EditText edt_name;
    private EditText edt_ID;
    private EditText edt_passwd;
    private SignUpTask signUpTask;
    private HttpURLConnection conn;
    private String name;
    private String email;
    private String password;
    private String organization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        android.support.v7.app.ActionBar ab =getSupportActionBar();
        ab.setTitle(Constants.ORGINIZATION);

        init();
    }

    public void init() {
        this.btn_OK = (Button)findViewById(R.id.btn_OK);
        this.edt_name = (EditText)findViewById(R.id.edt_name);
        this.edt_ID = (EditText)findViewById(R.id.edt_ID);
        this.edt_passwd = (EditText)findViewById(R.id.edt_passwd);

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = new String(edt_name.getText().toString());
                email = new String(edt_ID.getText().toString());
                password = new String(edt_passwd.getText().toString());
                organization = new String(Constants.ORGINIZATION);

                signUpTask = new SignUpTask();
                signUpTask.execute();

                //로그인화면으로 돌아가기
            }
        });
    }

    class SignUpTask extends AsyncTask<String ,Integer ,Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... value) {
            Boolean result = signUp();

            if(result != true) {
                publishProgress(0);
                return null;
            }
            publishProgress(1);
            finish();

            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if(value[0] == 0)
                Toast.makeText(getApplicationContext(), "가입에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            else if(value[0] == 1)
                Toast.makeText(getApplicationContext(), "가입 승인을 요청하였습니다.",Toast.LENGTH_SHORT).show();
        }

        public Boolean signUp() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            JSONObject json = new JSONObject();
            try {
                json.put("name", name);
                json.put("email", email);
                json.put("password", hashedPassword);
                json.put("organization", organization);
            }
            catch (JSONException ex) {
                ex.printStackTrace();
                result = false;
            }

            String body = json.toString();

            try {
                URL url = new URL(urlStr + "/account/signup");
                conn = (HttpURLConnection)url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes());
                    os.flush();

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

                        is.close();
                        os.close();
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

}
