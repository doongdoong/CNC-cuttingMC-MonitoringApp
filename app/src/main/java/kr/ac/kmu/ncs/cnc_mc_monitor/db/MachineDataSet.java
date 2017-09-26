package kr.ac.kmu.ncs.cnc_mc_monitor.db;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class MachineDataSet implements Serializable{
    private int id;
    private boolean lubricant_machine;
    private boolean lubricant_saw;
    private boolean pressure_air_main;
    private boolean pressure_oil_hydraulic;
    private boolean servo_cut;
    private boolean servo_transfer;
    private boolean spindle;
    private boolean safety_door;
    private boolean depletion;
    private long workload;
    private long timestamp;

    private MachineDataSet(){}

    public MachineDataSet(int id, boolean lubricant_machine, boolean lubricant_saw, boolean pressure_air_main, boolean pressure_oil_hydraulic, boolean servo_cut, boolean servo_transfer, boolean spindle, boolean safety_door, boolean depletion, long workload, int timestamp) {
        this.id = id;
        this.lubricant_machine = lubricant_machine;
        this.lubricant_saw = lubricant_saw;
        this.pressure_air_main = pressure_air_main;
        this.pressure_oil_hydraulic = pressure_oil_hydraulic;
        this.servo_cut = servo_cut;
        this.servo_transfer = servo_transfer;
        this.spindle = spindle;
        this.safety_door = safety_door;
        this.depletion = depletion;
        this.workload = workload;
        this.timestamp = timestamp;
    }

    protected MachineDataSet(Parcel in) {
        id = in.readInt();
        lubricant_machine = in.readByte() != 0;
        lubricant_saw = in.readByte() != 0;
        pressure_air_main = in.readByte() != 0;
        pressure_oil_hydraulic = in.readByte() != 0;
        servo_cut = in.readByte() != 0;
        servo_transfer = in.readByte() != 0;
        spindle = in.readByte() != 0;
        safety_door = in.readByte() != 0;
        depletion = in.readByte() != 0;
        workload = in.readLong();
        timestamp = in.readInt();
    }
/*
    public static final Creator<MachineDataSet> CREATOR = new Creator<MachineDataSet>() {
        @Override
        public MachineDataSet createFromParcel(Parcel in) {
            return new MachineDataSet(in);
        }

        @Override
        public MachineDataSet[] newArray(int size) {
            return new MachineDataSet[size];
        }
    };
*/
    public void update(int id, boolean lubricant_machine, boolean lubricant_saw, boolean pressure_air_main, boolean pressure_oil_hydraulic, boolean servo_cut, boolean servo_transfer, boolean spindle, boolean safety_door, boolean depletion, long workload, int timestamp) {
        this.id = id;
        this.lubricant_machine = lubricant_machine;
        this.lubricant_saw = lubricant_saw;
        this.pressure_air_main = pressure_air_main;
        this.pressure_oil_hydraulic = pressure_oil_hydraulic;
        this.servo_cut = servo_cut;
        this.servo_transfer = servo_transfer;
        this.spindle = spindle;
        this.safety_door = safety_door;
        this.depletion = depletion;
        this.workload = workload;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getLubricant_machine() {
        return lubricant_machine;
    }

    public void setLubricant_machine(boolean lubricant_machine) {
        this.lubricant_machine = lubricant_machine;
    }

    public boolean getLubricant_saw() {
        return lubricant_saw;
    }

    public void setLubricant_saw(boolean lubricant_saw) {
        this.lubricant_saw = lubricant_saw;
    }

    public boolean getPressure_air_main() {
        return pressure_air_main;
    }

    public void setPressure_air_main(boolean pressure_air_main) {
        this.pressure_air_main = pressure_air_main;
    }

    public boolean getPressure_oil_hydraulic() {
        return pressure_oil_hydraulic;
    }

    public void setPressure_oil_hydraulic(boolean pressure_oil_hydraulic) {
        this.pressure_oil_hydraulic = pressure_oil_hydraulic;
    }

    public boolean getServo_cut() {
        return servo_cut;
    }

    public void setServo_cut(boolean servo_cut) {
        this.servo_cut = servo_cut;
    }

    public boolean getServo_transfer() {
        return servo_transfer;
    }

    public void setServo_transfer(boolean servo_transfer) {
        this.servo_transfer = servo_transfer;
    }

    public boolean getSpindle() {
        return spindle;
    }

    public void setSpindle(boolean spindle) {
        this.spindle = spindle;
    }

    public boolean getSafety_door() {
        return safety_door;
    }

    public void setSafety_door(boolean safety_door) {
        this.safety_door = safety_door;
    }

    public boolean getDepletion() {
        return depletion;
    }

    public void setDepletion(boolean depletion) {
        this.depletion = depletion;
    }

    public long getWorkload() {
        return workload;
    }

    public void setWorkload(long workload) {
        this.workload = workload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(id);
//        parcel.writeInt((Boolean) lubricant_machine ? 1: 0);
//        parcel.writeInt((Boolean) lubricant_saw ? 1: 0);
//        parcel.writeInt((Boolean) pressure_air_main ? 1: 0);
//        parcel.writeInt((Boolean) pressure_oil_hydraulic ? 1: 0);
//        parcel.writeInt((Boolean) servo_cut ? 1: 0);
//        parcel.writeInt((Boolean) servo_transfer ? 1: 0);
//        parcel.writeInt((Boolean) spindle ? 1: 0);
//        parcel.writeInt((Boolean) safety_door ? 1: 0);
//        parcel.writeInt((Boolean) depletion ? 1: 0);
//        parcel.writeLong(workload);
//        parcel.writeInt(timestamp);
//    }
}
