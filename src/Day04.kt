import kotlin.math.pow

fun main() {
    data class ScratchCard(val cardId: Int, val winningNumbers: Set<Int>, val scratchCardNumbers: Set<Int>) {
        fun matchedWinningNumbersCount(): Int = winningNumbers.intersect(scratchCardNumbers).count()
    }

    fun parseCards(input: List<String>): List<ScratchCard> {
        return input.map { line ->
            val regex = Regex("""^Card\s*(\d+):""")
            val cardId = regex.find(line)!!.groups[1]!!.value.toInt()
            
            val (winningNumbersString, scratchCardNumbersString) = line.replace(regex, "")
                    .trim()
                    .split(" | ")

            val parseNumbers: (String) -> Set<Int> =  { text -> text.trim().split(" ")
                .filter { it.isNotBlank() }
                .map{ it.trim().toInt() }
                .toSet()
            }
            
            val winningNumbers = parseNumbers(winningNumbersString)
            val cardNumbers = parseNumbers(scratchCardNumbersString)

            ScratchCard(cardId, winningNumbers, cardNumbers)
        }
    }

    fun part1(input: List<String>): Int {
        return parseCards(input)
                .sumOf { (2.0.pow(it.matchedWinningNumbersCount() - 1)).toInt() }
    }

    fun part2(input: List<String>): Int {
        val cards = parseCards(input)
        val cardResultCache = cards.associate { it.cardId to ((it.cardId + 1)..(it.cardId + it.matchedWinningNumbersCount())).toList() }
        val unprocessedCardIds = cards.map { it.cardId }.toMutableList()
        
        var index = 0
        var numOfScratchCards = 0
        while (unprocessedCardIds.lastIndex >= index) {
            val cardId = unprocessedCardIds[index]
            unprocessedCardIds.addAll(cardResultCache.getValue(cardId))
            index++
            numOfScratchCards++
        }

        return numOfScratchCards
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    check(part1(input) == 21919)
    part2(input).println()
    check(part2(input) == 9881048)
}