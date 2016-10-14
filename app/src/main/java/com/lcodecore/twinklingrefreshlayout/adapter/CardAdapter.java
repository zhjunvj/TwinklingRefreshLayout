package com.lcodecore.twinklingrefreshlayout.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcodecore.twinklingrefreshlayout.beans.Card;
import com.lcodecore.twinklingrefreshlayout.R;
import com.lcodecore.twinklingrefreshlayout.adapter.base.BaseRecyclerAdapter;
import com.lcodecore.twinklingrefreshlayout.adapter.base.CommonHolder;

import butterknife.Bind;

/**
 * Created by lcodecore on 2016/10/14.
 */

public class CardAdapter extends BaseRecyclerAdapter<Card> {
    @Override
    public CommonHolder<Card> setViewHolder(ViewGroup parent) {
        return new CardHolder(parent.getContext(), parent);
    }

    class CardHolder extends CommonHolder<Card> {

        @Bind(R.id.tv_title)
        TextView tv_title;

        @Bind(R.id.tv_subtitle)
        TextView tv_subtitle;

        @Bind(R.id.iv_cover)
        ImageView iv_cover;

        public CardHolder(Context context, ViewGroup root) {
            super(context, root, R.layout.list_item);
        }

        @Override
        public void bindData(Card card) {
            tv_title.setText(card.title);
            tv_subtitle.setText(card.info);
            iv_cover.setImageResource(card.imageSrc);
        }
    }
}
