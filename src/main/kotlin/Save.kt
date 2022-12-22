import org.jetbrains.skiko.toBitmap
import java.awt.image.BufferedImage
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

interface Save{
    fun saveAs()
}

class SaveTxt: Save {
    override fun saveAs() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("txt files","txt")
        val openDialog = fileChooser.showOpenDialog(null)
        if (openDialog == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            val savedData = StringBuilder()
            selectedFile.createNewFile()
            savedData.append(Constants.birth.joinToString(" ") + "\n")
            savedData.append(Constants.stayAlive.joinToString(" ") + "\n")
            savedData.append(DrawBoard.scale.toString() + "\n")
            savedData.append(DrawBoard.centerX.toString() + "\n")
            savedData.append(DrawBoard.centerY.toString() + "\n")
            savedData.append(DrawBoard.board.toString())
            selectedFile.writeText(savedData.toString())
        }
    }
}
class SaveBmp: Save {
    override fun saveAs() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("bmp files","bmp")
        val openDialog = fileChooser.showOpenDialog(null)
        if (openDialog == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            val colorMap = mapOf(CONDITION.DEAD to 0xfffffff, CONDITION.ALIVE to 0x000000)
            val image = BufferedImage(Constants.size, Constants.size, BufferedImage.TYPE_INT_RGB)
            Constants.range.forEach { x ->
                Constants.range.forEach { y ->
                    image.setRGB(x-1,y-1,colorMap[DrawBoard.board.field[y][x].condition]?:throw
                    Exception("Problems with cell $y $x"))
                }
            }
            ImageIO.write(image, "BMP", selectedFile)
        }
    }
}
interface Load{
    fun loadAs()
}

class LoadTxt: Load {
    override fun loadAs() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Text files", "txt")
        val openDialog = fileChooser.showOpenDialog(null)
        if (openDialog == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            val dataInFile = selectedFile.readLines()
            Constants.birth = dataInFile[0].split(" ").mapNotNull { string -> string.toIntOrNull() }.toSet()
            Constants.stayAlive = dataInFile[1].split(" ").mapNotNull { string -> string.toIntOrNull() }.toSet()
            DrawBoard.scale = dataInFile[2].trim().toIntOrNull() ?: throw IllegalArgumentException()
            DrawBoard.centerX = dataInFile[3].trim().toIntOrNull() ?: throw IllegalArgumentException()
            DrawBoard.centerY = dataInFile[4].trim().toIntOrNull() ?: throw IllegalArgumentException()
            val intToCell = mapOf(1 to CONDITION.ALIVE, 0 to CONDITION.DEAD)
            repeat(Constants.size + 2) {
                DrawBoard.board.field[it] =
                    dataInFile[it + 5].split(" ").mapNotNull { string -> string.toIntOrNull() }.map { int ->
                        Cell(
                            intToCell[int]!!
                        )
                    }.toMutableList()
            }
        }
    }
}

class LoadBmp: Load {
    override fun loadAs() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Bitmap files", "bmp")
        val colorMap = mapOf(-1 to CONDITION.DEAD, -16777216 to CONDITION.ALIVE)    // here -1 and -16777216 are Ints for white and black, I have no idea why
        val openDialog = fileChooser.showOpenDialog(null)
        if (openDialog == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            val img = ImageIO.read(selectedFile).toBitmap()
            Constants.range.forEach { x->
                Constants.range.forEach { y->
                    DrawBoard.board.field[y][x].condition = colorMap[img.getColor(x-1,y-1)] ?: throw Exception("Wrong bitmap")
                }
            }
        }
    }
}
fun save() {
    val answer = JOptionPane.showOptionDialog(
        null, "How do you want to save the board?", "Save",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, arrayOf("Text file", "Bitmap file"), "Text file"
    )
    if (answer == 1) {
        val saver = SaveBmp()
        saver.saveAs()
    }
    if (answer == 0 ){
        val saver = SaveTxt()
        saver.saveAs()
    }
}
fun load() {
    val answer = JOptionPane.showOptionDialog(
        null, "How do you want to load the board?", "Load",
        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, arrayOf("Text file", "Bitmap file"), "Text file"
    )
    if (answer == 1) {
        val loader = LoadBmp()
        loader.loadAs()
    }
    if (answer == 0){
        val loader = LoadTxt()
        loader.loadAs()
    }
}