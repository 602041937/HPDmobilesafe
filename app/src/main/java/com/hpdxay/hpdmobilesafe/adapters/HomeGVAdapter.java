package com.hpdxay.hpdmobilesafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hpdxay.hpdmobilesafe.R;

import java.util.List;

/**
 * Created by hpd on 2016/2/1.
 */
public class HomeGVAdapter extends BaseAdapter {

    private String[] titles = {"手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"};
    private int[] ids = {R.mipmap.safe, R.mipmap.callmsgsafe, R.mipmap.app,
            R.mipmap.taskmanager, R.mipmap.netmanager, R.mipmap.trojan,
            R.mipmap.sysoptimize, R.mipmap.atools, R.mipmap.settings
    };
    private Context context;

    public HomeGVAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_home_gv, parent, false);
        ImageView image = (ImageView) view.findViewById(R.id.item_list_home_gv_image);
        TextView title = (TextView) view.findViewById(R.id.item_list_home_gv_title);
        image.setImageResource(ids[position]);
        title.setText(titles[position]);
        return view;
    }
}
