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
        private const val buttonWidth = 125f
        var fieldWidth = windowWidth - buttonWidth
        var fieldHeight = windowHeight
    }

    fun createWindow() = runBlocking(Dispatchers.Swing) {
        val layer = SkiaLayer()
        val mouse = Mouse()
        val button = Button(board)
        layer.addMouseListener(mouse.mouseListener)
        layer.addMouseWheelListener(mouse.wheelListener)
        layer.addKeyListener(mouse.keyboardListener)
        SwingUtilities.invokeLater {
            val window = JFrame("Life").apply {
                defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                preferredSize = Dimension(1000, 720)
            }
            val gameField = JPanel(GridLayout(4, 1))
            val but1 = JButton("start/pause")
            val but2 = JButton("restart")
            val but3 = JButton("save position")
            val but4 = JButton("generate")
            but1.addActionListener(button.actionListener)
            gameField.add(but1)
            gameField.add(but2)
            gameField.add(but3)
            gameField.add(but4)
            //but1.addKeyListener                          TODO
            layer.attachTo(window.contentPane)
            window.contentPane.add(gameField, BorderLayout.EAST)
            layer.needRedraw()
            window.pack()
            window.isVisible = true
            window.isResizable = true
            fieldRender(layer)
            window.addComponentListener(componentListener)
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

    fun fieldRender(skiaLayer: SkiaLayer) {
        skiaLayer.skikoView = GenericSkikoView(skiaLayer, object : SkikoView {
            override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                val x = fieldWidth / 2
                val y = fieldHeight / 2
                val cellSize = fieldWidth / scale
                Mouse.cellSize = cellSize
                val rows = ceil(fieldHeight / cellSize).toInt()
                val columns = ceil(fieldWidth / cellSize).toInt()
                repeat(rows / 2 + 5) {
                    canvas.drawLine(
                        0f, y - cellSize / 2 - it * cellSize, fieldWidth, y - cellSize / 2 - it * cellSize,
                        Constants.black
                    )
                    canvas.drawLine(
                        0f, y + cellSize / 2 + it * cellSize, fieldWidth, y + cellSize / 2 + it * cellSize,
                        Constants.black
                    )
                }
                repeat(columns / 2 + 5) {
                    canvas.drawLine(
                        x + cellSize / 2 + it * cellSize, 0f, x + cellSize / 2 + it * cellSize, fieldHeight,
                        Constants.black
                    )
                    canvas.drawLine(
                        x - cellSize / 2 - it * cellSize, 0f, x - cellSize / 2 - it * cellSize, fieldHeight,
                        Constants.black
                    )
                }
                (centerX - columns / 2 ..centerX + columns / 2 ).forEach { x ->
                    (centerY - rows / 2 ..centerY + rows / 2 ).forEach { y ->
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
        }
        )
    }
}