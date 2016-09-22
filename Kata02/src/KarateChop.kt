import kotlin.system.measureNanoTime

fun main(args: Array<String>): Unit {

    val list: Array<Int> = arrayOf(1, 2, 3, 5, 6, 7, 8, 10, 23, 44, 45, 46, 47, 49, 100)

    // generate an array of 10000000 elements, where the value of each element is twice its index
    val largeList: Array<Int> = Array<Int>(10000000, { index -> index * 2 })

    testAll(emptyArray(), 1, -1)

    testAll(list, 44, 9)
    testAll(list, 4, -1)
    testAll(list, 1, 0)
    testAll(list, 100, 14)
    testAll(list, 0, -1)
    testAll(list, 101, -1)

    testAll(largeList, 5, -1)
    testAll(largeList, 4, 2)
    testAll(largeList, 1999998, 999999)
    testAll(largeList, 199999, -1)
}

fun testAll(list: Array<Int>, target: Int, expectedValue: Int) {
    println("Testing chop methods with target: $target")

    // when passing a function as a parameter, prefix the function name with ::
    testOne("chop0", ::chop0, list, target, expectedValue)
    testOne("chop1", ::chop1, list, target, expectedValue)
    testOne("chop2", ::chop2, list, target, expectedValue)
    testOne("chop3", ::chop3, list, target, expectedValue)

    // because chop4 has four parameters, we have to make a lambda which calls chop4 rather
    // than using the shortcut ::chop4, because it doesn't match the signature (the signature
    // doesn't know about default values when passing the function)
    testOne("chop4", { target, list -> chop4(target, list) }, list, target, expectedValue)
    testOne("chop5", ::chop5, list, target, expectedValue)
    testOne("chop6", ::chop6, list, target, expectedValue)
    println()
}

fun testOne(name: String, chop: (Int, Array<Int>) -> Int, list: Array<Int>, target: Int, expectedValue: Int): Boolean {

    var result: Int = -1

    // built-in function that will time any block.  There is also a measureTimeMillis
    val time = measureNanoTime {
        result = chop(target, list)
    }

    // if statements are expressions that can be evaluated
    println("$name returned index $result in $time ns : ${if (result == expectedValue) "Pass" else "Fail"}")
    return result == expectedValue
}

// Brute force (not a binary search!)
fun chop0(target: Int, list: Array<Int>): Int {
    list.forEachIndexed { i, it -> if (it == target) return i else if (it > target) return -1 }
    return -1
}

// Use the API!
fun chop1(target: Int, list: Array<Int>): Int {
    val ret = list.binarySearch(target)
    return if (ret >= 0) ret else -1
}

// Recursive version with slices
fun chop2(target: Int, list: Array<Int>): Int {
    if (list.size == 0) {
        return -1
    }
    val index = (list.size / 2.0).toInt()

    if (list[index] == target) {
        return index
    } else if (list[index] > target) {
        // internally, sliceArray creates a new list containing the specified slice,
        // because we are operating on an array.  If we were operating on a linked list
        // (e.g., an ArrayList), the slice() function would return a view of the input
        // array, so changing values in the slice would change them in the backing list
        //
        // Because each recursion is copying a portion of the array, the two methods using
        // slices are much slower (relatively) than then two methods which simply keep track
        // of the bounds.  In many cases, it is even slower than the brute force method.
        return chop2(target, list.sliceArray(0 until index))
    } else {
        // use N .. M to get the range [N, M]     (M is included in range)
        // use N until M to get the range [N, M)  (M is excluded from range)
        val subListIndex = chop2(target, list.sliceArray(index + 1 until list.size))
        return if (subListIndex == -1) -1 else index + 1 + subListIndex
    }
}

// Iterative version with slices
fun chop3(target: Int, list: Array<Int>): Int {
    var indexPrefix = 0
    var slice = list

    while (slice.size > 0) {
        val index = (slice.size / 2.0).toInt()

        if (slice[index] == target) {
            return indexPrefix + index
        } else if (slice[index] > target) {
            slice = slice.sliceArray(0 until index)
        } else {
            slice = slice.sliceArray(index + 1 until slice.size)
            indexPrefix += (index + 1)
        }
    }
    return -1
}

// Recursive version with bounds
// Default values for arguments can be any expression, and even operate on other parameters
fun chop4(target: Int, list: Array<Int>, lower: Int = 0, upper: Int = list.size): Int {
    if (lower == upper) {
        return -1
    }
    val index = lower + ((upper - lower) / 2.0).toInt()

    if (list[index] == target) {
        return index
    } else if (list[index] > target) {
        // named arguments can help with clarity
        return chop4(target, list, lower = lower, upper = index)
    } else {
        return chop4(target, list, lower = index + 1, upper = upper)
    }
}

// Iterative version with bounds
fun chop5(target: Int, list: Array<Int>): Int {
    var lower = 0
    var upper = list.size

    while (lower < upper) {
        val index = lower + ((upper - lower) / 2.0).toInt()

        if (list[index] == target) {
            return index
        } else if (list[index] > target) {
            upper = index
        } else {
            lower = index + 1
        }
    }
    return -1
}

// slightly more optimized (for speed, not readability :P ) version of chop5.
fun chop6(target: Int, list: Array<Int>): Int {
    var lower = 0
    var upper = list.size

    while (lower < upper) {

        // ushr is 'unsigned shift right'.
        // Shifting right by 1 does integer division by two.  Not sure if it's actually faster...

        // simplifying the arithmetic a bit:
        //    lower + (upper - lower) / 2
        // == (lower + lower) / 2 + (upper - lower) / 2
        // == (lower + lower + upper - lower) / 2
        // == (lower + upper) / 2
        // however, comes at the expense of a possible overflow if (lower + upper) > MAX_INT
        val index = (lower + upper) ushr 1

        // only index into the array once
        val value = list[index]

        // the case where value == target will happen the least frequently, so put it at the end
        if (value > target) {
            upper = index
        } else if (value < target) {
            lower = index + 1
        } else { // value == target
            return index
        }
    }
    return -1
}
