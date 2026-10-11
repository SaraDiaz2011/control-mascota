package com.upb.controlmascota.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/** Muestra "15042023" como "15/04/2023" mientras se escribe. */
class FechaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val d = text.text.take(8)
        val formateado = buildString {
            d.forEachIndexed { i, c ->
                append(c)
                if ((i == 1 || i == 3) && i != d.lastIndex) append('/')
            }
        }
        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val t = when {
                    offset <= 1 -> offset
                    offset <= 3 -> offset + 1
                    else -> offset + 2
                }
                return t.coerceAtMost(formateado.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val o = when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    else -> offset - 2
                }
                return o.coerceAtMost(d.length)
            }
        }
        return TransformedText(AnnotatedString(formateado), mapa)
    }
}