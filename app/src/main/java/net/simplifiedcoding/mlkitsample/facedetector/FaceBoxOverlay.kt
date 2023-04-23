package net.simplifiedcoding.mlkitsample.facedetector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.ceil

open class FaceBoxOverlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val lock = Any()
    private val faceBoxes: MutableList<FaceBox> = mutableListOf()
    var mScale: Float? = null
    var mOffsetX: Float? = null
    var mOffsetY: Float? = null

    abstract class FaceBox(private val overlay: FaceBoxOverlay) {

        abstract fun draw(canvas: Canvas?)

        fun getBoxRect(imageRectWidth: Float, imageRectHeight: Float, faceBoundingBox: Rect): RectF {
            val scaleX = overlay.width.toFloat() / imageRectHeight
            val scaleY = overlay.height.toFloat() / imageRectWidth
            val scale = scaleX.coerceAtLeast(scaleY)

            overlay.mScale = scale

            val offsetX = (overlay.width.toFloat() - ceil(imageRectHeight * scale)) / 2.0f
            val offsetY = (overlay.height.toFloat() - ceil(imageRectWidth * scale)) / 2.0f

            overlay.mOffsetX = offsetX
            overlay.mOffsetY = offsetY

            val mappedBox = RectF().apply {
                left = faceBoundingBox.right * scale + offsetX
                top = faceBoundingBox.top * scale + offsetY
                right = faceBoundingBox.left * scale + offsetX
                bottom = faceBoundingBox.bottom * scale + offsetY
            }

            val centerX = overlay.width.toFloat() / 2

            return mappedBox.apply {
                left = centerX + (centerX - left)
                right = centerX - (right - centerX)
            }
        }
    }

    fun clear() {
        synchronized(lock) { faceBoxes.clear() }
        postInvalidate()
    }

    fun add(faceBox: FaceBox) {
        synchronized(lock) { faceBoxes.add(faceBox) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            for (graphic in faceBoxes) {
                graphic.draw(canvas)
            }
        }
    }

}