package fuzihao.test1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private GLSurfaceView glsv_content;
    //使用OpenGL库创建一个材质(Texture)，首先是获取一个Texture Id。
    private int[] textures = new int[1];
    private int divide = 40;
    private int radius = 3;
    private float move = 0.1f;
    private float angle = 0;//横向旋转角度
    private float angle2 = 0;//竖向旋转角度
//    private float angle2z = 80.0f;
//    private float angle2y = 0;
    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    private ArrayList<FloatBuffer> mTextureCoords = new ArrayList<FloatBuffer>();
    private Bitmap mBitmap;

    private ImageButton btnSetting;
    private TextView txtTitle;
    private ImageButton btnDay;

    private Intent intent;

    private int select = 0;
    private String title = "";

    private Float eyeX=0.0f;
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
    float origin = 1.0f;
    float zoom = 1.0f;

    Timer timer = new Timer();

    private Globe globe = new Globe();
    private circle label = new circle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        Intent intentFromSelect = getIntent();
        select = intentFromSelect.getIntExtra("num",0);
        // 绑定地图
        if (select==0){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe1);
            title = "Geographic Globe";
            txtTitle.setText(title);
        }else if(select==1){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe2);
            title = "National Globe";
            txtTitle.setText(title);
        }else if(select==2){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globe3);
            title = "Time Zone Globe";
            txtTitle.setText(title);
        }

        glsv_content = (GLSurfaceView)findViewById(R.id.glsv_content);
        glsv_content.setRenderer(new GLRender());
        glsv_content.setOnTouchListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        btnSetting.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
        btnDay.setVisibility(View.VISIBLE);
    }

    private void initView(){
        btnSetting=(ImageButton)findViewById(R.id.btnSetting);
        txtTitle=(TextView) findViewById(R.id.txtTitle);
        btnDay=(ImageButton)findViewById(R.id.btnDayAndNight);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this,Setting.class);
                startActivity(intent);
                btnSetting.setVisibility(View.INVISIBLE);
                txtTitle.setVisibility(View.INVISIBLE);
                btnDay.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时暂停地球仪的旋转
                mode = DRAG;
                x = motionEvent.getX();
                y = motionEvent.getY();
                move=0;
                break;
            case MotionEvent.ACTION_MOVE:
                move = 0f;
                if (mode == ZOOM) {
                    float newDist = distance(motionEvent);
                    if (newDist > oldDist + 1){
                        zoom=(newDist / oldDist);
                        origin = origin*zoom;
                        oldDist = newDist;
                    }else if(newDist < oldDist - 1){
                        zoom=(newDist / oldDist);
                        origin = origin*zoom;
                        oldDist = newDist;
                    }
                }else if (mode == DRAG){
                    newx = motionEvent.getX();
                    newy = motionEvent.getY();
                    changex = -(newx-x);
                    changey = (newy-y);
                    angle = angle + changex/360;
                    angle2 = angle2 + changey/360;

//                    if (changey >= 0){
//                        angle = angle + changex/100;
//                        angle2 = angle2 + changey/100;
//                        angle2z = (12800 - (160 * (float) Math.sin(angle2/160)) * (160 * (float) Math.sin(angle2/160))) / 160;
//                        angle2y = (float) Math.sqrt(6400 - (angle2z * angle2z));
//                    }else{
//                        angle = angle + changex/100;
//                        angle2 = angle2 + changey/100;
//                        angle2y = (12800 - (160 * (float) Math.sin(angle2/160)) * (160 * (float) Math.sin(angle2/160))) / 160;
//                        angle2z = (float) Math.sqrt(6400 - (angle2y * angle2y));
////                        angle2y = -angle2y;
//                    }

//                    newx = motionEvent.getRawX() - x;
//                    newy = motionEvent.getRawY() - y;
//                    float a = 180.0f / 32000;
//                    angle += newx * a;
//                    angle2 += newy * a;
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                //抬起时地球仪继续旋转
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        move=0.1f;
                    }
                },2000);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                move = 0f;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = distance(motionEvent);//两点按下时的距离
                move = 0f;
                break;
        }
        return true;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()& MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                //有按下动作时暂停地球仪的旋转
