package fuzihao.test1;

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

import static fuzihao.test1.FloatingMusicPlayerService.btnMusic;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, AdapterView.OnItemSelectedListener {
    private GLSurfaceView glsv_content;
    //使用OpenGL库创建一个材质(Texture)，首先是获取一个Texture Id。
    //Use the OpenGL library to create a texture.
    private int[] textures = new int[2];//Get a texture ID.
    private int divide = 40;
    private int radius = 3;
    private float move = 0.1f;
    private float angle = 0;//横向旋转角度 Lateral rotation angle
    private float angle2 = 0;//竖向旋转角度 Vertical rotation angle

    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> mTextureCoords = new ArrayList<FloatBuffer>();
    private Bitmap mBitmap;

    private ImageButton btnSetting;
//    private TextView txtTitle;
    private Spinner spTitle;
    private ImageButton btnDay;
    int dayNight = 0;
    private ImageButton btnRotate;
    int count = 0;
    Boolean isMove = true;
    Boolean isDayNight = false;

    private long startTime = 0;
    private long endTime = 0;

    private Intent intent;

    private int select = 0;
    private String title = "";

    private Float eyeX = 0.0f;
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
    float origin = 1.0f;//Original ratio
    float zoom = 1.0f;  //Zoom ratio

    Timer timer = new Timer();

    //Call other classes
    private Globe globe = new Globe();
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
//        SQLiteStudioService.instance().start(this);
//        SqlScoutServer.create(this, getPackageName());

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
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe2);
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
        glsv_content.setRenderer(new GLRender());
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

                    int hour = 0;
                    double res = Math.cos(Math.PI*hour/12)*4;
                    Toast toast2 = Toast.makeText(MainActivity.this, String.valueOf(res), Toast.LENGTH_SHORT);
                    toast2.show();

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
                    int color = GBData.getColor(Math.round(x), Math.round(y));
                    int a = Color.alpha(color);
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);

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
                                    Toast.makeText(MainActivity.this,name,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this,"Can not find anything!",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"Search Error!",Toast.LENGTH_SHORT).show();
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
        getCountryRes.setOnAsyncResponse(new CountryApi.AsyncResponse2(){
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

            // android.R.id.text1 is default text view in resource of the android.
            // android.R.layout.simple_spinner_item is default layout in resources of android.

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextSize(30);
            return convertView;
        }
    }

    //Renderer class
    private class GLRender implements GLSurfaceView.Renderer {
        //在GLSurfaceView内Surface被创建时调用
        //onSurfaceCreated will be called when the Surface is created in GLSurfaceView
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 背景：白色 Background: white
            //The glClearColor function specifies clear values for the color buffers.
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            // 设置着色模式 Set shade mode GL_SMOOTH/GL_FLAT
            // 启动阴影平滑 独立的处理图元中各个顶点的颜色
            // use GL_SMOOTH to independently handle the color of each vertex in the entity
            gl.glShadeModel(GL10.GL_SMOOTH);
            // 复位深度缓存
            //Specify a depth value,this value will be used by the glclear function to clean up the depth buffer
            gl.glClearDepthf(1f);
            // 所做深度测试的类型，同时必须开启GL10.GL_DEPTH_TEST
            // Specify depth comparison function
            // GL_LEQUAL: 如果输入的深度值小于或等于参考值，则通过
            // Depth value entered <= reference value, pass
            gl.glDepthFunc(GL10.GL_LEQUAL);
            // 启动某功能，对应的glDisable是关闭某功能。GL_DEPTH_TEST指的是深度测试
            // OpenGL只绘制最前面的一层, 被遮挡的不会绘制
            // GL_DEPTH_TEST: OpenGL draws only the first layer,and the occluded layer will not be drawn.
            gl.glEnable(GL10.GL_DEPTH_TEST);

            // 告诉OpenGL去生成textures.textures中存放了创建的Texture ID
            // Function to generate texture
            gl.glGenTextures(2, textures, 0);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            //通知OpenGL库使用这个Texture
//            //Binding texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
            //用来渲染的Texture可能比要渲染的区域大或者小,所以需要设置Texture需要放大或是缩小时OpenGL的模式
            //常用的两种模式为GL10.GL_LINEAR和GL10.GL_NEAREST。
            //需要比较清晰的图像使用GL10.GL_NEAREST,而使用GL10.GL_LINEAR则会得到一个较模糊的图像
            //set Texture sampling
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            //当定义的材质坐标点超过UV坐标定义的大小(UV坐标为0,0到1,1)，这时需要告诉OpenGL库如何去渲染这些不存在的Texture部分。
            //有两种设置:GL_REPEAT 重复Texture。GL_CLAMP_TO_EDGE 只靠边线绘制一次。
            //Set texture stretch,edge processing
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
//            //将Bitmap资源和Texture绑定起来
//            //Bind bitmap resource and texture
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        }

        //Surface尺寸改变时调用
        //It will be called when the surface size changes
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
//            int[] viewPort = new int[]{0,0,width,height};
            gl.glViewport(0, 0, width, height);//将标准化的设备坐标转换为屏幕坐标 Convert normalized device coordinate to screen coordinate
