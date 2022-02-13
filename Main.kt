package sorting

private const val SORT_ARGUMENT = "-sortIntegers"
private const val DATA_TYPE_ARGUMENT = "-dataType"

fun main(args: Array<String>) {
    val lines: MutableList<String> = parseInput()

    if (args.contains(SORT_ARGUMENT)) {
        sortInts(lines)
    } else {
        val type = if (args[0] == DATA_TYPE_ARGUMENT) {
            args[1]
        } else {
            "word"
        }
        
        when (type) {
            "long" -> countGreatestNumber(lines)
            "word" -> findLongestWord(lines)
            else -> findLongestLine(lines)
        }
    }
}

fun sortInts(lines: MutableList<String>) {
    val unsortedArray = parseStringsIntoArray(lines)

    val sortedArray: List<Int> = mergeSort(unsortedArray)

    println("Total numbers: ${sortedArray.size}.")
    println("Sorted data: ${sortedArray.joinToString(" ")}")
}

fun mergeSort(unsortedArray: MutableList<Int>): List<Int> {
    if (unsortedArray.size <= 1) {
        return unsortedArray
    }
    var leftList = unsortedArray.subList(0, unsortedArray.size / 2).toMutableList()
    var rightList = unsortedArray.subList(unsortedArray.size / 2, unsortedArray.lastIndex + 1).toMutableList()

    leftList = mergeSort(leftList) as MutableList<Int>
    rightList = mergeSort(rightList) as MutableList<Int>

    return mergeList(leftList, rightList)
}

fun mergeList(leftList: MutableList<Int>, rightList: MutableList<Int>): List<Int> {
    val result = mutableListOf<Int>()

    do {
        if (leftList.first() <= rightList.first()) {
            result.add(leftList.first())
            leftList.removeAt(0)
        } else {
            result.add(rightList.first())
            rightList.removeAt(0)
        }
    } while (leftList.size > 0 && rightList.size > 0)

    do {
        if (leftList.size > 0) {
            result.add(leftList.first())
            leftList.removeAt(0)
        }
    } while (leftList.size > 0)

    do {
        if (rightList.size > 0) {
            result.add(rightList.first())
            rightList.removeAt(0)
        }
    } while (rightList.size > 0)

    return result
}

private fun parseStringsIntoArray(lines: MutableList<String>): MutableList<Int> {
    val unsortedArray = mutableListOf<Int>()
    if (lines.filter { it != "" }.isEmpty()) {
        return unsortedArray
    }
    lines.forEach { s -> s.split("\\s+".toRegex()).map { it.toInt() }.forEach { unsortedArray.add(it) } }
    return unsortedArray
}

fun findLongestLine(lines: MutableList<String>) {
    var largestLine = ""
    for (line in lines) {
        if (line.length > largestLine.length) {
            largestLine = line
        }
    }
    val count = lines.count { it == largestLine }

    println("Total lines: ${lines.size}.")
    println("The longest line:")
    println(largestLine)
    println("(${count} time(s), ${calculatePercentage(count.toFloat(), lines.size.toFloat())}%).")
}

fun findLongestWord(lines: MutableList<String>) {
    var longestWord = ""
    var longestWordCounter = 1
    var totalWordsCounter = 0
    for (line in lines) {
        val words = line.split("\\s+".toRegex())
        for (word in words) {
            if (word.length > longestWord.length) {
                longestWord = word
                longestWordCounter = 1
            } else if (word == longestWord) {
                longestWordCounter += 1
            }
            totalWordsCounter++
        }
    }
    println("Total words: ${totalWordsCounter}.")
    println(
        "The longest word: $longestWord (${longestWordCounter} time(s), ${
            calculatePercentage(
                longestWordCounter.toFloat(),
                totalWordsCounter.toFloat()
            )
        }%)."
    )
}

private fun calculatePercentage(longestWordCounter: Float, totalWordsCounter: Float) =
    (longestWordCounter / (totalWordsCounter / 100.0)).toInt()

private fun countGreatestNumber(lines: MutableList<String>) {
    val numbers: MutableList<Int> = mutableListOf()
    for (line in lines) {
        for (number in line.split("\\s+".toRegex()).map { it.toInt() }) {
            numbers.add(number)
        }
    }

    var greatestNumber: Int = Int.MIN_VALUE
    var counter = 0
    for (number in numbers) {
        if (number > greatestNumber) {
            greatestNumber = number
            counter = 1
        } else if (number == greatestNumber) {
            counter++
        }
    }

    println("Total numbers: ${numbers.size}.")
    println("The greatest number: $greatestNumber (${counter} time(s)).")
}

private fun parseInput(): MutableList<String> {
    val lines = mutableListOf<String>()
    do {
        val input = readLine()
        if (input != null) {
            lines.add(input)
        }
    } while (input != null)
    return lines
}
