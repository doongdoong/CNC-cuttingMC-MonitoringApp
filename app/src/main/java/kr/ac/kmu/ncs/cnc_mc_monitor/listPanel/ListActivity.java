package kr.ac.kmu.ncs.cnc_mc_monitor.listPanel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.DbHelper;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.DbUpdateService;
import kr.ac.kmu.ncs.cnc_mc_monitor.db.MachineDataSet;
import kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity.DetailActivity;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class  ListActivity  extends Activity {

    private DbHelper helper;
    private Cursor cursor;
    private ArrayList<ListItem> mListItems;
    private ListView lvMachine;
    private int current;
    private int total;
    private IntentFilter filter;
    private ListAdapter listAdapter;
    private ArrayList<MachineDataSet> mListMachineDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        View parentLayout = findViewById(android.R.id.content);
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("a hh:mm");
        Snackbar.make(parentLayout, "마지막 접속 시간 " + date.format(Constants.TIME_LASTSEXION) + " " + time.format(Constants.TIME_LASTSEXION), Snackbar.LENGTH_LONG).show();

        init();
    }

    @Override
    public void onBackPressed() {
        Log.d("onBackPressed", "실행이 되나니뇨?");
        Constants.TOKEN = "";
        stopService(new Intent(this, DbUpdateService.class));
        unregisterReceiver(receiver);
        finish();
    }

    private void init(){
        filter = new IntentFilter();
        filter.addAction("renewed_data");
        filter.addAction("failed_to_renew_data");
        registerReceiver(receiver, filter);

        startService(new Intent(this, DbUpdateService.class));

        this.helper = new DbHelper(getBaseContext());
        mListItems = new ArrayList<ListItem>();

        this.lvMachine = (ListView)findViewById(R.id.lv_machine);
        listAdapter = new ListAdapter();
        this.lvMachine.setAdapter(listAdapter);
        this.lvMachine.setOnItemClickListener(new ListViewItemClicked());
    }

    protected void db_update() {
        if(mListItems.size() > 0) {
            for(int i = 0 ; i < mListMachineDataSet.size() ; i++) {
                if(mListMachineDataSet.get(i).getLubricant_machine() == false || mListMachineDataSet.get(i).getLubricant_saw() == false || mListMachineDataSet.get(i).getPressure_air_main() == false || mListMachineDataSet.get(i).getPressure_oil_hydraulic() == false
                        || mListMachineDataSet.get(i).getServo_cut() == false || mListMachineDataSet.get(i).getServo_transfer() == false || mListMachineDataSet.get(i).getSpindle() == false || mListMachineDataSet.get(i).getSafety_door() == false
                        || mListMachineDataSet.get(i).getDepletion() == false) {
                    mListItems.get(i).update(mListMachineDataSet.get(i).getId(), mListMachineDataSet.get(i).getTotal_Workload(), mListMachineDataSet.get(i).getCurrent_Workload(), Constants.drawableToBitmap(getResources(), R.drawable.ic_wb_cloudy_black_24dp));
                }
                else {
                    mListItems.get(i).update(mListMachineDataSet.get(i).getId(), mListMachineDataSet.get(i).getTotal_Workload(), mListMachineDataSet.get(i).getCurrent_Workload(), Constants.drawableToBitmap(getResources(), R.drawable.ic_wb_sunny_black_24dp));
                }
            }
        }


        else {
            for(int i = 0 ; i < mListMachineDataSet.size() ; i++) {
                if(mListMachineDataSet.get(i).getLubricant_machine() == false || mListMachineDataSet.get(i).getLubricant_saw() == false || mListMachineDataSet.get(i).getPressure_air_main() == false || mListMachineDataSet.get(i).getPressure_oil_hydraulic() == false
                        || mListMachineDataSet.get(i).getServo_cut() == false || mListMachineDataSet.get(i).getServo_transfer() == false || mListMachineDataSet.get(i).getSpindle() == false || mListMachineDataSet.get(i).getSafety_door() == false
                        || mListMachineDataSet.get(i).getDepletion() == false) {
                    mListItems.add(new ListItem(mListMachineDataSet.get(i).getId(), mListMachineDataSet.get(i).getTotal_Workload(), mListMachineDataSet.get(i).getCurrent_Workload(), Constants.drawableToBitmap(getResources(), R.drawable.ic_wb_cloudy_black_24dp)));
                }
                else {
                    mListItems.add(new ListItem(mListMachineDataSet.get(i).getId(), mListMachineDataSet.get(i).getTotal_Workload(), mListMachineDataSet.get(i).getCurrent_Workload(), Constants.drawableToBitmap(getResources(), R.drawable.ic_wb_sunny_black_24dp)));
                }
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Set adapter to make dynamical ListView
     */

    class ListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Call back method by ListView
         * @param position : Item index on the ListView
         * @param view : ListView item
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ListItem inflatedItem = mListItems.get(position);

            try{
                if(inflatedItem == null)
                    throw new NullPointerException();

                if(view == null){
                    LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item, parent, false);
                }

                // init element
                ImageView imgThumnail = (ImageView)view.findViewById(R.id.img_thumnail);
                TextView tvTitle = (TextView)view.findViewById(R.id.tv_title);
                ProgressBar pbarWorkload = (ProgressBar)view.findViewById(R.id.pbar_workload);
                TextView tvWorkload = (TextView)view.findViewById(R.id.tv_workload);

                // set to an element
                imgThumnail.setImageBitmap(inflatedItem.getThumnail());
                tvTitle.setText("#" + inflatedItem.getMachineID());

                current = (int) (inflatedItem.getCurrent_Workload());
                total = (int) (inflatedItem.getTotal_Workload());
                double d_current = (inflatedItem.getCurrent_Workload());
                double d_total = (inflatedItem.getTotal_Workload());

                tvWorkload.setText(current + "/" + total);

                try
                {
                    double progress = (d_current/d_total)*100;
                    pbarWorkload.setVisibility(pbarWorkload.VISIBLE);
                    pbarWorkload.setProgress((int)progress);
                }
                catch (Exception e)
                {
                    Log.d("progressbar", "out of range");
                    pbarWorkload.setProgress(100);
                }


            } catch (NullPointerException e){
                return null;
            }

            return view;
        }
    }

    class ListViewItemClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListItem selectedItem = mListItems.get(position);

            Intent intent = new Intent(getBaseContext(), DetailActivity.class);
            intent.putExtra(Constants.INTENT_KEY_MACHINE_ID, "" + selectedItem.getMachineID());

            startActivity(intent);
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(true == intent.getBooleanExtra("result", false)) {
                Log.d("리시버", "리시버");
                mListMachineDataSet = (ArrayList<MachineDataSet>) intent.getSerializableExtra("machineData");
                db_update();
            }

            else {
                onBackPressed();
            }
        }
    };
}
