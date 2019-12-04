package fuzihao.test1.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GoogleGetPhoto {
    // Use this interface to return data to the activity
    public interface AsyncResponse {
        void onDataReceivedSuccess(ArrayList<Bitmap> bitmap);
        void onDataReceivedFailed();
    }

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

        // Generate progress bar
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMax(10);
            pd.setTitle("Tips");
            pd.setMessage("Loading picture, please wait...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        // get url
        @Override
        protected ArrayList<Bitmap> doInBackground(String... urls) {
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

        //Update progress bar
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
