package com.example.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.emod.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class WordCloudView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    data class Word(
        val text: String,
        val weight: Float,
        val color: Int? = null
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        typeface = ResourcesCompat.getFont(context, R.font.nanumsquareroundextraboldotf)
        color = Color.BLACK
    }
    private val placed = mutableListOf<Pair<RectF, DrawItem>>()
    private var drawItems: List<DrawItem> = emptyList()

    // ===== 설정값 =====
    var minSp = 50f
    var maxSp = 100f
    var maxTryPerWord = 160
    var spacing = 8f * resources.displayMetrics.density // ✅ dp 기반 여백

    private var lastWords: List<Word> = emptyList()

    fun submit(words: List<Word>) {
        lastWords = words
        if (width == 0 || height == 0) {
            invalidate()
            return
        }
        drawItems = layoutWords(words)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && lastWords.isNotEmpty()) {
            drawItems = layoutWords(lastWords)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (item in drawItems) {
            paint.color = item.color
            paint.textSize = item.textSizePx
            canvas.drawText(item.text, item.x, item.y, paint)
        }
    }

    // ===== 내부 =====
    private data class DrawItem(
        val text: String,
        val textSizePx: Float,
        val color: Int,
        val x: Float,
        val y: Float
    )

    private fun layoutWords(words: List<Word>): List<DrawItem> {
        if (width == 0 || height == 0 || words.isEmpty()) return emptyList()
        placed.clear()

        val minW = words.minOf { it.weight }
        val maxW = max(minW + 1e-6f, words.maxOf { it.weight })
        fun weightToSp(w: Float): Float {
            val t = (w - minW) / (maxW - minW)
            return minSp + t * (maxSp - minSp)
        }

        val sorted = words.sortedByDescending { it.weight }
        val results = mutableListOf<DrawItem>()

        for (w in sorted) {
            val textSizePx = spToPx(weightToSp(w.weight))
            paint.textSize = textSizePx

            val bw = paint.measureText(w.text) // ✅ 폭은 measureText가 정확
            val fm = paint.fontMetrics
            val top = fm.ascent      // 음수
            val bottom = fm.descent

            val color = w.color ?: Color.BLACK
            val cx = width / 2f
            val cy = height / 2f

            var placedOk = false
            var bestX = 0f
            var bestY = 0f

            val a = 0f
            val b = 8f * resources.displayMetrics.density

            // ✅ 단어마다 시작 각도에 랜덤 오프셋
            var theta = (w.text.hashCode() and 0xFFFF) % 360 / 180.0 * Math.PI
            var tries = 0

            while (tries < maxTryPerWord && !placedOk) {
                val r = a + b * theta
                val x = cx + (r * cos(theta)).toFloat()
                val y = cy + (r * sin(theta)).toFloat()

                val rect = RectF(
                    x,
                    y + top,
                    x + bw,
                    y + bottom
                ).apply { inset(-spacing, -spacing) }

                val inside = rect.left >= 0 && rect.top >= 0 &&
                        rect.right <= width && rect.bottom <= height

                val overlapped = placed.any { RectF.intersects(it.first, rect) } // ✅ 비파괴 검사

                if (inside && !overlapped) {
                    placedOk = true
                    bestX = x
                    bestY = y
                    placed += RectF(rect) to DrawItem(w.text, textSizePx, color, x, y) // ✅ 복사 저장
                }

                theta += 0.3
                tries++
            }

            if (placedOk) {
                results += DrawItem(w.text, textSizePx, color, bestX, bestY)
            }
        }
        return results
    }

    private fun spToPx(sp: Float): Float =
        sp * resources.displayMetrics.scaledDensity
}
