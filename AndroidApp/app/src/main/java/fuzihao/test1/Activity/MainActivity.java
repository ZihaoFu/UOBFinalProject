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
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import fuzihao.test1.Api.CountryApi;
import fuzihao.test1.Label.GBData;
import fuzihao.test1.Model.Globe;
import fuzihao.test1.Label.LabelMenu;
import fuzihao.test1.Label.MenuItem;
import fuzihao.test1.Model.GlobeRender;
import fuzihao.test1.Music.FloatingMusicPlayerService;
import fuzihao.test1.Music.Player;
import fuzihao.test1.R;
import fuzihao.test1.SqlDatabase.MySQLiteOpenHelper;

import static fuzihao.test1.Music.FloatingMusicPlayerService.btnMusic;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemSelectedListener {
    private GLSurfaceView glsv_content;
    //使用OpenGL库创建一个材质(Texture)，首先是获取一个Texture Id。
    //Use the OpenGL library to create a texture.
    private int[] textures = new int[2];//Get a texture ID.
    public static float move = 0.1f;
    public static float angle = 0;//横向旋转角度 Lateral rotation angle
    public static float angle2 = 0;//竖向旋转角度 Vertical rotation angle

    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> mTextureCoords = new ArrayList<FloatBuffer>();
    public static Bitmap mBitmap;

    private ImageButton btnSetting;
//    private TextView txtTitle;
    private Spinner spTitle;
    private ImageButton btnDay;
    int dayNight = 0;
    private ImageButton btnRotate;
    int count = 0;
    public static boolean isMove = true;
    public static boolean isDayNight = false;

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

    //Call other classes
    public static Globe globe = new Globe();
//    private circle label = new circle();

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
    Drawable itemRes;
    int[] location = new int[2];
    int sendValue = 0;

    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase db;

    String name;
    int id;
    String sendString="";
    String sendCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe7);
            title = "Physical Globe";
            spTitle.setSelection(0);
//            txtTitle.setText(title);
        } else if (select == 1) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe6);
                title = "Continent Globe";
                spTitle.setSelection(1);
//            txtTitle.setText(title);
        } else if (select == 2) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe9);
            title = "Political Globe";
            spTitle.setSelection(2);
//            txtTitle.setText(title);
        } else if (select == 3) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe3);
            title = "Time Zone Globe";
            spTitle.setSelection(3);
