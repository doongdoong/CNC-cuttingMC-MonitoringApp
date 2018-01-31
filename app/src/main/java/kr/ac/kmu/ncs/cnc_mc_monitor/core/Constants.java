package kr.ac.kmu.ncs.cnc_mc_monitor.core;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class Constants {

    /**
     * For listPanel.ListActivity
     */
    public static final String INTENT_KEY_MACHINE_ID = "machineID";

    /**
     * Protocol
     {
     "id": 17834, //장치 고유 값
     "lubricant_machine": 1, // 장비 윤활유 부족
     "lubricant_saw": 1, //톱날 윤활유 부족
     "pressure_air_main": 1, //메인 공기압 부족
     "pressure_oil_hydraulic": 1, //유압유 압력 부족
     "servo_cut": 0, // 절단 서보 에러
     "servo_transfer": 1, //이송 서보 에러
     "spindle": 1, //스핀들 이상 에러
     "safety_door": 0, //안전 문 이상
     "depletion": 1, // 소제 부족
     "total_workload": 41 // 총 작업량
     "current_workload": 40 // 현재 작업량
     "timestamp": 1500621879 // 유닉스 타임
     }
     */

    /**
     * For db.DbHelper
     */
    public static final String DB_ID = "id";
    public static final String DB_LUBRICANT_MACHINE = "lubricant_machine";
    public static final String DB_LUBRICANT_SAW = "lubricant_saw";
    public static final String DB_PRESSURE_AIR_MAIN = "pressure_air_main";
    public static final String DB_PRESSURE_OIL_HYDRAULIC = "pressure_oil_hydraulic";
    public static final String DB_SERVO_CUT = "servo_cut";
    public static final String DB_SERVO_TRANSFER = "servo_transfer";
    public static final String DB_SPINDLE = "spindle";
    public static final String DB_SAFETY_DOOR = "safety_door";
    public static final String DB_DEPLETION = "depletion";
    public static final String DB_TOTAL_WORKLOAD = "total_workload";
    public static final String DB_CURRENT_WORKLOAD = "current_workload";
    public static final String DB_TIMESTAMP = "timestamp";

    public static String ID = "";
    public static String LUBRICANT_MACHINE = "";
    public static String LUBRICANT_SAW = "";
    public static String PRESSURE_AIR_MAIN = "";
    public static String PRESSURE_OIL_HYDRAULIC = "";
    public static String SERVO_CUT = "";
    public static String SERVO_TRANSFER = "";
    public static String SPINDLE = "";
    public static String SAFETY_DOOR = "";
    public static String DEPLETION = "";
    public static String TOTAL_WORKLOAD ;
    public static String CURRENT_WORKLOAD ;
    public static String TIMESTAMP = "";

    public static final int CONN_TIMEOUT = 10000;
    public static String ORGINIZATION = "";
    public static String SERVERADDRESS = "";
    public static String TOKEN = "";
    public static String FCM_TOKEN = "";
    public static Date TIME_LASTSEXION = new Date();

    /**
     * Bitmap Factory
     */
    public static Bitmap drawableToBitmap(Resources resources, int id){
        return BitmapFactory.decodeResource(resources, id);
    }
}
