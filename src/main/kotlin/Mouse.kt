import java.awt.event.*
import kotlin.math.ceil
import kotlin.math.round

class Mouse {
    companion object {
        var cellSize = DrawBoard.fieldWidth / DrawBoard.scale
    }

    var pressX = 0
    var pressY = 0
    var releaseX = 0
    var releaseY = 0
    val mouseListener = object : MouseListener {
        override fun mouseClicked(click: MouseEvent) {
            val clickCoordinates = calculateCell(click.x, click.y)
            //clickCell(clickCoordinates.x, clickCoordinates.y, DrawBoard.board)
        }

        override fun mouseExited(exit: MouseEvent) {}
        override fun mousePressed(press: MouseEvent) {
            val pressCoordinate = calculateCell(press.x, press.y)
            pressX = pressCoordinate.x
            pressY = pressCoordinate.y
        }

        override fun mouseEntered(enter: MouseEvent) {
        }

        override fun mouseReleased(release: MouseEvent) {
            val releaseCoordinate = calculateCell(release.x, release.y)
            releaseX = releaseCoordinate.x
            releaseY = releaseCoordinate.y
            if ((releaseX - pressX) == 0 && (releaseY - pressY) == 0) {
                clickCell(releaseX, releaseY, DrawBoard.board)
            } else {
                DrawBoard.centerX -= (releaseX - pressX)
                DrawBoard.centerY -= (releaseY - pressY)
            }
        }
    }
    val wheelListener = MouseWheelListener { e -> mouse(e.unitsToScroll) }
    val keyboardListener = object : KeyListener {
        override fun keyPressed(e: KeyEvent) {
            when (e.keyCode) {
                37 -> DrawBoard.centerX++
                38 -> DrawBoard.centerY++
                39 -> DrawBoard.centerX--
                40 -> DrawBoard.centerY--
            }
        }

        override fun keyReleased(e: KeyEvent) {
        }

        override fun keyTyped(e: KeyEvent) {
        }
    }
}

fun mouse(scroll: Int) {
    DrawBoard.scale = when {
        3 >= DrawBoard.scale + scroll -> 3
        DrawBoard.scale + scroll >= 1024 -> 1024
        else -> DrawBoard.scale + scroll
    }
}

data class Coordinates(val x: Int, val y: Int) {
}

fun calculateCell(x: Int, y: Int): Coordinates {
    val cellsDislocationX = round((x.toFloat() - DrawBoard.fieldWidth / 2) / Mouse.cellSize).toInt()
    val cellsDislocationY = round((y.toFloat() - DrawBoard.fieldHeight / 2) / Mouse.cellSize).toInt()
    return Coordinates(DrawBoard.centerX + cellsDislocationX, DrawBoard.centerY + cellsDislocationY)
}

fun clickCell(x: Int, y: Int, board: Field) {
    when (board.field[y][x].condition) {
        CONDITION.DEAD -> {
            board.field[y][x].condition = CONDITION.ALIVE; board.field[y][x].generation = 1
        }

        CONDITION.ALIVE -> {
            board.field[y][x].condition = CONDITION.DEAD; board.field[y][x].generation = 0
        }
    }
}
