package com.shark.dynamics.music.ui.rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class RViewAdapter<D> extends RecyclerView.Adapter<RViewHolder> {

    protected Context mContext;
    protected List<D> mData;
    private RView.OnItemClickListener mItemListener;
    private RView.OnItemLongClickListener mItemLongClickListener;

    public RViewAdapter(Context context, List<D> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId = getLayoutIdForType(viewType) == -1 ? getLayoutId() : getLayoutIdForType(viewType);
        RViewHolder holder =
                new RViewHolder(LayoutInflater.from(mContext).inflate(layoutId, parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RViewHolder holder, final int position) {

        final RView.OnItemClickListener listener = getItemClickListener();
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(v, position);
                }
                if (mItemListener != null) {
                    mItemListener.onItemClick(v, position);
                }
            }
        });
        holder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(v, position);
                }
                return false;
            }
        });
        bindDataToView(holder, position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public RView.OnItemClickListener getItemClickListener() {
        return null;
    }

    public int getLayoutIdForType(int type) {
        return -1;
    }

    public abstract int getLayoutId();

    public abstract void bindDataToView(RViewHolder holder, int position);

    public void setOnItemClickListener(RView.OnItemClickListener listener) {
        mItemListener = listener;
    }

    public void setOnItemLongClickListener(RView.OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public List<D> getDatas() {
        return mData;
    }

    public D getItemData(int pos) {
        return mData.get(pos);
    }

    public void setDatas(List<D> datas) {
        mData = datas;
    }

    public void addDatasWithoutMulti(List<D> datas) {
        mData.removeAll(datas);
        mData.addAll(datas);
    }

    public Runnable mNotify = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    public void postDelayNotifyDataSetChanged(RecyclerView view) {
        if (view.isComputingLayout()) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, 50);
        } else {
            notifyDataSetChanged();
        }
    }
}
