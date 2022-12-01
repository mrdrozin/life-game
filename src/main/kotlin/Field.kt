class Field {
    val field: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        repeat(Constants.size + 2) { field.add(mutableListOf()) }
        repeat(Constants.size + 2) { field[it].add(Cell()) }
        Constants.range.forEach { row ->
            Constants.range.forEach { column ->
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
    fun nextCondition(cell: Cell): CONDITION {
        val neighboursAlive = cell.neighbours.filter { neighbour -> neighbour.condition == CONDITION.ALIVE }.size
        if (cell.condition == CONDITION.ALIVE){
            return when(neighboursAlive){
                in Constants.lonelyDeath -> {cell.generation = 0; CONDITION.DEAD}
                in Constants.overpopulationDeath -> {cell.generation = 0; CONDITION.DEAD}
                else -> {cell.generation++; CONDITION.ALIVE}
            }
        }
        if (neighboursAlive in Constants.birth) {cell.generation++; return CONDITION.ALIVE}
        return CONDITION.DEAD
    }
}