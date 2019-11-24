package fuzihao.test1.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static fuzihao.test1.Api.Get.GET;

public class CountryApi {
    public interface AsyncResponse {
        void onDataReceivedSuccess(String string);
        void onDataReceivedFailed();
    }

    public static class GetCountryApiRes extends AsyncTask<String, Integer, String> {
        CountryApi.AsyncResponse asyncResponse;
        private Context mContext;
        ProgressDialog pd;

        public void setOnAsyncResponse(CountryApi.AsyncResponse asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        public GetCountryApiRes(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMax(1);
            pd.setTitle("Tips");
            pd.setMessage("Loading the label, please wait...");
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
            String timeZone = "";
            String currencyRes = "";
            String languageRes = "";

            String res = "";
            try {
                pd.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                String name = jsonObject.getString("name");
                String capital = jsonObject.getString("capital");
                String region = jsonObject.getString("region");
                String subregion = jsonObject.getString("subregion");
                String population = String.valueOf(jsonObject.getInt("population"));
                String area = String.valueOf(jsonObject.getInt("area"));
                JSONArray timezones = jsonObject.getJSONArray("timezones");
                for (int i = 0; i < timezones.length(); i++) {
                    String time = timezones.getString(i);
                    timeZone = timeZone + time;
                }
                JSONArray currencies = jsonObject.getJSONArray("currencies");
                for (int j = 0; j < currencies.length(); j++) {
                    String currency = currencies.getString(j);
                    currencyRes = currencyRes + currency;
                }
                JSONArray languages = jsonObject.getJSONArray("languages");
                for (int k = 0; k < languages.length(); k++) {
                    String languagesString = languages.getString(k);
                    languageRes = languageRes + languagesString;
                }
                if(!name.isEmpty())
                {
                    res = name + "@" + capital + "@" + population + "@" + area + "@" + timeZone + "@" + region + "@" + subregion + "@" + currencyRes + "@" + languageRes;
                    asyncResponse.onDataReceivedSuccess(res);
                }
                else{
                    asyncResponse.onDataReceivedFailed();
                }
            } catch (JSONException e) {
                asyncResponse.onDataReceivedFailed();
            }
        }
    }
}
