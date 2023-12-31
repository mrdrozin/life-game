
import Listeners.Companion.StateX
import Listeners.Companion.StateY
import Listeners.Companion.active
import java.awt.event.*
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.SwingConstants
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.system.exitProcess

class Listeners {
    companion object {
        var cellSize = DrawBoard.fieldWidth / DrawBoard.scale
        var active = false
        var numberOfMoves : Int = 0
        var StateX : Int = 0
        var StateY : Int = 0
    }

    var pressX = 0
    var pressY = 0
    var releaseX = 0
    var releaseY = 0
    val mouseListener = object : MouseListener {
        override fun mouseClicked(click: MouseEvent) {
        }
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
                    DrawBoard.centerX - (releaseX - pressX) < DrawBoard.scale / 2 ->
                        DrawBoard.scale / 2

                    DrawBoard.centerX - (releaseX - pressX) > Constants.size - DrawBoard.scale / 2 ->
                        Constants.size - DrawBoard.scale / 2

                    else -> DrawBoard.centerX - (releaseX - pressX)
                }
                DrawBoard.centerY = when {
                    DrawBoard.centerY - (releaseY - pressY) < DrawBoard.scale / 2 ->
                        DrawBoard.scale / 2

                    DrawBoard.centerY - (releaseY - pressY) > Constants.size - DrawBoard.scale / 2 ->
                        Constants.size - DrawBoard.scale / 2

                    else -> DrawBoard.centerY - (releaseY - pressY)
                }
            }
        }
    }
    val wheelListener = MouseWheelListener { e -> mouse(e.unitsToScroll) }
    val keyboardListener = object : KeyListener {
        override fun keyPressed(e: KeyEvent) {
            when (e.keyCode) {
                37 -> {
                    if (DrawBoard.centerX > DrawBoard.scale/2) StateX-=1
                    DrawBoard.centerX = max(DrawBoard.centerX - 1, DrawBoard.scale / 2)
                }

                38 -> {
                    if (DrawBoard.centerY > DrawBoard.scale/2) StateY-=1
                    DrawBoard.centerY = max(DrawBoard.centerY - 1, DrawBoard.scale / 2)
                }
                39 -> {
                    if (DrawBoard.centerX < Constants.size - DrawBoard.scale / 2) StateX+=1
                    DrawBoard.centerX = min(DrawBoard.centerX + 1, Constants.size - DrawBoard.scale / 2)
                }
                40 -> {
                    if (DrawBoard.centerY < Constants.size - DrawBoard.scale / 2) StateY+=1
                    DrawBoard.centerY = min(DrawBoard.centerY + 1, Constants.size - DrawBoard.scale / 2)
                }

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
                load()
            }
        }

        override fun windowClosing(e: WindowEvent) {
            val answer = JOptionPane.showOptionDialog(
                null, "Do you want to save this board?", "Save",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, arrayOf("Yes", "No"), "No"
            )
            if (answer == 0) {
                save()
                exitProcess(0)
            }
            if (answer == 1) {
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
    val mouseMotionListener = object : MouseMotionListener{
        override fun mouseDragged(e: MouseEvent?) {
        }

        override fun mouseMoved(e: MouseEvent) {
            StateX = calculateCell(e.x, e.y).x
            StateY = calculateCell(e.x, e.y).y
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

class SettingsMenu {
    fun setSettings() {
        val new = JFrame()
        val pane = JTabbedPane()
        val birth = JPanel()
        val stayAlive = JPanel()
        val birthBoxes = mutableListOf<JCheckBox>()
        val stayAliveBoxes = mutableListOf<JCheckBox>()
        birth.add(JLabel("how many for birth?"))
        stayAlive.add(JLabel("How many neighbors alive cell needs to stay alive?"))
        (0..8).forEach {
            birthBoxes.add(JCheckBox("$it"))
            stayAliveBoxes.add(JCheckBox("$it"))
        }
        Constants.birth.forEach { birthBoxes[it].isSelected = true }
        Constants.stayAlive.forEach { stayAliveBoxes[it].isSelected = true }
        pane.addTab("Birth", birth)
        pane.addTab("Stay alive", stayAlive)
        birthBoxes.forEach { birth.add(it) }
        stayAliveBoxes.forEach { stayAlive.add(it) }
        val birthSubmit = JButton("Submit")
        val stayAliveSubmit = JButton("Submit")
        val listener = ActionListener {
            val birthSet = mutableSetOf<Int>()
            val staySet = mutableSetOf<Int>()
            (0..8).forEach {
                if (birthBoxes[it].isSelected) birthSet.add(it)
                if (stayAliveBoxes[it].isSelected) staySet.add(it)
            }
            Constants.birth = birthSet
            Constants.stayAlive = staySet
            new.dispose()
        }
        birthSubmit.addActionListener(listener)
        stayAliveSubmit.addActionListener(listener)
        birth.add(birthSubmit)
        stayAlive.add(stayAliveSubmit)
        new.contentPane.add(pane)
        new.isVisible = true
        new.setLocation(200, 200)
        new.isResizable = false
        new.pack()
    }
    fun makeInputWindow() {
        val answer = JOptionPane.showInputDialog("How many moves you want to simulate?")
        if (answer!=null){
            val processed = answer.trim().toIntOrNull()
            if (processed!=null){
                active = false
                Listeners.numberOfMoves = processed
                DrawBoard.board.playNMoves()
            }
            if (processed == null){
                JOptionPane.showOptionDialog(
                    null, "Oops something went wrong in your input!", "Warning",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, arrayOf("Ok"), "Ok"
                )
            }
        }

    }
    fun cellGeneration(){
        fixedRateTimer(name = "timer",
            initialDelay = 0, period = 1,daemon = true) {
            DrawBoard.generation = DrawBoard.board.field[StateY][StateX].generation
            Button.label.text = "Cell ($StateX,$StateY) generation: ${DrawBoard.generation}"
        }
    }
}


class Button(field: Field) {
    companion object{
        val label = JLabel("Cell generation: ${DrawBoard.generation}", SwingConstants.CENTER)
    }
    private val actionListenerStart = ActionListener {
        if (!active){
            if (Listeners.numberOfMoves>0) Listeners.numberOfMoves = 0
            active = true
            DrawBoard.board.playGame()
        } else {
            active = false
        }
    }
    private val actionListenerGenerate = ActionListener {
        field.generate()
    }
    private val actionListenerSave = ActionListener {
        save()
    }
    private val actionListenerLoad = ActionListener {
        load()
    }
    private val settings = SettingsMenu()
    private val listeners = Listeners()
    fun addEastButtons(eastPanel: JPanel) {
        val buttonStart = JButton("Start/Pause")
        val buttonSave = JButton("Save board")
        val buttonLoad = JButton("Load board")
        val buttonGenerate = JButton("Generate")
        val eastButtons = listOf(buttonStart, buttonLoad,  buttonSave, buttonGenerate)
        buttonStart.addActionListener(actionListenerStart)
        buttonGenerate.addActionListener(actionListenerGenerate)
        buttonSave.addActionListener(actionListenerSave)
        buttonLoad.addActionListener(actionListenerLoad)
        buttonGenerate.addKeyListener(listeners.keyboardListener)
        eastButtons.forEach { it.addKeyListener(listeners.keyboardListener); eastPanel.add(it) }

    }
    fun addSouthButtons(northPanel: JPanel){
        val buttonSettings = JButton("Settings")
        val buttonClear = JButton("Clear")
        val buttonNMoves = JButton("Play N moves")
        buttonSettings.addActionListener { settings.setSettings() }
        buttonClear.addActionListener{DrawBoard.board.clear()}
        buttonNMoves.addActionListener{ settings.makeInputWindow() }
        val northButtons = listOf(buttonSettings, buttonNMoves, buttonClear)
        northButtons.forEach { it.addKeyListener(listeners.keyboardListener); northPanel.add(it) }
        settings.cellGeneration()
        northPanel.add(label)
    }
}