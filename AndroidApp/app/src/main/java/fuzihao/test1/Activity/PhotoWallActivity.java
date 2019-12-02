package fuzihao.test1.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fuzihao.test1.Adapter.ItemDecoration;
import fuzihao.test1.Adapter.RecyclerAdapter;
import fuzihao.test1.Api.GoogleGetPhoto;
import fuzihao.test1.Api.GoogleMapPhotoApi;
import fuzihao.test1.R;

import static fuzihao.test1.Api.GoogleMapPhotoApi.selectID;

public class PhotoWallActivity extends AppCompatActivity {
    final String apiKey = "AIzaSyBbmKXXOLKFsICWeJkFWtp4Z9Jy9RtljX4";
    String selectid;
    String selectName;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Drawable> data = new ArrayList<>();
    ArrayList<Integer> height = new ArrayList<>();
    ArrayList<Integer> width = new ArrayList<>();

    float screenHeight;
    float screenWidth;
    int vrHeight;
    int vrWidth;
    float vrScale;

    public String [] arr;

    List<String> vrCountry = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_wall);

        Intent intent = getIntent();
        selectid = intent.getStringExtra("placeid");
        selectName = intent.getStringExtra("placeName");

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();

        addVRImageToWall();

        selectID = 1;
        photoApi();

        vrCountry.add("France");
        vrCountry.add("United Kingdom");
        vrCountry.add("Russia");
        vrCountry.add("United States");
        vrCountry.add("China");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void addVRImageToWall(){
        try{
            if (selectName.equals("France")){
                data.add(getDrawable(R.drawable.france360));
            }
            else if(selectName.equals("United Kingdom")){
                data.add(getDrawable(R.drawable.panorama01));
            }
            else if(selectName.equals("Russia")){
                data.add(getDrawable(R.drawable.panorama02));
            }
            else if(selectName.equals("United States")){
                data.add(getDrawable(R.drawable.panorama03));
            }
            else if(selectName.equals("China")){
                data.add(getDrawable(R.drawable.panorama04));
            }

            if(data.get(0)!=null){
                vrHeight = data.get(0).getIntrinsicHeight();
                vrWidth = data.get(0).getIntrinsicWidth();
                vrScale = screenWidth/ (float) vrWidth;
                height.add(Math.round(vrHeight *vrScale));
                width.add(Math.round(vrWidth*vrScale));
            }
        }catch (Exception e){
            Toast.makeText(PhotoWallActivity.this,"This Country does not have VR image",Toast.LENGTH_SHORT).show();
            data = new ArrayList<>();
            height = new ArrayList<>();
            width = new ArrayList<>();
        }
    }

    private void initView(final String [] photoInfo){
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView = (RecyclerView) findViewById(R.id.photoWall);

        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setHasFixedSize(true);

        adapter = new RecyclerAdapter(data,height,width);
        ((RecyclerAdapter) adapter).setOnItemClickListener(new RecyclerAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                if (vrCountry.contains(selectName)){
                    if(position == 0){
                        Intent intent = new Intent(PhotoWallActivity.this, VRActivity.class);
                        intent.putExtra("vr",selectName);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(PhotoWallActivity.this,"click " + position + " item", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PhotoWallActivity.this, PhotoViewerActivity.class);
                        intent.putExtra("photoRef",position);
                        intent.putExtra("photoid",selectid);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(PhotoWallActivity.this, PhotoViewerActivity.class);
                    intent.putExtra("photoRef",position+1);
                    intent.putExtra("photoid",selectid);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecoration(this, data.size()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void photoApi(){
        GoogleMapPhotoApi.GetGoogleMapPhotoApiRes getCountryRes = new GoogleMapPhotoApi.GetGoogleMapPhotoApiRes();
        getCountryRes.execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectid + "&key=" + apiKey + "&fields=photos");
        getCountryRes.setOnAsyncResponse(new GoogleMapPhotoApi.AsyncResponse(){
            @Override
            public void onDataReceivedSuccess(String string) {
                arr = string.split(",");

                GoogleGetPhoto.GetPhotoApiRes getPhoto = new GoogleGetPhoto.GetPhotoApiRes(PhotoWallActivity.this);
                String [] urls = new String[10];
                int count = 0;
                for (int i = 1; i<=28; i = i + 3){
                    float scale = screenWidth/ Float.valueOf(arr[i+1]);
                    height.add(Math.round(Integer.valueOf(arr[i])*scale));
                    width.add(Math.round(Integer.valueOf(arr[i+1])*scale));
                    String url = "https://maps.googleapis.com/maps/api/place/photo?key="+apiKey+"&photoreference="+arr[i+2]+"&maxheight="+arr[i]+"&maxwidth="+arr[i+1];
                    urls[count] = url;
                    count = count + 1;
                }
                getPhoto.execute(urls);
                getPhoto.setOnAsyncResponse(new GoogleGetPhoto.AsyncResponse(){
                    @Override
                    public void onDataReceivedSuccess(ArrayList<Bitmap> result) {
//                        data.add(getDrawable(R.drawable.france360));
                        for(Bitmap res : result){
                            data.add(new BitmapDrawable(res));
                        }
                        initView(arr);
                    }

                    @Override
                    public void onDataReceivedFailed() {
                        Toast.makeText(PhotoWallActivity.this,"Photo received failed!",Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onDataReceivedFailed() {
                Toast.makeText(PhotoWallActivity.this,"data received failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
