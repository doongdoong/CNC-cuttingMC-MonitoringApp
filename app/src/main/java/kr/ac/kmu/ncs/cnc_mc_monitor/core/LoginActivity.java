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
import android.widget.CheckBox;
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
import java.util.Date;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.listPanel.ListActivity;

/**
 * Created by kimsiyoung on 2017-08-07.
 */
public class LoginActivity extends AppCompatActivity {
    private Button btn_login;
    private Button btn_signin;
    private Button btn_findPasswd;
    private CheckBox cbx_autologin;
    private EditText edt_ID;
    private EditText edt_passwd;
    private LoginTask loginTask;
    private HttpURLConnection conn;
    private String id;
    private String password;
    private String lastConnectUnixTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        android.support.v7.app.ActionBar ab =getSupportActionBar();
        ab.setTitle(Constants.ORGINIZATION);
        ab.setDisplayShowHomeEnabled(true);

        init();
    }

    public void init() {
        this.btn_login = (Button)findViewById(R.id.btn_login);
        this.btn_signin = (Button)findViewById(R.id.btn_signin);
        this.btn_findPasswd = (Button)findViewById(R.id.btn_findPasswd);
        this.edt_ID = (EditText)findViewById(R.id.edt_ID);
        this.edt_passwd = (EditText)findViewById(R.id.edt_passwd);
        this.cbx_autologin = (CheckBox)findViewById(R.id.cbx_autologin);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_login) {
            id = new String(edt_ID.getText().toString());
            password = new String(edt_passwd.getText().toString());

            v.setEnabled(false);

            loginTask = new LoginTask();
            loginTask.execute();
        }
        else if(v.getId() == R.id.btn_signin) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if(v.getId() == R.id.btn_findPasswd) {
            Intent intent = new Intent(getApplicationContext(), FindPasswdActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    class LoginTask extends AsyncTask<String ,Integer ,Integer> {
        ProgressDialog asyncDialog = new ProgressDialog(LoginActivity.this);

        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로그인중 입니다.");
            asyncDialog.show();
        }

        @Override
        protected Integer doInBackground(String... value) {
            Boolean result = cert();

            if(result != true) {
                publishProgress(0);
                Log.d(this.getClass().getSimpleName(), "/cert 에러");
                return null;
            }

            result = validate();

            if(result != true) {
                publishProgress(0);
                Log.d(this.getClass().getSimpleName(), "/validate 에러");
                return null;
            }

            result = fcm_compare();

            if(result != true) {
                result = fcm_regist();
                Log.d("regist", ""+result);
                fcm_compare();

                if(result != true) {
                    publishProgress(0);
                    Log.d(this.getClass().getSimpleName(), "/fcm_compare 에러");
                    return null;
                }
            }

            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            publishProgress(1);
            startActivity(intent);

            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if(value[0]==0)
                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
            asyncDialog.dismiss();
            btn_login.setEnabled(true);
        }

        public Boolean cert() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/account/cert");
                conn = (HttpURLConnection)url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("email", id);
                    json.put("password", password);
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                    result = false;
                }

                String body = json.toString();

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

                        result = (Boolean)responseJSON.get("type");

                        if(result == true)
                            Constants.TOKEN = (String)responseJSON.get("token");

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

        public Boolean validate() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/account/validate");
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

                        JSONObject responseJSON = new JSONObject(response);

                        result = (Boolean)responseJSON.get("type");

                        if(result==true) {
                            lastConnectUnixTime = "" + responseJSON.get("timestamp");
                            Constants.TIME_LASTSEXION = new Date(Long.parseLong(lastConnectUnixTime) * 1000L);
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

        Boolean fcm_compare() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/fcm/compare");
                conn = (HttpURLConnection)url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    SharedPreferences fcm = getSharedPreferences("fcm_token", MODE_PRIVATE);
                    String fcm_token = fcm.getString("fcm_token", "");
                    Log.d("토큰", fcm_token);

                    json.put("email", id);
                    json.put("fcm", fcm_token);
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                    result = false;
                }

                String body = json.toString();

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("authorization", Constants.TOKEN);
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

                        result = (Boolean)responseJSON.get("type");

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

        Boolean fcm_regist() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/fcm/register");
                conn = (HttpURLConnection)url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    SharedPreferences fcm = getSharedPreferences("fcm_token", MODE_PRIVATE);
                    String fcm_token = fcm.getString("fcm_token", "");
                    Log.d("regist토큰", fcm_token);

                    json.put("email", id);
                    json.put("fcm", fcm_token);
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                    result = false;
                }

                String body = json.toString();

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("authorization", Constants.TOKEN);
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

                        result = (Boolean)responseJSON.get("type");

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
}
