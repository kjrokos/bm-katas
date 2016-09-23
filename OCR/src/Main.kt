import java.io.File

fun main(args: Array<String>): Unit {
    var inputLines = File("src/input.txt").readLines()
    var outputLines = File("src/output.txt").readLines()

    while(inputLines.size > 0) {
        val input = inputLines.take(4).joinToString(separator = "\n")
        val output = outputLines.first()

        if("$output".equals(strToNum(input))) {
            println()
            println("=> $output")
        } else {
            println()
            println("=> ${strToNum(input)}")
            println("should be")
            println(output)
        }

        inputLines = inputLines.drop(4)
        outputLines = outputLines.drop(1)
    }
}

fun numToStr(num: Int): String {
    val line = Line()
    num.forEachBase10Digit {
        line.addDigit(it)
    }
    return line.toString()
}

fun strToNum(str: String): String {
    val line = Line(str.split('\n')[0].length / 3)
    str.split('\n').forEachIndexed { lineIndex, lineStr ->
        lineStr.forEachNCharsIndexed(3) { digitIndex, digitStr ->
            line.digitAt(digitIndex).setLine(lineIndex, digitStr)
        }
    }
    return line.toInt()
}


fun Int.forEachBase10Digit(block: (d: Int) -> Unit): Unit {
    "$this".forEachChar {
        block(it.toInt())
    }
}

fun String.forEachChar(block: (c: String) -> Unit): Unit {
    forEachNChars(1, block)
}

fun String.forEachNChars(n: Int, block: (s: String) -> Unit): Unit {
    for (i in 0 until this.length step n) {
        block(this.substring(i until i + n))
    }
}

fun String.forEachNCharsIndexed(n: Int, block: (i: Int, s: String) -> Unit): Unit {
    var index = 0
    for (i in 0 until this.length step n) {
        block(index, this.substring(i until i + n))
        index++
    }
}

