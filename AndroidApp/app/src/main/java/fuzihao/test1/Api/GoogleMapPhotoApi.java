package fuzihao.test1.Api;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static fuzihao.test1.Api.Get.GET;

public class GoogleMapPhotoApi {
    public static int selectID = 0;
    public static String countryName = "";

    public interface AsyncResponse {
        void onDataReceivedSuccess(String string);
        void onDataReceivedFailed();
    }

    public static class GetGoogleMapPhotoApiRes extends AsyncTask<String, Void, String> {
        GoogleMapPhotoApi.AsyncResponse asyncResponse;

        public void setOnAsyncResponse(GoogleMapPhotoApi.AsyncResponse asyncResponse) {
            this.asyncResponse = asyncResponse;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            String photoRef = "";
            try {
                if (selectID == 0) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray candidates = jsonObject.getJSONArray("candidates");
                    JSONObject candidate = candidates.getJSONObject(0);
                    String placeid = candidate.getString("place_id");
                    if(!placeid.isEmpty())
                    {
                        asyncResponse.onDataReceivedSuccess(placeid);
                    }
                    else{
                        asyncResponse.onDataReceivedFailed();
                    }
                }else if (selectID == 1) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject photoRes = jsonObject.getJSONObject("result");
                    JSONArray photos = photoRes.getJSONArray("photos");

                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photo = photos.getJSONObject(i);
                        String height = photo.getString("height");
                        String width = photo.getString("width");
                        String photo_reference = photo.getString("photo_reference");

                        photoRef = photoRef + "," + height + "," + width + "," + photo_reference;
                    }

                    if(!photoRef.isEmpty())
                    {
                        asyncResponse.onDataReceivedSuccess(photoRef);
                    }
                    else{
                        asyncResponse.onDataReceivedFailed();
                    }
                } else if (selectID == 2) {

                }
            }catch (JSONException e) {
                asyncResponse.onDataReceivedFailed();
            }
        }
    }
}
