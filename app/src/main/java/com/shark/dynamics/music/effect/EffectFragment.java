package com.shark.dynamics.music.effect;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.shark.dynamics.basic.thread.UI;
import com.shark.dynamics.basic.thread.Worker;
import com.shark.dynamics.basic.tips.Tips;
import com.shark.dynamics.music.R;
import com.shark.dynamics.music.test.TestSpectrumActivity;
import com.shark.dynamics.music.ui.fragment.BaseFragment;
import com.shark.dynamics.music.ui.rv.RView;
import com.shark.dynamics.music.util.WallpaperUtil;
import com.shark.dynamics.music.wallpaper.DynamicWallpaper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class EffectFragment extends BaseFragment {

    private RefreshLayout mRefreshLayout;
    private RecyclerView mEffectList;
    private EffectAdapter mEffectAdapter;
    private List<EffectItem> mEffectItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEffectItems = new ArrayList<>();
        mEffectAdapter = new EffectAdapter(getActivity(), mEffectItems);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effect, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(true);

        mRefreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        BallPulseFooter footer = new BallPulseFooter(getActivity());
        footer.setNormalColor(this.getResources().getColor(R.color.main_color));
        footer.setAnimatingColor(this.getResources().getColor(R.color.main_color));
        mRefreshLayout.setRefreshFooter(footer);
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mRefreshLayout.finishLoadMore(2000);
        });

        mEffectList = view.findViewById(R.id.id_effect_list);
        mEffectList.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        mEffectList.addItemDecoration(new GridItemDecoration(view.getContext(), 2));
        mEffectList.setAdapter(mEffectAdapter);

        mEffectAdapter.setOnItemClickListener((rootView, position) -> {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            Intent intent = new Intent(activity, PreviewActivity.class);
            intent.putExtra("item", mEffectItems.get(position));
            activity.startActivity(intent);
        });

        loadItems();
    }

    private void loadItems() {
        Worker.getInstance().postLightTask(() -> {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            EffectLoader.loadEffectsFromAssets(activity, config -> {
                if (config == null || config.effects == null) {
                    Tips.tips(activity, "Load failed.");
                    return;
                }
                mEffectItems.removeAll(config.effects);
                mEffectItems.addAll(config.effects);

                Iterator<EffectItem> it = mEffectItems.iterator();
                while (it.hasNext()) {
                    if (!it.next().visibility) {
                        it.remove();
                    }
                }

                UI.getInstance().post(() -> {
                    mEffectAdapter.notifyDataSetChanged();
                });
            });
        });
    }
}
