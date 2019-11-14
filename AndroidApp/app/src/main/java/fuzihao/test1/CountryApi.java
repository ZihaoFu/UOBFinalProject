package fuzihao.test1;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CountryApi {
    public interface AsyncResponse2 {
        void onDataReceivedSuccess(String string);
        void onDataReceivedFailed();
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("x-rapidapi-host", "restcountries-v1.p.rapidapi.com");
            httpGet.setHeader("x-rapidapi-key", "36fa6976b6msh0f070450f049f8dp180f17jsn8977e75b0711");

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else{
                result = "Did not work!";
            }

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

    public static class GetCountryApiRes extends AsyncTask<String, Void, String> {
        CountryApi.AsyncResponse2 asyncResponse;

        void setOnAsyncResponse(CountryApi.AsyncResponse2 asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            String timeZone = "";
            String currencyRes = "";
            String languageRes = "";

            String res = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
//                JSONArray jsonArray=new JSONArray(result);
//                JSONObject object=jsonArray.getJSONObject(0);
                String name = jsonObject.getString("name");
                String capital = jsonObject.getString("capital");
//                String region = jsonObject.getString("region");
//                String subregion = jsonObject.getString("subregion");
                String population = String.valueOf(jsonObject.getInt("population"));
                String area = String.valueOf(jsonObject.getInt("area"));
                JSONArray timezones = jsonObject.getJSONArray("timezones");
                for (int i = 0; i < timezones.length(); i++) {
                    String time = timezones.getString(i);
                    timeZone = timeZone + time;
                }
//                JSONArray currencies = jsonObject.getJSONArray("currencies");
//                for (int j = 0; j < currencies.length(); j++) {
//                    String currency = currencies.getString(j);
//                    currencyRes = currencyRes + currency;
//                }
//                JSONArray languages = jsonObject.getJSONArray("languages");
//                for (int k = 0; k < languages.length(); k++) {
//                    String languagesString = languages.getString(k);
//                    languageRes = languageRes + languagesString;
//                }
                if(!name.isEmpty())
                {
//                    res = name + "-" + capital + "-" + region + "-" + subregion + "-" + population + "-" + area + "-" + timeZone + "-" + currencyRes + "-" + languageRes;
                    res = name + "-" + capital + "-" + population + "-" + area + "-" + timeZone;
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
