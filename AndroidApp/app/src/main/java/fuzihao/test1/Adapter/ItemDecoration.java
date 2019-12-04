package fuzihao.test1.Adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import fuzihao.test1.R;

public class ItemDecoration extends RecyclerView.ItemDecoration {
    private Paint dividerPaint;
    private TextPaint textPaint;
    private Paint.FontMetrics fontMetrics;
    private int topGap;
    private int dataLength;

    public ItemDecoration(Context context,int length) {
        // set background parameters
        dataLength = length;
        dividerPaint = new Paint();
        dividerPaint.setColor(context.getResources().getColor(R.color.colorPrimary));

        // Set text parameters
        textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(70);
        textPaint.setColor(Color.BLACK);
        textPaint.getFontMetrics(fontMetrics);
        textPaint.setTextAlign(Paint.Align.LEFT);
        fontMetrics = new Paint.FontMetrics();
        topGap = context.getResources().getDimensionPixelSize(R.dimen.section_top);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // set position of title of different sections
        int pos = parent.getChildAdapterPosition(view);
        if(pos==0||pos == dataLength-10){
            outRect.top = topGap;
        }else {
            outRect.top = 0;
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        String groupID = "";
        String preGroupId = "";
        String textLine = "";

        // draw title to screen based on the position of photos
        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            preGroupId = groupID;
            if (position <dataLength - 10 && dataLength!=10){
                groupID = "Panoramic Photo";
                textLine = "Panoramic Photo";
            }else{
                groupID = "Normal Photo";
                textLine = "Normal Photo";
            }
            if (groupID.equals(preGroupId)) continue;

            int viewBottom = view.getBottom();
            float textY = Math.max(topGap, view.getTop());
            if(position+1<itemCount){
                String nextGroupID;
                if (position+1<dataLength-10){
                    nextGroupID = "Panoramic Photo";
                }else{
                    nextGroupID = "Normal Photo";
                }
                if (!nextGroupID.equals(groupID) && viewBottom < textY){
                    textY = viewBottom;
                }
            }

            canvas.drawRect(left, textY - topGap, right, textY, dividerPaint);
            canvas.drawText(textLine, left, textY, textPaint);
        }
    }
}
