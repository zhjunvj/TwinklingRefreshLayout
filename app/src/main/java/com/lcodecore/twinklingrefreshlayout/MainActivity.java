package com.lcodecore.twinklingrefreshlayout;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    public int setInflateId() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        setupRecyclerView(rv);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        List<Card> cards = new ArrayList<>();

        private void addCard() {
            for (int i = 0; i < 8; i++) {
                Card card = new Card();
                switch (i) {
                    case 0:
                        card.setTitle("God of Light", "点亮世界之光");
                        card.imageSrc = R.drawable.card_cover1;
                        break;
                    case 1:
                        card.setTitle("我的手机与众不同", "专题");
                        card.imageSrc = R.drawable.card_cover2;
                        break;
                    case 2:
                        card.setTitle("BlackLight", "做最纯粹的微博客户端");
                        card.imageSrc = R.drawable.card_cover3;
                        break;
                    case 3:
                        card.setTitle("BuzzFeed", "最好玩的新闻在这里");
                        card.imageSrc = R.drawable.card_cover4;
                        break;
                    case 4:
                        card.setTitle("Nester", "专治各种熊孩子");
                        card.imageSrc = R.drawable.card_cover5;
                        break;
                    case 5:
                        card.setTitle("二次元专题", "啊喂，别总想去四维空间啦");
                        card.imageSrc = R.drawable.card_cover6;
                        break;
                    case 6:
                        card.setTitle("Music Player", "闻其名，余音绕梁");
                        card.imageSrc = R.drawable.card_cover7;
                        break;
                    case 7:
                        card.setTitle("el", "剪纸人の唯美旅程");
                        card.imageSrc = R.drawable.card_cover8;
                        break;
                }
                cards.add(card);
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            public final ImageView mImageView;
            public final TextView tv_title;
            public final TextView tv_subTitle;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.iv_cover);
                tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_subTitle = (TextView) view.findViewById(R.id.tv_subtitle);
            }
        }

        public SimpleStringRecyclerViewAdapter(Context context) {
            super();
            addCard();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.tv_title.setText(cards.get(position).title);
            holder.tv_subTitle.setText(cards.get(position).info);
            holder.mImageView.setImageResource(cards.get(position).imageSrc);
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }
    }
}
