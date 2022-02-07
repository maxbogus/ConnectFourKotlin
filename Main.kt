package connectfour

import java.lang.Exception
import kotlin.math.min

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
                        board[index][rowIndex] = getPlayerSymbol(currentPlayer)

                        gameStatus = if (checkDraw(board, columns)) {
                            GameStatus.Draw
                        } else {
                            checkWin(board, getPlayerSymbol(currentPlayer))
                        }
                        if (gameStatus == GameStatus.Preparing) {
                            currentPlayer = if (currentPlayer == 0) 1 else 0
                        }
                    } else {
                        println("Column $move is full")
                    }
                } else {
                    println("The column number is out of range (1 - ${columns})")
                }
            } catch (e: Exception) {
                println("Incorrect column number")
            }
        } else {
            gameStatus = GameStatus.End
        }
    } while (gameStatus != GameStatus.End && gameStatus != GameStatus.Draw && gameStatus != GameStatus.Win)

    if (gameStatus != GameStatus.End) {
        drawBoard(rows, columns, board)
    }
    showResult(gameStatus, playerNames[currentPlayer])
}

private fun getPlayerSymbol(currentPlayer: Int) = if (currentPlayer == 0) "o" else "*"

fun generateBoard(rows: Int, columns: Int): MutableList<MutableList<String>> {
    val board = mutableListOf<MutableList<String>>()
    repeat(columns) { board.add(mutableListOf()) }
    for (column in board) {
        repeat(rows) { column.add(" ") }
    }
    return board
}

fun checkWin(board: MutableList<MutableList<String>>, playerSymbol: String): GameStatus {
    val winningPattern = playerSymbol.repeat(4)

    val result = GameStatus.Preparing
    val verticalList = mutableListOf<String>()
    val horizonList = mutableListOf<MutableList<String>>()
    repeat(board[0].size) { horizonList.add(mutableListOf()) }
    for (column in 0..board.size - 1) {
        verticalList.add(board[column].joinToString(""))
        for (row in 0..board[column].size - 1) {
            horizonList[row].add(board[column][row])
        }
    }

    val diagonalsSlashList = mutableListOf<MutableList<String>>()
    val diagonalsBackSlashList = mutableListOf<MutableList<String>>()

    val limit = min(board.size, board[0].size) - 1
    repeat(limit * 2) { diagonalsSlashList.add(mutableListOf()) }
    repeat(limit * 2) { diagonalsBackSlashList.add(mutableListOf()) }

    for (index in 0..limit) {
        for (shift in limit - 3 downTo 0) {
            if (shift == 0) {
                diagonalsSlashList[limit].add(board[index][index])
            } else {
                if ((limit + shift) <= diagonalsSlashList.size - 1 && (index + shift) <= limit) {
                    diagonalsSlashList[limit-shift].add(board[index][index+shift])
                    diagonalsSlashList[limit+shift].add(board[index+shift][index])
                }
            }
        }
    }

    for (diagonal in diagonalsSlashList) {
        if (diagonal.joinToString("").contains(winningPattern)) {
            return GameStatus.Win
        }
    }

    for (diagonal in diagonalsBackSlashList) {
        if (diagonal.joinToString("").contains(winningPattern)) {
            return GameStatus.Win
        }
    }

    for (vertical in verticalList) {
        if (vertical.contains(winningPattern)) {
            return GameStatus.Win
        }
    }

    for (horizon in horizonList) {
        if (horizon.joinToString("").contains(winningPattern)) {
            return GameStatus.Win
        }
    }

    return result
}

private fun checkDraw(
    board: MutableList<MutableList<String>>,
    columns: Int
): Boolean {
    var filledColumns = 0
    for (column in board) {
        if (column.count { it != " " } == column.size) {
            filledColumns++
        }
    }
    if (filledColumns == columns) {
        return true
    }
    return false
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
