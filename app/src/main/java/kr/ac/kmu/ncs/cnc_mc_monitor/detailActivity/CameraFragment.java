package kr.ac.kmu.ncs.cnc_mc_monitor.detailActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import kr.ac.kmu.ncs.cnc_mc_monitor.R;
import kr.ac.kmu.ncs.cnc_mc_monitor.core.Constants;

/**
 * Created by NCS-KSW on 2017-07-20.
 */
public class CameraFragment extends Fragment{

    private static CameraFragment instance;
    private HttpURLConnection conn;
    private ListView lvVideo;
    private VideoListAdapter videoListAdapter;
    private VideoListTask videoListTask;
    private ArrayList<VideoListItem> videoList;
    private Uri.Builder builder;
    private View view;
    private VideoView videoview;
    private String machineID;
    private String fullScreen;
    private MediaController mediacontroller;
    private ArrayList<String> titleList;
    private Comparator<String> cmpDesc;

    /**
     * Singleton pattern
     */
    private CameraFragment() {
    }

    public static CameraFragment getInstance() {
        if (instance == null)
            instance = new CameraFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDataFromIntent();

        Bundle extra = getArguments();
        fullScreen =  extra.getString("fullScreenInd");
        if("y".equals(fullScreen)){
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        view = inflater.inflate(R.layout.camera_fragment, container, false);
        videoview = (VideoView) view.findViewById(R.id.videoview);
        lvVideo = (ListView) view.findViewById(R.id.lv_video);
        mediacontroller = new FullScreenMC(getContext());
        mediacontroller.setAnchorView(videoview);
        videoview.setMediaController(mediacontroller);
        mediacontroller.setMediaPlayer(videoview);

        init();

        try {
            Thread.sleep(300);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    private void init() {
        titleList = new ArrayList<String>();
        videoList = new ArrayList<VideoListItem>();
        videoListAdapter = new VideoListAdapter();
        lvVideo.setAdapter(videoListAdapter);
        cmpDesc = new Comparator<String>() { @Override public int compare(String o1, String o2) { return o2.compareTo(o1) ; } } ;

        videoListTask = new VideoListTask();
        videoListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onBackPressed() {
        super.getActivity().onBackPressed();
    }

    private void getDataFromIntent() {
        Bundle extra = getArguments();
        machineID = extra.getString(Constants.INTENT_KEY_MACHINE_ID);
    }

    public void loadVideo(final String tempVideoTitle) {
        String urlStr = Constants.SERVERADDRESS;

        String videoTitle = tempVideoTitle.substring(1,tempVideoTitle.length()-1);

        Log.d("selectedVideoTitle", videoTitle);

        Toast.makeText(getContext(), "Loading video. Please wait", Toast.LENGTH_LONG).show();

        builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority(urlStr.substring(7, urlStr.length()))
                .appendPath("video")
                .appendPath("streaming")
                .appendPath(machineID)
                .appendEncodedPath(videoTitle);

        Log.d("dddddd", urlStr.substring(7,urlStr.length()));

        Map<String, String> map = new HashMap<String, String>();
        map.put("authorization", Constants.TOKEN);

        videoview.setVideoURI(builder.build(), map);
        videoview.requestFocus();
        videoview.start();

        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                return false;
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(this.getClass().getSimpleName(), "onCompletion: ");
            }
        });

        videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
    }

    class VideoListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return videoList.size();
        }

        @Override
        public Object getItem(int position) {
            return videoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            //AsyncTask에서 가져온 ListView에 대한 view를 출력
            final int pos = position;
            VideoListItem inflatedItem = videoList.get(pos);

            try {
                if (inflatedItem == null)
                    throw new NullPointerException();

                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_video_item, parent, false);
                }

                // init element
                ImageView imgThumnail = (ImageView) view.findViewById(R.id.img_thumnail);
                TextView tvMachineID = (TextView) view.findViewById(R.id.tv_machineID);
                TextView tvVideoTitle = (TextView) view.findViewById(R.id.tv_videoTitle);

                // set to an element
                imgThumnail.setImageBitmap(inflatedItem.getThumnail());
                tvMachineID.setText("#" + inflatedItem.getMachineID());
                tvVideoTitle.setText(inflatedItem.getTitle());
            } catch (Exception e) {
               e.printStackTrace();
                return null;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoListItem selectedItem = videoList.get(pos);
                    loadVideo(titleList.get(pos));
                }
            });
            return view;
        }
    }

    class VideoListTask extends AsyncTask<String, Integer, Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... value) {
            Boolean result = list();

            if (result != true) {
                publishProgress(0);
                Log.d(this.getClass().getSimpleName(), "/list 에러");
                return null;
            }

            //publishProgress(1);

            return null;
        }

        protected void onProgressUpdate(Integer... value) {
            if(value[0]==0)
                Toast.makeText(getContext(), "영상 목록 로딩에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

        public Boolean list() {
            StringBuilder output = new StringBuilder();
            InputStream is;
            ByteArrayOutputStream baos;
            String urlStr = Constants.SERVERADDRESS;
            Boolean result = false;

            try {
                URL url = new URL(urlStr + "/video/list");
                conn = (HttpURLConnection) url.openConnection();

                JSONObject json = new JSONObject();
                try {
                    json.put("id", machineID);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    result = false;
                }

                String body = json.toString();

                Log.d("비디오리스트 바디", body);

                if (conn != null) {
                    conn.setConnectTimeout(Constants.CONN_TIMEOUT);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("authorization", Constants.TOKEN);
                    conn.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = conn.getOutputStream();
                    os.write(body.getBytes());
                    os.flush();

                    String response;
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
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

                        Log.d("비디오리스트 성공여부", result + "");

                        if (result == true) {
                            String dataString = (String) responseJSON.get("data");
                            Log.d("datastring", dataString);

                            //data 파싱
                            Log.d("datastring", dataString.substring(1, dataString.length()-1));

                            //[] 떼고 ","로 토큰 자르기
                            StringTokenizer tokens = new StringTokenizer(dataString.substring(1, dataString.length()-1), ",");

                            int k=0;
                            while(tokens.hasMoreTokens()){
                                titleList.add(tokens.nextToken());
                                Log.d("datastring", titleList.get(k++));
                            }

                            Collections.sort(titleList, cmpDesc);

                            //prmt 수정해야함
                            for(int i=0 ; i<titleList.size(); i++) {
                                videoList.add(new VideoListItem(machineID, titleList.get(i).substring(1, titleList.get(i).length()-1), Constants.drawableToBitmap(getResources(), R.drawable.android_logo)));
                            }
                        }

                        is.close();
                        conn.disconnect();
                    }
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                result = false;
            } catch (Exception ex) {
                ex.printStackTrace();
                result = false;
            }

            return result;
        }
    }
}
