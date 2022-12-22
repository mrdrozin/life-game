import java.awt.event.*
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.system.exitProcess

class Listeners {
    companion object {
        var cellSize = DrawBoard.fieldWidth / DrawBoard.scale
    }

    var pressX = 0
    var pressY = 0
    var releaseX = 0
    var releaseY = 0
    val mouseListener = object : MouseListener {
        override fun mouseClicked(click: MouseEvent) {}
        override fun mouseExited(exit: MouseEvent) {}
        override fun mousePressed(press: MouseEvent) {
            val pressCoordinate = calculateCell(press.x, press.y)
            pressX = pressCoordinate.x
            pressY = pressCoordinate.y
        }
        override fun mouseEntered(enter: MouseEvent) {}
        override fun mouseReleased(release: MouseEvent) {
            val releaseCoordinate = calculateCell(release.x, release.y)
            releaseX = releaseCoordinate.x
            releaseY = releaseCoordinate.y
            if ((releaseX - pressX) == 0 && (releaseY - pressY) == 0) {
                clickCell(releaseX, releaseY, DrawBoard.board)
            } else {
                DrawBoard.centerX = when {
                    DrawBoard.centerX - (releaseX - pressX) < DrawBoard.scale/2 ->
                        DrawBoard.scale/2
                    DrawBoard.centerX - (releaseX - pressX) > Constants.size -  DrawBoard.scale/2 ->
                        Constants.size -  DrawBoard.scale/2
                    else -> DrawBoard.centerX - (releaseX - pressX)
                }
                DrawBoard.centerY = when {
                    DrawBoard.centerY - (releaseY - pressY) < DrawBoard.scale/2 ->
                        DrawBoard.scale/2
                    DrawBoard.centerY - (releaseY - pressY) > Constants.size -  DrawBoard.scale/2 ->
                        Constants.size -  DrawBoard.scale/2
                    else -> DrawBoard.centerY - (releaseY - pressY)
                }
            }
        }
    }
    val wheelListener = MouseWheelListener { e -> mouse(e.unitsToScroll) }
    val keyboardListener = object : KeyListener {
        override fun keyPressed(e: KeyEvent) {
            when (e.keyCode) {
                37 -> DrawBoard.centerX = min(DrawBoard.centerX+1, Constants.size-DrawBoard.scale/2)
                38 -> DrawBoard.centerY = min(DrawBoard.centerY+1, Constants.size-DrawBoard.scale/2)
                39 -> DrawBoard.centerX = max(DrawBoard.centerX-1, DrawBoard.scale/2)
                40 -> DrawBoard.centerY = max(DrawBoard.centerY-1, DrawBoard.scale/2)
            }
        }

        override fun keyReleased(e: KeyEvent) {
        }

        override fun keyTyped(e: KeyEvent) {
        }
    }
    val windowListener = object : WindowListener {
        override fun windowOpened(e: WindowEvent?) {
            val answer = JOptionPane.showOptionDialog(
                null, "Do you want to load board?", "Load",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, arrayOf("Yes", "No"), "No"
            )
            if (answer == 0) {
                val saver = SaveTxt()
                saver.saveAs()
            }
        }

        override fun windowClosing(e: WindowEvent) {

            val answer = JOptionPane.showOptionDialog(
                null, "Do you want to save this board?", "Exit",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, arrayOf("Yes", "No"), "No"
            )
            if (answer == 1) {
                exitProcess(0)
            } else {
                val saver = SaveTxt()
                saver.saveAs()
                exitProcess(0)
            }
        }

        override fun windowClosed(e: WindowEvent?) {

        }

        override fun windowIconified(e: WindowEvent?) {
        }

        override fun windowDeiconified(e: WindowEvent?) {
        }

        override fun windowActivated(e: WindowEvent?) {
        }

        override fun windowDeactivated(e: WindowEvent?) {
        }
    }
}

fun mouse(scroll: Int) {
    DrawBoard.scale = when {
        3 >= DrawBoard.scale + scroll -> 3
        DrawBoard.scale + scroll >= Constants.size -> Constants.size
        else -> DrawBoard.scale + scroll
    }
}

data class Coordinates(val x: Int, val y: Int)

fun calculateCell(x: Int, y: Int): Coordinates {
    val cellsDislocationX = round((x.toFloat() - DrawBoard.fieldWidth / 2) / Listeners.cellSize).toInt()
    val cellsDislocationY = round((y.toFloat() - DrawBoard.fieldHeight / 2) / Listeners.cellSize).toInt()
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

class Button(field: Field) {
    private val actionListenerStart = ActionListener {
        field.nextBoard()
    }
    private val actionListenerGenerate = ActionListener {
        field.generate()
    }
    private val actionListenerSave = ActionListener {
        val saver = SaveBmp()
        saver.saveAs()
    }
    private val actionListenerLoad = ActionListener {
        val loader = LoadBmp()
        loader.loadAs()
    }

    fun addButtons(panel: JPanel) {
        val a = Listeners()
        val but1 = JButton("Start/Pause")
        val but2 = JButton("Save board")
        val but3 = JButton("Load board")
        val but4 = JButton("generate")
        but1.addActionListener(actionListenerStart)
        but4.addActionListener(actionListenerGenerate)
        but2.addActionListener(actionListenerSave)
        but3.addActionListener(actionListenerLoad)
        but4.addKeyListener(a.keyboardListener)
        panel.add(but1)
        panel.add(but2)
        panel.add(but3)
        panel.add(but4)
    }
}