package fuzihao.test1.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fuzihao.test1.R;

public class SelectActivity extends Activity implements View.OnClickListener {
    private Button btnGeo;
    private Button btnContinent;
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
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;

    private Intent intent;

    Uri uri;

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
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
            imageView5.setVisibility(View.INVISIBLE);
            imageView6.setVisibility(View.INVISIBLE);
            imageView7.setVisibility(View.INVISIBLE);
        }else if(requestCode==1){
            btnGeo.setVisibility(View.INVISIBLE);
            btnContinent.setVisibility(View.INVISIBLE);
            btnNation.setVisibility(View.INVISIBLE);
            btnTime.setVisibility(View.INVISIBLE);
            txtAuthor.setVisibility(View.INVISIBLE);
            txtVersion.setVisibility(View.INVISIBLE);
            txtModifyTime.setVisibility(View.INVISIBLE);
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
            imageView5.setVisibility(View.INVISIBLE);
            imageView6.setVisibility(View.INVISIBLE);
            imageView7.setVisibility(View.INVISIBLE);
        }else if(requestCode==2){
            btnGeo.setVisibility(View.INVISIBLE);
            btnContinent.setVisibility(View.INVISIBLE);
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
        btnContinent = (Button) findViewById(R.id.btnContinent);
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
        imageView1 = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView7 = (ImageView) findViewById(R.id.imageView7);

        btnGeo.setOnClickListener(this);
        btnContinent.setOnClickListener(this);
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
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        imageView6.setOnClickListener(this);
        imageView7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGeo:
                intent = new Intent(SelectActivity.this, MainActivity.class);
                intent.putExtra("num",0);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnContinent:
                intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("num",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnNation:
                intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("num",2);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnTime:
                intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("num",3);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnWorldMap:
                intent = new Intent(SelectActivity.this, MapActivity.class);
                intent.putExtra("country","World");
                startActivity(intent);
                break;
            case R.id.btnAsia:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","Asia");
                startActivity(intent);
                break;
            case R.id.btnEurope:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","Europe");
                startActivity(intent);
                break;
            case R.id.btnAfrica:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","Africa");
                startActivity(intent);
                break;
            case R.id.btnNA:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","North America");
                startActivity(intent);
                break;
            case R.id.btnSA:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","South America");
                startActivity(intent);
                break;
            case R.id.btnOceania:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","Oceania");
                startActivity(intent);
                break;
            case R.id.btnAntarctica:
                intent = new Intent(SelectActivity.this,MapActivity.class);
                intent.putExtra("country","Antarctica");
                startActivity(intent);
                break;
            case R.id.imageView:
                uri = Uri.parse("http://ontheworldmap.com/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.imageView2:
                uri = Uri.parse("https://iconmonstr.com/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.imageView3:
                uri = Uri.parse("https://www.mediawiki.org/wiki/API:Main_page");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.imageView4:
                uri = Uri.parse("https://rapidapi.com/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.imageView5:
                uri = Uri.parse("https://www.countryflags.io/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.imageView6:
                uri = Uri.parse("http://www.nationalanthems.info/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.imageView7:
                uri = Uri.parse("https://developers.google.com/places/web-service/intro");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }
}
