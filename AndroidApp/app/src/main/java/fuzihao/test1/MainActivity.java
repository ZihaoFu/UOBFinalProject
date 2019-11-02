package fuzihao.test1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private GLSurfaceView glsv_content;
    //使用OpenGL库创建一个材质(Texture)，首先是获取一个Texture Id。
    //Use the OpenGL library to create a texture.
    private int[] textures = new int[1];//Get a texture ID.
    private int divide = 40;
    private int radius = 3;
    private float move = 0.1f;
    private float angle = 0;//横向旋转角度 Lateral rotation angle
    private float angle2 = 0;//竖向旋转角度 Vertical rotation angle

    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> mTextureCoords = new ArrayList<FloatBuffer>();
    private Bitmap mBitmap;

    private ImageButton btnSetting;
    private TextView txtTitle;
    private ImageButton btnDay;
    int dayNight = 0;
    private ImageButton btnRotate;
    int count = 0;
    Boolean isMove = true;

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

    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

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
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe1);
            title = "Geographic Globe";
            txtTitle.setText(title);
        } else if (select == 1) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe2);
            title = "National Globe";
            txtTitle.setText(title);
        } else if (select == 2) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe3);
            title = "Time Zone Globe";
            txtTitle.setText(title);
        }
        //绑定View, 初始化Renderer,并设置触摸监听器
        //Bind view, initialise renderer, and set touch listener
        glsv_content = (GLSurfaceView) findViewById(R.id.glsv_content);
        glsv_content.setRenderer(new GLRender());
        glsv_content.setOnTouchListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "User cancelled!", Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            setUpVirtualDisplay();
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
        txtTitle.setVisibility(View.VISIBLE);
        btnDay.setVisibility(View.VISIBLE);
        btnRotate.setVisibility(View.VISIBLE);
    }

    private void initView() {
        //将控件绑定至变量 Bind controls to variables
        btnSetting = (ImageButton) findViewById(R.id.btnSetting);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnDay = (ImageButton) findViewById(R.id.btnDayAndNight);
        btnRotate = (ImageButton) findViewById(R.id.btnRotate);

        //给设置按钮添加点击监听器 Add click listener to setting button
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                btnSetting.setVisibility(View.INVISIBLE);
                txtTitle.setVisibility(View.INVISIBLE);
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
                    btnDay.setImageDrawable(getDrawable(R.drawable.dayopen));
                    Toast toast = Toast.makeText(MainActivity.this, "Display of day and night areas: Close", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(MainActivity.this, VRActivity.class);
                    startActivity(intent);
                } else {
                    btnDay.setImageDrawable(getDrawable(R.drawable.dayclose));
                    Toast toast = Toast.makeText(MainActivity.this, "Display of day and night areas: Open", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
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

                    angle = angle + changex / 360;
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

//                Toast toast = Toast.makeText(MainActivity.this,String.valueOf(r)+String.valueOf(g)+String.valueOf(b),Toast.LENGTH_SHORT);
//                toast.show();

                    //button control
                    //france
                    if ((r == 0) && (g == 0) && (b >= 253)) {
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("map", 8);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    //uk
                    if ((r >= 253) && (g == 0) && (b == 0)) {
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("map", 9);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    //russia
                    if (color == -85326) {
//                if (r==255 && g==178 && b == 178){
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("map", 10);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    //us
                    if (color == -151428) {
//                if (r==255 && g==176 && b == 125){
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("map", 11);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    //china
                    if (color == -141812) {
                        intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("map", 12);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

                mode = 0;
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


            //Test part***********************************
            //在位置（1，1，1）处定义光源
//            float lightAmbient[] = new float[]{0.3f, 0.3f, 0.3f, 1};//环境光
//            float lightDiffuse[] = new float[]{1, 1, 1, 1};//漫射光
//            float lightPos[] = new float[]{1, 1, 1, 1};//位置
//            gl.glEnable(GL10.GL_LIGHTING);//禁用颜色抖动 消除可能性的性能高消耗
//            gl.glEnable(GL10.GL_LIGHT0);//设置清除颜色缓冲区时用的RGBA颜色值
//
//             //设置环境光
//            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
//            //设置漫射光
//            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
//            //设置光源位置
//            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);

            //定义立方体材质
//            float matAmbient[] = new float[]{1, 1, 1, 1};
//            float matDiffuse[] = new float[]{1, 1, 1, 1};
//            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
//            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
            //******************************************

            // 告诉OpenGL去生成textures.textures中存放了创建的Texture ID
            // Function to generate texture
            gl.glGenTextures(1, textures, 0);
            //通知OpenGL库使用这个Texture
            //Binding texture
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
            //将Bitmap资源和Texture绑定起来
            //Bind bitmap resource and texture
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
//            gl.glReadPixels();
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
            gl.glRotatef(-angle, 0, 1, 0);

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
            globe.drawGlobe(gl);//Call the globe class to draw a globe
            if (isMove == true) {
                angle = angle + move; // Auto rotate globe
            } else {
                angle = angle;
            }

            //add label
//            GLU.gluLookAt(gl,0.0f, 0.0f, 3.01f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
//            gl.glPushMatrix();
//            gl.glScalef(3f,3f,3f);
//            label.draw(gl);
//            gl.glPopMatrix();
        }
    }
}

//    public class Ray{
//        public Ray(GL10 gl, int width, int height, float xTouch, float yTouch){
//            MatrixGrabber matrixGrabber = new MatrixGrabber();
//            matrixGrabber.getCurrentState(gl);// get modelView and projectionView
//
//            // get the near and far ords for the click
//            int[] viewport = {0,0,width,height};
//            float[] temp = new float[4];
//            float[] temp2 = new float[4];
//
//            float winx = xTouch;
//            float winy =(float)viewport[3] - yTouch;
//            //use GLU.gluUnProject function
//            int result = GLU.gluUnProject(winx, winy, 1.0f, matrixGrabber.mModelView, 0, matrixGrabber.mProjection, 0, viewport, 0, temp, 0);
//            Matrix.multiplyMV(temp2, 0, matrixGrabber.mModelView, 0, temp, 0);
//            if(result == GL10.GL_TRUE){
//                nearCoOrds[0] = temp2[0] / temp2[3];
//                nearCoOrds[1] = temp2[1] / temp2[3];
//                nearCoOrds[2] = temp2[2] / temp2[3];
//            }
//            result = GLU.gluUnProject(winx, winy, 0, matrixGrabber.mModelView, 0, matrixGrabber.mProjection, 0, viewport, 0, temp, 0);
//            Matrix.multiplyMV(temp2,0,matrixGrabber.mModelView, 0, temp, 0);
//                if(result == GL10.GL_TRUE){
//                    farCoOrds[0] = temp2[0] / temp2[3];
//                    farCoOrds[1] = temp2[1] / temp2[3];
//                    farCoOrds[2] = temp2[2] / temp2[3];
//                }
//        }
//        public float[] nearCoOrds = new float[3];
//        public float[] farCoOrds = new float[3];
//    }
