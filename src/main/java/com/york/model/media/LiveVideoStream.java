package com.york.model.media;

// TODO
public interface LiveVideoStream {

    /**
     *
     * @return
     */
    int getWidthPixels();

    /**
     *
     * @return
     */
    int getHeightPixels();

    /**
     *
     * @return
     */
    int getFrameRate();

    /**
     *
     * @return
     */
    ShadingRaster getCurrentFrame();
}
