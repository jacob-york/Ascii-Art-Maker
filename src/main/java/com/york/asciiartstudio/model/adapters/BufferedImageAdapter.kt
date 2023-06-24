package com.york.asciiartstudio.model.adapters

import java.awt.image.BufferedImage

class BufferedImageAdapter(private val bufferedImage: BufferedImage) : ImageSource {

    override val width: Int = bufferedImage.width

    override val height: Int = bufferedImage.height

    override val name: String? = null

    private fun min3(a: Int, b: Int, c: Int): Int {
        return a.coerceAtMost(b.coerceAtMost(c))
    }

    private fun max3(a: Int, b: Int, c: Int): Int {
        return a.coerceAtLeast(b.coerceAtLeast(c))
    }

    override fun getBWValue(x: Int, y: Int): Int {
        val rgb: Int = bufferedImage.getRGB(x, y)

        val a = rgb and -0x1000000 shr 24
        val r = rgb and 0xff0000 shr 16
        val g = rgb and 0xff00 shr 8
        val b = rgb and 0xff

        return if (a < 1) -1 else ((min3(r, g, b) + max3(r, g, b)) * .5).toInt()
    }
}