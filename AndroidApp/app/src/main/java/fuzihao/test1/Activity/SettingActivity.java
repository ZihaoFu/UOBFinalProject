package fuzihao.test1.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fuzihao.test1.R;

public class SettingActivity extends Activity implements View.OnClickListener {
    private Intent intent;

    private Button btnGlobe;
    private Button btnMap;
    private Button btnHow;
    private Button btnInfo;

    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView(); // Initialize all interface variables
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume Visibility
        btnGlobe.setVisibility(View.VISIBLE);
        btnMap.setVisibility(View.VISIBLE);
        btnHow.setVisibility(View.VISIBLE);
        btnInfo.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
    }

    // Initialize all interface variables
    private void initView(){
        btnGlobe=(Button)findViewById(R.id.btnGlobe);
        btnMap=(Button)findViewById(R.id.btnMap) ;
        btnHow=(Button)findViewById(R.id.btnHow);
        btnInfo=(Button)findViewById(R.id.btnInfo);
        txtTitle=(TextView)findViewById(R.id.txtTitle1) ;

        btnGlobe.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnHow.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
    }

    // add click events
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnGlobe:
                intent = new Intent(SettingActivity.this, SelectActivity.class);
                intent.putExtra("num",0);
                startActivity(intent);
                break;
            case R.id.btnMap:
                intent = new Intent(SettingActivity.this,SelectActivity.class);
                intent.putExtra("num",1);
                startActivity(intent);
                break;
            case R.id.btnHow:
                intent = new Intent(SettingActivity.this,HelpActivity.class);
                intent.putExtra("num",3);
                startActivity(intent);
                break;
            case R.id.btnInfo:
                intent = new Intent(SettingActivity.this,SelectActivity.class);
                intent.putExtra("num",2);
                startActivity(intent);
                break;
        }
        // set Visibility to INVISIBLE after useing click events
        btnGlobe.setVisibility(View.INVISIBLE);
        btnMap.setVisibility(View.INVISIBLE);
        btnHow.setVisibility(View.INVISIBLE);
        btnInfo.setVisibility(View.INVISIBLE);
        txtTitle.setVisibility(View.INVISIBLE);
    }
}
