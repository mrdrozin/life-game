class Field {
    val field: MutableList<MutableList<Cell>>

    init {
        val constants = Constants()
        field = mutableListOf()
        repeat(constants.SIZE + 2) { field.add(mutableListOf()) }
        repeat(constants.SIZE + 2) { field[it].add(Cell(condition.DEAD)) }
        constants.RANGE.forEach { row ->
            constants.RANGE.forEach { column ->
                field[row][column].neighbours = listOf(
                    field[row - 1][column - 1],
                    field[row - 1][column],
                    field[row - 1][column + 1],
                    field[row][column - 1],
                    field[row][column + 1],
                    field[row + 1][column - 1],
                    field[row + 1][column],
                    field[row + 1][column + 1],
                )
            }
        }
    }
    fun nextCondition(cell: Cell): condition {
        return when {
            (cell.condition == condition.DEAD && cell.neighbours.filter { it.condition == condition.ALIVE }.size == 3) ||
                    (cell.condition == condition.DEAD &&
                            (cell.neighbours.filter { it.condition == condition.ALIVE }.size == 3 ||
                                    cell.neighbours.filter { it.condition == condition.ALIVE }.size == 2)) ->
                condition.ALIVE

            else -> condition.DEAD
        }
    }
}