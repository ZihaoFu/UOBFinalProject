package fuzihao.test1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectActivity extends Activity implements View.OnClickListener {
    private Button btnGeo;
    private Button btnNation;
    private Button btnTime;
    private Button btnWorldMap;
    private Button btnAsia;
    private Button btnEurope;
    private Button btnAfrica;
    private Button btnNA;
    private Button btnSA;
    private Button btnOceania;
    private Button btnAntarctica;

    private TextView txtAuthor;
    private TextView txtVersion;
    private TextView txtModifyTime;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initView();
        Intent intent = getIntent();
        int requestCode= intent.getIntExtra("num",0);
        if(requestCode==0){
            btnWorldMap.setVisibility(View.INVISIBLE);
            btnAsia.setVisibility(View.INVISIBLE);
            btnEurope.setVisibility(View.INVISIBLE);
            btnAfrica.setVisibility(View.INVISIBLE);
            btnNA.setVisibility(View.INVISIBLE);
            btnSA.setVisibility(View.INVISIBLE);
            btnOceania.setVisibility(View.INVISIBLE);
            btnAntarctica.setVisibility(View.INVISIBLE);
            txtAuthor.setVisibility(View.INVISIBLE);
            txtVersion.setVisibility(View.INVISIBLE);
            txtModifyTime.setVisibility(View.INVISIBLE);
        }else if(requestCode==1){
            btnGeo.setVisibility(View.INVISIBLE);
            btnNation.setVisibility(View.INVISIBLE);
            btnTime.setVisibility(View.INVISIBLE);
            txtAuthor.setVisibility(View.INVISIBLE);
            txtVersion.setVisibility(View.INVISIBLE);
            txtModifyTime.setVisibility(View.INVISIBLE);
        }else if(requestCode==2){
            btnGeo.setVisibility(View.INVISIBLE);
            btnNation.setVisibility(View.INVISIBLE);
            btnTime.setVisibility(View.INVISIBLE);
            btnWorldMap.setVisibility(View.INVISIBLE);
            btnAsia.setVisibility(View.INVISIBLE);
            btnEurope.setVisibility(View.INVISIBLE);
            btnAfrica.setVisibility(View.INVISIBLE);
            btnNA.setVisibility(View.INVISIBLE);
            btnSA.setVisibility(View.INVISIBLE);
            btnOceania.setVisibility(View.INVISIBLE);
            btnAntarctica.setVisibility(View.INVISIBLE);
        }
    }

    private void initView(){
        btnGeo = (Button) findViewById(R.id.btnGeo);
        btnNation = (Button) findViewById(R.id.btnNation);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnWorldMap = (Button) findViewById(R.id.btnWorldMap);
        btnAsia = (Button) findViewById(R.id.btnAsia);
        btnEurope = (Button) findViewById(R.id.btnEurope);
        btnAfrica = (Button) findViewById(R.id.btnAfrica);
        btnNA = (Button) findViewById(R.id.btnNA);
        btnSA = (Button) findViewById(R.id.btnSA);
        btnOceania = (Button) findViewById(R.id.btnOceania);
        btnAntarctica = (Button) findViewById(R.id.btnAntarctica);
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtModifyTime = (TextView) findViewById(R.id.txtModifyTime);

        btnGeo.setOnClickListener(this);
        btnNation.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnWorldMap.setOnClickListener(this);
        btnAsia.setOnClickListener(this);
        btnEurope.setOnClickListener(this);
        btnAfrica.setOnClickListener(this);
        btnNA.setOnClickListener(this);
        btnSA.setOnClickListener(this);
        btnOceania.setOnClickListener(this);
        btnAntarctica.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGeo:
                intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("num",0);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnNation:
                intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("num",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnTime:
                intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("num",2);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnWorldMap:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",0);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnAsia:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnEurope:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",2);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnAfrica:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",3);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnNA:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",4);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnSA:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",5);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnOceania:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",6);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnAntarctica:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("map",7);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }
}
