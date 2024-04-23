package com.apps.doctorkeeper_android.ui.consult.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.doctorkeeper_android.data.consult.CommonCode;
import com.apps.doctorkeeper_android.databinding.ItemCommonCodeBinding;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;

import java.util.ArrayList;

public class CommonCodeAdapter extends RecyclerView.Adapter<CommonCodeAdapter.ViewHolder> {

    private ArrayList<CommonCode> items;
    CommonCode fInfo;
    int count;
    Context mConText;
    ItemClick itemClick;
    int clickPos=-1;


    public CommonCodeAdapter(Context context, ArrayList<CommonCode> items, ItemClick itemClick) {
        this.items = items;
        this.count = items.size();
        this.itemClick = itemClick;
        this.mConText = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ItemCommonCodeBinding binding = ItemCommonCodeBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new ViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull CommonCodeAdapter.ViewHolder holder, int position) {
        holder.bindItem(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCommonCodeBinding itemBinding;
        public ViewHolder(@NonNull ItemCommonCodeBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
        }

        void bindItem(CommonCode item , int position){
            itemBinding.title.setText((item.getCdNm()));
            if(position==clickPos){
                itemBinding.title.setBackgroundColor(Color.parseColor("#000000"));
            }else{
                itemBinding.title.setBackgroundColor(Color.parseColor("#888888"));
            }


            itemBinding.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.itemClick("cateClick",item.getCdId());
                    clickPos=position;
                    notifyDataSetChanged();
                }
            });
        }



    }

}
