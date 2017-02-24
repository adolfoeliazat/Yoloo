package com.yoloo.android.feature.feed.component.newscomponent;

import android.graphics.drawable.GradientDrawable;
import com.airbnb.epoxy.EpoxyAdapter;
import com.annimon.stream.Stream;
import com.yoloo.android.data.model.NewsRealm;
import com.yoloo.android.ui.recyclerview.OnItemClickListener;
import java.util.List;

public class FeedNewsAdapter extends EpoxyAdapter {

  private final GradientDrawable gradientDrawable;

  public FeedNewsAdapter() {
    gradientDrawable = new GradientDrawable();
  }

  public void addNews(List<NewsRealm> items, OnItemClickListener<NewsRealm> onItemClickListener) {
    addModels(createNewsModel(items, onItemClickListener));
  }

  private List<SubNewsModel_> createNewsModel(List<NewsRealm> items,
      OnItemClickListener<NewsRealm> onItemClickListener) {
    return Stream.of(items)
        .map(news -> new SubNewsModel_()
            .news(news)
            .gradientDrawable(gradientDrawable)
            .onItemClickListener(onItemClickListener))
        .toList();
  }
}