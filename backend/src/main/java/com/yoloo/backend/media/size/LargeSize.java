package com.yoloo.backend.media.size;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.yoloo.backend.config.MediaConfig;
import com.yoloo.backend.media.Media;

@JsonRootName("large")
@ApiResourceProperty(name = "large")
public class LargeSize extends Media.Size {

    public LargeSize(String url) {
        super(url);
    }

    @Override
    public String getUrl() {
        return createUrl(url, MediaConfig.LARGE_SIZE, false);
    }

    @Override
    public int getWidth() {
        return MediaConfig.LARGE_SIZE;
    }

    @Override
    public int getHeight() {
        return MediaConfig.LARGE_SIZE;
    }
}
