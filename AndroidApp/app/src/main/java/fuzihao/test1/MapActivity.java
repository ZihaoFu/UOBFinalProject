package fuzihao.test1;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MapActivity extends AppCompatActivity {

    private Intent intent;

    private ImageView imgMap;
    private TextView txtIntroduction;
    private TextView txtSimple;
    private TextView txtLink;
    private ImageButton btnSetting;

    private String link = "https://en.wikipedia.org/wiki/";
    private String html = "<a href="+link+">Link to Wikipedia</a>";
    private String introduction = "World";
    private String simple = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
        intent = getIntent();
        int select = intent.getIntExtra("map",0);
        if (select==0){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.world1));
            introduction = "World";;
            txtIntroduction.setText(introduction);
            txtSimple.setText(R.string.introductionOfWorld);
        }else if (select==1){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.asia2));
            introduction = "Asia";
            txtIntroduction.setText(introduction);
            txtSimple.setText(R.string.introductionOfAsia);
        }else if (select==2){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.europe));
            introduction = "Europe";
            txtIntroduction.setText(introduction);
            txtSimple.setText(R.string.introductionOfEurope);
        }else if (select==3){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.africa));
            introduction = "Africa";
            txtIntroduction.setText(introduction);
            txtSimple.setText(R.string.introductionOfAfrica);
        }else if (select==4){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.north_america));
            introduction = "North America";
            txtIntroduction.setText(introduction);
            introduction = "North_America";
            txtSimple.setText(R.string.introductionOfNA);
        }else if (select==5){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.south_america));
            introduction = "South America";
            txtIntroduction.setText(introduction);
            introduction = "South_America";
            txtSimple.setText(R.string.introductionOfSA);
        }else if (select==6){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.australia_and_oceania));
            introduction = "Oceania";
            txtIntroduction.setText(introduction);
            txtSimple.setText(R.string.introductionOfOceania);
        }else if (select==7){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.antarctica));
            introduction = "Antarctica";
            txtIntroduction.setText(introduction);
            txtSimple.setText(R.string.introductionOfAntarctica);
        }
        link+=introduction;
        html = "<a href="+link+">Link to Wikipedia</a>";
        txtLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtLink.setText(Html.fromHtml(html));
    }

    public void onResume() {
        super.onResume();
        btnSetting.setVisibility(View.VISIBLE);
    }

    private void initView(){
        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtIntroduction = (TextView) findViewById(R.id.txtIntroduction);
        txtSimple = (TextView) findViewById(R.id.txtSimple);
        txtLink = (TextView) findViewById(R.id.txtLink);
        btnSetting = (ImageButton) findViewById(R.id.btnSetting2);

        txtSimple.setMovementMethod(ScrollingMovementMethod.getInstance());

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MapActivity.this,Setting.class);
                startActivity(intent);
                btnSetting.setVisibility(View.INVISIBLE);
            }
        });
    }
}
