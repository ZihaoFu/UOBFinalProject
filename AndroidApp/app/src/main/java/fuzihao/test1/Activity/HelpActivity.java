package fuzihao.test1.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import fuzihao.test1.Adapter.HelpAdapter;
import fuzihao.test1.R;

public class HelpActivity extends Activity {
    private ViewPager vpHelp;
    private int[] resIDs = new int[]{
            R.drawable.help1,
            R.drawable.help2,
            R.drawable.help3,
            R.drawable.help4,
            R.drawable.help5,};

    public static ArrayList<ImageView> imageViewList;
    private LinearLayout linearLayout;
    private ImageView imgPoint;
    private int pointDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initView();//初始化界面控件
        initData();
        vpHelp.setAdapter(new HelpAdapter());
    }

    private void initView(){
        vpHelp = (ViewPager) findViewById(R.id.vpHelp);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        imgPoint = (ImageView) findViewById(R.id.imgPoint);

        vpHelp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                Log.e("help" + "Current location:", i + "");
                Log.e("help" + "Offset:", v + "");

                int leftMargin = (int) (pointDis * v) + i * pointDis;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imgPoint.getLayoutParams();
                layoutParams.leftMargin = leftMargin;
                imgPoint.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        imgPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imgPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                pointDis = linearLayout.getChildAt(1).getLeft() - linearLayout.getChildAt(0).getLeft();
                Log.e("help" + "two point dis:", pointDis + "");
            }
        });
    }

    private void initData(){
        imageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < resIDs.length; i++) {
            final ImageView imageView = new ImageView(this);
//            imageView.setBackgroundResource(resIDs[i]);
            imageView.setScaleType(ImageView.ScaleType.MATRIX);

            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            int imageWidth = wm.getDefaultDisplay().getWidth();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resIDs[i]);
            int photoHeight = bitmap.getHeight();
            int photoWidth = bitmap.getWidth();
            float mapScale = imageWidth/ (float) photoWidth;
            final Matrix matrix = new Matrix();
            matrix.postScale(mapScale, mapScale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, photoWidth, photoHeight, matrix, true);
            imageView.setImageBitmap(bitmap);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                Matrix touchMatrix = new Matrix();
                Matrix currentMatrix = new Matrix();
                float startY;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()& MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            touchMatrix.set(imageView.getImageMatrix());
                            startY = motionEvent.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dy = motionEvent.getY() - startY;
                            currentMatrix.set(touchMatrix);
                            if (Math.abs(dy)>1){
                                currentMatrix.postTranslate(0,dy);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            startY = motionEvent.getY();
                            PointF p1 = getLeftPointF(currentMatrix,imageView);
                            PointF p2 = getRightPointF(currentMatrix,imageView);
                            //上下边界复位
                            if(p2.y-p1.y>imageView.getHeight()){
                                //上边界复位
                                if(p1.y>0){
                                    currentMatrix.postTranslate(0,-p1.y);
                                }
                                //下边界复位
                                if(p2.y<imageView.getHeight()){
                                    currentMatrix.postTranslate(0,imageView.getHeight()-p2.y);
                                }
                            }
                            else {
                                float row = (imageView.getHeight()-(p2.y-p1.y))/2;
                                currentMatrix.postTranslate(0,row-p1.y);
                            }
                            break;
                        default:
                            break;
                    }
                    imageView.setImageMatrix(currentMatrix);
                    return true;
                }
            });
            imageViewList.add(imageView);

            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.pointgray);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i > 0) {
                layoutParams.leftMargin = 10;
            }
            point.setLayoutParams(layoutParams);
            linearLayout.addView(point);
        }
    }

    //获取图片的上坐标
    private PointF getLeftPointF(Matrix matrix, ImageView img){
        Rect rectTemp = img.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2];
        float leftY = values[5];
        return new PointF(leftX,leftY);
    }

    //获取图片的下坐标
    private PointF getRightPointF(Matrix matrix, ImageView img){
        Rect rectTemp = img.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2]+rectTemp.width()*values[0];
        float leftY = values[5]+rectTemp.height()*values[4];
        return new PointF(leftX,leftY);
    }
}
