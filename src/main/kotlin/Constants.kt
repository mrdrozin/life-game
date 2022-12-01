class Constants {

    companion object{
        val black = org.jetbrains.skia.Paint().apply {
            color = 0xFF000000.toInt()
        }
        val white = org.jetbrains.skia.Paint().apply {
            color = 0xFFFFFFFF.toInt()
        }
        const val size = 20
        val range = (1..size)
        var birth = (3 until 4)
        var lonelyDeath = (0 until 2)
        var overpopulationDeath = (4 until 9)
    }

}
enum class CONDITION {ALIVE, DEAD}