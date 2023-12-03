private class SchemaBoard(input: List<String>) {
    private val schemaWidth = input.first().count()
    private val schemaHeight = input.count()

    private val schema: Map<Int, List<SchemaElement>> = input.mapIndexed { row, lineContent -> row to parseSchemaElementsOfRow(lineContent, row, schemaHeight, schemaWidth) }
            .toMap()

    fun getElement(row: Int, column: Int): SchemaElement? = schema.getValue(row).find { schemaElement -> schemaElement.containsColumnIndex(column) }
    
    fun getAdjacentElementsOf(schemaElement: SchemaElement): List<SchemaElement> {
        return calculateAdjacentCoordintesOfElement(schemaElement)
            .mapNotNull { (row, column) -> getElement(row, column) }
            .distinct()
    }

    fun calculateMissingPartNumber(): Int = schema.values.flatten()
            .filterIsInstance<NumberElement>()
            .sumOf { schemaElement ->
                schemaElement.getValueIfAdjacentElseZero(this) { adjacentElements -> adjacentElements.any { it.isSymbol() } }
            }

    fun calculateGearRatioNumber(): Int = schema.values.flatten()
            .filterIsInstance<SymbolElement>()
            .sumOf { schemaElement ->
                schemaElement.getValueIfAdjacentElseZero(this) { adjacentNumbers -> adjacentNumbers.count() == 2 }
            }

    private fun parseSchemaElementsOfRow(line: String, row: Int, schemaHeight: Int, schemaWidth: Int): List<SchemaElement> {
        return buildList<SchemaElement> {
            var processingNumber = false

            for ((index, character) in line.withIndex()) {
                if (character.isDigit()) {
                    if (processingNumber) {
                        continue
                    }

                    processingNumber = true

                    val digitSequence = line
                            .substring(index)
                            .takeWhile { it.isDigit() }

                    val number = NumberElement(digitSequence, row, index, index + digitSequence.count(), schemaHeight, schemaWidth)

                    add(number)
                } else if (character != '.') {
                    processingNumber = false

                    add(SymbolElement(character.toString(), row, index, schemaHeight, schemaWidth))
                } else {
                    processingNumber = false

                    add(DotElement(row, index, schemaHeight, schemaWidth))
                }
            }
        }
    }
    
    private fun calculateAdjacentCoordintesOfElement(schemaElement: SchemaElement): List<Pair<Int, Int>> {
        val row = schemaElement.row
        val startIndexInclusive = schemaElement.startColumnIndexInclusive
        val endIndexExclusive = schemaElement.endColumnIndexExclusive
        
        return buildList<Pair<Int, Int>> {
            ((row - 1)..(row + 1)).forEach { rowIndex ->
                ((startIndexInclusive - 1)..endIndexExclusive).forEach { columnIndex ->
                    if (rowIndex > -1 && rowIndex < schemaHeight && columnIndex > -1 && columnIndex < schemaWidth) {
                        add(rowIndex to columnIndex)
                    }
                }
            }
        }
    }
}

private sealed class SchemaElement(
        open val value: String,
        open val row: Int,
        open val startColumnIndexInclusive: Int,
        open val endColumnIndexExclusive: Int,
        open val schemaHeight: Int,
        open val schemaWidth: Int
) {
    abstract fun getValueIfAdjacentElseZero(schemaBoard: SchemaBoard, isAdjacent: (List<SchemaElement>) -> Boolean): Int

    fun containsColumnIndex(index: Int): Boolean = index in startColumnIndexInclusive..<endColumnIndexExclusive

    fun isSymbol(): Boolean = this is SymbolElement

    fun isNumber(): Boolean = this is NumberElement
}

private data class DotElement(
        override val row: Int,
        val index: Int,
        override val schemaHeight: Int,
        override val schemaWidth: Int
) : SchemaElement(".", row, index, index + 1, schemaHeight, schemaWidth) {
    override fun getValueIfAdjacentElseZero(schemaBoard: SchemaBoard, isAdjacent: (List<SchemaElement>) -> Boolean): Int = 0
}

private data class SymbolElement(
        override val value: String,
        override val row: Int,
        private val column: Int,
        override val schemaHeight: Int,
        override val schemaWidth: Int
) : SchemaElement(value, row, column, column + 1, schemaHeight, schemaWidth) {
    /**
     * Returns value of gear ratio if symbol is adjacent to exactly two numbers.
     * Gear ratio is result of multiplication of those two adjacent numbers
     */
    override fun getValueIfAdjacentElseZero(schemaBoard: SchemaBoard, isAdjacent: (List<SchemaElement>) -> Boolean): Int {
        if (value != "*") return 0
        
        val adjacentNumbers = schemaBoard.getAdjacentElementsOf(this)
            .filterIsInstance<NumberElement>()

        return when {
            isAdjacent(adjacentNumbers) -> adjacentNumbers[0].value.toInt() * adjacentNumbers[1].value.toInt()
            else -> return 0
        }
    }
}

private data class NumberElement(
        override val value: String,
        override val row: Int,
        override val startColumnIndexInclusive: Int,
        override val endColumnIndexExclusive: Int,
        override val schemaHeight: Int,
        override val schemaWidth: Int,
) : SchemaElement(value, row, startColumnIndexInclusive, endColumnIndexExclusive, schemaHeight, schemaWidth) {

    /**
     * Returns value of a number, if it is adjacent to symbol, else returns 0
     */
    override fun getValueIfAdjacentElseZero(schemaBoard: SchemaBoard, isAdjacent: (List<SchemaElement>) -> Boolean): Int {
        return if (isAdjacent(schemaBoard.getAdjacentElementsOf(this))) return value.toInt() else 0
    }
}

fun main() {
    fun part1(schemaBoard: SchemaBoard): Int = schemaBoard.calculateMissingPartNumber()

    fun part2(schemaBoard: SchemaBoard): Int = schemaBoard.calculateGearRatioNumber()

    // test if implementation meets criteria from the description, like:
    val testInput = SchemaBoard(readInput("Day03_test"))
    val part1 = part1(testInput)
    val part2 = part2(testInput).also { println(it) }
    check(part1 == 4361)
    check(part2 == 467835)


    val input = SchemaBoard(readInput("Day03"))
    val part1Result = part1(input).also { it.println() }
    check(part1Result == 520019)
    val part2Result = part2(input).also { it.println() }
    check(part2Result == 75519888)
}