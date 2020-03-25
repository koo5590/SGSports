package com.example.sgsports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/****** Adaptor for ListView in FacilitySearchActivity *********/
public class FacilityListAdapter extends BaseAdapter{

    private ArrayList<Facility> itemList;
    private LayoutInflater layoutInflater;

    //Constructor
    public FacilityListAdapter(ArrayList<Facility> itemList, Context context){
        this.itemList = itemList;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //set itemList and notify that the data has changed
    private void setItemList(ArrayList<Facility> itemList){
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    //return # of items in itemList
    @Override
    public int getCount(){
        return itemList.size();
    }

    //return item at *position* in itemList
    @Override
    public Object getItem(int position){
        if(0<=position && position<itemList.size()) return itemList.get(position);
        else return null;
    }

    //return the index of the item in itemList
    @Override
    public long getItemId(int position){
        if(0<=position && position<itemList.size()) return position;
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ListViewHolder viewHolder = new ListViewHolder();

        //inflating done only once
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.facility_list_item, parent, false);

            viewHolder.textName = (TextView)convertView.findViewById(R.id.textName);
            viewHolder.textAddr = (TextView)convertView.findViewById(R.id.textAddr);

            convertView.setTag(viewHolder);
        }

        else{
            viewHolder = (ListViewHolder)convertView.getTag();
        }

        //List each item on listView
        Facility item = itemList.get(position);
        //show name and address of the facility
        viewHolder.textName.setText(item.getName());
        viewHolder.textAddr.setText(item.getAddress());

        return convertView;
    }
}
