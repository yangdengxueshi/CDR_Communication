package com.dexin.cdr_communication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dexin.cdr_communication.R;
import com.dexin.cdr_communication.entity.OperateModuleBean;

import java.util.List;

/**
 * 操作模块Adapter
 */
public class OperateModuleAdapter extends RecyclerView.Adapter<OperateModuleAdapter.ViewHolder> {
    private Context mContext;
    private final List<OperateModuleBean> mOperateModuleBeanList;
    private OnItemClickListener mOnItemClickListener;           //TODO FIXME 定义 Item点击监听器

    public OperateModuleAdapter(List<OperateModuleBean> operateModuleBeanList) {
        mOperateModuleBeanList = operateModuleBeanList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OperateModuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_operate_module, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OperateModuleAdapter.ViewHolder holder, int position) {
        if ((mOperateModuleBeanList != null) && (mOperateModuleBeanList.size() > position)) {
            OperateModuleBean lOperateModuleBean = mOperateModuleBeanList.get(position);
            if (lOperateModuleBean != null) {
                Glide.with(mContext).load(lOperateModuleBean.getImgId()).diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true).into(holder.mIvOperateImg);
                holder.mTvOperateName.setText(lOperateModuleBean.getName());
                //FIXME 点击事件
                if ((mOnItemClickListener != null) && ((0 <= holder.getAdapterPosition()) && (holder.getAdapterPosition() < mOperateModuleBeanList.size()))) {
                    holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition()));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mOperateModuleBeanList != null) ? mOperateModuleBeanList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final ImageView mIvOperateImg;
        private final TextView mTvOperateName;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mIvOperateImg = itemView.findViewById(R.id.civ_operate_img);
            mTvOperateName = itemView.findViewById(R.id.tv_operate_name);
        }
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
