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
    int selectNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        try{
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            //Determine if there is a rotation vector sensor
            rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

            // set opengl es parameters
            glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_content);
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new VrSphereRender());
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

            intent = getIntent();
            select = intent.getStringExtra("vr");
            selectNum = intent.getIntExtra("pos",0);

            // set texture bitmap
            if (select.equals("France")||select.equals("United Kingdom")||select.equals("Russia")||select.equals("United States")||select.equals("China")){
                select = select.toLowerCase().replaceAll(" ","");
                int id = getResources().getIdentifier("p"+select+selectNum, "drawable", getPackageName());
                bitmap = BitmapFactory.decodeResource(getResources(), id);
            }
            vrSphere = new VrSphere(this.getApplicationContext(),bitmap);
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
                Toast.makeText(VRActivity.this,"Sensor cannot be detected",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(VRActivity.this,"This function meet some problems, please try again!",Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix,event.values); // get rotation matrix
        vrSphere.setMatrix(rotationMatrix); // apply matrix to model
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
