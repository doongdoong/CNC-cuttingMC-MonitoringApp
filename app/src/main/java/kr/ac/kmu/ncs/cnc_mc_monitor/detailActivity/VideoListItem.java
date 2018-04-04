package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.graphics.Bitmap;

public class VideoListItem {
    private String machineID;
    private String title;
    private Bitmap thumnail;

    private VideoListItem(){}

    public VideoListItem(String machineID, String title , Bitmap thumnail){
        this.machineID = machineID;
        this.title = title;
        this.thumnail = thumnail;
    }

    public String getMachineID() {
        return machineID;
    }

    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getThumnail() {
        return thumnail;
    }

    public void setThumnail(Bitmap thumnail) {
        this.thumnail = thumnail;
    }
}
