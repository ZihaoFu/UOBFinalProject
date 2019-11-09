package fuzihao.test1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 三角形指示器
 * Created by Zihao Fu on 2019/11/07.
 */
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
            path.moveTo(width / 2, 0);// 此点为多边形的起点
            path.lineTo(0, height);
            path.lineTo(width, height);
        } else {
            path.moveTo(0, 0);// 此点为多边形的起点
            path.lineTo(width, 0);
            path.lineTo(width / 2, height);
        }

        path.close(); // 使这些点构成封闭的多边形
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

    /**
     * 设置方向
     *
     * @param newIsUp 是否向上
     */
    public void setOrientation(boolean newIsUp) {
        isUp = newIsUp;
    }
}
