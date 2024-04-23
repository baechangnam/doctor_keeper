package com.apps.doctorkeeper_android.ui.consult.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.consult.DocumentModel;
import com.apps.doctorkeeper_android.databinding.ItemDocBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ConsultDocAdapter extends RecyclerView.Adapter<ConsultDocAdapter.ViewHolder> {

    private ArrayList<DocumentModel> items;
    int count;
    Context mConText;
    ItemClick itemClick;
    RbPreference pref;


    public ConsultDocAdapter(Context context, ArrayList<DocumentModel> items, ItemClick itemClick) {
        this.items = items;
        this.count = items.size();
        this.itemClick = itemClick;
        this.mConText = context;
        pref= new RbPreference(mConText);
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
        ItemDocBinding binding = ItemDocBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new ViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemDocBinding itemBinding;
        public ViewHolder(@NonNull ItemDocBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
        }

        void bindItem(DocumentModel item , int position){

            Glide.with(mConText).load(pref.getValue(RbPreference.API_URL, CommonValue.DEFAULT_URL)+"resource"+item.getImgPathNm()).error(R.drawable.icon)
                    .into(itemBinding.image);
            itemBinding.title.setText(item.getTitlNm());

            itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filePath =item.getFilePathNm().substring(1)+item.getFileNm();
                    itemClick.itemClick("docClick",filePath);

                }
            });
        }



    }

}
