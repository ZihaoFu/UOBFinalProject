package fuzihao.test1.Model;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import fuzihao.test1.Activity.MainActivity;

import static android.content.ContentValues.TAG;
import static fuzihao.test1.Activity.MainActivity.angle;
import static fuzihao.test1.Activity.MainActivity.angle2;
import static fuzihao.test1.Activity.MainActivity.globe;
import static fuzihao.test1.Activity.MainActivity.isDayNight;
import static fuzihao.test1.Activity.MainActivity.isMove;
import static fuzihao.test1.Activity.MainActivity.mBitmap;
import static fuzihao.test1.Activity.MainActivity.move;
import static fuzihao.test1.Activity.MainActivity.origin;

//Renderer class
public class GlobeRender implements GLSurfaceView.Renderer {
    private int[] textures = new int[2];//Get a texture ID.
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

    public static void handleTouchDown(float normalizedX, float normalizedY) {
        Log.w(TAG, "handleTouchDown normalizedX*normalizedY == " +normalizedX+" "+normalizedY);
    }

    public static void handleTouchMove(float normalizedX, float normalizedY) {
        Log.w(TAG, "handleTouchMove normalizedX*normalizedY == " +normalizedX+" "+normalizedY);
    }
}
