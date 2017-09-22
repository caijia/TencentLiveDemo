package com.caijiatest.tencentlivedemo.playController.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caijiatest.tencentlivedemo.R;
import com.caijiatest.tencentlivedemo.playController.entities.VideoQuality;

import java.util.List;

/**
 * 清晰度PopupWindow
 * Created by cai.jia on 2017/9/18 0018.
 */

public class PopVideoQuality extends PopupWindow {

    private Context context;
    private List<VideoQuality> qualityList;

    public PopVideoQuality(Context context, List<VideoQuality> qualityList) {
        super(context);
        this.context = context;
        this.qualityList = qualityList;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.popup_video_quality, new LinearLayout(context), false);
        setContentView(view);
        setOutsideTouchable(false);
        setFocusable(true);
        setAnimationStyle(0);
        setBackgroundDrawable(new ColorDrawable());
        setWidth((int) (getScreenWidth(context) *0.4f));
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        VideoQualityAdapter videoQualityAdapter = new VideoQualityAdapter(qualityList);
        videoQualityAdapter.setOnVideoQualityItemClickListener(new OnVideoQualityItemClickListener() {
            @Override
            public void onVideoQualityItemClick(VideoQuality quality) {
                if (listener != null) {
                    listener.onVideoQualityItemClick(quality);
                }
                dismiss();
            }
        });
        recyclerView.setAdapter(videoQualityAdapter);

        super.showAtLocation(parent, gravity, x, y);
    }

    private static class VideoQualityAdapter extends RecyclerView.Adapter<VideoQualityVH> {

        private List<VideoQuality> qualityList;

        private OnVideoQualityItemClickListener listener;

        public void setOnVideoQualityItemClickListener(OnVideoQualityItemClickListener listener) {
            this.listener = listener;
        }

        public VideoQualityAdapter(List<VideoQuality> qualityList) {
            this.qualityList = qualityList;
        }

        @Override
        public VideoQualityVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_quality, parent, false);
            return new VideoQualityVH(view,listener);
        }

        @Override
        public void onBindViewHolder(VideoQualityVH holder, int position) {
            VideoQuality videoQuality = qualityList.get(position);
            holder.tvVideoQuality.setText(videoQuality.getDesc());

            holder.setItem(videoQuality);
            holder.itemView.setOnClickListener(holder);
        }

        @Override
        public int getItemCount() {
            return qualityList != null ? qualityList.size() : 0;
        }
    }

    private static class VideoQualityVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvVideoQuality;
        private VideoQuality item;
        OnVideoQualityItemClickListener listener;

        public VideoQualityVH(View itemView, OnVideoQualityItemClickListener listener) {
            super(itemView);
            tvVideoQuality = (TextView) itemView.findViewById(R.id.tv_video_quality);
            this.listener = listener;
        }

        public void setItem(VideoQuality item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            if (listener != null && item != null) {
                listener.onVideoQualityItemClick(item);
            }
        }
    }

    public interface OnVideoQualityItemClickListener{

        void onVideoQualityItemClick(VideoQuality quality);
    }

    private OnVideoQualityItemClickListener listener;

    public void setOnVideoQualityItemClickListener(OnVideoQualityItemClickListener listener) {
        this.listener = listener;
    }

    private int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
