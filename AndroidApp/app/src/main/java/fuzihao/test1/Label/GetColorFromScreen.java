package fuzihao.test1.Label;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ByteBuffer;

public class GetColorFromScreen {
        private static final String TAG = "GBData";
        public static ImageReader reader;
        private static Bitmap bitmap;

        public static int getColor(int x, int y) {
            if (reader == null) {
                Log.w(TAG, "getColor: reader is null");
                return -1;
            }

            Image image = reader.acquireLatestImage(); // get image data

            if (image == null) {
                if (bitmap == null) {
                    return -1;
                }
                return bitmap.getPixel(x, y);
            }
            // get width and height of image
            int width = image.getWidth();
            int height = image.getHeight();
            // build plane based image
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer(); //Get buffered data of Y component data in plane
            int pixelStride = planes[0].getPixelStride(); // Get the distance (step) between two consecutive color values in a row
            int rowStride = planes[0].getRowStride(); // Get the distance between pixels in a row
            int rowPadding = rowStride - pixelStride * width;
            // create bitmap
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            }
            bitmap.copyPixelsFromBuffer(buffer); // Enter the data in the buffer into the bitmap
            image.close();

            return bitmap.getPixel(x, y); // return pixel of (x,y)
        }
}
