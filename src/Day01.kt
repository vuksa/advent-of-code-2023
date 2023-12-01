fun main() {
    fun extractDigits(digits: String): Int {
        require(digits.isNotEmpty()) { "Corrupted calibration input: $digits." }
        val digitSelection = when (digits.length) {
            1 -> "${digits.first()}${digits.first()}"
            else -> "${digits.first()}${digits.last()}"
        }
        return digitSelection.toInt()
    }

    fun convertToNumbersOnlySequence(letterNumberToDigitNumber: Map<String, String>, line: String): String {
        val normalizedLine = StringBuilder()

        var index = 0

        while (index <= line.lastIndex) {
            val character = line[index]
            if (character.isDigit()) {
                // Appends digit number representation
                normalizedLine.append(character)
            } else {
                for ((numberAsLetters, numberAsDigit) in letterNumberToDigitNumber) {
                    // Lookup for occurences of number letter representation in next line substring [index, index + length of number letter representation]
                    val windowStartIndex = index
                    val windowEndIndex = index + numberAsLetters.lastIndex
                    if (windowEndIndex <= line.lastIndex) {
                        val substring = line.substring(windowStartIndex, windowEndIndex + 1)
                        if (substring.contains(numberAsLetters)) {
                            // Replaces number letter representation with digit one - example `one` => 1
                            normalizedLine.append(numberAsDigit)
                            break
                        }
                    }
                }
            }

            index++
        }

        return normalizedLine.toString()
    }

    fun part1(input: List<String>): Int = input.sumOf { line -> extractDigits(line.filter { it.isDigit() }) }

    fun part2(input: List<String>): Int {
        val letterNumberToDigitNumber = mapOf(
                "one" to "1",
                "two" to "2",
                "three" to "3",
                "four" to "4",
                "five" to "5",
                "six" to "6",
                "seven" to "7",
                "eight" to "8",
                "nine" to "9"
        )

        return part1(input.map { line -> convertToNumbersOnlySequence(letterNumberToDigitNumber, line) })
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("Day01_part1_test")
    check(part1(testInputPart1) == 142)

    val testInputPart2 = readInput("Day01_part2_test")
    check(part2(testInputPart2) == 281)

    val inputPart1 = readInput("Day01")
    part1(inputPart1).println()

    val inputPart2 = readInput("Day01")
    part2(inputPart1).println()
    part2(inputPart2).println()
}
