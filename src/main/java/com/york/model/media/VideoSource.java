package com.york.model.media;

public interface VideoSource {

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
    int getFrameCount();

    /**
     *
     * @return
     */
    ShadingRaster[] getShadingRasterArray();
}
