package com.apps.doctorkeeper_android.ui.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.customer.model.CustomerXrayModel;
import com.apps.doctorkeeper_android.databinding.ItemImageBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomerXrayImageAdapter extends RecyclerView.Adapter<CustomerXrayImageAdapter.ViewHolder> {

    private ArrayList<CustomerXrayModel> items;
    CustomerXrayModel fInfo;
    int count;
    Context mConText;
    int clickPos=-1;
    ItemClick itemClick;
    String baseImageUrl="";
    int selPos=0;

    public int getSelPos() {
        return selPos;
    }

    public void setSelPos(int selPos) {
        this.selPos = selPos;
        notifyDataSetChanged();
    }

    public CustomerXrayImageAdapter(Context context, ArrayList<CustomerXrayModel> items, ItemClick itemClick) {
        this.items = items;
        this.count = items.size();
        this.mConText = context;
        this.itemClick = itemClick;

        RbPreference preference = new RbPreference(mConText);
        baseImageUrl = preference.getValue(RbPreference.BASE_URL, CommonValue.BASE_URL);
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
        ItemImageBinding binding = ItemImageBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new ViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull CustomerXrayImageAdapter.ViewHolder holder, int position) {
        holder.bindItem(items.get(position) ,position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemImageBinding itemBinding;
        public ViewHolder(@NonNull ItemImageBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
        }

        void bindItem(CustomerXrayModel item,int pos){
            String fileNm =item.getFileNm();
            String filePathNm = item.getFilePathNm();

            String imageUrl = baseImageUrl+fileNm;

            if(item.getUseFlg()==null){
                Glide.with(mConText).load(imageUrl).thumbnail(0.3f)
                        .into(itemBinding.image);
            }else{
                Glide.with(mConText).load(filePathNm).thumbnail(0.3f)
                        .into(itemBinding.image);
            }



            itemBinding.tvRegdate.setText(item.getUpdDt());
            if(selPos==pos){
                itemBinding.ivDot.setVisibility(View.VISIBLE);
            }else{
                itemBinding.ivDot.setVisibility(View.INVISIBLE);
            }

            itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selPos=pos;
                    itemClick.itemClick("xray",pos+"");
                }
            });


        }



    }

}