//            txtTitle.setText(title);
        }
        //绑定View, 初始化Renderer,并设置触摸监听器
        //Bind view, initialise renderer, and set touch listener
        glsv_content = (GLSurfaceView) findViewById(R.id.glsv_content);
        glsv_content.setRenderer(new GlobeRender());
        glsv_content.setOnTouchListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_FLOATING_BUTTON) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Authorization failure", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Authorization success", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, FloatingMusicPlayerService.class));
            }
        }
        else if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "User cancelled!", Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            setUpVirtualDisplay();
        }
    }

    public void startFloatingMusic(View view) {
        if (FloatingMusicPlayerService.isStarted) {
            FloatingMusicPlayerService.isStarted = false;
            FloatingMusicPlayerService.windowManager.removeView(btnMusic);
            FloatingMusicPlayerService.player.stop();
            FloatingMusicPlayerService.player = new Player();
            Intent intent = new Intent(MainActivity.this, FloatingMusicPlayerService.class);
            intent.putExtra("code",name);
            startService(intent);
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "No permission at present, please authorize!", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            Intent intent = new Intent(MainActivity.this, FloatingMusicPlayerService.class);
            intent.putExtra("code",name);
            startService(intent);
        }
    }

    private void setUpVirtualDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        ImageReader imageReader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 1);
        mMediaProjection.createVirtualDisplay("ScreenCapture",
                dm.widthPixels, dm.heightPixels, dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);
        GBData.reader = imageReader;
    }

    @Override
    public void onResume() {
        super.onResume();
        //回到界面时,显示所有控件
        //When returning to the interface, show all controls
        btnSetting.setVisibility(View.VISIBLE);
//        txtTitle.setVisibility(View.VISIBLE);
        spTitle.setVisibility(View.VISIBLE);
        btnDay.setVisibility(View.VISIBLE);
        btnRotate.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - firstPressTime < 2000){
            FloatingMusicPlayerService.windowManager.removeView(btnMusic);
            super.onBackPressed();
        }else {
            Toast.makeText(MainActivity.this,"Please press again to exit application",Toast.LENGTH_SHORT).show();
            firstPressTime = System.currentTimeMillis();
        }
    }

    private void initView() {
        //将控件绑定至变量 Bind controls to variables
        btnSetting = (ImageButton) findViewById(R.id.btnSetting);
//        txtTitle = (TextView) findViewById(R.id.txtTitle);
        spTitle = (Spinner) findViewById(R.id.spTitle);
        btnDay = (ImageButton) findViewById(R.id.btnDayAndNight);
        btnRotate = (ImageButton) findViewById(R.id.btnRotate);

        String[] arr = {"Physical Globe","Continent Globe","Political Globe","Time Zone Globe"};
        SpinnerAdapter adapter = new SpinnerAdapter(this,R.layout.activity_main,arr);
        spTitle.setAdapter(adapter);

        spTitle.setOnItemSelectedListener(MainActivity.this);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.array.globeMode);
        //给设置按钮添加点击监听器 Add click listener to setting button
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                btnSetting.setVisibility(View.INVISIBLE);
//                txtTitle.setVisibility(View.INVISIBLE);
                spTitle.setVisibility(View.INVISIBLE);
                btnDay.setVisibility(View.INVISIBLE);
                btnRotate.setVisibility(View.INVISIBLE);
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count + 1;
                if (count % 2 == 0) {
                    isMove = true;
                    btnRotate.setImageDrawable(getDrawable(R.drawable.stop));
                    Toast toast = Toast.makeText(MainActivity.this, "Automatic rotation: Open", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    isMove = false;
                    btnRotate.setImageDrawable(getDrawable(R.drawable.restart));
                    Toast toast = Toast.makeText(MainActivity.this, "Automatic rotation: Close", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayNight = dayNight + 1;
                if (dayNight % 2 == 0) {
                    isDayNight = false;
                    btnDay.setImageDrawable(getDrawable(R.drawable.dayopen));
                    Toast toast = Toast.makeText(MainActivity.this, "Display of day and night areas: Close", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    isDayNight = true;
                    btnDay.setImageDrawable(getDrawable(R.drawable.dayclose));
                    Toast toast = Toast.makeText(MainActivity.this, "Display of day and night areas: Open", Toast.LENGTH_SHORT);
                    toast.show();

//                    int hour = 0;
//                    double res = Math.cos(Math.PI*hour/12)*4;

//                    Toast toast2 = Toast.makeText(MainActivity.this, String.valueOf(res), Toast.LENGTH_SHORT);
//                    toast2.show();

                }
            }
        });

        mySQLiteOpenHelper = new MySQLiteOpenHelper(MainActivity.this,"colorPosition.db");
//        mySQLiteOpenHelper.getWritableDatabase();
        db = mySQLiteOpenHelper.getWritableDatabase();
//        db.execSQL("delete from color");
//        ContentValues values = new ContentValues();
//        values.put("name","france");
//        values.put("red1",0);
//        values.put("red2",5);
//        values.put("green1",0);
//        values.put("green2",5);
//        values.put("blue1",250);
//        values.put("blue2",255);
//        db.insert("color",null,values);
    }

    //Touch action
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent == null) {
            return false;
        }
        final float normalizedX = (motionEvent.getX() / view.getWidth()) * 2 - 1;
        final float normalizedY = -((motionEvent.getY() / view.getHeight()) * 2 - 1);




        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时获取当前屏幕坐标, 并暂停地球仪的旋转
                //Obtain the current screen coordinates when a finger is on the screen, and pause the rotation of the globe
                mode = DRAG;
                isMove = false;
                x = motionEvent.getX();
                y = motionEvent.getY();
                location[0] = Math.round(x);
                location[1] = Math.round(y);
                startTime = System.currentTimeMillis();

                glsv_content.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        GlobeRender.handleTouchDown(normalizedX,normalizedY);
                    }
                });
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

                glsv_content.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        GlobeRender.handleTouchMove(normalizedX,normalizedY);
                    }
                });
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) < 0.1 * 1000L) {
                    int color = GBData.getColor(Math.round(x), Math.round(y));
                    int a = Color.alpha(color);
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);

                    //display color data
                    Toast toast = Toast.makeText(MainActivity.this,String.valueOf(r)+String.valueOf(g)+String.valueOf(b),Toast.LENGTH_SHORT);
                    toast.show();

                    try{
                        String sql="";
                        if (spTitle.getSelectedItem() == "Physical Globe"){
                            sql = getResources().getString(R.string.physicalsqlsearch,r,g,b);
                        }
                        if (spTitle.getSelectedItem() == "Continent Globe"){
                            sql = getResources().getString(R.string.continentsqlsearch,r,g,b);
                        }
                        if (spTitle.getSelectedItem() == "Political Globe") {
                            sql = getResources().getString(R.string.politicalsqlsearch,r,g,b);
                        }
                        Cursor cursor = db.rawQuery(sql, null);
                        if (cursor!=null){
                            cursor.moveToFirst();//转移到结果的第一行
                            while (!cursor.isAfterLast()) {
                                id = cursor.getInt(cursor.getColumnIndex("id"));
                                if (spTitle.getSelectedItem() == "Physical Globe"){
                                    itemTitle = cursor.getString(cursor.getColumnIndex("title"));
                                    itemArea = String.valueOf(cursor.getInt(cursor.getColumnIndex("area")))+ " km² ";
                                    String itemTitle1 = itemTitle.toLowerCase().replaceAll(" ","");
                                    int resid = getResources().getIdentifier("map"+itemTitle1, "drawable", getPackageName());
                                    itemRes = getDrawable(resid);
                                    sendString = itemTitle;
                                    onClickPopIcon(glsv_content);
                                }
                                if (spTitle.getSelectedItem() == "Continent Globe"){
                                    itemTitle = cursor.getString(cursor.getColumnIndex("title"));
                                    itemArea = String.valueOf(cursor.getInt(cursor.getColumnIndex("area")))+ " km²";
                                    itemPopulation = String.valueOf(cursor.getInt(cursor.getColumnIndex("population")));
                                    String itemTitle1 = itemTitle.toLowerCase().replaceAll(" ","");
                                    int resid = getResources().getIdentifier("label"+itemTitle1, "drawable", getPackageName());
                                    itemRes = getDrawable(resid);
                                    sendString = itemTitle;
                                    onClickPopIcon(glsv_content);
                                }
                                if (spTitle.getSelectedItem() == "Political Globe") {
                                    name = cursor.getString(cursor.getColumnIndex("name"));
                                    //display country code
//                                    Toast.makeText(MainActivity.this,name,Toast.LENGTH_SHORT).show();
                                    name = name.toLowerCase();
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
                                    countryApi(name);
                                }
                                cursor.moveToNext();
                            }
                        }else {
                            Toast.makeText(MainActivity.this,"No matching data could be found",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"API error, please check API availability",Toast.LENGTH_SHORT).show();
                    }
                }
                //抬起时地球仪继续旋转
                //The globe continues to rotate when the fingers are raised
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (count % 2 == 0) {
                            isMove = true;
                        } else {
                            isMove = false;
                        }
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
        URL url = new URL("https://www.countryflags.io/"+name+"/flat/64.png");
        //定义一个 InputStream 获取url的输入流（或者直接将url.openStream放入decodeStream解析成bitmap）
        InputStream is=url.openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x11:
                    itemRes = new BitmapDrawable((Bitmap) msg.obj);
                    break;
            }
        }
    };

    //countryapi
    private void countryApi(final String name){
        CountryApi.GetCountryApiRes getCountryRes = new CountryApi.GetCountryApiRes();
        getCountryRes.execute("https://restcountries-v1.p.rapidapi.com/alpha/" + name);
        getCountryRes.setOnAsyncResponse(new CountryApi.AsyncResponse(){
            @Override
            public void onDataReceivedSuccess(String string) {
                String result = string;
//                Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                String [] arr = result.split("-");

                sendCode = name;
                sendString = arr[0];
                itemTitle = arr[0];
                itemCapital = arr[1];
                itemArea = arr[3]+ " km²";
                itemPopulation = arr[2];
                itemTime = arr[4];

                onClickPopIcon(glsv_content);
            }

            @Override
            public void onDataReceivedFailed() {
                Toast.makeText(MainActivity.this,"data received failed!",Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position){
            case 0:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe7);
                break;
            case 1:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe6);
                break;
            case 2:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe9);
                break;
            case 3:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe3);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast toast = Toast.makeText(this,"Nothing is selected by the user",Toast.LENGTH_SHORT);
        toast.show();
    }

    //Overwrite to change the font size and color of Spinner
    private class SpinnerAdapter extends ArrayAdapter<String> {
        Context context;
        String[] items = new String[] {};

        public SpinnerAdapter(final Context context, final int textViewResourceId, final String[] objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextSize(30);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_item, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextSize(30);
            return convertView;
        }
    }

    public void onClickPopIcon(View view) {
        count = 1;
        isMove = false;
        btnRotate.setImageDrawable(getDrawable(R.drawable.restart));
        final Toast toast = Toast.makeText(MainActivity.this, "Automatic rotation: Close", Toast.LENGTH_SHORT);
        toast.show();

        LabelMenu labelMenu = new LabelMenu(this);
        labelMenu.setTriangleIndicatorViewColor(Color.WHITE);
        labelMenu.setBackgroundResource(R.drawable.label_white);
        labelMenu.setItemTextColor(Color.BLACK);
        labelMenu.setIsShowIcon(true);

        labelMenu.setOnItemClickListener(new LabelMenu.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case 6:
                        startFloatingMusic(glsv_content);
                        break;
                    case 7:
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        if(spTitle.getSelectedItem() == "Political Globe"){
                            intent.putExtra("globe","click");
                        }
                        intent.putExtra("country",sendString);
                        intent.putExtra("code",sendCode);
                        startActivity(intent);
                        break;
                }
            }
        });
        labelMenu.setMenuList(getMenuList());

        labelMenu.show(glsv_content,location);
    }

    private List<MenuItem> getMenuList() {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem(itemRes,1, itemTitle));
        if(spTitle.getSelectedItem() == "Physical Globe"){
            list.add(new MenuItem(getDrawable(R.drawable.area),3, "Area: "+itemArea));
            list.add(new MenuItem(getDrawable(R.drawable.info),7, "More Info"));
        }
        if(spTitle.getSelectedItem() == "Continent Globe"){
            list.add(new MenuItem(getDrawable(R.drawable.area),3, "Area: "+itemArea));
            list.add(new MenuItem(getDrawable(R.drawable.population),4, "Population: "+itemPopulation));
            list.add(new MenuItem(getDrawable(R.drawable.info),7, "More Info"));
        }
        if(spTitle.getSelectedItem() == "Political Globe"){
            list.add(new MenuItem(getDrawable(R.drawable.capital),2, "Capital: "+itemCapital));
            list.add(new MenuItem(getDrawable(R.drawable.area),3, "Area: "+itemArea));
            list.add(new MenuItem(getDrawable(R.drawable.population),4, "Population: "+itemPopulation));
            list.add(new MenuItem(getDrawable(R.drawable.timezone),5, "Time Zone: "+itemTime));
            list.add(new MenuItem(getDrawable(R.drawable.music),6, "National Anthem"));
            list.add(new MenuItem(getDrawable(R.drawable.info),7, "More Info"));
        }
        return list;
    }
}
