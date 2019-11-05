package fuzihao.test1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VRActivity extends AppCompatActivity implements SensorEventListener {
    private GLSurfaceView glSurfaceView;
    private SensorManager sensorManager;
    private Sensor rotation;

    private float[] rotationMatrix = new float[16];

    private VrSphere vrSphere;

    private Intent intent;
    int select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        //判断是否存在rotation vector sensor
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_content);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new GLRender());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        intent = getIntent();
        select = intent.getIntExtra("vr",0);
        if (select == 0){
            int texture = R.drawable.france360;
            vrSphere = new VrSphere(this.getApplicationContext(),texture);
        }
        if(select == 1){

        }
        if(select == 2){

        }
        if(select == 3){

        }
        if(select == 4){
            int texture = R.drawable.panorama04;
            vrSphere = new VrSphere(this.getApplicationContext(),texture);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,rotation,SensorManager.SENSOR_DELAY_GAME);
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        glSurfaceView.onPause();
    }

    private class GLRender implements GLSurfaceView.Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            vrSphere.create();
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_FRONT);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            vrSphere.setSize(width, height);
            GLES20.glViewport(0,0,width,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glClearColor(1,1,1,1);
            vrSphere.draw();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix,event.values);
        vrSphere.setMatrix(rotationMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
