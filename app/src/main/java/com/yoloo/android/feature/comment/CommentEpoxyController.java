package com.yoloo.android.feature.comment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;
import com.annimon.stream.Stream;
import com.bumptech.glide.RequestManager;
import com.yoloo.android.data.model.CommentRealm;
import com.yoloo.android.feature.feed.common.listener.OnMentionClickListener;
import com.yoloo.android.feature.feed.common.listener.OnProfileClickListener;
import com.yoloo.android.feature.feed.common.listener.OnVoteClickListener;
import com.yoloo.android.feature.models.loader.LoaderModel;
import com.yoloo.android.ui.recyclerview.OnItemLongClickListener;
import com.yoloo.android.util.Preconditions;
import com.yoloo.android.util.glide.transfromation.CropCircleTransformation;
import java.util.ArrayList;
import java.util.List;

class CommentEpoxyController extends Typed2EpoxyController<List<CommentRealm>, Boolean> {

  private final int postType;

  private final RequestManager glide;
  private final CropCircleTransformation cropCircleTransformation;

  @AutoModel LoaderModel loader;

  private List<CommentRealm> comments;
  private OnItemLongClickListener<CommentRealm> onCommentLongClickListener;
  private OnProfileClickListener onProfileClickListener;
  private OnVoteClickListener onVoteClickListener;
  private OnMentionClickListener onMentionClickListener;
  private OnMarkAsAcceptedClickListener onMarkAsAcceptedClickListener;

  CommentEpoxyController(Context context, int postType, RequestManager glide) {
    this.postType = postType;
    this.glide = glide;
    this.cropCircleTransformation = new CropCircleTransformation(context);
    this.comments = new ArrayList<>();
    setDebugLoggingEnabled(true);
  }

  public void setOnCommentLongClickListener(OnItemLongClickListener<CommentRealm> listener) {
    this.onCommentLongClickListener = listener;
  }

  public void setOnProfileClickListener(OnProfileClickListener listener) {
    this.onProfileClickListener = listener;
  }

  public void setOnVoteClickListener(OnVoteClickListener listener) {
    this.onVoteClickListener = listener;
  }

  public void setOnMentionClickListener(OnMentionClickListener listener) {
    this.onMentionClickListener = listener;
  }

  public void setOnMarkAsAcceptedClickListener(OnMarkAsAcceptedClickListener listener) {
    this.onMarkAsAcceptedClickListener = listener;
  }

  @Override
  public void setData(List<CommentRealm> items, Boolean loadingMore) {
    this.comments = items;
    super.setData(items, Preconditions.checkNotNull(loadingMore, "loadingMore cannot be null."));
  }

  void addComment(CommentRealm comment) {
    comments.add(comment);
    setData(comments, false);
  }

  void delete(CommentRealm comment) {
    comments.remove(comment);
    setData(comments, false);
  }

  void scrollToEnd(RecyclerView recyclerView) {
    recyclerView.smoothScrollToPosition(getAdapter().getItemCount() - 1);
  }

  @Override
  protected void buildModels(List<CommentRealm> comments, Boolean loadingMore) {
    Stream.of(comments).forEach(this::createCommentModel);

    loader.addIf(loadingMore, this);
  }

  private void createCommentModel(CommentRealm comment) {
    new CommentModel_()
        .id(comment.getId())
        .comment(comment)
        .glide(glide)
        .postType(postType)
        .backgroundColor(Color.TRANSPARENT)
        .circleTransformation(cropCircleTransformation)
        .onCommentLongClickListener(onCommentLongClickListener)
        .onProfileClickListener(onProfileClickListener)
        .onMentionClickListener(onMentionClickListener)
        .onMarkAsAcceptedClickListener(onMarkAsAcceptedClickListener)
        .onVoteClickListener(onVoteClickListener)
        .addTo(this);
  }
}
