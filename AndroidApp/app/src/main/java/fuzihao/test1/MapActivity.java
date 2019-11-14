package fuzihao.test1;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.text.method.ReplacementTransformationMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MapActivity extends AppCompatActivity{

    private Intent intent;
    int select;

    private ImageView imgMap;
    private TextView txtIntroduction;
    private TextView txtSimple;
    private TextView txtLink;
    private ImageButton btnSetting;
    private Switch swShow;

    private String link = "https://en.wikipedia.org/wiki/"; // Set a basic link
    private String html = "<a href="+link+">Link to Wikipedia</a>";
    private String introduction = "World";

    private long startTime = 0;
    private long endTime = 0;

    WikiApi wikiApi = new WikiApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        intent = getIntent();
        select = intent.getIntExtra("map",0);

        initView();

        if (select==0){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.globe2));
            introduction = "World";;
            txtIntroduction.setText(introduction);
        }else if (select==1){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.asia));
            introduction = "Asia";
            txtIntroduction.setText(introduction);
        }else if (select==2){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.europe));
            introduction = "Europe";
            txtIntroduction.setText(introduction);
        }else if (select==3){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.africa));
            introduction = "Africa";
            txtIntroduction.setText(introduction);
        }else if (select==4){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.north_america));
            introduction = "North America";
            txtIntroduction.setText(introduction);
            introduction = "North_America";
        }else if (select==5){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.south_america));
            introduction = "South America";
            txtIntroduction.setText(introduction);
            introduction = "South_America";
        }else if (select==6){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.australia_and_oceania));
            introduction = "Oceania";
            txtIntroduction.setText(introduction);
        }else if (select==7){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.antarctica));
            introduction = "Antarctica";
            txtIntroduction.setText(introduction);
        }

        //place image
        else if (select==8){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.france));
            introduction = "France";
            txtIntroduction.setText(introduction);
        }else if (select==9){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.uk));
            introduction = "United Kingdom";
            txtIntroduction.setText(introduction);
            introduction = "United_Kingdom";
        }else if (select==10){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.russia));
            introduction = "Russia";
            txtIntroduction.setText(introduction);
        }else if (select==11){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.us));
            introduction = "United States";
            txtIntroduction.setText(introduction);
            introduction = "United_States";
        }else if (select==12){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.china));
            introduction = "China";
            txtIntroduction.setText(introduction);
        }

        //physical
        else if (select==13){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.pacificocean));
            introduction = "Pacific Ocean";
            txtIntroduction.setText(introduction);
            introduction = "Pacific_Ocean";
        }else if (select==14){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.atlanticocean));
            introduction = "Atlantic Ocean";
            txtIntroduction.setText(introduction);
            introduction = "Atlantic_Ocean";
        }else if (select==15){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.saharadesert));
            introduction = "Sahara";
            txtIntroduction.setText(introduction);
        }else if (select==16){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.indianocean));
            introduction = "Indian Ocean";
            txtIntroduction.setText(introduction);
            introduction = "Indian_Ocean";
        }else if (select==17){
            imgMap.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.tibetanplateau));
            introduction = "Tibetan Plateau";
            txtIntroduction.setText(introduction);
            introduction = "Tibetan_Plateau";
        }

        wikiApi();

        link+=introduction;
        html = "<a href="+link+">Link to Wikipedia</a>";
        txtLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtLink.setText(Html.fromHtml(html));
    }

    public void onResume() {
        super.onResume();
        btnSetting.setVisibility(View.VISIBLE);
    }

    //wikiapi
    private void wikiApi(){
        WikiApi.GetApiRes getRes = new WikiApi.GetApiRes();
        getRes.execute("https://en.wikipedia.org/w/api.php?" +
                "format=json" +
                "&action=query" +
                "&prop=extracts" +

                "&explaintext=" +
                "&titles="+introduction);
        getRes.setOnAsyncResponse(new WikiApi.AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String string) {
                txtSimple.setText(string);
            }
            @Override
            public void onDataReceivedFailed() {
                Toast toast = Toast.makeText(MapActivity.this,"data received failed!",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //Bind controls to variables, Set click listener and touch listener
    private void initView(){
        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtIntroduction = (TextView) findViewById(R.id.txtIntroduction);
        txtSimple = (TextView) findViewById(R.id.txtSimple);
        txtLink = (TextView) findViewById(R.id.txtLink);
        btnSetting = (ImageButton) findViewById(R.id.btnSetting2);
        swShow = (Switch) findViewById(R.id.swTime);
        swShow.setChecked(true);

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
        swShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){

                }else{

                }
            }
        });
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
                    startTime = System.currentTimeMillis();
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
                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 0.1 * 1000L) {
                        if (select==8){
                            Intent intent = new Intent(MapActivity.this, VRActivity.class);
                            intent.putExtra("vr",0);
                            startActivity(intent);
                        }
                        if (select==9){
                            Intent intent = new Intent(MapActivity.this, VRActivity.class);
                            intent.putExtra("vr",1);
                            startActivity(intent);
                        }
                        if (select==10){
                            Intent intent = new Intent(MapActivity.this, VRActivity.class);
                            intent.putExtra("vr",2);
                            startActivity(intent);
                        }
                        if (select==11){
                            Intent intent = new Intent(MapActivity.this, VRActivity.class);
                            intent.putExtra("vr",3);
                            startActivity(intent);
                        }
                        if (select==12){
                            Intent intent = new Intent(MapActivity.this, VRActivity.class);
                            intent.putExtra("vr",4);
                            startActivity(intent);
                        }
                    }

                    PointF p1 = getLeftPointF(currentMatrix);
                    PointF p2 = getRightPointF(currentMatrix);

                    //左边界复位
                    if(p1.x>0 && p1.x<=imgMap.getWidth()/2){
                        currentMatrix.postTranslate(-p1.x,0);
                    }
                    //拉过一半时操作前往上一张图像
                    if(p1.x>imgMap.getWidth()/2){
                        if(select==0){
                            intent = new Intent(MapActivity.this,MapActivity.class);
                            intent.putExtra("map",7);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        if(select>0&&select<=7){
                            intent = new Intent(MapActivity.this,MapActivity.class);
                            intent.putExtra("map",select-1);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    //右边界复位
                    if(p2.x<imgMap.getWidth() && p2.x>=imgMap.getWidth()/2){
                        currentMatrix.postTranslate(imgMap.getWidth()-p2.x,0);
                    }
                    //拉过一半时操作前往下一张图像
                    if(p2.x<imgMap.getWidth()/2){
                        if(select==7){
                            intent = new Intent(MapActivity.this,MapActivity.class);
                            intent.putExtra("map",0);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        if(select<7){
                            intent = new Intent(MapActivity.this,MapActivity.class);
                            intent.putExtra("map",select+1);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    //上下边界复位
                    if(p2.y-p1.y>imgMap.getHeight()){
                        //上边界复位
                        if(p1.y>0){
                            currentMatrix.postTranslate(0,-p1.y);
                        }
                        //下边界复位
                        if(p2.y<imgMap.getHeight()){
                            currentMatrix.postTranslate(0,imgMap.getHeight()-p2.y);
                        } }
                    else {
                        float row = (imgMap.getHeight()-(p2.y-p1.y))/2;
                        currentMatrix.postTranslate(0,row-p1.y);
                    }
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

    //获取图片的上坐标
    private PointF getLeftPointF(Matrix matrix){
        Rect rectTemp = imgMap.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2];
        float leftY = values[5];
        return new PointF(leftX,leftY);
    }

    //获取图片的下坐标
    private PointF getRightPointF(Matrix matrix){
        Rect rectTemp = imgMap.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2]+rectTemp.width()*values[0];
        float leftY = values[5]+rectTemp.height()*values[4];
        return new PointF(leftX,leftY);
    }

    String decodeSourceText(String sourceText){
        return sourceText.toLowerCase().replace(" ","%20");
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";

        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
            String UN = "", PW = "", user = "", wikiText = "";
            try {
                JSONObject json = new JSONObject(result); // convert String to JSONObject
                //JSONArray articles = json.getJSONArray("array"); // get articles array
                JSONObject query = json.getJSONObject("query");
                JSONObject pages = query.getJSONObject("pages");
                //user=pages.toString();

                String[] str_array = pages.toString().substring(0, 20).split(":");
                String string1 = str_array[0];
                String pageid = string1.substring(2, string1.length() - 1);
                user = string1 + "\n" + pageid;


                JSONObject page = pages.getJSONObject(pageid);

                wikiText = (String) page.get("extract");
                String[] str_array2 = wikiText.split("==");
                wikiText = "";
                for (String temp : str_array2) {
                    wikiText = wikiText + "\n-----------------------------------------------\n" + temp.trim();
                }

                if (!wikiText.isEmpty()) {
                    txtSimple.setText(wikiText.trim());

                } else {
                    txtSimple.setText("No Result Found");
                }

            } catch (JSONException e) {
                txtSimple.setText(e.toString());
                Toast.makeText(getBaseContext(), "JSONException!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}


