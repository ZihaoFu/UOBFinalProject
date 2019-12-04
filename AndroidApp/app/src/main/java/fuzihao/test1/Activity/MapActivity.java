package fuzihao.test1.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fuzihao.test1.Api.GoogleMapPhotoApi;
import fuzihao.test1.Api.WikiApi;
import fuzihao.test1.R;

import static fuzihao.test1.Api.GoogleMapPhotoApi.selectID;

public class MapActivity extends AppCompatActivity{

    private Intent intent;
    String selectString;
    String selectglobe;

    private ImageView imgMap;
    private TextView txtIntroduction;
    private TextView txtSimple;
    private TextView txtLink;
    private ImageButton btnSetting;
    private Switch swShow;

    private String link = "https://en.wikipedia.org/wiki/"; // Set a basic link
    private String html = "<a href="+link+">Link to Wikipedia</a>";
    private String introduction = "World";
    String introduction4;

    private long startTime = 0;
    private long endTime = 0;

    List<String> goToOtherMapList = new ArrayList<>();

    float imageHeight;
    float imageWidth;
    int mapHeight;
    int mapWidth;
    float mapScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Receive incoming data
        intent = getIntent();
        selectString = intent.getStringExtra("country");
        selectglobe = intent.getStringExtra("globe");

        initView(); // Initializing various data and interface controls

        introduction = selectString;
        txtIntroduction.setText(introduction); // Set introduction title

        // Special case handling, Used to load maps from the network
        final String introduction1;
        if (introduction.equals("United States")){
            introduction1 = "the-united-states-of-america";
        }else if(introduction.equals("Republic of Macedonia")){
            introduction1 = "macedonia";
        }else if(introduction.equals("Republic of Ireland")){
            introduction1 = "ireland";
        }else{
            introduction1 = introduction.toLowerCase().replaceAll(" ","-");
        }

        // introduction2 is used for Wikipedia links
        String introduction2 = introduction.toLowerCase().replaceAll(" ","_");
        // introduction3 is used to load local map pic
        String introduction3 = introduction.toLowerCase().replaceAll(" ","");
        // introduction4 is used to load Wikipedia content to textView
        introduction4 = introduction.replaceAll(" ","_");

        // local map pic id
        final int resid = getResources().getIdentifier("map"+introduction3, "drawable", getPackageName());
        try {
            new Thread(){
                @Override
                public void run() {
                    try{
                        Bitmap result;
                        if (resid == 0){
                            result = get(introduction1); // load network map
                        }else{
                            result = BitmapFactory.decodeResource(getResources(), resid); // load local map
                        }
                        Message msg=Message.obtain();
                        msg.what=0x11;
                        msg.obj=result;
                        handler.sendMessage(msg); // send to handler
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }catch (Exception e){
            Toast.makeText(MapActivity.this,"Get image resource failed",Toast.LENGTH_SHORT).show();
            Log.e("err",String.valueOf(e));
        }

        wikiApi(); // use wikiApi to get information from Wikipedia API

        // Generate a link and bind it to the control
        link+=introduction2;
        html = "<a href="+link+">Link to Wikipedia</a>";
        txtLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtLink.setText(Html.fromHtml(html));
    }

    private Bitmap get(String name) throws Exception {
        //Define a URL input stream and put the image address into the variable
        URL url = new URL("https://geology.com/world/"+name+"-map.gif");
        //Define an InputStream to get the input stream of URL
        InputStream is=url.openStream();
        Bitmap bitmap = (Bitmap) BitmapFactory.decodeStream(is); // Put the input stream into the decodestream and parse it into bitmap
        is.close();
        return bitmap;
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x11:
                    //Calculate the scale factor based on the resulting image to fit the screen width
                    imageHeight = imgMap.getHeight();
                    imageWidth = imgMap.getWidth();
                    Bitmap result = (Bitmap) msg.obj;
                    mapHeight = result.getHeight();
                    mapWidth = result.getWidth();
                    mapScale = imageWidth/ (float) mapWidth; // get scale factor
                    Matrix matrix = new Matrix();
                    matrix.postScale(mapScale, mapScale); // apply scale factor to matrix

                    // make the pic can be displayed in the center of the parent view
                    Matrix imgM = imgMap.getImageMatrix();
                    if(mapHeight*mapScale<imageHeight){
                        imgM.postTranslate(0,(imageHeight-(mapHeight*mapScale))/2);
                    }
                    result = Bitmap.createBitmap(result, 0, 0, mapWidth, mapHeight, matrix, true);
                    imgMap.setImageBitmap(result);
                    imgMap.setImageMatrix(imgM);
                    break;
            }
        }
    };

