package kr.ac.kmu.ncs.cnc_mc_monitor.listPanel;

import android.graphics.Bitmap;

public class ListItem {
    private int machineID;
    private Bitmap thumnail;
    private long total_workload;
    private long current_workload;

    private ListItem(){}

    public ListItem(int machineID, long total_workload, long current_workload, Bitmap thumnail){
        this.machineID = machineID;
        this.thumnail = thumnail;
        this.total_workload = total_workload;
        this.current_workload = current_workload;
    }

    public void update(int machineID, long total_workload, long current_workload, Bitmap thumnail){
        this.machineID = machineID;
        this.thumnail = thumnail;
        this.total_workload = total_workload;
        this.current_workload = current_workload;
    }

    public long getTotal_Workload() {
        return total_workload;
    }

    public void setTotal_Workload(long total_workload) {
        this.total_workload = total_workload;
    }

    public long getCurrent_Workload() {
        return current_workload;
    }

    public void setCurrent_Workload(long current_workload) { this.current_workload = current_workload; }

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