//                mode = 1;
//                x = ev.getX();
//                y = ev.getY();
//                move=0;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (mode >= 2) {
//                    float newDist = distance(ev);
//                    if (newDist > oldDist + 1){
//                        zoom=(newDist / oldDist);
//                        oldDist = newDist;
//                    }else if(newDist < oldDist+1){
//                        zoom=(newDist / oldDist);
//                        oldDist = newDist;
//                    }
//
//                }else{
//                    newx = ev.getX();
//                    newy = ev.getY();
//                    float changex = -(newx-x);
//                    float changey = (newy-y);
//                }
////                angle = changex;
//                break;
//            case MotionEvent.ACTION_UP:
//                mode = 0;
//                //抬起时地球仪继续旋转
//                move=0.1f;
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                mode -= 1;
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                oldDist = distance(ev);//两点按下时的距离
//                mode += 1;
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    private float distance(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float dis = (float) Math.sqrt(x*x+y*y);
        return dis;
    }

    @Override
    protected void onDestroy() {
        mBitmap.recycle();
        super.onDestroy();
    }

    private class GLRender implements GLSurfaceView.Renderer {

//      private Triangle triangle;
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 背景：白色
            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            // 启动阴影平滑
            gl.glShadeModel(GL10.GL_SMOOTH);
            // 复位深度缓存
            gl.glClearDepthf(1f);
            // 所做深度测试的类型，同时必须开启GL10.GL_DEPTH_TEST
            gl.glDepthFunc(GL10.GL_LEQUAL);
            // 启动某功能，对应的glDisable是关闭某功能。GL_DEPTH_TEST指的是深度测试
            gl.glEnable(GL10.GL_DEPTH_TEST);
            //gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

            //在位置（1，1，1）处定义光源
            float lightAmbient[] = new float[]{0.3f, 0.3f, 0.3f, 1};
            float lightDiffuse[] = new float[]{1, 1, 1, 1};
            float lightPos[] = new float[]{1, 1, 1, 1};
            gl.glEnable(GL10.GL_LIGHTING);//禁用颜色抖动 消除可能性的性能高消耗
            gl.glEnable(GL10.GL_LIGHT0);//设置清除颜色缓冲区时用的RGBA颜色值

            //设置环境光
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);

            //设置漫射光
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);

            //设置光源位置
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);

            //定义立方体材质
            float matAmbient[] = new float[]{1, 1, 1, 1};
            float matDiffuse[] = new float[]{1, 1, 1, 1};
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);

            // 告诉OpenGL去生成textures.textures中存放了创建的Texture ID
            gl.glGenTextures(1, textures, 0);
            //通知OpenGL库使用这个Texture
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
            //用来渲染的Texture可能比要渲染的区域大或者小,所以需要设置Texture需要放大或是缩小时OpenGL的模式
            //常用的两种模式为GL10.GL_LINEAR和GL10.GL_NEAREST。
            //需要比较清晰的图像使用GL10.GL_NEAREST,而使用GL10.GL_LINEAR则会得到一个较模糊的图像
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            //当定义的材质坐标点超过UV坐标定义的大小(UV坐标为0,0到1,1)，这时需要告诉OpenGL库如何去渲染这些不存在的Texture部分。
            //有两种设置:GL_REPEAT 重复Texture。GL_CLAMP_TO_EDGE 只靠边线绘制一次。
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            //将Bitmap资源和Texture绑定起来
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
//            triangle = new Triangle(mview);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //GL_PROJECTION和GL_MODELVIEW有什么区别？能否去掉GL_PROJECTION？
            gl.glViewport(0,0,width,height);//将当前矩阵模式设为投影矩形
            gl.glMatrixMode(GL10.GL_PROJECTION);//初始化单位矩阵
            gl.glLoadIdentity();//计算透视窗口的宽度高度比
            //第二个参数是视角，越大则视野越广
            //第三个参数是宽高比
            //第四个参数表示眼睛距离物体最近处的距离
            //第五个参数表示眼睛距离物体最远处的距离
            //gluPerspective和gluLookAt需要配合使用，才能调节观察到的物体大小
            //GLU.gluPerspective(gl, 50, (float) width / (float) height, 0.1f, 100.0f);
            GLU.gluPerspective(gl, 8, (float) width / (float) height, 0.1f, 100.0f);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);//启用顶点坐标数据
            //下面的glLoadIdentity是否确有必要？
            gl.glLoadIdentity();
            //这个是俯视，眼睛在y坐标5.0，球体半径为3
            //GLU.gluLookAt(gl, 0.0f, 5.0f, 15.0f
            //这个是平视，眼睛在y坐标0.0，球体半径为3
            //修改eyeY可更改上下角度, eyeZ可更改远近
            //GLU.gluLookAt(gl, 0.0f, 70.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);//正上
            //GLU.gluLookAt(gl, 0.0f, -70.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);//正下
            //GLU.gluLookAt(gl, 0.0f, 30.0f, 60.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
            GLU.gluLookAt(gl, 0.0f, 0.0f, 80.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

            if (angle2>=60){
                angle2 = 60.0f;
                gl.glRotatef(60.0f,1,0,0);
            }else if (angle2<=-60){
                angle2 = -60.0f;
                gl.glRotatef(-60.0f,1,0,0);
            }else{
                gl.glRotatef(angle2,1,0,0);
            }
            gl.glRotatef(-angle, 0, 1, 0);  //设置旋转动画

            if (origin>=3.0f){
                origin = 3.0f;
                gl.glScalef(3f,3f,3f);
            }else if(origin<=0.5f){
                origin = 0.5f;
                gl.glScalef(0.5f,0.5f,0.5f);
            }else
            {
                gl.glScalef(origin,origin,origin);
            }
            //gl.glTranslatef(3, 0, 0);  //设置平移动画
            //gl.glRotatef(angle, 0, 0, -1);
            //gl.glRotatef(angle, 0, -1, 0);
//            drawGlobe(gl);
            globe.drawGlobe(gl);
            angle=angle+move;

            //add label
//            gl.glLoadIdentity();
            GLU.gluLookAt(gl,0.0f, 0.0f, 3.01f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f);
            gl.glPushMatrix();
            // Rotate Star A counter-clockwise.
//            gl.glRotatef(angle, 0, 0, 1);
//            gl.glTranslatef(3, 0, 0);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            label.draw(gl);
            gl.glPopMatrix();
        }
    }
}
