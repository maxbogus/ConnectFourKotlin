package connectfour

import java.lang.Exception
import kotlin.math.min
import kotlin.math.abs

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

data class GameSetup(val rows: Int, val columns: Int, val playersNames: List<String>, val numberOfGames: Int)

data class GameScores(
    var gameCounter: Int = 0,
    var firstPlayerWins: Int = 0,
    var secondPlayerWins: Int,
    var endByExit: Boolean = false,
    var playerNames: List<String>
)

fun main() {
    println("Connect Four")
    val gameSetup: GameSetup = setupBoard()

    val scores = GameScores(0, 0, 0, false, gameSetup.playersNames)
    do {
        val (board, currentPlayer, gameStatus) = playGame(gameSetup, scores)
        if (gameStatus != GameStatus.End) {
            drawBoard(gameSetup.rows, gameSetup.columns, board)
            if (gameStatus == GameStatus.Draw) {
                scores.firstPlayerWins++
                scores.secondPlayerWins++
            } else {
                if (currentPlayer == 0) {
                    scores.firstPlayerWins++
                } else {
                    scores.secondPlayerWins++
                }
            }
        } else {
            scores.endByExit = true
        }
        if (gameSetup.numberOfGames > 1) {
            println(
                """
                Score
                ${scores.playerNames[0]}: ${scores.firstPlayerWins} ${scores.playerNames[1]}: ${scores.secondPlayerWins}
            """.trimIndent()
            )
            scores.gameCounter++
        }
    } while (scores.gameCounter <= gameSetup.numberOfGames && !scores.endByExit)

    showResult(scores)
}

private fun playGame(
    gameSetup: GameSetup,
    scores: GameScores
): Triple<MutableList<MutableList<String>>, Int, GameStatus> {
    var gameStatus: GameStatus = GameStatus.Preparing
    val board: MutableList<MutableList<String>> = generateBoard(gameSetup.rows, gameSetup.columns)
    var currentPlayer = 0
    if (gameSetup.numberOfGames > 1) {
        println("Game #${scores.gameCounter + 1}")
    }
    do {
        if (gameStatus == GameStatus.Preparing) {
            drawBoard(gameSetup.rows, gameSetup.columns, board)
        }
        gameStatus = GameStatus.MakeMove
        println("${scores.playerNames[currentPlayer]}'s turn")
        val input = readLine()!!
        if (input != "end") {
            try {
                val move = input.toInt()
                val index = move - 1
                if (move in 1..gameSetup.columns) {
                    val rowIndex = board[index].indexOf(" ")
                    if (rowIndex != -1) {
                        board[index][rowIndex] = getPlayerSymbol(currentPlayer)

                        gameStatus = if (checkDraw(board, gameSetup.columns)) {
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
                    println("The column number is out of range (1 - ${gameSetup.columns})")
                }
            } catch (e: Exception) {
                println("Incorrect column number")
            }
        } else {
            gameStatus = GameStatus.End
        }
    } while (gameStatus != GameStatus.End && gameStatus != GameStatus.Draw && gameStatus != GameStatus.Win)
    return Triple(board, currentPlayer, gameStatus)
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
                    diagonalsSlashList[limit - shift].add(board[index][index + shift])
                    diagonalsSlashList[limit + shift].add(board[index + shift][index])
                }
            }
        }
    }

    for (index in limit downTo 0) {
        for (shift in limit - 3 downTo 0) {
            if (shift == 0) {
                diagonalsBackSlashList[limit].add(board[index][abs(index - 4)])
            } else {
                if ((abs(index - 4) + shift) <= diagonalsBackSlashList.size - 1 && (abs(index - 4) + shift) <= limit) {
                    diagonalsBackSlashList[limit - shift].add(board[index][abs(index - 4) + shift])
                    diagonalsBackSlashList[limit + shift].add(board[index - shift][abs(index - 4)])
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

fun showResult(scores: GameScores) {
    if (!scores.endByExit) {
        if (scores.firstPlayerWins != scores.secondPlayerWins) {
            println("Player ${if (scores.firstPlayerWins > scores.secondPlayerWins) scores.playerNames[0] else scores.playerNames[1]} won")
        } else {
            println("It is a draw")
        }
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

private fun setupBoard(): GameSetup {
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
    println(
        """
        Do you want to play single or multiple games?
        For a single game, input 1 or press Enter
        Input a number of games:
    """.trimIndent()
    )
    var numberOfGames: Int = -1
    do {
        val input: String = readLine()!!
        if (input == "") {
            numberOfGames = 1
        }
        try {
            numberOfGames = input.toInt()
        } catch (_: Exception) {

        }
    } while (input != "" && numberOfGames < 1)
    println("$firstPlayerName VS $secondPlayerName")
    println("$rows X $columns board")
    println(if (numberOfGames == 1) "Single game" else "Total $numberOfGames games")

    return GameSetup(rows, columns, playersNames, numberOfGames)
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
