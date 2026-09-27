package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speedY: Float,
    val speedX: Float,
    val color: Color,
    val size: Float
)

@Composable
fun CelebrationOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val colors = listOf(AccentGold, AccentEmerald, SecondaryCyan, PrimaryIndigo, AccentRose)
        val particles = remember {
            List(45) {
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat() * 0.4f,
                    speedY = Random.nextFloat() * 10f + 5f,
                    speedX = (Random.nextFloat() - 0.5f) * 6f,
                    color = colors[Random.nextInt(colors.size)],
                    size = Random.nextFloat() * 12f + 6f
                )
            }
        }

        LaunchedEffect(isVisible) {
            if (isVisible) {
                delay(2200)
                onDismiss()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                particles.forEach { p ->
                    val posX = p.x * canvasWidth
                    val posY = p.y * canvasHeight + (p.speedY * 18f)

                    drawCircle(
                        color = p.color,
                        radius = p.size,
                        center = Offset(
                            x = (posX + (p.speedX * 10f)).coerceIn(0f, canvasWidth),
                            y = posY % canvasHeight
                        )
                    )
                }
            }
        }
    }
}
