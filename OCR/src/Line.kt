import java.util.*

class Line(n: Int = 0) {
    var digits: ArrayList<Digit> = ArrayList<Digit>()

    init {
        (0 until n).forEach {
            addDigit(0)
        }
    }

    fun addDigit(n: Int) {
        digits.add(Digit(n))
    }

    override fun toString(): String {
        var l1: String = ""
        var l2: String = ""
        var l3: String = ""
        var l4: String = ""

        digits.forEach { digit ->
            val lines = digit.asStrings()
            l1 += lines[0]
            l2 += lines[1]
            l3 += lines[2]
            l4 += lines[3]
        }

        return "$l1\n$l2\n$l3\n$l4\n"
    }

    fun digitAt(digitIndex: Int): Digit {
        return digits[digitIndex]
    }

    fun toInt(): String {
        val intStr = digits.joinToString(separator = "", transform = { it.asInt() })

        if(intStr.contains("?")) {
            return correct(intStr, "ILL")
        } else if(!verify(intStr)) {
            return correct(intStr, "ERR")
        } else {
            return intStr
        }
    }

    fun correct(initial:String, errorType:String): String {
        val possibleCorrections = correct(maxEditDistance = 1)

        if(possibleCorrections.size == 0) {
            return "$initial $errorType"
        }
        else if (possibleCorrections.size == 1) {
            return possibleCorrections.first()
        } else {
            return "$initial AMB ${possibleCorrections.sorted().joinToString(separator = "', '", prefix = "['", postfix = "']")}"
        }
    }

    private fun correct(maxEditDistance:Int, digitIndex: Int = 0, digitStr:String = "", totalEditDistance: Int = 0): Set<String> {
        if(totalEditDistance > maxEditDistance) {
            return setOf()
        }
        if(digitIndex == 9) {
            if(verify(digitStr)) {
                return setOf(digitStr)
            }
            return setOf()
        } else {
            val ret:MutableSet<String> = mutableSetOf()
            (0 .. maxEditDistance-totalEditDistance).forEach { i ->
                digits[digitIndex].possibleDigits(i).forEach {
                    ret.addAll(correct(maxEditDistance, digitIndex + 1, digitStr + it, totalEditDistance + i))
                }
            }
            return ret
        }
    }

    fun verify(n:String): Boolean {
        if(n.length != 9) {
            return false
        }
        return n.toCharArray().mapIndexed { i, digit -> "$digit".toInt() * (9-i) }.sum() % 11 == 0
    }
}

class Digit(digit: Int) {
    companion object {
        val ZERO = booleanArrayOf(true, true, false, true, true, true, true)
        val ONE = booleanArrayOf(false, false, false, true, false, false, true)
        val TWO = booleanArrayOf(true, false, true, true, true, true, false)
        val THREE = booleanArrayOf(true, false, true, true, false, true, true)
        val FOUR = booleanArrayOf(false, true, true, true, false, false, true)
        val FIVE = booleanArrayOf(true, true, true, false, false, true, true)
        val SIX = booleanArrayOf(true, true, true, false, true, true, true)
        val SEVEN = booleanArrayOf(true, false, false, true, false, false, true)
        val EIGHT = booleanArrayOf(true, true, true, true, true, true, true)
        val NINE = booleanArrayOf(true, true, true, true, false, true, true)

        val DIGITS = arrayOf(ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE)
    }

    var segments = BooleanArray(7)

    init {
        segments = DIGITS[digit].clone()
    }

    fun setLine(lineIndex: Int, digitStr: String) {
        when (lineIndex) {
            0 -> {
                segments[0] = digitStr.equals(" _ ")
            }
            1 -> {
                segments[1] = digitStr.startsWith("|")
                segments[2] = digitStr.contains(Regex("._."))
                segments[3] = digitStr.endsWith("|")
            }
            2 -> {
                segments[4] = digitStr.startsWith("|")
                segments[5] = digitStr.contains(Regex("._."))
                segments[6] = digitStr.endsWith("|")
            }
        }
    }

    fun asStrings(): Array<String> {
        val l1: String = " "  + h(0) + " "
        val l2: String = v(1) + h(2) + v(3)
        val l3: String = v(4) + h(5) + v(6)
        val l4: String = " "  + " "  + " "

        return arrayOf(l1, l2, l3, l4)
    }

    // creates a horizontal line if the specified segment is set
    private fun h(i: Int): String {
        return if (segments[i]) "_" else " "
    }

    // creates a vertical line if the specified segment is set
    private fun v(i: Int): String {
        return if (segments[i]) "|" else " "
    }

    fun asInt(): String {
        var match: String = "?"
        DIGITS.forEachIndexed { i, digit ->
            if (digit.matches(segments)) {
                match = "$i"
            }
        }
        return match
    }

    fun possibleDigits(editDistance: Int) : List<String> {
        val matches:MutableList<String> = mutableListOf()

        DIGITS.forEachIndexed { i, digit ->
            if (digit.editDistance(segments) == editDistance) {
                matches.add("$i")
            }
        }

        return matches
    }

    fun BooleanArray.matches(other: BooleanArray): Boolean {
        return this.editDistance(other) == 0
    }

    fun BooleanArray.editDistance(other: BooleanArray): Int {
        var editDistance = 0
        editDistance += Math.abs(this.size - other.size)
        for (i in 0 until Math.min(this.size, other.size)) {
            if (this[i] != other[i]) {
                editDistance ++
            }
        }
        return editDistance
    }
}

