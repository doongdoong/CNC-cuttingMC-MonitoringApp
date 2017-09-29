package kr.ac.kmu.ncs.cnc_mc_monitor.core;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;

/**
 * Created by kimsiyoung on 2017-09-26.
 */
public class SettingActivity extends ActionBarActivity {
    private EditText edt_IP;
    private EditText edt_renewed_data_interval;
    private Button btn_save;
    private SharedPreferences prf;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        init();
    }

    private void init() {
        prf = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        editor = prf.edit();

        this.edt_IP = (EditText) findViewById(R.id.edt_IP);
        //this.edt_recording_interval = (EditText) findViewById(R.id.edt_recording_interval);
        this.edt_renewed_data_interval = (EditText) findViewById(R.id.edt_renewed_data_interval);
        this.btn_save = (Button) findViewById(R.id.btn_save);

        edt_IP.setText(prf.getString("IP", ""));
        edt_renewed_data_interval.setText(prf.getString("rnw_data_interval", "2"));

        this.btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 옮겨야해
                editor.putString("IP", edt_IP.getText().toString());
                editor.putString("rnw_data_interval", edt_renewed_data_interval.getText().toString());
                editor.commit();
                finish();
            }
        });
    }
}
