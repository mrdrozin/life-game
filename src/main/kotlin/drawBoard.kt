import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import kotlin.math.ceil

class DrawBoard {
    companion object {
        var board = Field()
        var scale = 20
        var centerX = Constants.size / 2
        var centerY = Constants.size / 2
        var windowWidth = 1000f
        var windowHeight = 720f
        private const val buttonWidth = 110f
        var fieldWidth = windowWidth - buttonWidth
        var fieldHeight = windowHeight
        var generation = 0

    }

    fun createWindow() = runBlocking(Dispatchers.Swing) {
        val layer = SkiaLayer()
        val listeners = Listeners()
        val button = Button(board)
        layer.addMouseListener(listeners.mouseListener)
        layer.addMouseWheelListener(listeners.wheelListener)
        layer.addKeyListener(listeners.keyboardListener)
        SwingUtilities.invokeLater {
            val window = JFrame("Life").apply {
                defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
                preferredSize = Dimension(1000, 720)
            }
            val rightPanel = JPanel(GridLayout(4, 1))
            val bottomPanel = JPanel(GridLayout(1, 1))
            button.addButtons(rightPanel, bottomPanel)
            layer.attachTo(window.contentPane)
            window.contentPane.add(rightPanel, BorderLayout.EAST)
            window.contentPane.add(bottomPanel, BorderLayout.NORTH)
            layer.needRedraw()
            window.pack()
            window.isVisible = true
            window.isResizable = true
            fieldRender(layer)
            window.addComponentListener(componentListener)
            window.addWindowListener(listeners.windowListener)
        }


    }



    private val componentListener = object : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent) {
            windowWidth = e.component.width.toFloat()
            windowHeight = e.component.height.toFloat()
            fieldWidth = windowWidth - buttonWidth
            fieldHeight = windowHeight

        }
    }



    private fun drawCells(canvas: Canvas, columns: Int, rows: Int, cellSize: Float) {
        (centerX - columns / 2..centerX + columns / 2).forEach { x ->
            (centerY - rows / 2..centerY + rows / 2).forEach { y ->
                if (board.field[y][x].condition == CONDITION.ALIVE) {
                    canvas.drawRect(
                        Rect.makeXYWH(
                            fieldWidth / 2 - cellSize / 2 - (centerX - x) * cellSize,
                            fieldHeight / 2 - cellSize / 2 - (centerY - y) * cellSize,
                            cellSize,
                            cellSize
                        ),
                        Constants.black
                    )
                }
            }
        }
    }
    private fun drawColumns(canvas: Canvas, columns: Int, cellSize: Float){
        repeat(columns / 2 + 5) {
            canvas.drawLine(
                fieldWidth / 2 + cellSize / 2 + it * cellSize, 0f,
                fieldWidth / 2 + cellSize / 2 + it * cellSize, fieldHeight,
                Constants.black
            )
            canvas.drawLine(
                fieldWidth / 2 - cellSize / 2 - it * cellSize, 0f,
                fieldWidth / 2 - cellSize / 2 - it * cellSize, fieldHeight,
                Constants.black
            )
        }
    }
    private fun drawRows(canvas: Canvas, rows: Int, cellSize: Float){
        repeat(rows / 2 + 5) {
            canvas.drawLine(
                0f, fieldHeight / 2 - cellSize / 2 - it * cellSize, fieldWidth,
                fieldHeight / 2 - cellSize / 2 - it * cellSize,
                Constants.black
            )
            canvas.drawLine(
                0f, fieldHeight / 2 + cellSize / 2 + it * cellSize,
                fieldWidth, fieldHeight / 2 + cellSize / 2 + it * cellSize,
                Constants.black
            )
        }
    }
    private fun fieldRender(skiaLayer: SkiaLayer) {
        skiaLayer.skikoView = GenericSkikoView(skiaLayer, object : SkikoView {
            override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                val cellSize = fieldWidth / scale
                Listeners.cellSize = cellSize
                val rows = ceil(fieldHeight / cellSize).toInt()
                val columns = ceil(fieldWidth / cellSize).toInt()
                drawRows(canvas, rows, cellSize)
                drawColumns(canvas, columns, cellSize)
                drawCells(canvas, columns, rows, cellSize)
            }
        }
        )
    }
}