//            int[] viewport = {0,0,width,height};
            gl.glMatrixMode(GL10.GL_PROJECTION);//将当前矩阵模式设为投影矩形以设置透视关系 Set the current matrix mode to GL_PROJECTION to set the perspective relationship
            gl.glLoadIdentity();//初始化单位矩阵 Initialize unit matrix
            //计算透视窗口的宽度高度比
            //第二个参数是视角，越大则视野越广
            //第三个参数是宽高比
            //第四个参数表示眼睛距离物体最近处的距离
            //第五个参数表示眼睛距离物体最远处的距离
            //gluPerspective和gluLookAt需要配合使用，才能调节观察到的物体大小
            //GLU.gluPerspective(gl, 50, (float) width / (float) height, 0.1f, 100.0f);
            GLU.gluPerspective(gl, 8, (float) width / (float) height, 0.1f, 100.0f);
            gl.glMatrixMode(GL10.GL_MODELVIEW);//切换到GL_MODELVIEW来绘制图像 Switch to GL_MODELVIEW to paint the texture
            gl.glLoadIdentity();
        }

        //重复调用这个方法 Call this method repeatedly
        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);//清除颜色缓冲以及深度缓冲 Clear color buffer and depth buffer
            gl.glLoadIdentity();

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);

//            GLES20.glEnable(GLES20.GL_BLEND);
//            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
            //这个是俯视，眼睛在y坐标5.0，球体半径为3
            //GLU.gluLookAt(gl, 0.0f, 5.0f, 15.0f
            //这个是平视，眼睛在y坐标0.0，球体半径为3
            //修改eyeY可更改上下角度, eyeZ可更改远近
            //GLU.gluLookAt(gl, 0.0f, 70.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);//正上
            //GLU.gluLookAt(gl, 0.0f, -70.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);//正下
            //GLU.gluLookAt(gl, 0.0f, 30.0f, 60.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            //设置眼睛的位置，眼睛朝向的位置，以及头顶朝向的方向
            //Set the position of the eyes, a position where the eyes are facing and the direction of the head
            GLU.gluLookAt(gl, 0.0f, 0.0f, 80.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

            if (isDayNight){
                //get system time
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                //calculate the change of direction
                float lightX = (float) (Math.cos(Math.PI*hour/12)*5);
                float lightY = (float) (Math.sin(Math.PI*hour/12)*5);

                gl.glEnable(GL10.GL_LIGHTING);//启用灯光总开关
                gl.glEnable(GL10.GL_LIGHT0);//启用第0盏灯
                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[] {0.1f, 0.1f, 0.1f, 1}, 0);
                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[] { 1.0f, 1.0f, 1.0f, 0.5f }, 0);
                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[]{-lightX, 0, lightY, 0}, 0);
//                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, new float[] { 0, 0, 0 },0);
//                gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 45f);
//                gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_EXPONENT, 50f);
            }else
            {
                gl.glDisable(GL10.GL_LIGHTING);
                gl.glDisable(GL10.GL_LIGHT0);
            }

            // 设置旋转动画 Set rotation angle and direction (gestures)
            if (angle2 >= 60) {
                angle2 = 60.0f;
                gl.glRotatef(60.0f, 1, 0, 0);
            } else if (angle2 <= -60) {
                angle2 = -60.0f;
                gl.glRotatef(-60.0f, 1, 0, 0);
            } else {
                gl.glRotatef(angle2, 1, 0, 0);
            }
            gl.glRotatef(angle, 0, 1, 0);

            // 设置缩放动画 Set zoom ratio(gestures)
            if (origin >= 5.0f) {
                origin = 5.0f;
                gl.glScalef(5f, 5f, 5f);
            } else if (origin <= 0.5f) {
                origin = 0.5f;
                gl.glScalef(0.5f, 0.5f, 0.5f);
            } else {
                gl.glScalef(origin, origin, origin);
            }
            globe.drawGlobe(gl,mBitmap);//Call the globe class to draw a globe
            if (isMove == true) {
                angle = angle + move; // Auto rotate globe
            } else {
                angle = angle;
            }
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
                        intent = new Intent(MainActivity.this,MapActivity.class);
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
