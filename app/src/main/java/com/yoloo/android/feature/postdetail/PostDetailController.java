package com.yoloo.android.feature.postdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import com.airbnb.epoxy.EpoxyModel;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.yoloo.android.R;
import com.yoloo.android.data.Response;
import com.yoloo.android.data.model.AccountRealm;
import com.yoloo.android.data.model.CommentRealm;
import com.yoloo.android.data.model.PostRealm;
import com.yoloo.android.data.repository.comment.CommentRepository;
import com.yoloo.android.data.repository.comment.datasource.CommentDiskDataStore;
import com.yoloo.android.data.repository.comment.datasource.CommentRemoteDataStore;
import com.yoloo.android.data.repository.post.PostRepository;
import com.yoloo.android.data.repository.post.datasource.PostDiskDataStore;
import com.yoloo.android.data.repository.post.datasource.PostRemoteDataStore;
import com.yoloo.android.data.repository.user.UserRepository;
import com.yoloo.android.data.repository.user.datasource.UserDiskDataStore;
import com.yoloo.android.data.repository.user.datasource.UserRemoteDataStore;
import com.yoloo.android.feature.comment.OnMarkAsAcceptedClickListener;
import com.yoloo.android.feature.feed.common.adapter.FeedAdapter;
import com.yoloo.android.feature.feed.common.annotation.PostType;
import com.yoloo.android.feature.feed.common.event.PostDeleteEvent;
import com.yoloo.android.feature.feed.common.event.UpdateEvent;
import com.yoloo.android.feature.feed.common.listener.OnCommentClickListener;
import com.yoloo.android.feature.feed.common.listener.OnContentImageClickListener;
import com.yoloo.android.feature.feed.common.listener.OnMentionClickListener;
import com.yoloo.android.feature.feed.common.listener.OnPostOptionsClickListener;
import com.yoloo.android.feature.feed.common.listener.OnProfileClickListener;
import com.yoloo.android.feature.feed.common.listener.OnShareClickListener;
import com.yoloo.android.feature.feed.common.listener.OnVoteClickListener;
import com.yoloo.android.feature.photo.PhotoController;
import com.yoloo.android.feature.profile.ProfileController;
import com.yoloo.android.framework.MvpController;
import com.yoloo.android.ui.recyclerview.EndlessRecyclerViewScrollListener;
import com.yoloo.android.ui.tokenizer.SpaceTokenizer;
import com.yoloo.android.ui.widget.AutoCompleteMentionAdapter;
import com.yoloo.android.util.BundleBuilder;
import com.yoloo.android.util.ControllerUtil;
import com.yoloo.android.util.KeyboardUtil;
import com.yoloo.android.util.MenuHelper;
import com.yoloo.android.util.RxBus;
import com.yoloo.android.util.ShareUtil;
import com.yoloo.android.util.WeakHandler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class PostDetailController extends MvpController<PostDetailView, PostDetailPresenter>
    implements PostDetailView, SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListener.OnLoadMoreListener, OnProfileClickListener,
    OnPostOptionsClickListener, OnShareClickListener, OnCommentClickListener,
    FeedAdapter.OnCategoryClickListener, OnVoteClickListener, OnContentImageClickListener,
    OnMentionClickListener, OnMarkAsAcceptedClickListener {

  private static final String KEY_POST_ID = "POST_ID";

  @BindView(R.id.toolbar_post_detail) Toolbar toolbar;
  @BindView(R.id.rv_post_detail) RecyclerView rvFeed;
  @BindView(R.id.swipe_post_detail) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.tv_input_comment) MultiAutoCompleteTextView tvWriteComment;

  @BindColor(R.color.primary) int primaryColor;

  private PostDetailAdapter adapter;

  private AutoCompleteMentionAdapter mentionAdapter;
  private WeakHandler handler;
  private Runnable mentionDropdownRunnable = () -> tvWriteComment.showDropDown();

  private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

  private String cursor;
  private String eTag;

  private String postId;

  private boolean reEnter;
  private boolean refresh;

  public PostDetailController(@Nullable Bundle args) {
    super(args);
    setRetainViewMode(RetainViewMode.RETAIN_DETACH);
  }

  public static PostDetailController create(String postId) {
    final Bundle bundle = new BundleBuilder()
        .putString(KEY_POST_ID, postId)
        .build();

    return new PostDetailController(bundle);
  }

  @Override
  protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return inflater.inflate(R.layout.controller_post_detail, container, false);
  }

  @Override protected void onViewCreated(@NonNull View view) {
    super.onViewCreated(view);
    handler = new WeakHandler();

    final Bundle args = getArgs();
    postId = args.getString(KEY_POST_ID);

    setupPullToRefresh();
    setupToolbar();
    setHasOptionsMenu(true);
    setupRecyclerView();
    setupMentionsAdapter();
  }

  @Override protected void onAttach(@NonNull View view) {
    super.onAttach(view);
    if (!reEnter) {
      getPresenter().loadData(postId);
      reEnter = true;
    }

    rvFeed.addOnScrollListener(endlessRecyclerViewScrollListener);

    mentionAdapter.getQuery()
        .debounce(400, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(s -> getPresenter().suggestUser(s));
  }

  @Override protected void onDetach(@NonNull View view) {
    super.onDetach(view);
    rvFeed.removeOnScrollListener(endlessRecyclerViewScrollListener);
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final int itemId = item.getItemId();
    switch (itemId) {
      case android.R.id.home:
        getRouter().handleBack();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public boolean handleBack() {
    KeyboardUtil.hideKeyboard(tvWriteComment);
    return super.handleBack();
  }

  @Override public void onLoading(boolean pullToRefresh) {

  }

  @Override public void onLoaded(Response<List<CommentRealm>> value) {
    // empty
  }

  @Override public void onPostLoaded(PostRealm post) {
    adapter.addPost(post, refresh);
  }

  @Override public void onPostUpdated(PostRealm post) {
    RxBus.get().sendEvent(new UpdateEvent(post), ControllerUtil.getPreviousControllerClass(this));
  }

  @Override public void onCommentsLoaded(Response<List<CommentRealm>> value, String currentUserId,
      boolean postOwner, boolean accepted, @PostType int postType) {
    cursor = value.getCursor();
    eTag = value.geteTag();

    adapter.addComments(value.getData(), currentUserId, postOwner, accepted, postType);
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void onAcceptedCommentLoaded(CommentRealm comment, boolean postOwner,
      @PostType int postType) {
    adapter.addAcceptedComment(comment, postOwner, postType);
  }

  @Override
  public void onNewCommentLoaded(CommentRealm comment, boolean postOwner, @PostType int postType) {
    adapter.addComment(comment, postOwner, postType);
    adapter.scrollToEnd(rvFeed);

    RxBus.get().sendEvent(new UpdateEvent(new PostRealm().setId(postId)),
        ControllerUtil.getPreviousControllerClass(this));
  }

  @Override public void onNewAccept(String commentId) {

  }

  @Override public void onMentionSuggestionsLoaded(List<AccountRealm> suggestions) {
    mentionAdapter.replaceItems(suggestions);
    handler.post(mentionDropdownRunnable);
  }

  @Override public void onError(Throwable e) {
    Timber.e(e);
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void onEmpty() {
    Timber.d("onEmpty()");
  }

  @Override public void onLoadMore() {
    Timber.d("onLoadMore");
  }

  @Override public void onRefresh() {
    refresh = true;
    endlessRecyclerViewScrollListener.resetState();
    adapter.clear();

    getPresenter().loadData(postId);
  }

  @NonNull @Override public PostDetailPresenter createPresenter() {
    return new PostDetailPresenter(
        CommentRepository.getInstance(
            CommentRemoteDataStore.getInstance(),
            CommentDiskDataStore.getInstance()),
        PostRepository.getInstance(
            PostRemoteDataStore.getInstance(),
            PostDiskDataStore.getInstance()),
        UserRepository.getInstance(
            UserRemoteDataStore.getInstance(),
            UserDiskDataStore.getInstance()));
  }

  @Override public void onCategoryClick(View v, String categoryId, String name) {

  }

  @Override public void onCommentClick(View v, String postId, String postOwnerId,
      String acceptedCommentId, @PostType int postType) {
    tvWriteComment.requestFocus();
    KeyboardUtil.showDelayedKeyboard(tvWriteComment);
  }

  @Override
  public void onPostOptionsClick(View v, EpoxyModel<?> model, String postId, String postOwnerId) {
    final PopupMenu optionsMenu = MenuHelper.createMenu(getActivity(), v, R.menu.menu_post_popup);
    final boolean self = false;
    optionsMenu.getMenu().getItem(1).setVisible(self);
    optionsMenu.getMenu().getItem(2).setVisible(self);

    optionsMenu.setOnMenuItemClickListener(item -> {
      final int itemId = item.getItemId();
      switch (itemId) {
        case R.id.action_feed_popup_delete:
          getPresenter().deletePost(postId);
          RxBus.get().sendEvent(new PostDeleteEvent(new PostRealm().setId(postId)),
              ControllerUtil.getPreviousControllerClass(this));
          getRouter().popCurrentController();
          return true;
      }
      return false;
    });
  }

  @Override public void onContentImageClick(View v, String url) {
    startTransaction(PhotoController.create(url), new FadeChangeHandler());
  }

  @Override public void onProfileClick(View v, String ownerId) {
    startTransaction(ProfileController.create(ownerId), new VerticalChangeHandler());
  }

  @Override public void onShareClick(View v, PostRealm post) {
    ShareUtil.share(this, null, post.getContent());
  }

  @Override public void onVoteClick(String votableId, int direction, @Type int type) {
    if (type == Type.POST) {
      getPresenter().votePost(votableId, direction);
    } else {
      getPresenter().voteComment(votableId, direction);
    }
  }

  @Override public void onMentionClick(String value) {
    Snackbar.make(getView(), value, Snackbar.LENGTH_SHORT).show();
  }

  @Override public void onMarkAsAccepted(View v, CommentRealm comment) {
    Snackbar.make(getView(), R.string.label_comment_accepted_confirm, Snackbar.LENGTH_SHORT).show();
    getPresenter().acceptComment(comment);
  }

  @OnClick(R.id.btn_send_comment) void sendComment() {
    final String content = tvWriteComment.getText().toString().trim();

    if (TextUtils.isEmpty(content)) {
      return;
    }

    CommentRealm comment = new CommentRealm()
        .setId(UUID.randomUUID().toString())
        .setContent(content)
        .setCreated(new Date())
        .setPostId(postId);

    getPresenter().sendComment(comment);

    tvWriteComment.setText("");
  }

  private void setupRecyclerView() {
    adapter = PostDetailAdapter.builder()
        .onProfileClickListener(this)
        .onCommentClickListener(this)
        .onContentImageClickListener(this)
        .onOptionsClickListener(this)
        .onVoteClickListener(this)
        .onShareClickListener(this)
        .onMentionClickListener(this)
        .onMarkAsAcceptedListener(this)
        .build();

    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

    rvFeed.setLayoutManager(layoutManager);

    final DefaultItemAnimator animator = new DefaultItemAnimator();
    animator.setSupportsChangeAnimations(false);
    rvFeed.setItemAnimator(animator);

    rvFeed.setHasFixedSize(true);
    rvFeed.setAdapter(adapter);

    endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager, this);
  }

  private void setupPullToRefresh() {
    swipeRefreshLayout.setOnRefreshListener(this);
    swipeRefreshLayout.setColorSchemeColors(primaryColor);
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);

    final ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setDisplayShowTitleEnabled(false);
      ab.setDisplayHomeAsUpEnabled(true);
      ab.setDisplayShowHomeEnabled(true);
    }
  }

  private void setupMentionsAdapter() {
    mentionAdapter = new AutoCompleteMentionAdapter(getActivity());
    tvWriteComment.setAdapter(mentionAdapter);
    tvWriteComment.setTokenizer(new SpaceTokenizer());
  }

  private void startTransaction(Controller to, ControllerChangeHandler handler) {
    getRouter().pushController(
        RouterTransaction.with(to).pushChangeHandler(handler).popChangeHandler(handler));
  }
}