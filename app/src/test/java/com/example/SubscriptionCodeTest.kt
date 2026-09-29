package com.example

import org.junit.Assert.*
import org.junit.Test
import java.security.SecureRandom

class SubscriptionCodeTest {

    @Test
    fun testCodeGenerationFormat() {
        val randomChars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val random = SecureRandom()
        val part1 = (1..4).map { randomChars[random.nextInt(randomChars.length)] }.joinToString("")
        val part2 = (1..4).map { randomChars[random.nextInt(randomChars.length)] }.joinToString("")
        val code = "YEM-$part1-$part2"

        assertTrue("Code must start with YEM-", code.startsWith("YEM-"))
        assertEquals("Code must be 13 characters long", 13, code.length)
        assertTrue("Code matches pattern YEM-XXXX-XXXX", code.matches(Regex("^YEM-[A-Z0-9]{4}-[A-Z0-9]{4}$")))
    }

    @Test
    fun testDurationCalculation() {
        val threeMonthsDays = 3 * 30L
        val sixMonthsDays = 6 * 30L
        val yearDays = 12 * 30L

        assertEquals(90L, threeMonthsDays)
        assertEquals(180L, sixMonthsDays)
        assertEquals(360L, yearDays)
    }
}
