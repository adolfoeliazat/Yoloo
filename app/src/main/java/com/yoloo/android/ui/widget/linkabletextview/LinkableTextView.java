package com.yoloo.android.ui.widget.linkabletextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Patterns;
import com.yoloo.android.R;
import com.yoloo.android.ui.widget.BaselineGridTextView;
import java.util.regex.Pattern;

public class LinkableTextView extends BaselineGridTextView {

  private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
          + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
  private static Pattern mentionPattern;
  private static Pattern hashtagPattern;
  private boolean enabledLinks = true;
  private boolean enabledHashtag = true;
  private boolean enabledMention = true;
  private boolean enabledEmailAddress = true;
  private boolean enabledPhone = true;
  private boolean enabledWebUrl = true;
  private boolean enabledIpAddress = true;
  private boolean enabledUnderlines = true;
  private boolean enabledHashtagUnderline = true;
  private boolean enabledMentionUnderline = true;
  private boolean enabledEmailAddressUnderline = true;
  private boolean enabledPhoneUnderline = true;
  private boolean enabledWebUrlUnderline = true;
  private boolean enabledIpAddressUnderline = true;

  public LinkableTextView(Context context) {
    super(context);
  }

  public LinkableTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public LinkableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    String mentionPattern = null;
    String hashtagPattern = null;

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinkableTextView);

    int n = a.getIndexCount();

    for (int i = 0; i < n; i++) {
      int attr = a.getIndex(i);

      if (attr == R.styleable.LinkableTextView_pattern_mention) {
        mentionPattern = a.getString(attr);
      } else if (attr == R.styleable.LinkableTextView_pattern_hashtag) {
        hashtagPattern = a.getString(attr);
      } else if (attr == R.styleable.LinkableTextView_enabledLinks) {
        enabledLinks = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledHashtag) {
        enabledHashtag = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledMention) {
        enabledMention = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledEmail_address) {
        enabledEmailAddress = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledPhone) {
        enabledPhone = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledWeb_url) {
        enabledWebUrl = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledIp_address) {
        enabledIpAddress = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledUnderlines) {
        enabledUnderlines = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledHashtagUnderline) {
        enabledHashtagUnderline = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledMentionUnderline) {
        enabledMentionUnderline = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledEmail_addressUnderline) {
        enabledEmailAddressUnderline = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledPhoneUnderline) {
        enabledPhoneUnderline = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledWeb_urlUnderline) {
        enabledWebUrlUnderline = a.getBoolean(attr, true);
      } else if (attr == R.styleable.LinkableTextView_enabledIp_addressUnderline) {
        enabledIpAddressUnderline = a.getBoolean(attr, true);
      }
    }
    setMentionPattern(mentionPattern);
    setHashtagPattern(hashtagPattern);

    setEnabledLinks(enabledLinks);
    setEnabledHashtag(enabledHashtag);
    setEnabledMention(enabledMention);
    setEnabledEmailAddress(enabledEmailAddress);
    setEnabledPhone(enabledPhone);
    setEnabledWebUrl(enabledWebUrl);
    setEnabledIpAddress(enabledIpAddress);

    setEnabledUnderlines(enabledUnderlines);
    setEnabledHashtagUnderline(enabledHashtagUnderline);
    setEnabledMentionUnderline(enabledMentionUnderline);
    setEnabledEmailAddressUnderline(enabledEmailAddressUnderline);
    setEnabledPhoneUnderline(enabledPhoneUnderline);
    setEnabledWebUrlUnderline(enabledWebUrlUnderline);
    setEnabledIpAddressUnderline(enabledIpAddressUnderline);

    a.recycle();
  }

  public Pattern getMentionPattern() {
    return mentionPattern;
  }

  public void setMentionPattern(@Nullable String mentionPattern) {
    if (!TextUtils.isEmpty(mentionPattern)) {
      LinkableTextView.mentionPattern = Pattern.compile(mentionPattern);
    } else {
      LinkableTextView.mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
    }
  }

  public void setDefaultMentionPattern() {
    setMentionPattern(null);
  }

  public Pattern getHashtagPattern() {
    return hashtagPattern;
  }

  public void setHashtagPattern(@Nullable String hashTagPattern) {
    if (!TextUtils.isEmpty(hashTagPattern)) {
      hashtagPattern = Pattern.compile(hashTagPattern);
    } else {
      hashtagPattern = Pattern.compile("#(\\w+)");
    }
  }

  public void setDefaultHashtagPattern() {
    setHashtagPattern(null);
  }

  public boolean isEnabledLinks() {
    return enabledLinks;
  }

  public void setEnabledLinks(boolean enabledLinks) {
    this.enabledLinks = enabledLinks;
  }

  public boolean isEnabledHashtag() {
    return enabledHashtag;
  }

  public void setEnabledHashtag(boolean enabledHashtag) {
    this.enabledHashtag = enabledHashtag;
  }

  public boolean isEnabledMention() {
    return enabledMention;
  }

  public void setEnabledMention(boolean enabledMention) {
    this.enabledMention = enabledMention;
  }

  public boolean isEnabledEmailAddress() {
    return enabledEmailAddress;
  }

  public void setEnabledEmailAddress(boolean enabledEmailAddress) {
    this.enabledEmailAddress = enabledEmailAddress;
  }

  public boolean isEnabledPhone() {
    return enabledPhone;
  }

  public void setEnabledPhone(boolean enabledPhone) {
    this.enabledPhone = enabledPhone;
  }

  public boolean isEnabledWebUrl() {
    return enabledWebUrl;
  }

  public void setEnabledWebUrl(boolean enabledWebUrl) {
    this.enabledWebUrl = enabledWebUrl;
  }

  public boolean isEnabledIpAddress() {
    return enabledIpAddress;
  }

  public void setEnabledIpAddress(boolean enabledIpAddress) {
    this.enabledIpAddress = enabledIpAddress;
  }

  public boolean isEnabledUnderlines() {
    return enabledUnderlines;
  }

  public void setEnabledUnderlines(boolean enabledUnderlines) {
    this.enabledUnderlines = enabledUnderlines;
  }

  public boolean isEnabledHashtagUnderline() {
    return enabledHashtagUnderline;
  }

  public void setEnabledHashtagUnderline(boolean enabledHashtagUnderline) {
    this.enabledHashtagUnderline = enabledHashtagUnderline;
  }

  public boolean isEnabledMentionUnderline() {
    return enabledMentionUnderline;
  }

  public void setEnabledMentionUnderline(boolean enabledMentionUnderline) {
    this.enabledMentionUnderline = enabledMentionUnderline;
  }

  public boolean isEnabledEmailAddressUnderline() {
    return enabledEmailAddressUnderline;
  }

  public void setEnabledEmailAddressUnderline(boolean enabledEmailAddressUnderline) {
    this.enabledEmailAddressUnderline = enabledEmailAddressUnderline;
  }

  public boolean isEnabledPhoneUnderline() {
    return enabledPhoneUnderline;
  }

  public void setEnabledPhoneUnderline(boolean enabledPhoneUnderline) {
    this.enabledPhoneUnderline = enabledPhoneUnderline;
  }

  public boolean isEnabledWebUrlUnderline() {
    return enabledWebUrlUnderline;
  }

  public void setEnabledWebUrlUnderline(boolean enabledWebUrlUnderline) {
    this.enabledWebUrlUnderline = enabledWebUrlUnderline;
  }

  public boolean isEnabledIpAddressUnderline() {
    return enabledIpAddressUnderline;
  }

  public void setEnabledIpAddressUnderline(boolean enabledIpAddressUnderline) {
    this.enabledIpAddressUnderline = enabledIpAddressUnderline;
  }

  public void setOnLinkClickListener(@Nullable final OnLinkClickListener listener) {
    setOnLinkClickListener((type, value) -> {
      if (listener == null || !enabledLinks) {
        return;
      }

      switch (type) {
        case Link.HASH_TAG:
          if (enabledHashtag) {
            listener.onHashtagClick(value);
          }
          break;
        case Link.MENTION:
          if (enabledMention) {
            listener.onMentionClick(value);
          }
          break;
        case Link.EMAIL_ADDRESS:
          if (enabledEmailAddress) {
            listener.onEmailAddressClick(value);
          }
          break;
        case Link.IP_ADDRESS:
          break;
        case Link.WEB_URL:
          if (enabledWebUrl) {
            listener.onWebUrlClick(value);
          }
          break;
        case Link.PHONE:
          if (enabledPhone) {
            listener.onPhoneClick(value);
          }
          break;
      }
    });
  }

  public void setOnLinkClickListener(@Nullable final LinkableCallback callback) {
    Linkify.TransformFilter filter = (match, url) -> match.group();

    if (enabledLinks) {
      if (enabledUnderlines) {
        if (enabledHashtag) {
          Linkify.addLinks(this, hashtagPattern,
              LinkableMovementMethod.LINKABLE_HASHTAG_SCHEME, null, filter);

          if (!enabledHashtagUnderline) {
            stripUnderlines();
          }
        }

        if (enabledMention) {
          Linkify.addLinks(this, mentionPattern,
              LinkableMovementMethod.LINKABLE_MENTION_SCHEME, null, filter);

          if (!enabledMentionUnderline) {
            stripUnderlines();
          }
        }

        if (enabledEmailAddress) {
          Linkify.addLinks(this, Patterns.EMAIL_ADDRESS, null, null, filter);

          if (!enabledEmailAddressUnderline) {
            stripUnderlines();
          }
        }

        if (enabledPhone) {
          Linkify.addLinks(this, Patterns.PHONE, null, null, filter);

          if (!enabledPhoneUnderline) {
            stripUnderlines();
          }
        }

        if (enabledWebUrl) {
          Linkify.addLinks(this, Patterns.WEB_URL, null, null, filter);

          if (!enabledWebUrlUnderline) {
            stripUnderlines();
          }
        }

        if (enabledIpAddress) {
          Linkify.addLinks(this, IP_ADDRESS_PATTERN,
              LinkableMovementMethod.LINKABLE_IP_ADDRESS_SCHEME, null, filter);

          if (!enabledIpAddressUnderline) {
            stripUnderlines();
          }
        }
      } else {
        if (enabledHashtag) {
          Linkify.addLinks(this, hashtagPattern,
              LinkableMovementMethod.LINKABLE_HASHTAG_SCHEME, null, filter);
        }

        if (enabledMention) {
          Linkify.addLinks(this, mentionPattern,
              LinkableMovementMethod.LINKABLE_MENTION_SCHEME, null, filter);
        }

        if (enabledEmailAddress) {
          Linkify.addLinks(this, Patterns.EMAIL_ADDRESS, null, null, filter);
        }

        if (enabledPhone) {
          Linkify.addLinks(this, Patterns.PHONE, null, null, filter);
        }

        if (enabledWebUrl) {
          Linkify.addLinks(this, Patterns.WEB_URL, null, null, filter);
        }

        if (enabledIpAddress) {
          Linkify.addLinks(this, IP_ADDRESS_PATTERN,
              LinkableMovementMethod.LINKABLE_IP_ADDRESS_SCHEME, null, filter);
        }

        stripUnderlines();
      }
    }

    MovementMethod movementMethod = null;
    if (callback != null) {
      movementMethod = new LinkableMovementMethod(callback);
    }
    setMovementMethod(movementMethod);
  }

  private void stripUnderlines() {
    Spannable s = new SpannableString(getText());
    URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
    for (URLSpan span : spans) {
      int start = s.getSpanStart(span);
      int end = s.getSpanEnd(span);
      s.removeSpan(span);
      span = new URLSpanNoUnderline(span.getURL());
      s.setSpan(span, start, end, 0);
    }
    setText(s);
  }

  public interface Link {
    int HASH_TAG = 0x1;
    int MENTION = 0x2;
    int EMAIL_ADDRESS = 0x3;
    int PHONE = 0x4;
    int WEB_URL = 0x5;
    int IP_ADDRESS = 0x6;
  }
}
