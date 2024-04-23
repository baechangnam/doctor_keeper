package com.apps.doctorkeeper_android.ui.customer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.constants.CommonValue;
import com.apps.doctorkeeper_android.data.customer.model.CustomerImageModel;
import com.apps.doctorkeeper_android.databinding.ItemImageBinding;
import com.apps.doctorkeeper_android.databinding.ItemImageShareBinding;
import com.apps.doctorkeeper_android.db.RbPreference;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.util.CommonUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

public class CustomerImageShareAdapter extends RecyclerView.Adapter<CustomerImageShareAdapter.ViewHolder> {

    private ArrayList<CustomerImageModel> items;
    CustomerImageModel fInfo;
    int count;
    Context mConText;
    int clickPos=-1;
    ItemClick itemClick;
    String baseImageUrl="";

    public int getSelPos() {
        return selPos;
    }

    public void setSelPos(int selPos) {
        this.selPos = selPos;
        notifyDataSetChanged();
    }

    int selPos=0;

    public CustomerImageShareAdapter(Context context, ArrayList<CustomerImageModel> items, ItemClick itemClick) {
        this.items = items;
        this.count = items.size();
        this.mConText = context;
        this.itemClick = itemClick;

        RbPreference preference = new RbPreference(mConText);
        baseImageUrl = preference.getValue(RbPreference.IMG_URL, CommonValue.IMAGE_URL);
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
        ItemImageShareBinding binding = ItemImageShareBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new ViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull CustomerImageShareAdapter.ViewHolder holder, int position) {
        holder.bindItem(items.get(position) ,position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemImageShareBinding itemBinding;
        public ViewHolder(@NonNull ItemImageShareBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
        }

        void bindItem(CustomerImageModel item,int pos){
            String fileNm =item.getThmbFileNm();
            String filePathNm = item.getFilePathNm();

            String imageUrl = baseImageUrl+filePathNm+fileNm;

            Glide.with(mConText).load(imageUrl).thumbnail(0.3f)
                    .into(itemBinding.image);

            if(selPos==pos){
                itemBinding.back.setBackgroundResource(R.drawable.border_share_sel);
            }else{
                itemBinding.back.setBackgroundResource(R.drawable.border_share_none);
            }

            itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selPos=pos;

                    itemClick.itemClick("image",pos+"");
                }
            });


        }



    }



}
