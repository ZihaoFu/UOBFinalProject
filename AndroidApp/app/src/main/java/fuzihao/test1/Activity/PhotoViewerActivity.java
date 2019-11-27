package fuzihao.test1.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import fuzihao.test1.Api.GoogleGetPhoto;
import fuzihao.test1.Api.GoogleMapPhotoApi;
import fuzihao.test1.R;

public class PhotoViewerActivity extends AppCompatActivity {
    private ImageView imgPhoto;
    private Intent intent;

    final String apiKey = "AIzaSyBbmKXXOLKFsICWeJkFWtp4Z9Jy9RtljX4";
    String[] arr;

    public int photoRef;
    String photoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final int screenHeight = wm.getDefaultDisplay().getHeight();
        final int screenWidth = wm.getDefaultDisplay().getWidth();

        intent = getIntent();
        photoRef = intent.getIntExtra("photoRef",0);
        photoid = intent.getStringExtra("photoid");

        GoogleMapPhotoApi.GetGoogleMapPhotoApiRes getCountryRes = new GoogleMapPhotoApi.GetGoogleMapPhotoApiRes();
        getCountryRes.execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + photoid + "&key=" + apiKey + "&fields=photos");
        getCountryRes.setOnAsyncResponse(new GoogleMapPhotoApi.AsyncResponse(){
            @Override
            public void onDataReceivedSuccess(String string) {
                arr = string.split(",");

                GoogleGetPhoto.GetPhotoApiRes getPhoto = new GoogleGetPhoto.GetPhotoApiRes(PhotoViewerActivity.this);
                String url = "https://maps.googleapis.com/maps/api/place/photo?key=" + apiKey + "&photoreference=" + arr[3* photoRef] + "&maxheight=" + arr[3* photoRef - 2] + "&maxwidth=" + arr[3* photoRef - 1];
                getPhoto.execute(url);

                getPhoto.setOnAsyncResponse(new GoogleGetPhoto.AsyncResponse() {
                    @Override
                    public void onDataReceivedSuccess(ArrayList<Bitmap> result) {
                        Bitmap bitmap = result.get(0);
                        int photoHeight = bitmap.getHeight();
                        int photoWidth = bitmap.getWidth();
                        float mapScale = screenWidth/ (float) photoWidth;
                        Matrix matrix = new Matrix();
                        matrix.postScale(mapScale, mapScale);
                        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, photoWidth, photoHeight, matrix, true);
                        Matrix imgM = imgPhoto.getImageMatrix();
                        if(photoHeight*mapScale<imgPhoto.getHeight()){
                            imgM.postTranslate(0,(imgPhoto.getHeight()-(photoHeight*mapScale))/2);
                        }
                        imgPhoto.setImageBitmap(resizeBitmap);
                        imgPhoto.setImageMatrix(imgM);
                    }

                    @Override
                    public void onDataReceivedFailed() {
                        Toast.makeText(PhotoViewerActivity.this, "Photo received failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onDataReceivedFailed() {
                Toast.makeText(PhotoViewerActivity.this,"data received failed!",Toast.LENGTH_SHORT).show();
            }
        });
        imgPhoto.setOnTouchListener(new Move());
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
                    matrix.set(imgPhoto.getImageMatrix());
                    startPoint.set(motionEvent.getX(),motionEvent.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == ZOOM) {
                        float[] values = new float[9];
                        float newDist = distance(motionEvent);
                        if (newDist > 10f){
                            zoom = newDist / oldDist;
                            currentMatrix.set(matrix);
                            currentMatrix.postScale(zoom,zoom,midPoint.x,midPoint.y);
                        }

                        currentMatrix.getValues(values);
                        if(values[0]<=0.5f){
                            currentMatrix.postScale((0.5f)/values[0],(0.5f)/values[4],midPoint.x,midPoint.y);
                        }
                        else if(values[4]>=3.0f){
                            currentMatrix.postScale((3.0f)/values[0],(3.0f)/values[4],midPoint.x,midPoint.y);
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
                    PointF p1 = getLeftPointF(currentMatrix);
                    PointF p2 = getRightPointF(currentMatrix);

                    //左边界复位
                    if(p1.x>0){
                        currentMatrix.postTranslate(-p1.x,0);
                    }

                    //右边界复位
                    if(p2.x<imgPhoto.getWidth()){
                        currentMatrix.postTranslate(imgPhoto.getWidth()-p2.x,0);
                    }

                    //上下边界复位
                    if(p2.y-p1.y>imgPhoto.getHeight()){
                        //上边界复位
                        if(p1.y>0){
                            currentMatrix.postTranslate(0,-p1.y);
                        }
                        //下边界复位
                        if(p2.y<imgPhoto.getHeight()){
                            currentMatrix.postTranslate(0,imgPhoto.getHeight()-p2.y);
                        }
                    }
                    else {
                        float row = (imgPhoto.getHeight()-(p2.y-p1.y))/2;
                        currentMatrix.postTranslate(0,row-p1.y);
                    }

                    //拉过一半时操作前往上一张图像
                    if(p1.x>imgPhoto.getWidth()/2){
                        if(photoRef==0){
                            intent = new Intent(PhotoViewerActivity.this, PhotoViewerActivity.class);
                            intent.putExtra("photoRef",9);
                        }
                        if(photoRef>0&&photoRef<=9){
                            intent = new Intent(PhotoViewerActivity.this, PhotoViewerActivity.class);
                            intent.putExtra("photoRef",photoRef-1);
                        }
                        intent.putExtra("photoid",photoid);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    //拉过一半时操作前往下一张图像
                    if(p2.x<imgPhoto.getWidth()/2){
                        if(photoRef==9){
                            intent = new Intent(PhotoViewerActivity.this, PhotoViewerActivity.class);
                            intent.putExtra("photoRef",0);
                        }
                        if(photoRef<9){
                            intent = new Intent(PhotoViewerActivity.this, PhotoViewerActivity.class);
                            intent.putExtra("photoRef",photoRef+1);
                        }
                        intent.putExtra("photoid",photoid);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
                        currentMatrix.set(imgPhoto.getImageMatrix());
                    }
                    break;
                default:
                    break;
            }
            imgPhoto.setImageMatrix(currentMatrix);
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
        Rect rectTemp = imgPhoto.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2];
        float leftY = values[5];
        return new PointF(leftX,leftY);
    }

    //获取图片的下坐标
    private PointF getRightPointF(Matrix matrix){
        Rect rectTemp = imgPhoto.getDrawable().getBounds();
        float[] values = new float[9];
        matrix.getValues(values);
        float leftX = values[2]+rectTemp.width()*values[0];
        float leftY = values[5]+rectTemp.height()*values[4];
        return new PointF(leftX,leftY);
    }
}
