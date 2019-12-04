package fuzihao.test1.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fuzihao.test1.Adapter.SpinnerAdapter;
import fuzihao.test1.Api.CountryApi;
import fuzihao.test1.Api.GoogleGetCountryCode;
import fuzihao.test1.Label.GetColorFromScreen;
import fuzihao.test1.Model.Globe;
import fuzihao.test1.Label.LabelMenu;
import fuzihao.test1.Label.MenuItem;
import fuzihao.test1.Model.GlobeRender;
import fuzihao.test1.Music.FloatingMusicPlayerService;
import fuzihao.test1.Music.Player;
import fuzihao.test1.R;

import static fuzihao.test1.Model.GlobeRender.latitude;
import static fuzihao.test1.Model.GlobeRender.longitude;
import static fuzihao.test1.Music.FloatingMusicPlayerService.btnMusic;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemSelectedListener {
    public static GLSurfaceView glsv_content;
    final String API_KEY = "AIzaSyBbmKXXOLKFsICWeJkFWtp4Z9Jy9RtljX4";
    public static float move = 0.1f;
    public static float angle = 0;//横向旋转角度 Horizontal rotation angle
    public static float angle2 = 0;//竖向旋转角度 Vertical rotation angle

    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> mTextureCoords = new ArrayList<FloatBuffer>();
    public static Bitmap mBitmap;

    private ImageButton btnSetting;
    private Spinner spTitle;
    private ImageButton btnDay;
    int dayNight = 0;
    private ImageButton btnRotate;
    int count = 0;
    public static boolean isMove = true; // Control rotation
    public static boolean isDayNight = false; // Control light

    private long startTime = 0;
    private long endTime = 0;

    private Intent intent;

    private int select = 0;
    private String title = "";

    private Float x = 0.0f;
    private Float y = 0.0f;
    private Float newx = 0.0f;
    private Float newy = 0.0f;
    private Float changex = 0.0f;
    private Float changey = 0.0f;

    private int mode = 0;
    private static final int DRAG = 1;//Drag mode
    private static final int ZOOM = 2;//Zoom mode
    float oldDist;
    public static float origin = 1.0f;//Original ratio
    float zoom = 1.0f;  //Zoom ratio

    Timer timer = new Timer();

    public static Globe globe = new Globe();

    private static final int REQUEST_FLOATING_BUTTON = 0;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private long firstPressTime;

    String itemTitle = "";
    String itemCapital = "";
    String itemArea = "";
    String itemPopulation = "";
    String itemTime = "";
    String itemRegion = "";
    String itemCurrencyRes = "";
    String itemLanguageRes = "";

    Drawable itemRes;
    int[] location = new int[2];
    int sendValue = 0;

    private SQLiteDatabase db;

    String name;
    int id;
    String sendString="";
    String sendCode="";

    float lat;
    float lon;

    int color=0;
    int a=0;
    int r=0;
    int g=0;
    int b=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get system services to capture the screen
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        assert mMediaProjectionManager != null;
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);

        initView();//初始化控件 Initialise controls

        //获取选择界面选择的选项
        //Get the options selected in the selectActivity
        Intent intentFromSelect = getIntent();
        select = intentFromSelect.getIntExtra("num", 0);
        // 绑定地图 Binding map
        if (select == 0) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe1);
            title = "Physical Globe";
            spTitle.setSelection(0);
        } else if (select == 1) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe2);
                title = "Continent Globe";
                spTitle.setSelection(1);
        } else if (select == 2) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe3);
            title = "Political Globe";
            spTitle.setSelection(2);
        } else if (select == 3) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe4);
            title = "Time Zone Globe";
            spTitle.setSelection(3);
        }

        //绑定View, 初始化Renderer,并设置触摸监听器
        //Bind view, initialise renderer, and set touch listener
        glsv_content = findViewById(R.id.glsv_content);
        glsv_content.setRenderer(new GlobeRender());
        glsv_content.setOnTouchListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Access to display buttons in other interfaces, floating buttons
        if (requestCode == REQUEST_FLOATING_BUTTON) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Authorization failure", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Authorization success", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, FloatingMusicPlayerService.class));
            }
        }
        // Get screen capture permission
        else if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "User cancelled!", Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            setUpVirtualDisplay();
        }
    }

    // Function to open a floating button
    public void startFloatingMusic(View view) {
        // If it is already open, close the previous button and reopen the new button
        // The relationship between button and country will be updated
        if (FloatingMusicPlayerService.isStarted) {
            FloatingMusicPlayerService.isStarted = false;
            FloatingMusicPlayerService.windowManager.removeView(btnMusic);
            FloatingMusicPlayerService.player.stop();
            // Open New Button
            FloatingMusicPlayerService.player = new Player();
            Intent intent = new Intent(MainActivity.this, FloatingMusicPlayerService.class);
            intent.putExtra("code",name);
            startService(intent);
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            // Floating button permission required
            Toast.makeText(this, "No permission at present, please authorize!", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            // Open floating button
            Intent intent = new Intent(MainActivity.this, FloatingMusicPlayerService.class);
            intent.putExtra("code",name);
            startService(intent);
        }
    }

    // Initialize virtual display, transfer data to GetColorFromScreen
    private void setUpVirtualDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        ImageReader imageReader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 1);
        mMediaProjection.createVirtualDisplay("ScreenCapture", dm.widthPixels, dm.heightPixels, dm.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
        GetColorFromScreen.reader = imageReader;
    }

    @Override
    public void onResume() {
        super.onResume();
        //回到界面时,显示所有控件
        //When returning to this interface, show all controls
        btnSetting.setVisibility(View.VISIBLE);
        spTitle.setVisibility(View.VISIBLE);
        btnDay.setVisibility(View.VISIBLE);
        btnRotate.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // Double click to close app
        // Interval less than two seconds
        if(System.currentTimeMillis() - firstPressTime < 2000){
            try{
                // close floating button and database
                FloatingMusicPlayerService.windowManager.removeView(btnMusic);
                db.close();
            }catch (Exception e){

            }
            super.onBackPressed();
        }else {
            Toast.makeText(MainActivity.this,"Please press again to exit application",Toast.LENGTH_SHORT).show();
            firstPressTime = System.currentTimeMillis(); // record time
        }
    }

    // Initialize interface controls
    // Add event to interface controls
    private void initView() {
        //将控件绑定至变量 Bind controls to variables
        btnSetting = findViewById(R.id.btnSetting);
        spTitle = findViewById(R.id.spTitle);
        btnDay = findViewById(R.id.btnDayAndNight);
        btnRotate = findViewById(R.id.btnRotate);

        String[] arr = {"Physical Globe","Continent Globe","Political Globe","Time Zone Globe"}; // spinner items
        SpinnerAdapter adapter = new SpinnerAdapter(this,R.layout.activity_main,arr); // set spinner adapter
        spTitle.setAdapter(adapter); // set adapter for spinner

        spTitle.setOnItemSelectedListener(MainActivity.this); // set item selected listener for spinner items

        // Add click listener to Main menu button
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                btnSetting.setVisibility(View.INVISIBLE);
                spTitle.setVisibility(View.INVISIBLE);
                btnDay.setVisibility(View.INVISIBLE);
                btnRotate.setVisibility(View.INVISIBLE);
            }
        });

        // Add click listener to rotation control button
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count + 1; // Determine whether to rotate
                // rotate
                if (count % 2 == 0) {
                    isMove = true;
                    btnRotate.setImageDrawable(getDrawable(R.drawable.stop));
                    Toast toast = Toast.makeText(MainActivity.this, "Automatic rotation: Open", Toast.LENGTH_SHORT);
                    toast.show();
                }
                // Stop
                else {
                    isMove = false;
                    btnRotate.setImageDrawable(getDrawable(R.drawable.restart));
                    Toast toast = Toast.makeText(MainActivity.this, "Automatic rotation: Close", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Add click listener to light control button
        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayNight = dayNight + 1; // Determine whether to display day and night areas
                // No
                if (dayNight % 2 == 0) {
                    isDayNight = false;
                    btnDay.setImageDrawable(getDrawable(R.drawable.dayopen));
                    Toast toast = Toast.makeText(MainActivity.this, "Display of day and night areas: Close", Toast.LENGTH_SHORT);
                    toast.show();
                }
                // Yes
                else {
                    isDayNight = true;
                    btnDay.setImageDrawable(getDrawable(R.drawable.dayclose));
                    Toast toast = Toast.makeText(MainActivity.this, "Display of day and night areas: Open", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Import the SQLite database file to the corresponding directory
        String DB_FILE_PATH = "/data/data/fuzihao.test1/databases/colorposition.db";
        importDataBase();
        db = SQLiteDatabase.openOrCreateDatabase(DB_FILE_PATH, null); // open database
    }

    // Import database file
    public void importDataBase(){
        String DB_PATH = "/data/data/fuzihao.test1/databases";
        File dir = new File(DB_PATH);

        // Create if there is no corresponding path
        if(!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, "colorposition.db");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            //LOAD DATABASE
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.colorposition);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere=new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    //Touch action of the globe
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent == null) {
            return false;
        }
        // Calculate normalized coordinates for ray picking
        final float normalizedX = (motionEvent.getX() / view.getWidth()) * 2 - 1;
        final float normalizedY = -((motionEvent.getY() / view.getHeight()) * 2 - 1);

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时获取当前屏幕坐标, 并暂停地球仪的旋转
                //Obtain the current screen coordinates when a finger is on the screen, and pause the rotation of the globe
                mode = DRAG;
                isMove = false; // stop rotate

                // Finger touch position
                x = motionEvent.getX();
                y = motionEvent.getY();

                // Location of label display
                location[0] = Math.round(x);
                location[1] = Math.round(y);
                startTime = System.currentTimeMillis(); // Record time
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = false;
                // 手指移动时首先判断模式是拖拽还是缩放
                // judge whether the mode is drag or zoom when the fingers move
                if (mode == ZOOM) {
                    //获取新的距离, 然后计算缩放比例
                    //Get the new distance and calculate the zoom ratio
                    float newDist = distance(motionEvent);
                    if (newDist > oldDist + 1) {
                        zoom = (newDist / oldDist);
                        origin = origin * zoom;
                        oldDist = newDist;
                    } else if (newDist < oldDist - 1) {
                        zoom = (newDist / oldDist);
                        origin = origin * zoom;
                        oldDist = newDist;
                    }
                } else if (mode == DRAG) {
                    //获取新的坐标, 计算变化的距离
                    //Get new coordinates and calculate the changing distance
                    newx = motionEvent.getX();
                    newy = motionEvent.getY();
                    changex = -(newx - x);
                    changey = (newy - y);

                    angle = angle - changex / 360;
                    angle2 = angle2 + changey / 360;
                }
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) < 0.1 * 1000L) {
                    //Ray picking
                    //Send normalized coordinates to calculate longitude and latitude
                    GlobeRender.handleTouchDown(normalizedX,normalizedY);
                    lat = latitude;
                    lon = longitude;

                    //colour picking
                    //Get touch point color data from GetColorFromScreen
                    color = GetColorFromScreen.getColor(Math.round(x), Math.round(y));
                    a = Color.alpha(color);
                    r = Color.red(color);
                    g = Color.green(color);
                    b = Color.blue(color);

                    //display color data
//                    Toast.makeText(MainActivity.this,String.valueOf(r)+String.valueOf(g)+String.valueOf(b),Toast.LENGTH_SHORT).show();

                    try{
                        String sql="";
                        if (spTitle.getSelectedItem() == "Physical Globe"||spTitle.getSelectedItem() == "Continent Globe"){
                            // colour picking and database to get label objects
                            // Use SQL statement to query corresponding target based on colour result
                            if (spTitle.getSelectedItem() == "Physical Globe"){
                                sql = getResources().getString(R.string.physicalsqlsearch,r,g,b);
                            }
                            if (spTitle.getSelectedItem() == "Continent Globe"){
                                sql = getResources().getString(R.string.continentsqlsearch,r,g,b);
                            }

                            // Get results
                            Cursor cursor = db.rawQuery(sql, null);
                            if (cursor.getCount()!=0){
                                cursor.moveToFirst();//Move to first line of result
                                while (!cursor.isAfterLast()) {
                                    id = cursor.getInt(cursor.getColumnIndex("id"));

                                    if (spTitle.getSelectedItem() == "Continent Globe"){
                                        // set Population item content
                                        itemPopulation = String.valueOf(cursor.getInt(cursor.getColumnIndex("population")));
                                        itemPopulation = addComma(itemPopulation,3);
                                    }
                                    itemTitle = cursor.getString(cursor.getColumnIndex("title")); // set Title item content
                                    itemArea = String.valueOf(cursor.getInt(cursor.getColumnIndex("area")));
                                    itemArea = addComma(itemArea,3);
                                    itemArea = itemArea + " km² ";// set Area item content
                                    String itemTitle1 = itemTitle.toLowerCase().replaceAll(" ","");
                                    int resid = getResources().getIdentifier("map"+itemTitle1, "drawable", getPackageName());
                                    itemRes = getDrawable(resid);// set national flag
                                    sendString = itemTitle; // Set the data to be sent to the map interface
                                    onClickPopIcon(glsv_content); // Generate a label
                                    cursor.moveToNext();//Move to next line of result
                                }
                            }
                            // Other places set as world labels
                            else if(cursor.getCount()==0&&r!=0&&g!=0&&b!=0){
                                itemTitle = "World";
                                itemArea = "510000000";
                                itemArea = addComma(itemArea,3);
                                itemArea = itemArea + " km²";
                                itemPopulation = "7700000000";
                                itemPopulation = addComma(itemPopulation,3);

                                String itemTitle1 = itemTitle.toLowerCase().replaceAll(" ","");
                                int resid = getResources().getIdentifier("map"+itemTitle1, "drawable", getPackageName());
                                itemRes = getDrawable(resid);
                                sendString = itemTitle;
                                onClickPopIcon(glsv_content);
                            }
                        }

                        // Ray picking to get label objects
                        else if (spTitle.getSelectedItem() == "Political Globe") {
                            getCountryCode(lat,lon); // Enter latitude and longitude into getCountryCode method
                        }

                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"API/Database error, please check API/Database availability",Toast.LENGTH_SHORT).show();
                    }
                }
                //抬起时地球仪继续旋转
                //The globe continues to rotate when the fingers are raised
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isMove = count % 2 == 0;
                    }
                }, 2000);
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                isMove = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = distance(motionEvent);//Distance when two points are pressed
                isMove = false;
                break;
        }
        return true;
    }

    private Bitmap get(String name) throws Exception {
        //定义一个url输入流，将图片地址放入
        //Define a URL input stream and put the image address into the variable
        URL url = new URL("https://www.countryflags.io/"+name+"/flat/64.png");
        //定义一个 InputStream 获取url的输入流
        // Define an InputStream to get the input stream of URL
        InputStream is=url.openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);//使用decodeStream解析成bitmap Using decodeStream to parse into bitmap
        is.close(); // Close input stream
        return bitmap;
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x11:
                    itemRes = new BitmapDrawable((Bitmap) msg.obj); // Convert results to drawable and set national flag
                    break;
            }
        }
    };

    // use Google Map API to get country name, and then use countryApi function to get country code
    private void getCountryCode(float latitude, float longitude){
        count = 1;
        isMove = false;
        btnRotate.setImageDrawable(getDrawable(R.drawable.restart));

        GoogleGetCountryCode.GetCountryCodeRes getCountryCodeRes = new GoogleGetCountryCode.GetCountryCodeRes(MainActivity.this);
        getCountryCodeRes.execute("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key="+API_KEY+"&result_type=country");
        getCountryCodeRes.setOnAsyncResponse(new GoogleGetCountryCode.AsyncResponse(){
            @Override
            public void onDataReceivedSuccess(String string) {
                name = string.toLowerCase(); // get country name
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Bitmap result = get(name);
                            Message msg=Message.obtain();
                            msg.what=0x11;
                            msg.obj=result;
                            handler.sendMessage(msg);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                countryApi(name); // use countryApi function to get country code
            }
            @Override
            public void onDataReceivedFailed() {
                //If no country is detected, it will be set as UN
                if(r!=0&&g!=0&&b!=0){
                    itemTitle = "United Nations";
                    itemCapital = "New York";
                    itemArea = "Null";
                    name = "uno";
                    itemPopulation = "61000";
                    itemPopulation = addComma(itemPopulation,3);
                    itemRegion = "Null";
                    itemCurrencyRes = "Null";
                    itemLanguageRes = "enfrruzhares";
                    itemLanguageRes = addComma(itemLanguageRes,2);
                    String itemTitle1 = itemTitle.toLowerCase().replaceAll(" ","");
                    int resid = getResources().getIdentifier("map"+itemTitle1, "drawable", getPackageName());
                    itemRes = getDrawable(resid);
                    sendString = itemTitle;
                    onClickPopIcon(glsv_content);
                }
            }
        });
    }

    // use countryApi function to get country code
    private void countryApi(final String name){
        CountryApi.GetCountryApiRes getCountryRes = new CountryApi.GetCountryApiRes(MainActivity.this);
        getCountryRes.execute("https://restcountries-v1.p.rapidapi.com/alpha/" + name);
        getCountryRes.setOnAsyncResponse(new CountryApi.AsyncResponse(){
            // Set parameters of label
            @Override
            public void onDataReceivedSuccess(String string) {
                String result = string;
                String [] arr = result.split("@");

                sendCode = name;
                sendString = arr[0];
                itemTitle = arr[0];
                itemCapital = arr[1];
                itemArea = arr[3];
                itemArea = addComma(itemArea,3);
                itemArea = itemArea + " km²";
                itemPopulation = arr[2];
                itemPopulation = addComma(itemPopulation,3);
                itemTime = arr[4];
                itemRegion = arr[5]+","+arr[6];
                itemCurrencyRes = arr[7];
                itemCurrencyRes = addComma(itemCurrencyRes,3);
                itemLanguageRes = arr[8];
                itemLanguageRes = addComma(itemLanguageRes,2);

                onClickPopIcon(glsv_content); // Generate a label
            }

            @Override
            public void onDataReceivedFailed() {
                Toast.makeText(MainActivity.this,"Country data received failed, please check longitude and latitude or API!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add a comma to each three digits of the number
    private String addComma(String input, int interval){
        input = new StringBuilder(input).reverse().toString();
        String str2 = "";
        for(int i = 0; i < input.length(); i++){
            if(i*interval+interval>input.length()) {
                str2 += input.substring(i*interval, input.length());
                break;
            }
            str2 += input.substring(i*interval, i*interval+interval)+",";
        }
        if(str2.toString().endsWith(",")){
            str2 = str2.substring(0, str2.length()-1);
        }
        input = new StringBuilder(str2).reverse().toString();
        return input;
    }

    //计算欧式距离
    //Calculate Euclidean distance
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float dis = (float) Math.sqrt(x * x + y * y);
        return dis;
    }

    @Override
    protected void onDestroy() {
        mBitmap.recycle();
        super.onDestroy();
    }

    // Item selection events for spinner
    // Switch the display of the globe
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position){
            case 0:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe1);
                break;
            case 1:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe2);
                break;
            case 2:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe3);
                break;
            case 3:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe4);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast toast = Toast.makeText(this,"Nothing is selected by the user",Toast.LENGTH_SHORT);
        toast.show();
    }

    //Generate labels
    public void onClickPopIcon(View view) {
        count = 1;
        isMove = false;
        btnRotate.setImageDrawable(getDrawable(R.drawable.restart));

        // Set label parameters
        LabelMenu labelMenu = new LabelMenu(this);
        labelMenu.setTriangleIndicatorViewColor(Color.WHITE);
        labelMenu.setBackgroundResource(R.drawable.label_white);
        labelMenu.setItemTextColor(Color.BLACK);
        labelMenu.setIsShowIcon(true);

        // Set click event of label item
        labelMenu.setOnItemClickListener(new LabelMenu.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case 6:
                        startFloatingMusic(glsv_content);
                        break;
                    case 7:
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        if(spTitle.getSelectedItem().toString().equals("Political Globe")){
                            intent.putExtra("globe","click");
                        }
                        intent.putExtra("country",sendString);
                        intent.putExtra("code",sendCode);
                        startActivity(intent);
                        break;
                }
            }
        });

        // Set label menu
        labelMenu.setMenuList(getMenuList());
        // Set label display location
        labelMenu.show(glsv_content,location);
    }

    // Set label menu item
    private List<MenuItem> getMenuList() {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem(itemRes,1, itemTitle));
        if(spTitle.getSelectedItem() == "Physical Globe"){
            list.add(new MenuItem(getDrawable(R.drawable.iconarea),3, "Area: "+itemArea));
            list.add(new MenuItem(getDrawable(R.drawable.iconinfo),7, "More Info"));
        }
        if(spTitle.getSelectedItem() == "Continent Globe"){
            list.add(new MenuItem(getDrawable(R.drawable.iconarea),3, "Area: "+itemArea));
            list.add(new MenuItem(getDrawable(R.drawable.iconpopulation),4, "Population: "+itemPopulation));
            list.add(new MenuItem(getDrawable(R.drawable.iconinfo),7, "More Info"));
        }
        if(spTitle.getSelectedItem() == "Political Globe"){
            list.add(new MenuItem(getDrawable(R.drawable.iconcapital),2, "Capital: "+itemCapital));
            list.add(new MenuItem(getDrawable(R.drawable.iconarea),3, "Area: "+itemArea));
            list.add(new MenuItem(getDrawable(R.drawable.iconpopulation),4, "Population: "+itemPopulation));
//            list.add(new MenuItem(getDrawable(R.drawable.timezone),5, "Time Zone: "+itemTime));
            list.add(new MenuItem(getDrawable(R.drawable.iconregion),8, "Region: "+itemRegion));
            list.add(new MenuItem(getDrawable(R.drawable.iconcurrency),10, "Currency: "+itemCurrencyRes));
            list.add(new MenuItem(getDrawable(R.drawable.iconlanguage),11, "Language: "+itemLanguageRes));
            list.add(new MenuItem(getDrawable(R.drawable.iconmusic),6, "National Anthem"));
            list.add(new MenuItem(getDrawable(R.drawable.iconinfo),7, "More Info"));
        }
        return list;
    }
}
