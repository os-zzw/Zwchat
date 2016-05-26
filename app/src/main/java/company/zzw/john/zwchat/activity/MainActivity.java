package company.zzw.john.zwchat.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import company.zzw.john.zwchat.R;
import company.zzw.john.zwchat.adapter.FragmentAdapter;
import company.zzw.john.zwchat.fragment.ContactsFragment;
import company.zzw.john.zwchat.fragment.SessionFragment;
import company.zzw.john.zwchat.utils.ToolBarUtils;

/**
 * Created by john on 2016/5/16.
 */
public class MainActivity extends FragmentActivity {

    @InjectView(R.id.tv_title)
    TextView tv_title;

    @InjectView(R.id.vp_vp)
    ViewPager vp_vp;

    @InjectView(R.id.ll_main_bottom)
    LinearLayout ll_main_bottom;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private FragmentPagerAdapter fragmentPagerAdapter;
    private ToolBarUtils toolBarUtils;
    private String[] toolbartitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initData();
        initEvent();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initEvent() {
        /**
         * viewpager 的切换事件
         */
        vp_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //选中的时候切换效果
                toolBarUtils.changColor(position);
                //修改title
                tv_title.setText(toolbartitles[position]);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /**
         * toolbar 的  点击事件
         */
        toolBarUtils.setOntoolBarClickListenerr(new ToolBarUtils.OnToolBarClickListener() {
            @Override
            public void onToolBarClick(int position) {
                vp_vp.setCurrentItem(position);
            }
        });
    }

    private void initData() {
        //添加fragment到集合中
        fragments.add(new SessionFragment());
        fragments.add(new ContactsFragment());
        //viewpager 添加adapter

        vp_vp.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        //底部按钮
        //文字内容
        toolbartitles = new String[]{"会话", "联系人"};
        //图片内容
        int[] icons = {R.drawable.selector_meassage, R.drawable.selector_selfinfo};

        toolBarUtils = new ToolBarUtils();
        toolBarUtils.createToolbar(ll_main_bottom, toolbartitles, icons);

        toolBarUtils.changColor(0);
    }
}
