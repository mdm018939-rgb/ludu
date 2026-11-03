package com.example.mludo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mludo.logic.LudoBoardLogic
import com.example.mludo.model.BoardCell
import com.example.mludo.model.GameState
import com.example.mludo.model.PlayerColor
import com.example.mludo.model.TokenState
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun LudoBoardView(
    gameState: GameState,
    legalTokenIds: List<Int>,
    onTokenClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(4.dp, Color(0xFF071330), RoundedCornerShape(16.dp))
            .testTag("ludo_board")
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val cellSizePx = boardWidthPx / 15f
        val cellSizeDp = with(androidx.compose.ui.platform.LocalDensity.current) { cellSizePx.toDp() }

        // Canvas draws the 15x15 board grid, yards, arrows, and stars
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBoardGrid(cellSizePx)
        }

        // Render Yard Player Labels
        val activeColor = gameState.players.getOrNull(gameState.activePlayerIndex)?.color
        for (player in gameState.players) {
            val isTopPlayer = (player.color == PlayerColor.GREEN || player.color == PlayerColor.YELLOW)
            val isRightTop = (player.color == PlayerColor.YELLOW)
            val isRightBottom = (player.color == PlayerColor.BLUE)

            val align = when (player.color) {
                PlayerColor.GREEN -> Alignment.TopStart
                PlayerColor.YELLOW -> Alignment.TopEnd
                PlayerColor.RED -> Alignment.BottomStart
                PlayerColor.BLUE -> Alignment.BottomEnd
            }

            val isCurrentTurn = (player.color == activeColor)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = align
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = if (isTopPlayer) 4.dp else 0.dp, bottom = if (!isTopPlayer) 4.dp else 0.dp)
                        .then(if (isTopPlayer) Modifier.rotate(180f) else Modifier)
                        .background(
                            color = if (isCurrentTurn) Color(0xFFFFD700) else Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = player.name,
                        color = if (isCurrentTurn) Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Group all tokens by their exact BoardCell
        val tokensOnBoard = mutableListOf<Pair<PlayerColor, TokenState>>()
        for (player in gameState.players) {
            for (token in player.tokens) {
                tokensOnBoard.add(player.color to token)
            }
        }

        val cellTokenMap = tokensOnBoard.groupBy { (color, token) ->
            LudoBoardLogic.getTokenCell(color, token.stepPosition, token.id)
        }

        // Draw Token Composable overlays
        for ((cell, tokenList) in cellTokenMap) {
            val cellLeftDp = cellSizeDp * cell.col
            val cellTopDp = cellSizeDp * cell.row

            val stackSize = tokenList.size

            tokenList.forEachIndexed { index, (color, token) ->
                val isLegal = (color == activeColor && token.id in legalTokenIds)

                // If stacked on same cell, offset tokens slightly
                val offsetX = if (stackSize > 1) {
                    when (index % 4) {
                        0 -> (-3).dp
                        1 -> 3.dp
                        2 -> (-3).dp
                        else -> 3.dp
                    }
                } else 0.dp

                val offsetY = if (stackSize > 1) {
                    when (index % 4) {
                        0 -> (-3).dp
                        1 -> (-3).dp
                        2 -> 3.dp
                        else -> 3.dp
                    }
                } else 0.dp

                Box(
                    modifier = Modifier
                        .offset(
                            x = cellLeftDp + (cellSizeDp * 0.5f) - (cellSizeDp * 0.65f) + offsetX,
                            y = cellTopDp + (cellSizeDp * 0.5f) - (cellSizeDp * 0.65f) + offsetY
                        )
                ) {
                    MapPinTokenView(
                        color = color,
                        isMovable = isLegal,
                        onClick = { onTokenClicked(token.id) },
                        stackCount = if (index == 0) stackSize else 1,
                        sizeDp = cellSizeDp * 0.95f
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawBoardGrid(cellSize: Float) {
    val greenColor = PlayerColor.GREEN.primaryColor
    val yellowColor = PlayerColor.YELLOW.primaryColor
    val redColor = PlayerColor.RED.primaryColor
    val blueColor = PlayerColor.BLUE.primaryColor

    val gridLineColor = Color(0xFFD0D0D0)
    val strokeWidth = 1.5f

    // 1. Draw Corner Yards (6x6 cells each)
    // Top-Left Green Yard
    drawRect(greenColor, Offset(0f, 0f), Size(6 * cellSize, 6 * cellSize))
    // Inner white yard square
    drawRect(Color.White, Offset(1 * cellSize, 1 * cellSize), Size(4 * cellSize, 4 * cellSize))
    // 4 Green Token Slots
    drawCircle(greenColor, radius = cellSize * 0.6f, center = Offset(2 * cellSize, 2 * cellSize))
    drawCircle(greenColor, radius = cellSize * 0.6f, center = Offset(4 * cellSize, 2 * cellSize))
    drawCircle(greenColor, radius = cellSize * 0.6f, center = Offset(2 * cellSize, 4 * cellSize))
    drawCircle(greenColor, radius = cellSize * 0.6f, center = Offset(4 * cellSize, 4 * cellSize))

    // Top-Right Yellow Yard
    drawRect(yellowColor, Offset(9 * cellSize, 0f), Size(6 * cellSize, 6 * cellSize))
    drawRect(Color.White, Offset(10 * cellSize, 1 * cellSize), Size(4 * cellSize, 4 * cellSize))
    drawCircle(yellowColor, radius = cellSize * 0.6f, center = Offset(11 * cellSize, 2 * cellSize))
    drawCircle(yellowColor, radius = cellSize * 0.6f, center = Offset(13 * cellSize, 2 * cellSize))
    drawCircle(yellowColor, radius = cellSize * 0.6f, center = Offset(11 * cellSize, 4 * cellSize))
    drawCircle(yellowColor, radius = cellSize * 0.6f, center = Offset(13 * cellSize, 4 * cellSize))

    // Bottom-Left Red Yard
    drawRect(redColor, Offset(0f, 9 * cellSize), Size(6 * cellSize, 6 * cellSize))
    drawRect(Color.White, Offset(1 * cellSize, 10 * cellSize), Size(4 * cellSize, 4 * cellSize))
    drawCircle(redColor, radius = cellSize * 0.6f, center = Offset(2 * cellSize, 11 * cellSize))
    drawCircle(redColor, radius = cellSize * 0.6f, center = Offset(4 * cellSize, 11 * cellSize))
    drawCircle(redColor, radius = cellSize * 0.6f, center = Offset(2 * cellSize, 13 * cellSize))
    drawCircle(redColor, radius = cellSize * 0.6f, center = Offset(4 * cellSize, 13 * cellSize))

    // Bottom-Right Blue Yard
    drawRect(blueColor, Offset(9 * cellSize, 9 * cellSize), Size(6 * cellSize, 6 * cellSize))
    drawRect(Color.White, Offset(10 * cellSize, 10 * cellSize), Size(4 * cellSize, 4 * cellSize))
    drawCircle(blueColor, radius = cellSize * 0.6f, center = Offset(11 * cellSize, 11 * cellSize))
    drawCircle(blueColor, radius = cellSize * 0.6f, center = Offset(13 * cellSize, 11 * cellSize))
    drawCircle(blueColor, radius = cellSize * 0.6f, center = Offset(11 * cellSize, 13 * cellSize))
    drawCircle(blueColor, radius = cellSize * 0.6f, center = Offset(13 * cellSize, 13 * cellSize))

    // 2. Home Paths (5 colored cells for each color)
    // Red Home Column (bottom up, col 7, row 9..13)
    for (r in 9..13) {
        drawRect(redColor, Offset(7 * cellSize, r * cellSize), Size(cellSize, cellSize))
    }
    // Green Home Row (left right, row 7, col 1..5)
    for (c in 1..5) {
        drawRect(greenColor, Offset(c * cellSize, 7 * cellSize), Size(cellSize, cellSize))
    }
    // Yellow Home Column (top down, col 7, row 1..5)
    for (r in 1..5) {
        drawRect(yellowColor, Offset(7 * cellSize, r * cellSize), Size(cellSize, cellSize))
    }
    // Blue Home Row (right left, row 7, col 9..13)
    for (c in 9..13) {
        drawRect(blueColor, Offset(c * cellSize, 7 * cellSize), Size(cellSize, cellSize))
    }

    // 3. Start Squares (Red Start: 13,6 / Green Start: 6,1 / Yellow Start: 1,8 / Blue Start: 8,13)
    drawRect(redColor, Offset(6 * cellSize, 13 * cellSize), Size(cellSize, cellSize))
    drawRect(greenColor, Offset(1 * cellSize, 6 * cellSize), Size(cellSize, cellSize))
    drawRect(yellowColor, Offset(8 * cellSize, 1 * cellSize), Size(cellSize, cellSize))
    drawRect(blueColor, Offset(13 * cellSize, 8 * cellSize), Size(cellSize, cellSize))

    // Directional Arrows on Start Squares
    drawArrow(Offset(6.5f * cellSize, 13.8f * cellSize), direction = "UP", cellSize = cellSize)
    drawArrow(Offset(1.2f * cellSize, 6.5f * cellSize), direction = "RIGHT", cellSize = cellSize)
    drawArrow(Offset(8.5f * cellSize, 1.2f * cellSize), direction = "DOWN", cellSize = cellSize)
    drawArrow(Offset(13.8f * cellSize, 8.5f * cellSize), direction = "LEFT", cellSize = cellSize)

    // 4. Center Triangles (rows 6..8, cols 6..8)
    val centerTop = 6 * cellSize
    val centerLeft = 6 * cellSize
    val centerRight = 9 * cellSize
    val centerBottom = 9 * cellSize
    val centerMidX = 7.5f * cellSize
    val centerMidY = 7.5f * cellSize

    // Top Triangle (Yellow)
    drawPath(
        path = Path().apply {
            moveTo(centerLeft, centerTop)
            lineTo(centerRight, centerTop)
            lineTo(centerMidX, centerMidY)
            close()
        },
        color = yellowColor
    )
    // Left Triangle (Green)
    drawPath(
        path = Path().apply {
            moveTo(centerLeft, centerTop)
            lineTo(centerLeft, centerBottom)
            lineTo(centerMidX, centerMidY)
            close()
        },
        color = greenColor
    )
    // Bottom Triangle (Red)
    drawPath(
        path = Path().apply {
            moveTo(centerLeft, centerBottom)
            lineTo(centerRight, centerBottom)
            lineTo(centerMidX, centerMidY)
            close()
        },
        color = redColor
    )
    // Right Triangle (Blue)
    drawPath(
        path = Path().apply {
            moveTo(centerRight, centerTop)
            lineTo(centerRight, centerBottom)
            lineTo(centerMidX, centerMidY)
            close()
        },
        color = blueColor
    )

    // Center divider lines
    drawLine(Color.White, Offset(centerLeft, centerTop), Offset(centerRight, centerBottom), strokeWidth = 2f)
    drawLine(Color.White, Offset(centerRight, centerTop), Offset(centerLeft, centerBottom), strokeWidth = 2f)

    // 5. Draw 15x15 Cell Grid Lines across cross tracks
    for (i in 0..15) {
        // Vertical lines in middle track
        if (i in 6..9) {
            drawLine(gridLineColor, Offset(i * cellSize, 0f), Offset(i * cellSize, 6 * cellSize), strokeWidth)
            drawLine(gridLineColor, Offset(i * cellSize, 9 * cellSize), Offset(i * cellSize, 15 * cellSize), strokeWidth)
        }
        // Vertical lines in full rows (6..9)
        drawLine(gridLineColor, Offset(i * cellSize, 6 * cellSize), Offset(i * cellSize, 9 * cellSize), strokeWidth)

        // Horizontal lines in middle track
        if (i in 6..9) {
            drawLine(gridLineColor, Offset(0f, i * cellSize), Offset(6 * cellSize, i * cellSize), strokeWidth)
            drawLine(gridLineColor, Offset(9 * cellSize, i * cellSize), Offset(15 * cellSize, i * cellSize), strokeWidth)
        }
        // Horizontal lines in full cols (6..9)
        drawLine(gridLineColor, Offset(6 * cellSize, i * cellSize), Offset(9 * cellSize, i * cellSize), strokeWidth)
    }

    // Outer Board Border
    drawRect(Color.Black, Offset(0f, 0f), Size(15 * cellSize, 15 * cellSize), style = Stroke(width = 3f))

    // 6. Draw Safe Star Icons on the 8 safe squares
    // 4 Start Squares (red 13,6 / green 6,1 / yellow 1,8 / blue 8,13)
    drawStar(Offset(6.5f * cellSize, 13.5f * cellSize), cellSize * 0.35f, Color.White)
    drawStar(Offset(1.5f * cellSize, 6.5f * cellSize), cellSize * 0.35f, Color.White)
    drawStar(Offset(8.5f * cellSize, 1.5f * cellSize), cellSize * 0.35f, Color.White)
    drawStar(Offset(13.5f * cellSize, 8.5f * cellSize), cellSize * 0.35f, Color.White)

    // 4 Star Squares (left 8,2 / top 2,6 / right 6,12 / bottom 12,8)
    drawStar(Offset(2.5f * cellSize, 8.5f * cellSize), cellSize * 0.35f, Color(0xFF666666))
    drawStar(Offset(6.5f * cellSize, 2.5f * cellSize), cellSize * 0.35f, Color(0xFF666666))
    drawStar(Offset(12.5f * cellSize, 6.5f * cellSize), cellSize * 0.35f, Color(0xFF666666))
    drawStar(Offset(8.5f * cellSize, 12.5f * cellSize), cellSize * 0.35f, Color(0xFF666666))
}

private fun DrawScope.drawArrow(center: Offset, direction: String, cellSize: Float) {
    val size = cellSize * 0.3f
    val path = Path()
    when (direction) {
        "UP" -> {
            path.moveTo(center.x, center.y - size)
            path.lineTo(center.x - size, center.y + size)
            path.lineTo(center.x + size, center.y + size)
        }
        "DOWN" -> {
            path.moveTo(center.x, center.y + size)
            path.lineTo(center.x - size, center.y - size)
            path.lineTo(center.x + size, center.y - size)
        }
        "LEFT" -> {
            path.moveTo(center.x - size, center.y)
            path.lineTo(center.x + size, center.y - size)
            path.lineTo(center.x + size, center.y + size)
        }
        "RIGHT" -> {
            path.moveTo(center.x + size, center.y)
            path.lineTo(center.x - size, center.y - size)
            path.lineTo(center.x - size, center.y + size)
        }
    }
    path.close()
    drawPath(path, Color.White.copy(alpha = 0.9f))
    drawPath(path, Color.Black.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val numPoints = 5
    val outerRadius = radius
    val innerRadius = radius * 0.45f

    for (i in 0 until numPoints * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = i * Math.PI / numPoints - Math.PI / 2
        val x = center.x + r * Math.cos(angle).toFloat()
        val y = center.y + r * Math.sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
    drawPath(path, Color.Black.copy(alpha = 0.4f), style = Stroke(width = 1.5f))
}
