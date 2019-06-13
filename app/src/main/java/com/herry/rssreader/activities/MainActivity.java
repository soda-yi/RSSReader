package com.herry.rssreader.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.camera2.DngCreator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.herry.rssreader.R;
import com.herry.rssreader.adapters.ChannelAdapter;
import com.herry.rssreader.dao.ArticleDao;
import com.herry.rssreader.dao.DbContext;
import com.herry.rssreader.models.Article;
import com.herry.rssreader.models.Channel;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class MainActivity extends AppCompatActivity {

    private List<Channel> channels;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChannelAdapter channelAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        channels = getChannels();

        recyclerView = findViewById(R.id.main_recycler_view);
        addButton = findViewById(R.id.add_button);
        swipeRefreshLayout = findViewById(R.id.main_swipe_refresh_layout);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("添加Rss订阅源");
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.channel_info, null);
                builder.setView(view);
                final EditText linkEditText = (EditText) view.findViewById(R.id.channel_link);

                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String link = linkEditText.getText().toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addChannel(link);
                                    }
                                }).start();
                            }
                        });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        channelAdapter = new ChannelAdapter(channels, MainActivity.this);
        recyclerView.setAdapter(channelAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshChannels();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                channelAdapter.notifyChannelRefreshed();
                            }
                        });
                    }
                }).start();
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshChannels();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        channelAdapter.notifyChannelRefreshed();
                    }
                });
            }
        }).start();

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main_toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_quit:
                        System.exit(0);
                }
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        channelAdapter.notifyChannelRefreshed();
    }

    private List<Channel> getChannels() {
        return DbContext.getChannelDao().queryBuilder().list();
    }

    private void addChannel(String link) {
        String urlString;
        if (link.startsWith("http://")) {
            urlString = link;
        } else if (link.startsWith("https://")) {
            urlString = link;//.replace("https", "http");
        } else {
            urlString = "https://" + link;
        }

        try {
            URL url = new URL(urlString);
            XmlReader reader = new XmlReader(url);
            SyndFeedInput input = new SyndFeedInput();

            SyndFeed feed = input.build(reader);
            String channelTitle = feed.getTitle();
            String siteUrl = feed.getLink();
            String channelDesc = feed.getDescription();
            Date channelDate = feed.getPublishedDate();
            Long channelTime = channelDate == null ? null : channelDate.getTime();
            Channel channel = new Channel(null, "feed/" + urlString, channelTitle, urlString, channelTime, siteUrl, channelDesc);
            channels.add(channel);
            DbContext.getChannelDao().insert(channel);

            List<SyndEntry> entries = feed.getEntries();
            List<Article> newArticles = new ArrayList<>();
            for (int j = 0; j < entries.size(); j++) {
                SyndEntry entry = (SyndEntry) entries.get(j);
                String articleTitle = entry.getTitle();
                String articleLink = entry.getLink();
                SyndContent desc = entry.getDescription();
                Date articleDate = entry.getPublishedDate();
                String articleDesc = desc.getValue();
                Long articlePublished = articleDate == null ? null : articleDate.getTime();
                newArticles.add(new Article(null, articleTitle, articleLink, articleDesc, false, false, null, channel.getId(), articlePublished));
            }
            DbContext.getArticleDao().insertInTx(newArticles);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    channelAdapter.notifyChannelAdded();
                }
            });
        } catch (FeedException | IllegalArgumentException | UnknownHostException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "订阅源输入有误", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void refreshChannels() {
        for (Channel channel : channels) {
            refreshChannel(channel);
        }
    }

    private void refreshChannel(Channel channel) {
        try {
            URL url = new URL(channel.getUrl());
            XmlReader reader = new XmlReader(url);
            SyndFeedInput input = new SyndFeedInput();

            SyndFeed feed = input.build(reader);
            List<SyndEntry> entries = feed.getEntries();
            List<Article> responseArticles = new ArrayList<>();
            for (int j = 0; j < entries.size(); j++) {
                SyndEntry entry = (SyndEntry) entries.get(j);
                String articleTitle = entry.getTitle();
                String articleLink = entry.getLink();
                SyndContent desc = entry.getDescription();
                Date articleDate = entry.getPublishedDate();
                String articleDesc = desc.getValue();
                Long articlePublished = articleDate == null ? null : articleDate.getTime();
                responseArticles.add(new Article(null, articleTitle, articleLink, articleDesc, false, false, null, channel.getId(), articlePublished));
            }
            List<Article> originArticles = DbContext.getArticleDao().queryBuilder().where(
                    ArticleDao.Properties.ChannelId.eq(channel.getId())).list();
            List<Article> newArticles = new ArrayList<>();
            for (Article article : responseArticles) {
                if (!originArticles.contains(article)) {
                    newArticles.add(article);
                }
            }
            if (newArticles.size() != 0) {
                DbContext.getArticleDao().insertInTx(newArticles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
