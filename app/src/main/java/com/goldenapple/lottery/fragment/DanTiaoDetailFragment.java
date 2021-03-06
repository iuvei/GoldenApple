package com.goldenapple.lottery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenapple.lottery.R;
import com.goldenapple.lottery.app.BaseFragment;


public class DanTiaoDetailFragment extends BaseFragment {

    /**
     时时彩、P3P5
     11选5
     快三
     3D */
    private int type=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        switch (type){
            case 0:
                return inflater.inflate(R.layout.dan_tiao_ssc, container, false);
            case 1:
                return inflater.inflate(R.layout.dan_tiao_11select5, container, false);
            case 2:
                return inflater.inflate(R.layout.dan_tiao_kl10fen, container, false);
            case 3:
                return inflater.inflate(R.layout.dan_tiao_kl12, container, false);
            case 4:
                return inflater.inflate(R.layout.dan_tiao_other, container, false);
            default:
                return inflater.inflate(R.layout.dan_tiao_ssc, container, false);
        }

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
