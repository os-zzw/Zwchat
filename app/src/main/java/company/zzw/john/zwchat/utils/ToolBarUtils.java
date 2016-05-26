package company.zzw.john.zwchat.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import company.zzw.john.zwchat.R;

/**
 * Created by john on 2016/5/16.
 */
public class ToolBarUtils {

    private List<TextView> textViews = new ArrayList<>();



    public void createToolbar(LinearLayout container, String[] toolbartitles, int[] icons) {
        for (int i = 0; i < icons.length; i++) {
            TextView tv = (TextView) View.inflate(container.getContext(), R.layout.inflater_toobar_bottom, null);
            tv.setText(toolbartitles[i]);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, icons[i], 0, 0);
            int width = 0;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.weight = 1;
            container.addView(tv, params);
            textViews.add(tv);
            //设置点击事件
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //不同模块之间传值需要用接口进行回调
                    toolBarClickListener.onToolBarClick(finalI);
                }
            });
        }
    }

    public void changColor(int position) {
        //还原所有的颜色
        for (TextView tv : textViews) {
            tv.setSelected(false);
        }
        textViews.get(position).setSelected(true);//设置选中状态.
    }

    //1创建一个接口和接口方法
    public interface OnToolBarClickListener {
        void onToolBarClick(int position);
    }

    //2 定义接口变量
    OnToolBarClickListener toolBarClickListener;

    //3.使用该接口变量
    //4.暴露一个公共的方法,外界可以进行监听
    public void setOntoolBarClickListenerr(OnToolBarClickListener toolBarClickListener) {
        this.toolBarClickListener = toolBarClickListener;
    }
}
