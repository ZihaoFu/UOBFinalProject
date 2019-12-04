package fuzihao.test1.Api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import static fuzihao.test1.Api.Get.GET;

public class WikiApi{
    // Use this interface to return data to the activity
    public interface AsyncResponse {
        void onDataReceivedSuccess(String string);
        void onDataReceivedFailed();
    }

    public static class GetApiRes extends AsyncTask<String, Integer, String> {
        AsyncResponse asyncResponse;
        private Context mContext;
        ProgressDialog pd;

        public void setOnAsyncResponse(AsyncResponse asyncResponse)
        {
            this.asyncResponse = asyncResponse;
        }

        public GetApiRes(Context context){
            mContext = context;
        }

        // Generate progress bar
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMax(1);
            pd.setTitle("Tips");
            pd.setMessage("Loading the Wikipedia content, please wait...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        // use GET function to get url
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        //Update progress bar
        @Override
        protected void onProgressUpdate(Integer... values) {
            pd.setIndeterminate(false);
            pd.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            String wikiText="";

            // Parse the returned json
            try {
                JSONObject json = new JSONObject(result); // convert String to JSONObject
                JSONObject query = json.getJSONObject("query");
                JSONObject pages = query.getJSONObject("pages");

                String[] str_array = pages.toString().substring(0,20).split(":");
                String string1 = str_array[0];
                String pageid=string1.substring(2,string1.length()-1);

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
                }
                else{
                    asyncResponse.onDataReceivedFailed();
                }
            }
            catch(JSONException e)
            {
                asyncResponse.onDataReceivedFailed();
            }
        }
    }
}


