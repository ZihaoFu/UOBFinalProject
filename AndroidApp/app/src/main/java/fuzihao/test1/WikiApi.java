package fuzihao.test1;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WikiApi{
//    public String getRes() {
//        return res;
//    }
//
//    String res = "";
//    public void create(final Context context, String str){
//
//        String sourceText=decodeSourceText(str);
//        GetApiRes getRes = new GetApiRes();
//        getRes.execute("https://en.wikipedia.org/w/api.php?" +
//                "format=json" +
//                "&action=query" +
//                "&prop=extracts" +
//
//                "&explaintext=" +
//                "&titles="+sourceText);
//        getRes.setOnAsyncResponse(new AsyncResponse() {
//            @Override
//            public void onDataReceivedSuccess(String string) {
//                res = string;
//                Toast toast = Toast.makeText(context,"ok",Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            @Override
//            public void onDataReceivedFailed() {
//                Toast toast = Toast.makeText(context,"data received failed!",Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });
//        Toast toast = Toast.makeText(context,res,Toast.LENGTH_LONG);
//        toast.show();
//
//    }


//    String decodeSourceText(String sourceText){
//        return sourceText.toLowerCase().replace(" ","%20");
//    }

    public interface AsyncResponse {
        void onDataReceivedSuccess(String string);
        void onDataReceivedFailed();
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

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

    public static class GetApiRes extends AsyncTask<String, Void, String> {
        AsyncResponse asyncResponse;
        void setOnAsyncResponse(AsyncResponse asyncResponse)
        {
            this.asyncResponse = asyncResponse;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//        Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
//            String UN="";
//            String PW="";
//            String user="";
            String wikiText="";
            try {
                JSONObject json = new JSONObject(result); // convert String to JSONObject
                //JSONArray articles = json.getJSONArray("array"); // get articles array
                JSONObject query = json.getJSONObject("query");
                JSONObject pages = query.getJSONObject("pages");
                //user=pages.toString();

                String[] str_array = pages.toString().substring(0,20).split(":");
                String string1 = str_array[0];
                String pageid=string1.substring(2,string1.length()-1);
//                user=string1+"\n"+pageid;


                JSONObject page = pages.getJSONObject(pageid);

                wikiText=(String) page.get("extract");
                String[] str_array2 = wikiText.split("==");
                wikiText="";
                for(String temp:str_array2)
                {
                    wikiText=wikiText+"\n----------------------------\n"+temp.trim();
                }

                if(!wikiText.isEmpty())
                {
                    asyncResponse.onDataReceivedSuccess(wikiText.trim());
//                return wikiText.trim();
//                tvResponse.setText(wikiText.trim());

                }
                else{
                    asyncResponse.onDataReceivedFailed();
                }
//                tvResponse.setText("No Result Found");
            }
            catch(JSONException e)
            {
                asyncResponse.onDataReceivedFailed();
//            tvResponse.setText(e.toString());
//            Toast.makeText(getBaseContext(), "JSONException!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