    public void onResume() {
        super.onResume();
        btnSetting.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    // call Wikipedia API
    private void wikiApi(){
        // Handling special situations
        if (introduction.equals("Republic of Macedonia")){
            introduction4 = "North_Macedonia";
        }
        WikiApi.GetApiRes getRes = new WikiApi.GetApiRes(MapActivity.this);
        getRes.execute("https://en.wikipedia.org/w/api.php?" +
                "format=json" +
                "&action=query" +
                "&prop=extracts" +

                "&explaintext=" +
                "&titles="+introduction4);
        getRes.setOnAsyncResponse(new WikiApi.AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String string) {
                txtSimple.setText(string); // apply the result to the control
            }
            @Override
            public void onDataReceivedFailed() {
                Toast toast = Toast.makeText(MapActivity.this,"data received failed!",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //Bind controls to variables, Set click listener and touch listener
    private void initView(){
        // Initializing arrays for sliding switching
        goToOtherMapList.add("World");
        goToOtherMapList.add("Asia");
        goToOtherMapList.add("Europe");
        goToOtherMapList.add("Africa");
        goToOtherMapList.add("North America");
        goToOtherMapList.add("South America");
        goToOtherMapList.add("Oceania");
        goToOtherMapList.add("Antarctica");

        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtIntroduction = (TextView) findViewById(R.id.txtIntroduction);
        txtSimple = (TextView) findViewById(R.id.txtSimple);
        txtLink = (TextView) findViewById(R.id.txtLink);
        btnSetting = (ImageButton) findViewById(R.id.btnSetting2);
        swShow = (Switch) findViewById(R.id.swTime);
        swShow.setChecked(true);
        swShow.setVisibility(View.GONE);

        txtSimple.setMovementMethod(ScrollingMovementMethod.getInstance());

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MapActivity.this, SettingActivity.class);
                startActivity(intent);
                btnSetting.setVisibility(View.INVISIBLE);
            }
        });
        imgMap.setOnTouchListener(new Move());
        swShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){

                }else{

                }
            }
        });
    }

    // touch event
    private class Move implements View.OnTouchListener {
        private Matrix matrix = new Matrix();
        private PointF startPoint = new PointF();
        private PointF midPoint;//Two-finger center point
        private Matrix currentMatrix = new Matrix();
        private float oldDist;
        private float zoom = 0f;

        private int mode = 0;//Initial state
        private static final int DRAG = 1;//Drag mode
        private static final int ZOOM = 2;//Zoom mode

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()& MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;
                    matrix.set(imgMap.getImageMatrix());
                    startPoint.set(motionEvent.getX(),motionEvent.getY());
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // scale the map
                    if (mode == ZOOM) {
                        float[] values = new float[9];
                        float newDist = distance(motionEvent);
                        if (newDist > 10f){
                            zoom = newDist / oldDist;
                            currentMatrix.set(matrix);
                            currentMatrix.postScale(zoom,zoom,midPoint.x,midPoint.y);
                        }

                        currentMatrix.getValues(values);
                        if(values[0]<=0.5f){
                            currentMatrix.postScale((0.5f)/values[0],(0.5f)/values[4],midPoint.x,midPoint.y);
                        }
                        else if(values[4]>=3.0f){
                            currentMatrix.postScale((3.0f)/values[0],(3.0f)/values[4],midPoint.x,midPoint.y);
                        }
                    }else if (mode == DRAG){
                        float dx = motionEvent.getX() - startPoint.x;
                        float dy = motionEvent.getY() - startPoint.y;
                        currentMatrix.set(matrix);
                        if (Math.abs(dx)>1 || Math.abs(dy)>1){
                            currentMatrix.postTranslate(dx,dy);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mode = 0;
                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 0.1 * 1000L) {
                        try {
                            // click event to access to the photo wall activity
                            if (selectglobe.equals("click")) {
                                // get photo reference from Google Map API
                                final String apiKey = "AIzaSyBbmKXXOLKFsICWeJkFWtp4Z9Jy9RtljX4";

                                selectID = 0;
                                GoogleMapPhotoApi.GetGoogleMapPhotoApiRes getCountryRes = new GoogleMapPhotoApi.GetGoogleMapPhotoApiRes();
                                getCountryRes.execute("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=" + apiKey + "&input=" + txtIntroduction.getText() + "&inputtype=textquery&fields=place_id");
                                getCountryRes.setOnAsyncResponse(new GoogleMapPhotoApi.AsyncResponse() {
                                    @Override
                                    public void onDataReceivedSuccess(String string) {
                                        Intent intent = new Intent(MapActivity.this, PhotoWallActivity.class);
                                        intent.putExtra("placeName", txtIntroduction.getText());
                                        intent.putExtra("placeid", string);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onDataReceivedFailed() {

                                    }
                                });
                            }
                        }catch (Exception e){
                            Toast.makeText(MapActivity.this,"This map does not have image page",Toast.LENGTH_SHORT).show();
                        }
                    }

                    PointF p1 = getLeftPointF(currentMatrix);
                    PointF p2 = getRightPointF(currentMatrix);

                    //Left boundary reset
                    if(p1.x>0){
                        currentMatrix.postTranslate(-p1.x,0);
                    }

                    //Right boundary reset
                    if(p2.x<imgMap.getWidth()){
                        currentMatrix.postTranslate(imgMap.getWidth()-p2.x,0);
                    }

                    //Top and bottom boundary reset
                    if(p2.y-p1.y>imgMap.getHeight()){
                        //Top boundary reset
                        if(p1.y>0){
                            currentMatrix.postTranslate(0,-p1.y);
                        }
                        //Bottom boundary reset
                        if(p2.y<imgMap.getHeight()){
                            currentMatrix.postTranslate(0,imgMap.getHeight()-p2.y);
                        }
                    }
                    else {
                        float row = (imgMap.getHeight()-(p2.y-p1.y))/2;
                        currentMatrix.postTranslate(0,row-p1.y);
                    }

                    if(goToOtherMapList.contains(txtIntroduction.getText().toString())){
                        int positon = goToOtherMapList.indexOf(txtIntroduction.getText().toString());

                        //Image moves to the previous image as it slides half the screen to the left
                        if(p1.x>imgMap.getWidth()/2){
                            if(positon==0){
                                intent = new Intent(MapActivity.this,MapActivity.class);
                                intent.putExtra("country",goToOtherMapList.get(7));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            if(positon>0&&positon<=7){
                                intent = new Intent(MapActivity.this,MapActivity.class);
                                intent.putExtra("country",goToOtherMapList.get(positon-1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                        //Image moves to the next image as it slides right the screen to the left
                        if(p2.x<imgMap.getWidth()/2){
                            if(positon==7){
                                intent = new Intent(MapActivity.this,MapActivity.class);
                                intent.putExtra("country",goToOtherMapList.get(0));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            if(positon<7){
                                intent = new Intent(MapActivity.this,MapActivity.class);
                                intent.putExtra("country",goToOtherMapList.get(positon+1));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }

                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = ZOOM;
                    oldDist = distance(motionEvent);//两点按下时的距离 Distance when two points are pressed
                    if (oldDist > 10f){
                        midPoint = mid(motionEvent);
                        currentMatrix.set(imgMap.getImageMatrix());
                    }
                    break;
                default:
                    break;
            }
            imgMap.setImageMatrix(currentMatrix);
            return true;
        }
    }

    // Calculate Euclidean distance
    private float distance(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float dis = (float) Math.sqrt(x * x + y * y);
        return dis;
    }

    // Calculate the midpoint of two fingers
    private static PointF mid(MotionEvent event){
        float midX = event.getX(1)+event.getX(0);
        float midY = event.getY(1)+event.getY(0);

        return new PointF(midX/2,midY/2);
    }

    //Get the top coordinate of the picture
    private PointF getLeftPointF(Matrix matrix){
        Rect rectTemp = imgMap.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2];
        float leftY = values[5];
        return new PointF(leftX,leftY);
    }

    //Get the bottom coordinates of the picture
    private PointF getRightPointF(Matrix matrix){
        Rect rectTemp = imgMap.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2]+rectTemp.width()*values[0];
        float leftY = values[5]+rectTemp.height()*values[4];
        return new PointF(leftX,leftY);
    }
}


