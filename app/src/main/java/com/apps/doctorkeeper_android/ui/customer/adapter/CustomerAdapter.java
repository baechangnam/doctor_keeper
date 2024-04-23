package com.apps.doctorkeeper_android.ui.customer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.databinding.ItemCommonCodeBinding;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private ArrayList<CustomerModel> items;
    CustomerModel fInfo;
    int count;
    Context mConText;
    int clickPos=-1;
    ItemClick itemClick;

    public CustomerAdapter(Context context, ArrayList<CustomerModel> items, ItemClick itemClick) {
        this.items = items;
        this.count = items.size();
        this.mConText = context;
        this.itemClick = itemClick;


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
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
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

        void bindItem(CustomerModel item){
            itemBinding.title.setText(item.getCustNm()+"("+ item.getChrtNo()+")");
            itemBinding.title.setBackgroundColor(Color.parseColor("#000000"));
            itemBinding.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.itemClick(item.getCustNo(),item.getChrtNo());
                }
            });



        }



    }

}
