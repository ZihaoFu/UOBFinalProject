package fuzihao.test1.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
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
    private ViewPager vpHelp; // Initialize viewpager
    // Initialize picture for help page
    private int[] resIDs = new int[]{
            R.drawable.help1,
            R.drawable.help2,
            R.drawable.help3,
            R.drawable.help4,
            R.drawable.help5};

    // initialize variables
    public static ArrayList<ImageView> imageViewList;
    private LinearLayout linearLayout;
    private ImageView imgPoint;
    private int pointDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initView();// Initialize interface controls
        initData();// Initialize image, scale image size and add click events
        vpHelp.setAdapter(new HelpAdapter()); //Add adapter to viewpager
    }

    // Initialize interface controls
    private void initView(){
        vpHelp = (ViewPager) findViewById(R.id.vpHelp);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        imgPoint = (ImageView) findViewById(R.id.imgPoint);

        // add OnPageChangeListener to viewpager
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
            imageView.setScaleType(ImageView.ScaleType.MATRIX);

            // scale pictures to fit the size of screen
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

            // add touch event to imageView, Slide down
            imageView.setOnTouchListener(new View.OnTouchListener() {
                Matrix touchMatrix = new Matrix();
                Matrix currentMatrix = new Matrix();
                float startY;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()& MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            touchMatrix.set(imageView.getImageMatrix()); // Get initial matrix
                            startY = motionEvent.getY(); // Get initial coordinates
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dy = motionEvent.getY() - startY; // Calculate displacement distance
                            currentMatrix.set(touchMatrix);
                            if (Math.abs(dy)>1){
                                currentMatrix.postTranslate(0,dy); // translation matrix
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            startY = motionEvent.getY();
                            PointF p1 = getLeftPointF(currentMatrix,imageView);
                            PointF p2 = getRightPointF(currentMatrix,imageView);
                            //Top and bottom boundary reset
                            if(p2.y-p1.y>imageView.getHeight()){
                                //Top boundary reset
                                if(p1.y>0){
                                    currentMatrix.postTranslate(0,-p1.y);
                                }
                                //bottom boundary reset
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

            // Set point properties
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.pointgray);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Calculate left margin
            if (i > 0) {
                layoutParams.leftMargin = 10;
            }
            point.setLayoutParams(layoutParams);
            linearLayout.addView(point); // Add points to layout
        }
    }

    //Get the top coordinate of the picture
    private PointF getLeftPointF(Matrix matrix, ImageView img){
        Rect rectTemp = img.getDrawable().getBounds(); // Get picture boundary
        float[] values = new float[9];
        matrix.getValues(values); // Take value from matrix
        // Calculated coordinates
        float leftX = values[2];
        float leftY = values[5];
        return new PointF(leftX,leftY);
    }

    //Get the bottom coordinates of the picture
    private PointF getRightPointF(Matrix matrix, ImageView img){
        Rect rectTemp = img.getDrawable().getBounds(); // Get picture boundary
        float[] values = new float[9];
        matrix.getValues(values); // Take value from matrix
        // Calculated coordinates
        float leftX = values[2]+rectTemp.width()*values[0];
        float leftY = values[5]+rectTemp.height()*values[4];
        return new PointF(leftX,leftY);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
