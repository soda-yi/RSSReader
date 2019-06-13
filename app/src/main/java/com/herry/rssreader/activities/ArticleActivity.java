package com.herry.rssreader.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.herry.rssreader.R;
import com.herry.rssreader.dao.ArticleDao;
import com.herry.rssreader.dao.ChannelDao;
import com.herry.rssreader.dao.DbContext;
import com.herry.rssreader.models.Article;
import com.herry.rssreader.models.Channel;
import com.herry.rssreader.util.DateUtil;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ArticleActivity extends AppCompatActivity {
    private HtmlTextView contentTextView;
    private TextView titleTextView;
    private TextView channelNameTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private Toolbar toolbar;
    private Article article;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        final long articleId = getIntent().getLongExtra("article_id",0L);

        titleTextView = (TextView) findViewById(R.id.article_title);
        dateTextView = (TextView) findViewById(R.id.article_date);
        timeTextView = (TextView) findViewById(R.id.article_time);
        channelNameTextView = (TextView) findViewById(R.id.subscription_name);
        contentTextView = findViewById(R.id.article_content);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.menu_article_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.open_in_browser:
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(article.getLink()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                setArticle(articleId);
            }
        }).start();
    }

    private void setArticle(final Long articleId){
        final List<Article> articleList = DbContext.getArticleDao().queryBuilder()
                .where(ArticleDao.Properties.Id.eq(articleId)).list();
        if (articleList.size() != 1) {
            return;
        }
        article = articleList.get(0);

        final List<Channel> channelList = DbContext.getChannelDao().queryBuilder()
                .where(ChannelDao.Properties.Id.eq(article.getChannelId())).list();
        if (channelList.size() != 1) {
            return;
        }
        channel=channelList.get(0);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(channel.getTitle());
                titleTextView.setText(article.getTitle());
                dateTextView.setText(DateUtil.formatDate(article.getPublished()));
                timeTextView.setText(DateUtil.formatTime(article.getPublished()));
                channelNameTextView.setText(channel.getTitle());
                contentTextView.setHtml(article.getDescription(),new HtmlHttpImageGetter(contentTextView,null,true));
            }
        });
    }
}
