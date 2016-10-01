package com.lcodecore.twinklingrefreshlayout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lcodecore.library.v2.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcodecore on 2016/10/1.
 */

public class ListViewFragment extends Fragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListView((ListView) rootView.findViewById(R.id.listView));
    }

    private void setupListView(ListView listView) {
        TwinklingRefreshLayout refreshLayout = (TwinklingRefreshLayout) rootView.findViewById(R.id.refresh);
        TextHeaderView headerView = (TextHeaderView) View.inflate(getContext(),R.layout.header_tv,null);
        refreshLayout.setHeaderView(headerView);
        //TODO loadmore隐藏可能有bug
        //refreshLayout.setEnableLoadmore(false);

        listView.setAdapter(new SimpleAdapter());
    }

    public class SimpleAdapter extends BaseAdapter {

        public SimpleAdapter() {
            addCard();
        }

        @Override
        public int getCount() {
            return cards.size();
        }

        @Override
        public Card getItem(int position) {
            return cards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_title.setText(cards.get(position).title);
            holder.tv_subTitle.setText(cards.get(position).info);
            holder.mImageView.setImageResource(cards.get(position).imageSrc);

            return convertView;
        }

        List<Card> cards = new ArrayList<>();

        private void addCard() {
            for (int i = 0; i < 13; i++) {
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
                    case 8:
                        card.setTitle("God of Light", "点亮世界之光");
                        card.imageSrc = R.drawable.card_cover1;
                        break;
                    case 9:
                        card.setTitle("我的手机与众不同", "专题");
                        card.imageSrc = R.drawable.card_cover2;
                        break;
                    case 10:
                        card.setTitle("BlackLight", "做最纯粹的微博客户端");
                        card.imageSrc = R.drawable.card_cover3;
                        break;
                    case 11:
                        card.setTitle("BuzzFeed", "最好玩的新闻在这里");
                        card.imageSrc = R.drawable.card_cover4;
                        break;
                    case 12:
                        card.setTitle("Nester", "专治各种熊孩子");
                        card.imageSrc = R.drawable.card_cover5;
                        break;
                }
                cards.add(card);
            }
        }

        class ViewHolder {
            final ImageView mImageView;
            final TextView tv_title;
            final TextView tv_subTitle;

            ViewHolder(View view) {
                mImageView = (ImageView) view.findViewById(R.id.iv_cover);
                tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_subTitle = (TextView) view.findViewById(R.id.tv_subtitle);
            }
        }
    }
}
