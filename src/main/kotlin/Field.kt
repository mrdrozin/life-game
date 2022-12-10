class Field {
    val field: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        repeat(Constants.size + 2) { row ->
            field.add(mutableListOf())
            repeat(Constants.size + 2) {
                field[row].add(Cell())
            }
        }
    }
    fun nextCondition(cell: Cell, x: Int, y: Int): CONDITION {
        val neighbours = listOf<Cell>(
            field[x-1][y-1],
            field[x][y-1],
            field[x+1][y-1],
            field[x-1][y],
            field[x+1][y],
            field[x-1][y+1],
            field[x][y+1],
            field[x+1][y+1],)
        val neighboursAlive = neighbours.filter { neighbour -> neighbour.condition == CONDITION.ALIVE }.size
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