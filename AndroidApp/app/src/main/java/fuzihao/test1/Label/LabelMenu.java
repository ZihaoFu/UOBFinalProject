package fuzihao.test1.Label;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import fuzihao.test1.R;

public class LabelMenu {
    private static final int MIN_MARGIN_linearLayout = 5;
    private static final int MARGIN_SCREEN = 5;
    private Context mContext;
    private PopupWindow popupWindow;

    private TriangleIndicatorView triangleIndicatorViewUp;
    private TriangleIndicatorView triangleIndicatorViewDown;
    private LinearLayout linearLayout;
    private LabelMenuLayout labelMenuLayout;

    private ListView listView;
    private View btnView;
    private LabelAdapter labelAdapter;
    private OnItemClickListener mOnItemClickListener;

    private List<MenuItem> menuItemList;

    //Minimum distance between indicator and menu layout
    private int indicatorToContainerMinMargin;
    private int horizontalMargin = 10;
    private int iconTextMargin = 10;

    //Distance from screen
    private int marginScreen;
    private int screenWidth;
    private int screenHeight;
    private int iconWidth = 25;
    private int textSize;
    private int itemHeight;
    private int totalHeight;
    private int maxTextWidth;
    private int width;

    private boolean isShowIcon = false;
    private boolean isShowAtUp = false;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private int itemTextColor = 0;

    public LabelMenu(Context context){
        mContext = context;
        init();

        labelMenuLayout = new LabelMenuLayout(context);
        triangleIndicatorViewUp = labelMenuLayout.getTriangleIndicatorViewUp();
        triangleIndicatorViewDown = labelMenuLayout.getTriangleIndicatorViewDown();
        linearLayout = labelMenuLayout.getLinearLayout();

        screenWidth = getScreenWidth(mContext);
        create();
    }

    //Initialize variables
    private void init(){
        indicatorToContainerMinMargin = dip2px(mContext,MIN_MARGIN_linearLayout);
        marginScreen = dip2px(mContext,MARGIN_SCREEN);

        iconTextMargin = (int) mContext.getResources().getDimension(R.dimen.drop_pop_menu_icon_text_margin);
        horizontalMargin = (int) mContext.getResources().getDimension(R.dimen.drop_pop_menu_item_horizontal_margin);
        iconWidth = (int) mContext.getResources().getDimension(R.dimen.drop_pop_menu_icon_width);
        itemHeight = (int) mContext.getResources().getDimension(R.dimen.drop_pop_menu_item_height);
        textSize = (int) mContext.getResources().getDimension(R.dimen.drop_pop_menu_text_size);

        screenHeight = getScreenHeight(mContext);
    }

