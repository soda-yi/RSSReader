package com.herry.rssreader.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.herry.rssreader.R;
import com.herry.rssreader.activities.ArticleActivity;
import com.herry.rssreader.activities.ChannelActivity;
import com.herry.rssreader.dao.DbContext;
import com.herry.rssreader.models.Article;
import com.herry.rssreader.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articleList;
    private Context context;
    private Pattern imgPattern, srcPattern;

    public ArticleAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
        imgPattern = Pattern.compile("<img.*src\\s*=\\s*(.*?)[^>]*?>");
        srcPattern = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private TextView desc;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.article_title);
            image = itemView.findViewById(R.id.article_image);
            desc = itemView.findViewById(R.id.article_desc);
            time = itemView.findViewById(R.id.article_time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.article, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Intent intent = new Intent(context, ArticleActivity.class);
                Article article = articleList.get(position);
                intent.putExtra("article_id", article.getId());
                if (!article.getRead()) {
                    article.setRead(true);
                    DbContext.getArticleDao().update(article);
                }
                notifyItemChanged(position);
                context.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Article article = articleList.get(position);
        if (article.getRead()) {
            viewHolder.title.setAlpha(0.54f);
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.main_grey_normal));
            viewHolder.image.setAlpha(0.24f);
        } else {
            viewHolder.title.setAlpha(0.87f);
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.main_grey_dark));
            viewHolder.image.setAlpha(1f);
        }
        viewHolder.title.setText(article.getTitle());
        //viewHolder.desc.setText(article.getDescription());
        viewHolder.time.setText(formatTime(article.getPublished()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Matcher m = imgPattern.matcher(article.getDescription());
                    if (m.find()) {
                        String imgUrl = m.group();
                        m = srcPattern.matcher(imgUrl);
                        if (m.find()) {
                            final String url = m.group(1);
                            ((ChannelActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(context)
                                            .load(url)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    viewHolder.image.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    viewHolder.image.setVisibility(View.VISIBLE);
                                                    return false;
                                                }
                                            })
                                            .thumbnail(0.2f)
                                            .into(viewHolder.image);
                                }
                            });
                        } else {
                            viewHolder.image.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.image.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    private String formatTime(Long time) {
        String result = "";
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date beforeYesterday = calendar.getTime();
        if (DateUtil.isSameDay(date, today)) {
            result += DateFormat.format("HH:mm", date);
        } else if (DateUtil.isSameDay(date, yesterday)) {
            result += "昨天";
            result += DateFormat.format(" HH:mm", date);
        } else if (DateUtil.isSameDay(date, beforeYesterday)) {
            result += "前天";
            result += DateFormat.format(" HH:mm", date);
        } else if (DateUtil.isSameYear(date, today)) {
            result += DateFormat.format("MM月dd日", date);
        } else {
            result += DateFormat.format("yyyy年MM月dd日", date);
        }

        return result;
    }
}
