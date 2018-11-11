package com.music.localmusicTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.AppContant.MusicContant;
import com.music.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 */
public class LocalMusicFragment extends Fragment {


    @BindView(R.id.localmusic)
    RecyclerView localmusic;
    Unbinder unbinder;
    private LocalMusicAdapter adapter;

    public LocalMusicFragment() {
        // Required empty public constructor
    }

    /**

     */
    // TODO: Rename and change types and number of parameters
    public static LocalMusicFragment newInstance() {
        LocalMusicFragment fragment = new LocalMusicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        unbinder = ButterKnife.bind(this, view);
        localmusic.setLayoutManager(new LinearLayoutManager(getContext()));
     //   localmusic.addItemDecoration(new DividerItemDecoration(getContext(),1));
        adapter = new LocalMusicAdapter(this, MusicContant.MusicPlayData.musicData);
        localmusic.setAdapter(adapter);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
