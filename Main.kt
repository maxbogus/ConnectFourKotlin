package connectfour

val CORRECT_BOARD_SIZE_REGEX = Regex("(^\\s*[5-9]\\s*[xX]\\s*[5-9]\\s*$)")
val INVALID_ROW_BOARD_REGEX = Regex("(^\\s*\\d+\\s*[xX]\\s*[5-9]\\s*\$)")
val INVALID_COLUMN_BOARD_REGEX = Regex("(^\\s*[5-9]\\s*[xX]\\s*\\d+\\s*\$)")

fun main() {
    println("Connect Four")
    val (rows, columns) = drawBoard()
    drawBoard(rows, columns)
}

private fun drawBoard(rows: Int, columns: Int) {
    for (row in 0..rows) {
        when (row) {
            0 -> {
                println("start".repeat(columns))
            }
            rows -> {
                println("end".repeat(columns))
            }
            else -> {
                println("middle".repeat(columns))
            }
        }
    }
}

private fun drawBoard(): Pair<Int, Int> {
    println("First player's name:")
    val firstPlayerName = readLine()!!
    println("Second player's name:")
    val secondPlayerName = readLine()!!
    var rows = 6
    var columns = 7
    var errorMessage = ""
    do {
        showBoardDemensionsMessage(errorMessage)
        val userInput = readLine()!!
        if (!userInput.matches(CORRECT_BOARD_SIZE_REGEX)) {
            errorMessage = getErrorMessage(userInput)
        } else {
            val list = userInput.filter { it.isDigit() }
            rows = list.first().toInt()
            columns = list.last().toInt()
        }
    } while (userInput != "" && !userInput.matches(CORRECT_BOARD_SIZE_REGEX))
    println("$firstPlayerName VS $secondPlayerName")
    println("$rows X $columns board")
    return Pair(rows, columns)
}

private fun showBoardDemensionsMessage(errorMessage: String) {
    if (errorMessage != "") {
        println(errorMessage)
    }
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
}

private fun getErrorMessage(string: String): String {
    return if (string.matches(INVALID_ROW_BOARD_REGEX)) {
        "Board rows should be from 5 to 9"
    } else if (string.matches(INVALID_COLUMN_BOARD_REGEX)) {
        "Board columns should be from 5 to 9"
    } else {
        "Invalid input"
    }
}
