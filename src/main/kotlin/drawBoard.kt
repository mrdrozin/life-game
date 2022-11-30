import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import org.jetbrains.skia.makeFromFile
import org.jetbrains.skija.Typeface
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

class drawBoard {
    fun createWindow() = runBlocking(Dispatchers.Swing){
        val layer = SkiaLayer()
        SwingUtilities.invokeLater {
            val window = JFrame("Life").apply {
                defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
                preferredSize = Dimension(800, 800)
            }
            layer.attachTo(window.contentPane)
            layer.needRedraw()
            window.pack()
            window.isVisible = true
            window.isResizable = false
        }

    }
}