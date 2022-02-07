package connectfour

import java.lang.Exception

val CORRECT_BOARD_SIZE_REGEX = Regex("(^\\s*[5-9]{1}\\s*[xX]\\s*[5-9]{1}\\s*$)")
val INVALID_ROW_BOARD_REGEX = Regex("(^\\s*\\d+\\s*[xX]\\s*[5-9]\\s*\$)")
val INVALID_COLUMN_BOARD_REGEX = Regex("(^\\s*[5-9]\\s*[xX]\\s*\\d+\\s*\$)")

enum class GameStatus {
    Preparing,
    MakeMove,
    Win,
    Draw,
    End
}

fun main() {
    var gameStatus: GameStatus = GameStatus.Preparing
    println("Connect Four")
    val (rows, columns, playerNames: List<String>) = drawBoard()
    val board: MutableList<MutableList<String>> = generateBoard(rows, columns)
    var currentPlayer = 0
    do {
        if (gameStatus == GameStatus.Preparing) {
            drawBoard(rows, columns, board)
        }
        gameStatus = GameStatus.MakeMove
        println("${playerNames[currentPlayer]}'s turn")
        val input = readLine()!!
        if (input != "end") {
            try {
                val move = input.toInt()
                val index = move - 1
                if (move in 1..columns) {
                    val rowIndex = board[index].indexOf(" ")
                    if (rowIndex != -1) {
                        board[index][rowIndex] = (if (currentPlayer == 0) "o" else "*")
                        currentPlayer = if (currentPlayer == 0) 1 else 0
                        gameStatus = checkWin(board, columns, index, rowIndex, playerNames[currentPlayer])
                    } else {
                        println("Column $move is full")
                    }
                } else {
                    println("The column number is out of range (1 - ${columns})")
                }
            } catch (_: Exception) {
                println("Incorrect column number")
            }
        } else {
            gameStatus = GameStatus.End
        }
    } while (gameStatus != GameStatus.End && gameStatus != GameStatus.Draw && gameStatus != GameStatus.Win)

    showResult(gameStatus, playerNames[currentPlayer])
}

fun generateBoard(rows: Int, columns: Int): MutableList<MutableList<String>> {
    val board = mutableListOf<MutableList<String>>()
    repeat(columns) { board.add(mutableListOf()) }
    for (column in board) {
        repeat(rows) { column.add(" ") }
    }
    return board
}

fun checkWin(board: MutableList<MutableList<String>>, columns: Int, columnIndex: Int, rowIndex: Int, playerSymbol: String): GameStatus {
    // count draw
    var filledColumns = 0
    for (column in board) {
        if (column.count { it != " " } == column.size) {
            filledColumns++
        }
    }
    if (filledColumns == columns) {
        return GameStatus.Draw
    }
    // calculate win
    // check horizon
        // find left limit
            // set limit - 3
            // set counter
            // subtract limit
            // if result is positive - save and exit
            // else counter is not zero and result is negative - lower limit and lower counter
            // repeat
        // find opposite limit
            // set limit - 3
            // set counter
            // add limit
            // if result lower than boundary positive - save and exit
            // else counter is not zero and result is higher than boundary - lower limit and lower counter
            // repeat
        // create sublist from left boundary till left + 3
        // check if sublist list contains only playerSymbols
        // move right
        // repeat until right limit
    // check vertices
        // find upper limit
            // set limit - 3
            // set counter
            // subtract limit
            // if result is positive - save and exit
            // else counter is not zero and result is negative - lower limit and lower counter
            // repeat
        // fined lower limit
            // set limit - 3
            // set counter
            // add limit
            // if result lower than boundary positive - save and exit
            // else counter is not zero and result is higher than boundary - lower limit and lower counter
            // repeat
        // create sublist from low boundary till low + 3 up
        // check if sublist list contains only playerSymbols
        // move up
        // repeat until upper limit
    // check diagonals
        // check slash
        // check backslash
    return GameStatus.Preparing
}

fun showResult(gameStatus: GameStatus, winningPlayer: String) {
    if (gameStatus == GameStatus.Win) {
        println("Player $winningPlayer won")
    } else if (gameStatus == GameStatus.Draw) {
        println("It is a draw")
    }
    println("Game over!")
}

private fun drawBoard(rows: Int, columns: Int, board: MutableList<MutableList<String>>) {
    val limit = rows + 1
    for (row in limit downTo 0) {
        when (row) {
            limit -> {
                println(" ${(1..columns).toList().joinToString(" ")} ")
            }
            0 -> {
                println("╚${"═╩".repeat(columns - 1)}═╝")
            }
            else -> {
                val list = mutableListOf<String>()
                for (index in 0..columns - 1) {
                    val value = board[index][row - 1]
                    list.add(value)
                }
                println("║${list.joinToString("║")}║")
            }
        }
    }
}

private fun drawBoard(): Triple<Int, Int, List<String>> {
    println("First player's name:")
    val firstPlayerName = readLine()!!
    println("Second player's name:")
    val secondPlayerName = readLine()!!
    val playersNames: List<String> = listOf(firstPlayerName, secondPlayerName)
    var rows = 6
    var columns = 7
    var errorMessage = ""
    do {
        showBoardDimensionsMessage(errorMessage)
        val userInput = readLine()!!
        if (!userInput.matches(CORRECT_BOARD_SIZE_REGEX)) {
            errorMessage = getErrorMessage(userInput)
        } else {
            val list = userInput.filter { it.isDigit() }
            rows = "${list.first()}".toInt()
            columns = "${list.last()}".toInt()
        }
    } while (userInput != "" && !userInput.matches(CORRECT_BOARD_SIZE_REGEX))
    println("$firstPlayerName VS $secondPlayerName")
    println("$rows X $columns board")

    return Triple(rows, columns, playersNames)
}

private fun showBoardDimensionsMessage(errorMessage: String) {
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
