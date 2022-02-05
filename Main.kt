package connectfour

val REGEX = Regex("(^\\s*[5-9]\\s*[xX]\\s*[5-9]\\s*$)")

fun main() {
    println("Connect Four")
    println("First player's name:")
    val firstPlayerName = readLine()!!
    println("Second player's name:")
    val secondPlayerName = readLine()!!
    var rows = '6'
    var columns = '7'
    var errorShouldBeShown = false
    do {
        showBoardDemensionsMessage(errorShouldBeShown)
        val userInput = readLine()!!
        if (!userInput.matches(REGEX)) {
            errorShouldBeShown = true
        } else {
            val list = userInput.filter { it.isDigit() }
            rows = list.first()
            columns = list.last()
        }
    } while (userInput != "" && !userInput.matches(REGEX))
    println("$firstPlayerName VS $secondPlayerName")
    println("$rows X $columns board")
}

private fun showBoardDemensionsMessage(errorShouldBeShown: Boolean) {
    if (errorShouldBeShown) {
        println("Board rows should be from 5 to 9")
    }
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
}
