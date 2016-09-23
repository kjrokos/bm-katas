fun main(args: Array<String>): Unit {
    testBasics()
    testSimpleDiscounts()
    testSeveralDiscounts()
    testEdgeCases()
}

fun testBasics() {
    assert_equal(0, price(arrayOf()))
    assert_equal(8, price(arrayOf(0)))
    assert_equal(8, price(arrayOf(1)))
    assert_equal(8, price(arrayOf(2)))
    assert_equal(8, price(arrayOf(3)))
    assert_equal(8, price(arrayOf(4)))
    assert_equal(8 * 2, price(arrayOf(0, 0)))
    assert_equal(8 * 3, price(arrayOf(1, 1, 1)))
}

fun testSimpleDiscounts() {
    assert_equal(8 * 2 * 0.95, price(arrayOf(0, 1)))
    assert_equal(8 * 3 * 0.9, price(arrayOf(0, 2, 4)))
    assert_equal(8 * 4 * 0.8, price(arrayOf(0, 1, 2, 4)))
    assert_equal(8 * 5 * 0.75, price(arrayOf(0, 1, 2, 3, 4)))
}

fun testSeveralDiscounts() {
    assert_equal(8 + (8 * 2 * 0.95), price(arrayOf(0, 0, 1)))
    assert_equal(2 * (8 * 2 * 0.95), price(arrayOf(0, 0, 1, 1)))
    assert_equal((8 * 4 * 0.8) + (8 * 2 * 0.95), price(arrayOf(0, 0, 1, 2, 2, 3)))
    assert_equal(8 + (8 * 5 * 0.75), price(arrayOf(0, 1, 1, 2, 3, 4)))
}

fun testEdgeCases() {
    assert_equal(2 * (8 * 4 * 0.8), price(arrayOf(0, 0, 1, 1, 2, 2, 3, 4)))
    assert_equal(3 * (8 * 5 * 0.75) + 2 * (8 * 4 * 0.8),
            price(arrayOf(0, 0, 0, 0, 0,
                    1, 1, 1, 1, 1,
                    2, 2, 2, 2,
                    3, 3, 3, 3, 3,
                    4, 4, 4, 4)))
}

fun assert_equal(expected: Number, actual: Number): Boolean {
    if (Math.abs(expected.toDouble() - actual.toDouble()) < 0.001) {
        return true
    }
    println("Assert failed: expected: $expected, actual: $actual")
    return false
}

fun price(books: Array<Int>): Double {
    val bookCounts = listOf(0,0,0,0,0).mapIndexed { index, i -> books.count({ it == index}) }
    return priceByCount(bookCounts)
}

// Key thing here is that books of the same title are indistinguishable, so we really
// only need to keep track of the count of each kind.  This makes finding the best sets
// to remove much easier
fun priceByCount(allBooks: List<Int>): Double {
    if(noDuplicates(allBooks)) {
        return priceForDistinctSet(allBooks.sum())
    }

    // The only case we have to worry about (with this specific set of discounts)
    // is setOf(4) + setOf(4) < setOf(5) + setOf(3).  This 'if' eliminates cases
    // where the non-greedy computation would be guaranteed to give the same or
    // worse price than the greedy version, but it is not strictly necessary - we
    // could just always compute all cases
    if(allBooks.sum() >= 8 && canRemoveOneOfEach(allBooks)) {
        return Math.min(computePrice(allBooks), computePrice(allBooks, maxBooksToRemove = 4))
    } else {
        return computePrice(allBooks)
    }
}

fun noDuplicates(books: List<Int>): Boolean {
    return books.none { it > 1 }
}

fun canRemoveOneOfEach(books: List<Int>): Boolean {
    return books.all { it >= 1 }
}

fun computePrice(books: List<Int>, maxBooksToRemove: Int = books.size): Double {
    val remainingBooks = removeSetFrom(books, maxBooksToRemove)
    val removedBooks = books.sum() - remainingBooks.sum()
    return priceForDistinctSet(removedBooks) + priceByCount(remainingBooks)
}

fun removeSetFrom(books: List<Int>, maxBooksToRemove: Int = books.size): List<Int> {
    // sort the piles so that if we are not removing as many as possible (greedy version),
    // we take from the piles that have the most copies first.  This leaves us the most options
    // for future sets. Note that we don't really care what order the piles are in
    return books.sortedDescending().mapIndexed { index, i ->
        if (i > 0 && index < maxBooksToRemove) {
            i - 1
        } else {
            i
        }
    }
}

fun priceForDistinctSet(size:Int): Double {
    when(size) {
        0 -> return 0.0
        1 -> return 8  * 1.00
        2 -> return 16 * 0.95
        3 -> return 24 * 0.90
        4 -> return 32 * 0.80
        5 -> return 40 * 0.75
    }
    return -1000.0 // something that will be obvious.  Probably should throw something instead...
}

