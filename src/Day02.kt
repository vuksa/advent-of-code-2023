import kotlin.math.max

data class Game(val gameId: Int, val draws: List<Draw>) {
    companion object {
        fun from(input: String): Game {
            require(input.startsWith("Game "))

            val rowColumns = input.split(":")
            
            val gameId = rowColumns
                    .first()
                    .trim()
                    .split(" ")
                    .last()
                    .toInt()

            val draws = rowColumns
                    .last()
                    .trim()
                    .split(";")
                    .map { Draw.from(it) }

            return Game(gameId, draws)
        }
    }
}

data class Draw(val redCubesSum: Int, val blueCubesSum: Int, val greenCubesSum: Int) {
    fun allows(draw: Draw): Boolean {
        return draw.redCubesSum <= redCubesSum
               && draw.blueCubesSum <= blueCubesSum
               && draw.greenCubesSum <= greenCubesSum
    }

    companion object {
        enum class CubeColor(val value: String) {
            RED("red"), GREEN("green"), BLUE("blue")
        }

        // Here we pass only a single draw result - example: 3 blue, 4 red
        fun from(input: String): Draw {
            val cubes = input.split(", ")
            val redCubesSum = cubes.parseNumberOfCubesOfColor(CubeColor.RED)
            val blueCubesSum = cubes.parseNumberOfCubesOfColor(CubeColor.BLUE)
            val greenCubesSum = cubes.parseNumberOfCubesOfColor(CubeColor.GREEN)

            return Draw(redCubesSum, blueCubesSum, greenCubesSum)
        }

        private fun List<String>.parseNumberOfCubesOfColor(cubeColor: CubeColor): Int {
            val cubesOfColorString = this.find { it.contains(cubeColor.value, true) } ?: return 0

            return cubesOfColorString.trim().split(" ").first().toInt()
        }
    }
}


fun main() {
    fun part1(games: List<Game>, maxDraw: Draw): Int {
        return games
            .filter { it.draws.all { draw -> maxDraw.allows(draw) } }
            .sumOf { it.gameId }
    }

    fun part2(games: List<Game>): Int {
        return games.sumOf { game ->
            game.draws
                    .reduce { acc, draw ->
                        val maxRedCubes = max(acc.redCubesSum, draw.redCubesSum)
                        val maxBlueCubes = max(acc.blueCubesSum, draw.blueCubesSum)
                        val maxGreenCubes = max(acc.greenCubesSum, draw.greenCubesSum)

                        Draw(maxRedCubes, maxBlueCubes, maxGreenCubes)
                    }
                    .let { minimalDraw -> minimalDraw.redCubesSum * minimalDraw.greenCubesSum * minimalDraw.blueCubesSum }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test").map { Game.from(it) }
    check(part1(testInput, maxDraw = Draw(12, 14, 13)) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02").map { Game.from(it) }
    part1(input, Draw(12, 14, 13)).println()
    part2(input).println()
}