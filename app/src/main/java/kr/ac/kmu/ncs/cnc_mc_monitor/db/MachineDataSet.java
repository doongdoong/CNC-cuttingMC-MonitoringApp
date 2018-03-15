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
    private long emission_barrel;
    private long yield_saw;
    private long total_workload;
    private long current_workload;
    private long timestamp;

    private MachineDataSet(){}

    public MachineDataSet(int id, boolean lubricant_machine, boolean lubricant_saw, boolean pressure_air_main, boolean pressure_oil_hydraulic, boolean servo_cut, boolean servo_transfer, boolean spindle, boolean safety_door, boolean depletion, long emission_barrel, long yield_saw, long total_workload, long current_workload, int timestamp) {
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
        this.emission_barrel = emission_barrel;
        this.yield_saw = yield_saw;
        this.total_workload = total_workload;
        this.current_workload = current_workload;
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
        emission_barrel = in.readLong();
        yield_saw = in.readLong();
        total_workload = in.readLong();
        current_workload = in.readLong();
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
    public void update(int id, boolean lubricant_machine, boolean lubricant_saw, boolean pressure_air_main, boolean pressure_oil_hydraulic, boolean servo_cut, boolean servo_transfer, boolean spindle, boolean safety_door, boolean depletion, long emission_barrel, long yield_saw, long total_workload, long current_workload, int timestamp) {
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
        this.emission_barrel = emission_barrel;
        this.yield_saw = yield_saw;
        this.total_workload = total_workload;
        this.current_workload = current_workload;
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

    public void setDepletion(boolean depletion) { this.depletion = depletion; }

    public long getEmission_Barrel() { return emission_barrel; }

    public void setEmission_Barrel(long emission_barrel) {
        this.emission_barrel = emission_barrel;
    }

    public long getYield_Saw() { return yield_saw; }

    public void setYield_Saw(long yield_saw) { this.yield_saw = yield_saw; }

    public long getTotal_Workload() { return total_workload; }

    public void setTotal_Workload(long total_workload) {
        this.total_workload = total_workload;
    }

    public long getCurrent_Workload() { return current_workload; }

    public void setCurrent_Workload(long current_workload) { this.current_workload = current_workload; }

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
