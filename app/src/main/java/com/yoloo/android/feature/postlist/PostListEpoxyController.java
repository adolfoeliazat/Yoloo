package com.yoloo.android.feature.postlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintSet;
import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.yoloo.android.R;
import com.yoloo.android.data.db.PostRealm;
import com.yoloo.android.data.feed.BlogFeedItem;
import com.yoloo.android.data.feed.FeedItem;
import com.yoloo.android.data.feed.RichPostFeedItem;
import com.yoloo.android.data.feed.TextPostFeedItem;
import com.yoloo.android.feature.feed.common.listener.OnBookmarkClickListener;
import com.yoloo.android.feature.feed.common.listener.OnCommentClickListener;
import com.yoloo.android.feature.feed.common.listener.OnContentImageClickListener;
import com.yoloo.android.feature.feed.common.listener.OnPostOptionsClickListener;
import com.yoloo.android.feature.feed.common.listener.OnProfileClickListener;
import com.yoloo.android.feature.feed.common.listener.OnShareClickListener;
import com.yoloo.android.feature.feed.common.listener.OnVoteClickListener;
import com.yoloo.android.feature.feed.component.post.BlogModel_;
import com.yoloo.android.feature.feed.component.post.RichQuestionModel_;
import com.yoloo.android.feature.feed.component.post.TextQuestionModel_;
import com.yoloo.android.feature.models.loader.LoaderModel;
import com.yoloo.android.ui.recyclerview.OnItemClickListener;
import com.yoloo.android.util.glide.transfromation.CropCircleTransformation;
import java.util.ArrayList;
import java.util.List;

import static com.yoloo.android.util.Preconditions.checkNotNull;

