package fuzihao.test1.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fuzihao.test1.Api.GoogleGetPhoto;
import fuzihao.test1.Api.GoogleMapPhotoApi;
import fuzihao.test1.Model.VrSphereRender;
import fuzihao.test1.R;
import fuzihao.test1.Model.VrSphere;

public class VRActivity extends AppCompatActivity implements SensorEventListener {
    private GLSurfaceView glSurfaceView;
    private SensorManager sensorManager;
    private Sensor rotation;

    private float[] rotationMatrix = new float[16];

    public static VrSphere vrSphere;
    Bitmap bitmap;

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
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.france360);
        }
        //uk
        if(select.equals("United Kingdom")){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panorama01);
        }
        //ru
        if(select.equals("Russia")){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panorama02);
        }
        //us
        if(select.equals("United States")){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panorama03);
        }
        //cn
        if(select.equals("China")){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panorama04);
        }
        vrSphere = new VrSphere(this.getApplicationContext(),bitmap);
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
