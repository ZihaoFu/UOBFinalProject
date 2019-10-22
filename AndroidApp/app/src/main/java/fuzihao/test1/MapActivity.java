package fuzihao.test1;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MapActivity extends AppCompatActivity{

    private Intent intent;

    private ImageView imgMap;
    private TextView txtIntroduction;
    private TextView txtSimple;
    private TextView txtLink;
    private ImageButton btnSetting;

    private String link = "https://en.wikipedia.org/wiki/"; // Set a basic link
    private String html = "<a href="+link+">Link to Wikipedia</a>";
    private String introduction = "World";

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

    //Bind controls to variables, Set click listener and touch listener
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
        imgMap.setOnTouchListener(new Move());
    }

    private class Move implements View.OnTouchListener {
        private Matrix matrix = new Matrix();
        private PointF startPoint = new PointF();
        private PointF midPoint;//Two-finger center point
        private Matrix currentMatrix = new Matrix();
        private float oldDist;
        private float zoom = 0f;

        private int mode = 0;//Initial state
        private static final int DRAG = 1;//Drag mode
        private static final int ZOOM = 2;//Zoom mode

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()& MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;
                    matrix.set(imgMap.getImageMatrix());
                    startPoint.set(motionEvent.getX(),motionEvent.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == ZOOM) {
                        float newDist = distance(motionEvent);
                        if (newDist > 10f){
                            zoom = newDist / oldDist;
                            currentMatrix.set(matrix);
                            currentMatrix.postScale(zoom,zoom,midPoint.x,midPoint.y);
                        }
                    }else if (mode == DRAG){
                        float dx = motionEvent.getX() - startPoint.x;
                        float dy = motionEvent.getY() - startPoint.y;
                        currentMatrix.set(matrix);
                        if (Math.abs(dx)>1 || Math.abs(dy)>1){
                            currentMatrix.postTranslate(dx,dy);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = ZOOM;
                    oldDist = distance(motionEvent);//两点按下时的距离 Distance when two points are pressed
                    if (oldDist > 10f){
                        midPoint = mid(motionEvent);
                        currentMatrix.set(imgMap.getImageMatrix());
                    }
                    break;
                default:
                    break;
            }
            imgMap.setImageMatrix(currentMatrix);
            return true;
        }
    }

    // Calculate Euclidean distance
    private float distance(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float dis = (float) Math.sqrt(x * x + y * y);
        return dis;
    }

    // Calculate the midpoint of two fingers
    private static PointF mid(MotionEvent event){
        float midX = event.getX(1)+event.getX(0);
        float midY = event.getY(1)+event.getY(0);

        return new PointF(midX/2,midY/2);
    }
}


