package fuzihao.test1.Activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import fuzihao.test1.Model.VrSphereRender;
import fuzihao.test1.R;
import fuzihao.test1.Model.VrSphere;

public class VRActivity extends AppCompatActivity implements SensorEventListener {
    private GLSurfaceView glSurfaceView;
    private SensorManager sensorManager;
    private Sensor rotation;

    private float[] rotationMatrix = new float[16];

    public static VrSphere vrSphere;

    private Intent intent;
    String select;

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
        glSurfaceView.setRenderer(new VrSphereRender());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        intent = getIntent();
        select = intent.getStringExtra("vr");
        //fr
        if (select.equals("France")){
            int texture = R.drawable.france360;
            vrSphere = new VrSphere(this.getApplicationContext(),texture);
        }
        //uk
        if(select.equals("United Kingdom")){
            int texture = R.drawable.panorama01;
            vrSphere = new VrSphere(this.getApplicationContext(),texture);
        }
        //ru
        if(select.equals("Russia")){
            int texture = R.drawable.panorama02;
            vrSphere = new VrSphere(this.getApplicationContext(),texture);
        }
        //us
        if(select.equals("United States")){
            int texture = R.drawable.panorama03;
            vrSphere = new VrSphere(this.getApplicationContext(),texture);
        }
        //cn
        if(select.equals("China")){
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix,event.values);
        vrSphere.setMatrix(rotationMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
