package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class DetailActivity extends FragmentActivity {

    private String machineID;
    private List<Fragment> mFragmentList;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        getDataFromIntent();
        setDataToIntent();

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getDataFromIntent(){
        Intent intent = getIntent();
        machineID = intent.getStringExtra(Constants.INTENT_KEY_MACHINE_ID);
        Log.d("머신아이디//////////////////", machineID);
    }

    private void setDataToIntent(){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.INTENT_KEY_MACHINE_ID, machineID);
        CameraFragment.getInstance().setArguments(bundle);
    }

    //onBackPressed 구현


    private void init(){
        this.mFragmentList = new ArrayList<>();
        this.mFragmentList.add(OverviewFragment.getInstance());
        this.mFragmentList.add(CameraFragment.getInstance());

        this.mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        this.mViewPager = (ViewPager)findViewById(R.id.pager);
        this.mViewPager.setAdapter(mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        });
       changeActionBarToTabBar();
    }

    private void changeActionBarToTabBar(){
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText("Overview").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Camera").setTabListener(tabListener));
        actionBar.setDisplayShowTitleEnabled(false);
    }

    class PagerAdapter extends FragmentStatePagerAdapter{
        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }
}
