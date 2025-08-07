package com.example.hackathon

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

class FaceContourOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs)  {
    private var faces: List<Face> = emptyList()
    private var transformMatrix: Matrix? = null
    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    fun setFaces(faces: List<Face>, matrix: Matrix) {
        this.faces = faces
        this.transformMatrix = matrix
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (face in faces) {
            // 입 윤곽
            face.getContour(FaceContour.UPPER_LIP_TOP)?.points?.let { points ->
                for (i in 1 until points.size) {
                    val src1 = floatArrayOf(points[i-1].x, points[i-1].y)
                    val src2 = floatArrayOf(points[i].x, points[i].y)
                    val dst1 = FloatArray(2)
                    val dst2 = FloatArray(2)
                    transformMatrix?.mapPoints(dst1, src1)
                    transformMatrix?.mapPoints(dst2, src2)
                    canvas.drawLine(dst1[0], dst1[1], dst2[0], dst2[1], paint)
                }
            }
            face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points?.let { points ->
                for (i in 1 until points.size) {
                    val src1 = floatArrayOf(points[i-1].x, points[i-1].y)
                    val src2 = floatArrayOf(points[i].x, points[i].y)
                    val dst1 = FloatArray(2)
                    val dst2 = FloatArray(2)
                    transformMatrix?.mapPoints(dst1, src1)
                    transformMatrix?.mapPoints(dst2, src2)
                    canvas.drawLine(dst1[0], dst1[1], dst2[0], dst2[1], paint)
                }
            }
            // 왼쪽 눈
            face.getContour(FaceContour.LEFT_EYE)?.points?.let { points ->
                for (i in 1 until points.size) {
                    val src1 = floatArrayOf(points[i-1].x, points[i-1].y)
                    val src2 = floatArrayOf(points[i].x, points[i].y)
                    val dst1 = FloatArray(2)
                    val dst2 = FloatArray(2)
                    transformMatrix?.mapPoints(dst1, src1)
                    transformMatrix?.mapPoints(dst2, src2)
                    canvas.drawLine(dst1[0], dst1[1], dst2[0], dst2[1], paint)
                }
            }
            // 오른쪽 눈
            face.getContour(FaceContour.RIGHT_EYE)?.points?.let { points ->
                for (i in 1 until points.size){
                    val src1 = floatArrayOf(points[i-1].x, points[i-1].y)
                    val src2 = floatArrayOf(points[i].x, points[i].y)
                    val dst1 = FloatArray(2)
                    val dst2 = FloatArray(2)
                    transformMatrix?.mapPoints(dst1, src1)
                    transformMatrix?.mapPoints(dst2, src2)
                    canvas.drawLine(dst1[0], dst1[1], dst2[0], dst2[1], paint)
                }
            }
        }
    }

    private fun drawContourLine(canvas: Canvas, points: List<PointF>?, matrix: Matrix) {
        if (points == null || points.size < 2) return
        val dst1 = FloatArray(2)
        val dst2 = FloatArray(2)
        for (i in 1 until points.size) {
            val src1 = floatArrayOf(points[i - 1].x, points[i - 1].y)
            val src2 = floatArrayOf(points[i].x, points[i].y)
            matrix.mapPoints(dst1, src1)
            matrix.mapPoints(dst2, src2)
            canvas.drawLine(dst1[0], dst1[1], dst2[0], dst2[1], paint)
        }
    }



}