    private void create() {
        popupWindow = new PopupWindow(labelMenuLayout, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f);
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss();
                }
            }
        });

        labelMenuLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void initListView() {
        listView = new ListView(mContext);
        listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT));
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mOnItemClickListener != null) {
                    MenuItem menuItem = null;
                    if (menuItemList != null) {
                        menuItem = menuItemList.get(position);
                    }
                    mOnItemClickListener.onItemClick(adapterView,view,position,id,menuItem);
                }
                popupWindow.dismiss();
            }
        });
        linearLayout.addView(listView);
    }

    public ListView getListView() {
        return listView;
    }

    public void setWidth(int newWidth) {
        width = newWidth;
        if (btnView != null) {
            updateViewPosition(btnView);
        }
    }

    public void setIsShowIcon(boolean newIsShowIcon) {
        isShowIcon = newIsShowIcon;
    }

    public void setMenuList(List<MenuItem> menuList) {
        if (menuItemList != null) {
            menuItemList.clear();
        } else {
            menuItemList = new ArrayList<>();
        }
        menuItemList.addAll(menuList);

        checkWidth();
        checkHeight();

        initListView();
        if (labelAdapter == null) {
            labelAdapter = new LabelAdapter();
            listView.setAdapter(labelAdapter);
        } else {
            labelAdapter.notifyDataSetChanged();
        }
    }

    public void setBackgroundResource(int backgroundResource) {
        labelMenuLayout.setBackgroundResource(backgroundResource);
    }

    public void setBackgroundColor(int color) {
        labelMenuLayout.setBackgroundColor(color);
    }
    // Set Triangle Indicator arrow colour
    public void setTriangleIndicatorViewColor(int color) {
        labelMenuLayout.setTriangleIndicatorViewColor(color);
    }

    // set item text colour
    public void setItemTextColor(int color) {
        itemTextColor = color;
    }

    private void checkWidth() {
        int size = getListSize(menuItemList);
        String temp = "";
        for (int i = 0; i < size; i++) {
            String itemTitle = menuItemList.get(i).getItemTitle();
            if (itemTitle.length() > temp.length()) {
                temp = itemTitle;
            }
        }

        maxTextWidth = (int) getTextWidth(temp, textSize);
    }

    private void checkHeight() {
        int size = getListSize(menuItemList);
        totalHeight = size * itemHeight;
    }

    int[] location = new int[2];

    public void show(final View parent,int[] labelLocation) {
        btnView = parent;
        setBackgroundAlpha(50f);
        labelMenuLayout.requestFocus();

        isShowAtUp = false;
//        int parentHeight = parent.getHeight();
//        int[] location = new int[2];
//        parent.getLocationOnScreen(location);
        location = labelLocation;
        int x = location[0];
        int y = location[1];
        int popMenuHeight = totalHeight + triangleIndicatorViewUp.getRealHeight();
//        if (screenHeight - y - parentHeight < popMenuHeight) {
//            isShowAtUp = true;
//        }

        if (screenHeight - y < popMenuHeight) {
            isShowAtUp = true;
        }

        if (isShowAtUp) {
            popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, screenHeight - y);
        } else {
            popupWindow.showAtLocation(parent, Gravity.TOP, 0, y);
//            popupWindow.showAsDropDown(parent, 0, 0);
        }
        updateView(x);
    }

    private void updateView(final int x) {

        labelMenuLayout.post(new TimerTask() {
            @Override
            public void run() {
                updateViewPosition(btnView);
            }
        });
    }

    private void updateViewPosition(View parent) {
//        int parentWidth = parent.getMeasuredWidth();
//        int[] location = new int[2];
//        parent.getLocationOnScreen(location);
        int x = location[0];

//        int centerX = x + parentWidth / 2;
        int centerX = x;
        int leftMargin = x;
//        int rightMargin = screenWidth - leftMargin - parentWidth;
        int rightMargin = screenWidth - leftMargin;
        int containerViewHalfWidth = width / 2;
        int indicatorViewHalfWidth = triangleIndicatorViewUp.getRealWidth() / 2;

        LinearLayout.LayoutParams upIndicatorParams = (LinearLayout.LayoutParams) triangleIndicatorViewUp.getLayoutParams();
        LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        containerParams.width = width;

        if (leftMargin < rightMargin) {//in the left
            if (leftMargin >= containerViewHalfWidth) {//Show in the middle
                upIndicatorParams.leftMargin = centerX - indicatorViewHalfWidth;
                containerParams.leftMargin = centerX - containerViewHalfWidth;
            } else {
                upIndicatorParams.leftMargin = centerX - indicatorViewHalfWidth;
                containerParams.leftMargin = marginScreen;
                if (upIndicatorParams.rightMargin > containerParams.rightMargin - indicatorToContainerMinMargin
                        && width <= screenWidth / 2) {//Correction arrow to the right of the list
                    int newLeftMargin = upIndicatorParams.leftMargin - indicatorToContainerMinMargin;
                    if (newLeftMargin >= marginScreen) {
                        containerParams.leftMargin = newLeftMargin;
                    }
                }
            }
        } else if (leftMargin > rightMargin) {//in the right
            if (rightMargin >= containerViewHalfWidth) {
                upIndicatorParams.leftMargin = centerX - indicatorViewHalfWidth;
                containerParams.leftMargin = centerX - containerViewHalfWidth;
            } else {
                upIndicatorParams.leftMargin = centerX - indicatorViewHalfWidth;
                containerParams.leftMargin = screenWidth - containerViewHalfWidth * 2 - marginScreen;
                if (upIndicatorParams.leftMargin < containerParams.leftMargin + indicatorToContainerMinMargin) {//Correction arrow to the left of the list
                    containerParams.leftMargin = upIndicatorParams.leftMargin - indicatorToContainerMinMargin;
                }
            }
        } else {//in the middle
            int left = centerX - indicatorViewHalfWidth;
            int right = centerX - containerViewHalfWidth;
            upIndicatorParams.leftMargin = left;
            containerParams.leftMargin = right;
        }

        if (upIndicatorParams.leftMargin <= 0) {//Correction of the bounds of the triangle indicator over the range
            upIndicatorParams.leftMargin = marginScreen + indicatorToContainerMinMargin;
        } else if (upIndicatorParams.leftMargin + indicatorViewHalfWidth * 2 >= screenWidth) {
            upIndicatorParams.leftMargin = screenWidth - indicatorViewHalfWidth * 2 - marginScreen - indicatorToContainerMinMargin;
        }

        labelMenuLayout.setOrientation(isShowAtUp);

        triangleIndicatorViewUp.setLayoutParams(upIndicatorParams);
        triangleIndicatorViewDown.setLayoutParams(upIndicatorParams);
        linearLayout.setLayoutParams(containerParams);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    // set Background Alpha
    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    // Convert dp to px
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

//    private int sp2px(Context context, float spValue) {
//        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * fontScale + 0.5f);
//    }

    private int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public float getTextWidth(String text, int textSize) {
//        int digitAndLetterCount = getDigitLetterCount(text);
//        int chLength = text.length() - digitAndLetterCount;
//        return chLength * textSize + digitAndLetterCount * textSize * 2 / 3;
        return text.length() * textSize/1.9f;
    }

//    private int getDigitLetterCount(String text) {
//        int length = text.length();
//        int count = 0;
//        for (int i = 0; i < length; i++) {
//            char c = text.charAt(i);
//            if (c >= '0' && c <= '9') {
//                count++;
//            } else if (c >= 'a' && c <= 'z') {
//                count++;
//            }
//        }
//        return count;
//    }

    private int getListSize(List list) {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    private class LabelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menuItemList == null ? 0 : menuItemList.size();
        }

        @Override
        public MenuItem getItem(int position) {
            return menuItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null || view.getTag() == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_label_menu, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.iconIv.setVisibility(isShowIcon ? View.VISIBLE : View.GONE);
            if (isShowIcon) {
                holder.iconIv.setImageDrawable(getItem(position).iconResId);
//                holder.iconIv.setImageResource(getItem(position).iconResId);
            }
            holder.textTv.setText(getItem(position).itemTitle);
            if (itemTextColor != 0) {
                holder.textTv.setTextColor(itemTextColor);
            }

            //Adjust total width based on longest text
            boolean iconIsGone = holder.iconIv.getVisibility() == View.GONE;
            if (position == 0) {
                width = maxTextWidth + horizontalMargin * 2;
                if (!iconIsGone) {
                    width += iconWidth + iconTextMargin;
                }
            }

            return view;
        }

        private class ViewHolder {
            ImageView iconIv;
            TextView textTv;

            public ViewHolder(View view) {
                iconIv = (ImageView) view.findViewById(R.id.iv_icon);
                textTv = (TextView) view.findViewById(R.id.tv_text);
            }
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> adapterView, View view, int position, long id, MenuItem menuItem);
    }
}
