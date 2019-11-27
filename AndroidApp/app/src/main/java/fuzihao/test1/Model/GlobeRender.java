package fuzihao.test1.Model;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import fuzihao.test1.Geometry;

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

    private static float[] mVMatrix = new float[16];
    private static float[] mProjMatrix = new float[16];
    private static float[] mMVPMatrix = new float[16];
    private static float[] invertedViewProjectionMatrix = new float[16];

    public static float latitude = 0;//纬度
    public static float longitude = 0;//经度

    public static int wwidth;
    public static int wheight;

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

        int[] fbos = new int[1];
        GLES20.glGenFramebuffers(1, fbos,0);
        int fboId = fbos[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        gl.glGenTextures(2, textures, 0);
        //通知OpenGL库使用这个Texture
        //Binding texture
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
        wwidth = width;
        wheight = height;
        gl.glViewport(0, 0, width, height);//将标准化的设备坐标转换为屏幕坐标 Convert normalized device coordinate to screen coordinate
        gl.glMatrixMode(GL10.GL_PROJECTION);//将当前矩阵模式设为投影矩形以设置透视关系 Set the current matrix mode to GL_PROJECTION to set the perspective relationship
        gl.glLoadIdentity();//初始化投影矩阵 Initialize PROJECTION matrix
//        //计算透视窗口的宽度高度比
//        //第二个参数是视角，越大则视野越广
//        //第三个参数是宽高比
//        //第四个参数表示眼睛距离物体最近处的距离
//        //第五个参数表示眼睛距离物体最远处的距离
//        //gluPerspective和gluLookAt需要配合使用，才能调节观察到的物体大小
//        Matrix.perspectiveM(mProjMatrix,0,8, (float) width / (float) height, 0.1f, 100.0f);
//        Matrix.frustumM(mProjMatrix,0,-(float) width / (float) height,(float) width / (float) height,-1,1,0.1f,100f);
        GLU.gluPerspective(gl, 8, (float) width / (float) height, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);//切换到GL_MODELVIEW来绘制图像 Switch to GL_MODELVIEW to paint the texture
        gl.glLoadIdentity();//初始化MODELVIEW矩阵 Initialize MODELVIEW matrix
    }

    //重复调用这个方法 Call this method repeatedly
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);//清除颜色缓冲以及深度缓冲 Clear color buffer and depth buffer
        gl.glLoadIdentity();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);

        //这个是俯视，眼睛在y坐标5.0，球体半径为3
        //GLU.gluLookAt(gl, 0.0f, 5.0f, 15.0f
        //这个是平视，眼睛在y坐标0.0，球体半径为3
        //修改eyeY可更改上下角度, eyeZ可更改远近
        //设置眼睛的位置，眼睛朝向的位置，以及头顶朝向的方向
        //Set the position of the eyes, a position where the eyes are facing and the direction of the head
        GLU.gluLookAt(gl, 0.0f, 0.0f, 80.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
//        Matrix.setLookAtM(mVMatrix,0,0.0f, 0.0f, 80.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

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
        if(angle >=360){
            angle = 0;
        }
        if(angle <=-360){
            angle = 0;
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
        final float[] nearPointNdc = { normalizedX, normalizedY, -1, 1 };
        final float[] farPointNdc = { normalizedX, normalizedY, 1, 1 };
        float[] NearPos4 = new float[4];
        float[] FarPos4 = new float[4];
        RayPickingHelper.Vector NearPos = new RayPickingHelper.Vector(0,0,0);
        RayPickingHelper.Vector FarPos = new RayPickingHelper.Vector(0,0,0);
        RayPickingHelper.Vector newPos = new RayPickingHelper.Vector(0,0,0);

        Matrix.setIdentityM(mVMatrix,0);
        Matrix.setIdentityM(mProjMatrix,0);
        Matrix.setIdentityM(mMVPMatrix,0);
        Matrix.setIdentityM(invertedViewProjectionMatrix,0);
        Matrix.perspectiveM(mProjMatrix,0,8, (float) wwidth / (float) wheight, 0.1f, 100.0f);
        Matrix.setLookAtM(mVMatrix,0,0.0f, 0.0f, 80.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mVMatrix,0,-angle2, 1, 0, 0);
        Matrix.rotateM(mVMatrix,0,-angle, 0, 1, 0);
        Matrix.scaleM(mVMatrix,0,origin, origin, origin);
        Matrix.multiplyMM(mMVPMatrix,0,mProjMatrix,0,mVMatrix,0);
        Matrix.invertM(invertedViewProjectionMatrix, 0, mMVPMatrix, 0);

        Matrix.multiplyMV(NearPos4, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(FarPos4, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        NearPos.x = NearPos4[0]/NearPos4[3];
        NearPos.y = NearPos4[1]/NearPos4[3];
        NearPos.z = NearPos4[2]/NearPos4[3];

        FarPos.x = FarPos4[0]/FarPos4[3];
        FarPos.y = FarPos4[1]/FarPos4[3];
        FarPos.z = FarPos4[2]/FarPos4[3];

        RayPickingHelper.Vector dir = new RayPickingHelper.Vector(FarPos.x - NearPos.x,FarPos.y-NearPos.y,FarPos.z-NearPos.z);
        float dirLength = (float) Math.sqrt((dir.x*dir.x)+(dir.y*dir.y)+dir.z*dir.z);
        dir.x = dir.x/dirLength;
        dir.y = dir.y/dirLength;
        dir.z = dir.z/dirLength;

        double a = Math.pow(dir.x,2)+Math.pow(dir.y,2)+Math.pow(dir.z,2);
        double b = 2*(dir.x*NearPos.x+dir.y*NearPos.y+dir.z*NearPos.z);
        double c = Math.pow(NearPos.x,2)+Math.pow(NearPos.y,2)+Math.pow(NearPos.z,2) - 9;

        double d = Math.pow(b,2) - 4 * a * c;
        double t = (-b + Math.sqrt(d))/(2*a);
        double t2 = (-b - Math.sqrt(d))/(2*a);

        Log.e("delta","delta="+t+","+t2);

        //get x, y and z coordinates of the touch point
        newPos.x = (float) (NearPos.x + dir.x * t);
        newPos.y = (float) (NearPos.y + dir.y * t);
        newPos.z = (float) (NearPos.z + dir.z * t);
//        newPos.z = NearPos.z;
        Log.e("touch point","x="+newPos.x+"y="+newPos.y+"z="+newPos.z);

        //calculate latitude
        float dis = (float) Math.sqrt(newPos.x*newPos.x+newPos.z*newPos.z);//The distance from the coordinate of the touch point on the x-z axis plane to the origin
        dis = dis/3;//get cos(angle)

        if (newPos.y>=0){
            latitude = (float) (Math.acos(dis)*180/Math.PI);//use arccosine function to get angle, and angle is the latitude of touch point
        }else{
            latitude = (float) -(Math.acos(dis)*180/Math.PI);//use arccosine function to get angle, and angle is the latitude of touch point
        }

        //calculate longitude
        if (newPos.x<0&&newPos.z<0){
            longitude = (float) ((Math.atan(newPos.z/newPos.x))*180/Math.PI);
        }else if(newPos.x>0&&newPos.z<0) {
            longitude = (float) (180+((Math.atan(newPos.z/newPos.x))*180/Math.PI));
        }else if(newPos.x>0&&newPos.z>0){
            longitude = (float) (180-((Math.atan(newPos.z/newPos.x))*180/Math.PI));
            longitude = -longitude;
        }else if(newPos.x<0&&newPos.z>0){
            longitude = (float) -((Math.atan(newPos.z/newPos.x))*180/Math.PI);
            longitude = -longitude;
        }

        Log.e("posi","latitude="+latitude+" ,longitude="+longitude);

//        float[] endResult = unproject(normalizedX, normalizedY, 1,wwidth,wheight);

//        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

//        Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 0, 1));//平面
//        Geometry.Plane plane2 = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 0, 1));//平面

//        Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
//        Geometry.Point touchedPoint2 = Geometry.intersectionPoint(ray, plane2);

//        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(0, 0, 0), 3f);

//        float[] xyzw = {0, 0, 0, 0};
//        android.opengl.GLU.gluUnProject(normalizedX, normalizedY, -1,mVMatrix,0,mProjMatrix,0, new int[]{0, 0, wwidth, wheight},0,xyzw,0);
//        xyzw[0] /= xyzw[3];
//        xyzw[1] /= xyzw[3];
//        xyzw[2] /= xyzw[3];
//
//        xyzw[3] = 1;

//        if (Geometry.intersects(malletBoundingSphere, ray)){
//        }else{
//            Log.e("globe","nono");
//        }
    }

//    public static void handleTouchMove(float normalizedX, float normalizedY) {
//        Log.w(TAG, "handleTouchMove normalizedX*normalizedY == " +normalizedX+" "+normalizedY);
//    }

//    private static Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
//        final float[] nearPointNdc = { normalizedX, normalizedY, -1, 1 };
//        final float[] farPointNdc = { normalizedX, normalizedY, 1, 1 };
//
//        final float[] nearPointWorld = new float[4];
//        final float[] farPointWorld = new float[4];
//
//        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
//
//        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
//
//        divideByW(nearPointWorld);
//        divideByW(farPointWorld);
//
//        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
//        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
//
//        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
//    }

//    private static void divideByW(float[] vector) {
//        vector[0] /= vector[3];
//        vector[1] /= vector[3];
//        vector[2] /= vector[3];
//    }

//    public static float[] unproject(float rx, float ry, float rz, int width, int height) {//TODO Factor in projection matrix
//        float[] modelInv = new float[16];
//        if (!android.opengl.Matrix.invertM(modelInv, 0, mVMatrix, 0))
//            throw new IllegalArgumentException("ModelView is not invertible.");
//        float[] projInv = new float[16];
//        if (!android.opengl.Matrix.invertM(projInv, 0, mProjMatrix, 0))
//            throw new IllegalArgumentException("Projection is not invertible.");
//        float[] combo = new float[16];
//        android.opengl.Matrix.multiplyMM(combo, 0, modelInv, 0, projInv, 0);
//        float[] result = new float[4];
//        float vx = 0;
//        float vy = 0;
//        float vw = width;
//        float vh = height;
//        float[] rhsVec = {((2*(rx-vx))/vw)-1,((2*(ry-vy))/vh)-1,2*rz-1,1};
//        android.opengl.Matrix.multiplyMV(result, 0, combo, 0, rhsVec, 0);
//        float d = 1 / result[3];
//        float[] endResult = {result[0] * d, result[1] * d, result[2] * d};
//        return endResult;
//
//
//    }

//    public static void mapToSphere(float x, float y, Geometry.Vector position, int[] viewport, float[] viewM, float[] proM){
//        float[] tempPos = new float[4];
//        GLU.gluUnProject(x,viewport[3]-y,0.7f,viewM,0,proM,0,viewport,0,tempPos,0);
//
//        position.x = tempPos[0]/tempPos[3];
//        position.y = tempPos[1]/tempPos[3];
//        position.z = -tempPos[2]/tempPos[3];
//    }
}
