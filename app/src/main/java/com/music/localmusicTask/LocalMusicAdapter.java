package com.music.localmusicTask;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.AppContant.AppContant;
import com.music.R;
import com.music.javabean.MusicData;
import com.music.manager.CoverLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LocalMusicFragment localMusicFragment;
    private List<MusicData> musicData;

    public LocalMusicAdapter(LocalMusicFragment localMusicFragment) {
        this.localMusicFragment = localMusicFragment;
        this.musicData = AppContant.PlayContant.musicData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(localMusicFragment.getContext()).inflate(R.layout.view_holder_music, null);
        return new LocalMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        LocalMusicViewHolder holder = (LocalMusicViewHolder) viewHolder;
        MusicData data = this.musicData.get(position);
        holder.tvTitle.setText(data.getMusicName());
        holder.tvArtist.setText(data.getArtist());
        Bitmap bitmap = CoverLoader.getInstance().loadThumb(data);
        holder.ivCover.setImageBitmap(bitmap);
        holder.view.setOnClickListener((view) -> {
            if(listener!=null){
                listener.onItemClick(view,position,data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.musicData.size();
    }

    class LocalMusicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.v_playing)
        View vPlaying;
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_artist)
        TextView tvArtist;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.v_divider)
        View vDivider;
        View view;

        LocalMusicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, MusicData data);
    }
}
