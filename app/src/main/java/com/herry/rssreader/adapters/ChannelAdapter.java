package com.herry.rssreader.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herry.rssreader.R;
import com.herry.rssreader.activities.ChannelActivity;
import com.herry.rssreader.activities.MainActivity;
import com.herry.rssreader.dao.ArticleDao;
import com.herry.rssreader.dao.DbContext;
import com.herry.rssreader.models.Channel;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private List<Channel> channelList;
    private Context context;

    public ChannelAdapter(List<Channel> channelList, Context context) {
        this.channelList = channelList;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView count;

        public ViewHolder(@NonNull View channelView) {
            super(channelView);
            title = channelView.findViewById(R.id.channel_title);
            count = channelView.findViewById(R.id.channel_count);
        }
    }

    @NonNull
    @Override
    public ChannelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.channel, parent, false);
        final ChannelAdapter.ViewHolder viewHolder = new ChannelAdapter.ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Intent intent = new Intent(context, ChannelActivity.class);
                intent.putExtra("channel_id", channelList.get(position).getId());
                context.startActivity(intent);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopMenu(viewHolder);
                return false;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChannelAdapter.ViewHolder viewHolder, int position) {
        final Channel channel = channelList.get(position);
        viewHolder.title.setText(channel.getTitle());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long unreadCount = DbContext.getArticleDao().queryBuilder().where(
                        ArticleDao.Properties.ChannelId.eq(channel.getId()),
                        ArticleDao.Properties.Read.eq(false)).count();
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(unreadCount==0){
                            viewHolder.count.setText("");
                        }else {
                            viewHolder.count.setText(String.valueOf(unreadCount));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    private void showPopMenu(final ViewHolder viewHolder) {
        PopupMenu popupMenu = new PopupMenu(context, viewHolder.itemView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_channel_long_click, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.remove_item:
                        removeChannel(viewHolder.getAdapterPosition());
                        break;
                }
                return false;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    public void notifyChannelAdded(){
        notifyItemInserted(channelList.size());
    }

    public void notifyChannelRefreshed(){
        notifyDataSetChanged();
    }

    private void removeChannel(int pos){
        DbContext.getChannelDao().delete(channelList.get(pos));
        channelList.remove(pos);
        notifyItemRemoved(pos);
    }
}
