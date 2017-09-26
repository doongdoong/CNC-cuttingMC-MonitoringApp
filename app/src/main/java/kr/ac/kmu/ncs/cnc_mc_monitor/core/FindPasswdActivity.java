package kr.ac.kmu.ncs.cnc_mc_monitor.core;

import android.app.ProgressDialog;
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
public class FindPasswdActivity extends AppCompatActivity {
    private Button btn_reset;
    private EditText edt_organization;
    private EditText edt_name;
    private EditText edt_ID;
    private EditText edt_password;
    private String organization;
    private String name;
    private String ID;
    private String password;
    private FindPasswdTask findPasswdTask;
    private HttpURLConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findpasswd_activity);

        android.support.v7.app.ActionBar ab =getSupportActionBar();
        ab.setTitle(Constants.ORGINIZATION);

        init();
    }

    public void init() {
        this.btn_reset = (Button)findViewById(R.id.btn_reset);
        this.edt_name = (EditText)findViewById(R.id.edt_name);
        this.edt_organization = (EditText)findViewById(R.id.edt_organization);
        this.edt_ID = (EditText)findViewById(R.id.edt_ID);
        this.edt_password = (EditText)findViewById(R.id.edt_password);

        edt_organization.setText(Constants.ORGINIZATION);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = new String(edt_name.getText().toString());
                ID = new String(edt_ID.getText().toString());
                password = new String(edt_password.getText().toString());
                organization = new String(edt_organization.getText().toString());


                findPasswdTask = new FindPasswdTask();
                findPasswdTask.execute();

                btn_reset.setEnabled(false);
            }
        });
    }

    class FindPasswdTask extends AsyncTask<String ,Integer ,Integer> {
        ProgressDialog asyncDialog = new ProgressDialog(FindPasswdActivity.this);

        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("요청중 입니다.");
            asyncDialog.show();
        }

        @Override
        protected Integer doInBackground(String... value) {
            Boolean result = reset();

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
                Toast.makeText(getApplicationContext(), "비밀번호를 변경하지 못했습니다.",Toast.LENGTH_SHORT).show();
            else if(value[0] == 1)
                Toast.makeText(getApplicationContext(), "비밀번호를 변경하였습니다.",Toast.LENGTH_SHORT).show();
            asyncDialog.dismiss();
            btn_reset.setEnabled(true);
        }

        public Boolean reset() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            //String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            JSONObject json = new JSONObject();
            try {
                json.put("name", name);
                json.put("email", ID);
                json.put("password", password);
                json.put("organization", organization);
            }
            catch (JSONException ex) {
                ex.printStackTrace();
                result = false;
            }

            String body = json.toString();

            try {
                URL url = new URL(urlStr + "/account/reset");
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
