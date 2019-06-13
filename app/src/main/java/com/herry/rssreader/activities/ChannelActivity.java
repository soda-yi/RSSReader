package com.herry.rssreader.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.herry.rssreader.R;
import com.herry.rssreader.adapters.ArticleAdapter;
import com.herry.rssreader.dao.ArticleDao;
import com.herry.rssreader.dao.ChannelDao;
import com.herry.rssreader.dao.DbContext;
import com.herry.rssreader.models.Article;
import com.herry.rssreader.models.Channel;

import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Channel channel;
    private List<Article> articles;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        recyclerView = findViewById(R.id.article_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Long channelId = getIntent().getLongExtra("channel_id", 0L);
                channel = getChannel(channelId);
                articles = getArticles(channelId);
                adapter = new ArticleAdapter(articles, ChannelActivity.this);
                recyclerView.setAdapter(adapter);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setTitle(channel.getTitle());
                    }
                });
            }
        }).start();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.menu_channel_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.all_read:
                        for(Article article:articles){
                            article.setRead(true);
                        }
                        DbContext.getArticleDao().updateInTx(articles);
                        adapter.notifyDataSetChanged();
                        onBackPressed();
                        break;
                }
                return false;
            }
        });
    }

    private static List<Article> getArticles(Long channelId) {
        return DbContext.getArticleDao().queryBuilder()
                .where(ArticleDao.Properties.ChannelId.eq(channelId))
                .orderDesc(ArticleDao.Properties.Published)
                .list();
    }

    private static Channel getChannel(Long channelId) {
        return DbContext.getChannelDao().queryBuilder()
                .where(ChannelDao.Properties.Id.eq(channelId))
                .list().get(0);
    }
}