public class PostListEpoxyController
    extends Typed2EpoxyController<List<? super FeedItem<?>>, Boolean> {

  private final RequestManager glide;
  private final Transformation<Bitmap> bitmapTransformation;
  private final ConstraintSet constraintSet;

  @AutoModel LoaderModel loader;

  private List<? super FeedItem<?>> items;
  private String userId;

  private OnProfileClickListener onProfileClickListener;
  private OnPostOptionsClickListener onPostOptionsClickListener;
  private OnItemClickListener<PostRealm> onPostClickListener;
  private OnShareClickListener onShareClickListener;
  private OnCommentClickListener onCommentClickListener;
  private OnVoteClickListener onVoteClickListener;
  private OnContentImageClickListener onContentImageClickListener;
  private OnBookmarkClickListener onBookmarkClickListener;

  public PostListEpoxyController(Context context) {
    this.bitmapTransformation = new CropCircleTransformation(context);
    this.glide = Glide.with(context);
    this.constraintSet = new ConstraintSet();
    this.items = new ArrayList<>(20);
    setDebugLoggingEnabled(true);
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setOnProfileClickListener(OnProfileClickListener onProfileClickListener) {
    this.onProfileClickListener = onProfileClickListener;
  }

  public void setOnPostOptionsClickListener(OnPostOptionsClickListener onPostOptionsClickListener) {
    this.onPostOptionsClickListener = onPostOptionsClickListener;
  }

  public void setOnPostClickListener(OnItemClickListener<PostRealm> onPostClickListener) {
    this.onPostClickListener = onPostClickListener;
  }

  public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
    this.onShareClickListener = onShareClickListener;
  }

  public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
    this.onCommentClickListener = onCommentClickListener;
  }

  public void setOnVoteClickListener(OnVoteClickListener onVoteClickListener) {
    this.onVoteClickListener = onVoteClickListener;
  }

  public void setOnContentImageClickListener(
      OnContentImageClickListener onContentImageClickListener) {
    this.onContentImageClickListener = onContentImageClickListener;
  }

  public void setOnBookmarkClickListener(OnBookmarkClickListener onBookmarkClickListener) {
    this.onBookmarkClickListener = onBookmarkClickListener;
  }

  @Override
  public void setData(List<? super FeedItem<?>> items, Boolean loadingMore) {
    this.items = items;
    super.setData(items, checkNotNull(loadingMore, "loadingMore cannot be null."));
  }

  public void updatePost(PostRealm post) {
    if (post.isTextPost()) {
      FeedItem<?> item = new TextPostFeedItem(post);
      final int index = items.indexOf(item);
      items.add(index, item);
    } else if (post.isRichPost()) {
      FeedItem<?> item = new RichPostFeedItem(post);
      final int index = items.indexOf(item);
      items.add(index, item);
    } else if (post.isBlogPost()) {
      FeedItem<?> item = new BlogFeedItem(post);
      final int index = items.indexOf(item);
      items.add(index, item);
    }

    setData(items, false);
  }

  public void deletePost(PostRealm post) {
    if (post.isTextPost()) {
      items.remove(new TextPostFeedItem(post));
    } else if (post.isRichPost()) {
      items.remove(new RichPostFeedItem(post));
    } else if (post.isBlogPost()) {
      items.remove(new BlogFeedItem(post));
    }

    setData(items, false);
  }

  public void showLoader() {
    setData(items, true);
  }

  public void hideLoader() {
    setData(items, false);
  }

  private void createRichPost(PostRealm post) {
    new RichQuestionModel_()
        .id(post.getId())
        .onProfileClickListener(onProfileClickListener)
        .onPostOptionsClickListener(onPostOptionsClickListener)
        .onItemClickListener(onPostClickListener)
        .onShareClickListener(onShareClickListener)
        .onCommentClickListener(onCommentClickListener)
        .onVoteClickListener(onVoteClickListener)
        .onContentImageClickListener(onContentImageClickListener)
        .onBookmarkClickListener(onBookmarkClickListener)
        .layout(R.layout.item_feed_question_rich)
        .bitmapTransformation(bitmapTransformation)
        .glide(glide)
        .set(constraintSet)
        .post(post)
        .userId(userId)
        .addTo(this);
  }

  private void createTextPost(PostRealm post) {
    new TextQuestionModel_()
        .id(post.getId())
        .post(post)
        .onProfileClickListener(onProfileClickListener)
        .onPostOptionsClickListener(onPostOptionsClickListener)
        .onItemClickListener(onPostClickListener)
        .onShareClickListener(onShareClickListener)
        .onCommentClickListener(onCommentClickListener)
        .onVoteClickListener(onVoteClickListener)
        .onBookmarkClickListener(onBookmarkClickListener)
        .layout(R.layout.item_feed_question_text)
        .bitmapTransformation(bitmapTransformation)
        .glide(glide)
        .userId(userId)
        .addTo(this);
  }

  private void createBlog(PostRealm post) {
    new BlogModel_()
        .id(post.getId())
        .post(post)
        .userId(userId)
        .layout(R.layout.item_feed_blog)
        .onProfileClickListener(onProfileClickListener)
        .onPostOptionsClickListener(onPostOptionsClickListener)
        .onItemClickListener(onPostClickListener)
        .onShareClickListener(onShareClickListener)
        .onCommentClickListener(onCommentClickListener)
        .onVoteClickListener(onVoteClickListener)
        .onBookmarkClickListener(onBookmarkClickListener)
        .bitmapTransformation(bitmapTransformation)
        .glide(glide)
        .addTo(this);
  }

  @Override
  protected void buildModels(List<? super FeedItem<?>> feedItems, Boolean loadingMore) {
    Stream.of(feedItems).forEach(item -> {
      if (item instanceof TextPostFeedItem) {
        createTextPost(((TextPostFeedItem) item).getItem());
      } else if (item instanceof RichPostFeedItem) {
        createRichPost(((RichPostFeedItem) item).getItem());
      } else if (item instanceof BlogFeedItem) {
        createBlog(((BlogFeedItem) item).getItem());
      }
    });

    loader.addIf(loadingMore, this);
  }
}
