package fuzihao.test1.Api;

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

    public static class GetCountryCodeRes extends AsyncTask<String, Void, String> {
        GoogleGetCountryCode.AsyncResponse asyncResponse;

        public void setOnAsyncResponse(GoogleGetCountryCode.AsyncResponse asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
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
