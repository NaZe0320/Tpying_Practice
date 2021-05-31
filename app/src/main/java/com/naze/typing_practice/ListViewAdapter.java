package com.naze.typing_practice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewAdapterData> list = new ArrayList<ListViewAdapterData>();

    public ListViewAdapter() {

    }

    @Override
    public int getCount(){
        return list.size();
    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현


    @Override
    public long getItemId(int position) {
        return position;
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null ){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }
//
//        TextView tv_date = (TextView)convertView.findViewById(R.id.tv_date);
        TextView tv_title = (TextView)convertView.findViewById(R.id.tv_title);
        TextView tv_type = (TextView)convertView.findViewById(R.id.tv_type);
        TextView tv_acc = (TextView)convertView.findViewById(R.id.tv_acc);

        ListViewAdapterData item = list.get(position);

        //tv_date.setText(item.getDate()+"");
        tv_title.setText(item.getTitle());
        tv_type.setText(item.getType()+"타");
        tv_acc.setText(item.getAcc()+"%");

        return convertView;
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현



    public void addItem( String title, int type, int acc){
        ListViewAdapterData item = new ListViewAdapterData();

        //item.setDate(date);
        item.setTitle(title);
        item.setType(type);
        item.setAcc(acc);

        list.add(item);

    }
}
