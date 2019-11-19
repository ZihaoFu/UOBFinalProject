package fuzihao.test1.Api;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import static fuzihao.test1.Api.Get.GET;

public class WikiApi{

    public interface AsyncResponse {
        void onDataReceivedSuccess(String string);
        void onDataReceivedFailed();
    }

    public static class GetApiRes extends AsyncTask<String, Void, String> {
        AsyncResponse asyncResponse;
        public void setOnAsyncResponse(AsyncResponse asyncResponse)
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


