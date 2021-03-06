package com.yoloo.android.ui.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import com.yoloo.android.R;
import com.yoloo.android.util.CountUtil;
import com.yoloo.android.util.DrawableHelper;
import com.yoloo.android.util.Preconditions;

public class VoteView extends BaselineGridTextView {

  public static final int DIRECTION_UP = 1;
  public static final int DIRECTION_DEFAULT = 0;
  public static final int DIRECTION_DOWN = -1;

  private static final int LEFT_DRAWABLE = 0;
  private static final int RIGHT_DRAWABLE = 2;

  private OnVoteEventListener onVoteEventListener;

  private boolean upConsumed;
  private boolean downConsumed;

  private long votes = 0L;

  private int direction;

  public VoteView(Context context) {
    super(context);
    init();
  }

  public VoteView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public VoteView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    if (!isInEditMode()) {
      setDefaultTint(LEFT_DRAWABLE);
      setDefaultTint(RIGHT_DRAWABLE);
    }

    setGravity(Gravity.CENTER);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    Preconditions.checkNotNull(onVoteEventListener,
        "Vote listener is not implemented. Please implement OnVoteEventListener");

    if (event.getAction() == MotionEvent.ACTION_UP) {
      if (isUpDrawableClick(event)) {
        if (upConsumed) {
          setText(CountUtil.formatCount(--votes));
          setDefaultTint(LEFT_DRAWABLE);
          upConsumed = false;

          onVoteEventListener.onVoteEvent(DIRECTION_DEFAULT);
        } else if (downConsumed) {
          votes += 2;
          setText(CountUtil.formatCount(votes));
          setDefaultTint(RIGHT_DRAWABLE);
          setUpTint();
          downConsumed = false;
          upConsumed = true;

          onVoteEventListener.onVoteEvent(DIRECTION_UP);
        } else {
          setText(CountUtil.formatCount(++votes));
          setUpTint();
          upConsumed = true;

          onVoteEventListener.onVoteEvent(DIRECTION_UP);
        }
      } else if (isDownDrawableClick(event)) {
        if (downConsumed) {
          setText(CountUtil.formatCount(++votes));
          setDefaultTint(RIGHT_DRAWABLE);
          onVoteEventListener.onVoteEvent(DIRECTION_DEFAULT);
          downConsumed = false;
        } else if (upConsumed) {
          votes -= 2;
          setText(CountUtil.formatCount(votes));
          setDefaultTint(LEFT_DRAWABLE);
          setDownTint();
          upConsumed = false;
          downConsumed = true;

          onVoteEventListener.onVoteEvent(DIRECTION_DOWN);
        } else {
          setText(CountUtil.formatCount(--votes));
          setDownTint();
          downConsumed = true;

          onVoteEventListener.onVoteEvent(DIRECTION_DOWN);
        }
      }
    }
    return true;
  }

  private boolean isDownDrawableClick(MotionEvent event) {
    return event.getRawX() >= getRight() - getTotalPaddingRight();
  }

  private boolean isUpDrawableClick(MotionEvent event) {
    return event.getRawX() <= getLeft() + getTotalPaddingLeft();
  }

  private void setUpTint() {
    setTint(R.color.accent, LEFT_DRAWABLE);
  }

  private void setDefaultTint(int compoundIndex) {
    setTint(android.R.color.secondary_text_dark, compoundIndex);
  }

  private void setDownTint() {
    setTint(R.color.primary_blue, RIGHT_DRAWABLE);
  }

  private void setTint(@ColorRes int color, int compoundIndex) {
    DrawableHelper.create()
        .withDrawable(getCompoundDrawables()[compoundIndex])
        .withColor(getContext(), color)
        .tint();
  }

  public void setOnVoteEventListener(OnVoteEventListener onVoteEventListener) {
    this.onVoteEventListener = onVoteEventListener;
  }

  public int getDirection() {
    return direction;
  }

  public void setVoteDirection(int direction) {
    this.direction = direction;
    switch (direction) {
      case -1:
        upConsumed = false;
        downConsumed = true;
        setDownTint();
        setDefaultTint(LEFT_DRAWABLE);
        break;
      case 1:
        downConsumed = false;
        upConsumed = true;
        setUpTint();
        setDefaultTint(RIGHT_DRAWABLE);
        break;
      default:
        upConsumed = false;
        downConsumed = false;
        setDefaultTint(LEFT_DRAWABLE);
        setDefaultTint(RIGHT_DRAWABLE);
        break;
    }
  }

  public long getVotes() {
    return votes;
  }

  public void setVoteCount(long votes) {
    this.votes = votes;
    setText(CountUtil.formatCount(this.votes));
  }

  public interface OnVoteEventListener {
    void onVoteEvent(int direction);
  }
}
