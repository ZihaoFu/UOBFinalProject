package fuzihao.test1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

public class LabelMenuLayout extends LinearLayout {
    private TriangleIndicatorView triangleIndicatorViewUp;
    private TriangleIndicatorView triangleIndicatorViewDown;
    private LinearLayout linearLayout;

    private int backgroundResource = R.drawable.label_shap;

    public LabelMenuLayout(Context context){
        super(context);
        initView();
    }

    public LabelMenuLayout(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        initView();
    }

    private void initView(){
        setOrientation(VERTICAL);
        setGravity(Gravity.LEFT);

        triangleIndicatorViewUp = new TriangleIndicatorView(getContext());
        triangleIndicatorViewUp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(triangleIndicatorViewUp);

        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setBackgroundResource(backgroundResource);

        addView(linearLayout);

        triangleIndicatorViewDown = new TriangleIndicatorView(getContext());
        triangleIndicatorViewDown.setOrientation(false);
        triangleIndicatorViewUp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(triangleIndicatorViewDown);
        triangleIndicatorViewDown.setVisibility(GONE);
    }

    /**
     * 设置弹窗显示位置
     *
     * @param isUp true、在上面
     */
    public void setOrientation(boolean isUp) {
        if (isUp) {
            triangleIndicatorViewUp.setVisibility(GONE);
            triangleIndicatorViewDown.setVisibility(VISIBLE);
        } else {
            triangleIndicatorViewUp.setVisibility(VISIBLE);
            triangleIndicatorViewDown.setVisibility(GONE);
        }
    }

    public void setBackgroundResource(int newBackgroundResource) {
        backgroundResource = newBackgroundResource;
        if (linearLayout != null) {
            linearLayout.setBackgroundResource(backgroundResource);
        }
    }

    public void setTriangleIndicatorViewColor(int color) {
        triangleIndicatorViewUp.setColor(color);
        triangleIndicatorViewDown.setColor(color);
    }

    public void setBackgroundColor(int color) {
        linearLayout.setBackgroundColor(color);
    }

    public TriangleIndicatorView getTriangleIndicatorViewUp() {
        return triangleIndicatorViewUp;
    }

    public TriangleIndicatorView getTriangleIndicatorViewDown() {
        return triangleIndicatorViewDown;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }
}
