package fuzihao.test1.Label;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import fuzihao.test1.R;

// Triangle Indicator
public class TriangleIndicatorView extends View {
    private int width = 16;
    private int height = 8;
    private int color = getResources().getColor(R.color.colorPrimary);
    private boolean isUp = true;

    public TriangleIndicatorView(Context context) {
        super(context);
        init();
    }
    public TriangleIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        width = dip2px(getContext(), width);
        height = dip2px(getContext(), height);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(color);

        Path path = new Path();
        if (isUp) {
            path.moveTo(width / 2, 0);// This point is the starting point of the polygon
            path.lineTo(0, height);
            path.lineTo(width, height);
        } else {
            path.moveTo(0, 0);// This point is the starting point of the polygon
            path.lineTo(width, 0);
            path.lineTo(width / 2, height);
        }

        path.close(); // Make these points a closed polygon
        canvas.drawPath(path, p);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLayoutParams().height = height;
        getLayoutParams().width = width;
    }

    public int getRealWidth() {
        return width;
    }

    public int getRealHeight(){
        return height;
    }
    public void setColor(int newColor) {
        color = newColor;
        invalidate();
    }

    //Set direction
    public void setOrientation(boolean newIsUp) {
        isUp = newIsUp;
    }
}
