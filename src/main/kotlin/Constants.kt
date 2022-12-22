class Constants {
    companion object{
        val black = org.jetbrains.skia.Paint().apply {
            color = 0xFF000000.toInt()
        }
        const val size = 1024
        val range = (1..size)
        var birth = setOf<Int>(3)
        var stayAlive = setOf<Int>(2,3)
        var probability = 0.3
    }
}
enum class CONDITION {ALIVE, DEAD}