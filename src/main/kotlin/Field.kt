import java.lang.StringBuilder

class Field {
    var field: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        repeat(Constants.size + 2) { row ->
            field.add(mutableListOf())
            repeat(Constants.size + 2) {
                field[row].add(Cell(CONDITION.DEAD))
            }
        }
    }

    override fun toString(): String {
        val cellToString = mapOf<CONDITION, Int>(CONDITION.ALIVE to 1, CONDITION.DEAD to 0)
        val string = StringBuilder()
        this.field.forEach { row ->
            string.append(row.map { cell -> cellToString[cell.condition]}.joinToString (" ") + "\n")
        }
        return string.toString()
    }
    private fun nextCondition(x: Int, y: Int): Cell {
        val cell = field[y][x]
        val neighbours = listOf(
            field[y - 1][x - 1],
            field[y][x - 1],
            field[y + 1][x - 1],
            field[y - 1][x],
            field[y + 1][x],
            field[y - 1][x + 1],
            field[y][x + 1],
            field[y + 1][x + 1]
        )
        val neighboursAlive = neighbours.count { neighbour -> neighbour.condition == CONDITION.ALIVE }
        val newCell = Cell(CONDITION.DEAD)
        newCell.condition = cell.condition
        newCell.generation = cell.generation
        if (cell.condition == CONDITION.ALIVE) {
            when (neighboursAlive) {
                in Constants.stayAlive -> {
                    newCell.generation++
                    newCell.condition = CONDITION.ALIVE
                }
                else -> {
                    newCell.generation = 0
                    newCell.condition = CONDITION.DEAD
                }
            }
            return newCell
        }
        if (neighboursAlive in Constants.birth) {
            newCell.generation++
            newCell.condition = CONDITION.ALIVE
        }
        return newCell
    }

    fun nextBoard(){
        val newBoard = Field()
        Constants.range.forEach { y ->
            Constants.range.forEach { x ->
                newBoard.field[y][x] = this.nextCondition(x, y)
            }
        }
        this.field = newBoard.field
    }
    fun generate(){
        Constants.range.forEach { y ->
            Constants.range.forEach { x ->
                val a = (1..100).random()
                if (a <= Constants.probability*100){
                    field[y][x].condition = CONDITION.ALIVE
                    field[y][x].generation = 1
                } else {
                    field[y][x] = Cell(CONDITION.DEAD)
                }
            }
        }
    }
}