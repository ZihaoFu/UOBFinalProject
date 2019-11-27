package fuzihao.test1.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static fuzihao.test1.Api.Get.GET;

public class GoogleGetCountryCode {
    public interface AsyncResponse {
        void onDataReceivedSuccess(String res);
        void onDataReceivedFailed();
    }

    public static class GetCountryCodeRes extends AsyncTask<String, Integer, String> {
        GoogleGetCountryCode.AsyncResponse asyncResponse;
        private Context mContext;
        ProgressDialog pd;

        public void setOnAsyncResponse(GoogleGetCountryCode.AsyncResponse asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        public GetCountryCodeRes(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMax(1);
            pd.setTitle("Tips");
            pd.setMessage("Searching country code, please wait...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pd.setIndeterminate(false);
            pd.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            String countryCode="";
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("results");
                JSONObject item = results.getJSONObject(0);
                JSONArray subItem = item.getJSONArray("address_components");
                JSONObject subsubItem = subItem.getJSONObject(0);
                countryCode = subsubItem.getString("short_name");
                Log.e("CountryCode",countryCode);
                if(!countryCode.isEmpty())
                {
                    asyncResponse.onDataReceivedSuccess(countryCode);
                }
                else{
                    asyncResponse.onDataReceivedFailed();
                }
            }catch (JSONException e) {
                Log.e("errorInfo",String.valueOf(e));
                asyncResponse.onDataReceivedFailed();
            }
        }

    }
}
