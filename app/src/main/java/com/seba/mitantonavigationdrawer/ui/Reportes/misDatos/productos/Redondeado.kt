package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import android.graphics.*
import com.squareup.picasso.Transformation

class Redondeado(private val radius: Float) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawRoundRect(
            RectF(0f, 0f, source.width.toFloat(), source.height.toFloat()),
            radius,
            radius,
            paint
        )

        source.recycle()
        return output
    }

    override fun key(): String {
        return "rounded(radius=$radius)"
    }
}
