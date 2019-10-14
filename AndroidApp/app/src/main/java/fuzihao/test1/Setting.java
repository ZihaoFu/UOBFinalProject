package fuzihao.test1;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends Activity implements View.OnClickListener {
//    private boolean clickButton=false;

    private Intent intent;

    private Button btnGlobe;
    private Button btnMap;
//    private Button btnDay;
    private Button btnHow;
    private Button btnInfo;

    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    @Override
    public void onResume() {

        super.onResume();
        btnGlobe.setVisibility(View.VISIBLE);
        btnMap.setVisibility(View.VISIBLE);
//        btnDay.setVisibility(View.VISIBLE);
        btnHow.setVisibility(View.VISIBLE);
        btnInfo.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
    }

    private void initView(){
        btnGlobe=(Button)findViewById(R.id.btnGlobe);
        btnMap=(Button)findViewById(R.id.btnMap) ;
//        btnDay=(Button)findViewById(R.id.btnDay);
        btnHow=(Button)findViewById(R.id.btnHow);
        btnInfo=(Button)findViewById(R.id.btnInfo);
        txtTitle=(TextView)findViewById(R.id.txtTitle1) ;


        btnGlobe.setOnClickListener(this);
        btnMap.setOnClickListener(this);
//        btnDay.setOnClickListener(this);
        btnHow.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnGlobe:
                intent = new Intent(Setting.this,SelectActivity.class);
                intent.putExtra("num",0);
                startActivity(intent);
//                btnGlobe.setVisibility(View.INVISIBLE);
//                btnMap.setVisibility(View.INVISIBLE);
//                btnDay.setVisibility(View.INVISIBLE);
//                btnHow.setVisibility(View.INVISIBLE);
//                btnInfo.setVisibility(View.INVISIBLE);
//                finish();
//                clickButton=true;
                break;
            case R.id.btnMap:
                intent = new Intent(Setting.this,SelectActivity.class);
                intent.putExtra("num",1);
                startActivity(intent);
                break;
//                btnGlobe.setVisibility(View.INVISIBLE);
//                btnMap.setVisibility(View.INVISIBLE);
//                btnDay.setVisibility(View.INVISIBLE);
//                btnHow.setVisibility(View.INVISIBLE);
//                btnInfo.setVisibility(View.INVISIBLE);
//                finish();
            case R.id.btnHow:
                break;
            case R.id.btnInfo:
                intent = new Intent(Setting.this,SelectActivity.class);
                intent.putExtra("num",2);
                startActivity(intent);
                break;
        }
        btnGlobe.setVisibility(View.INVISIBLE);
        btnMap.setVisibility(View.INVISIBLE);
//        btnDay.setVisibility(View.INVISIBLE);
        btnHow.setVisibility(View.INVISIBLE);
        btnInfo.setVisibility(View.INVISIBLE);
        txtTitle.setVisibility(View.INVISIBLE);
    }

//    private boolean isTopActivity(){
//        boolean isTop = false;
//        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        ComponentName componentName = activityManager.getRecentTasks(1).get(0).topActivity;
//        if(componentName.getClassName().contains("Setting")){
//            isTop = true;
//        }
//        return isTop;
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        String result = data.getExtras().getString("result");
//        btnGlobe.setVisibility(View.VISIBLE);
//        btnMap.setVisibility(View.VISIBLE);
//        btnDay.setVisibility(View.VISIBLE);
//        btnHow.setVisibility(View.VISIBLE);
//        btnInfo.setVisibility(View.VISIBLE);
//    }

//    private void showToast(String text) {
//        Toast toast = Toast.makeText(Setting.this, text, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//
//                break;
//            case MotionEvent.ACTION_UP:
//                if (clickButton)
//                    finish();
//                else
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
}
