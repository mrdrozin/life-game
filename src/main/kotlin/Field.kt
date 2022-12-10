class Field {

    var field: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        repeat(Constants.size + 2) { row ->
            field.add(mutableListOf())
            repeat(Constants.size + 2) {
                field[row].add(Cell())
            }
        }
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
        val newCell = Cell()
        newCell.condition = cell.condition
        newCell.generation = cell.generation
        if (cell.condition == CONDITION.ALIVE) {
            when (neighboursAlive) {
                in Constants.lonelyDeath -> {
                    newCell.generation = 0
                    newCell.condition = CONDITION.DEAD
                }

                in Constants.overpopulationDeath -> {
                    newCell.generation = 0
                    newCell.condition = CONDITION.DEAD
                }

                else -> {
                    newCell.generation++
                    newCell.condition = CONDITION.ALIVE
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

}