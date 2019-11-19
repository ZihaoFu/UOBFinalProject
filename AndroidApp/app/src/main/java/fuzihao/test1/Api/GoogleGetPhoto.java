package fuzihao.test1.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fuzihao.test1.Activity.PhotoWallActivity;
import fuzihao.test1.R;

import static fuzihao.test1.Api.Get.GET;

public class GoogleGetPhoto {

    public interface AsyncResponse {
        void onDataReceivedSuccess(ArrayList<Bitmap> bitmap);
        void onDataReceivedFailed();
    }
//
//    public static ArrayList<Bitmap> GET2(String... urls){
//        ArrayList<Bitmap> res = new ArrayList<>();
//        for (String url : urls) {
//            try {
//                URL url2 = new URL(url);
//                InputStream is = url2.openStream();
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//                is.close();
//                res.add(bitmap);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                return null;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        return res;
//    }

    public static class GetPhotoApiRes extends AsyncTask<String, Integer, ArrayList<Bitmap>> {
        GoogleGetPhoto.AsyncResponse asyncResponse;
        private Context mContext;
        ProgressDialog pd;

        public void setOnAsyncResponse(GoogleGetPhoto.AsyncResponse asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        public GetPhotoApiRes(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMax(10);
            pd.setTitle("Tips");
            pd.setMessage("Loading picture, please wait...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... urls) {
//            return GET2(urls);
            ArrayList<Bitmap> res = new ArrayList<>();
            int i = 0;
            for (String url : urls) {
                try {
                    URL url2 = new URL(url);
                    InputStream is = url2.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    res.add(bitmap);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                i = i + 1;
                publishProgress(i);
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pd.setIndeterminate(false);
            pd.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
            pd.dismiss();
            asyncResponse.onDataReceivedSuccess(result);
        }
    }
}
