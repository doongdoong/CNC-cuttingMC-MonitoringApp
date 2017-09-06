package kr.ac.kmu.ncs.cnc_mc_monitor.listPanel;

import android.graphics.Bitmap;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class ListItem {
    private int machineID;
    private Bitmap thumnail;
    private long workload;

    private ListItem(){}

    public ListItem(int machineID, long workload , Bitmap thumnail){
        this.machineID = machineID;
        this.thumnail = thumnail;
        this.workload = workload;
    }

    public void update(int machineID, long workload ,Bitmap thumnail){
        this.machineID = machineID;
        this.thumnail = thumnail;
        this.workload = workload;
    }

    public long getWorkload() {
        return workload;
    }

    public void setWorkload(long workload) {
        this.workload = workload;
    }

    public int getMachineID() {
        return machineID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    public Bitmap getThumnail() {
        return thumnail;
    }

    public void setThumnail(Bitmap thumnail) {
        this.thumnail = thumnail;
    }
}
