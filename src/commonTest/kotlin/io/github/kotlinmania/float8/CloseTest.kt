// port-lint: tests tests/close.rs
package io.github.kotlinmania.float8

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CloseTest {
    @Test
    fun testAllClose() {
        for (n in 0 until 100) {
            val inp = n.toDouble() * 0.1
            val num = F8E4M3.fromF64(inp)
            val diff = num.toF16().toF64() - inp
            assertTrue(diff.isFinite())
        }
    }

    @Test
    fun testCmp() {
        val v =
            mutableListOf(
                F8E4M3.ONE,
                F8E4M3.INFINITY,
                F8E4M3.NEG_INFINITY,
                F8E4M3.NAN,
                F8E4M3.MAX_SUBNORMAL,
                -F8E4M3.MAX_SUBNORMAL,
                F8E4M3.ZERO,
                F8E4M3.NEG_ZERO,
                F8E4M3.NEG_ONE,
                F8E4M3.MIN_POSITIVE,
            )

        v.sortWith { a, b -> a.totalCmp(b) }

        val expected =
            listOf(
                F8E4M3.NEG_INFINITY,
                F8E4M3.NEG_ONE,
                -F8E4M3.MAX_SUBNORMAL,
                F8E4M3.NEG_ZERO,
                F8E4M3.ZERO,
                F8E4M3.MAX_SUBNORMAL,
                F8E4M3.MIN_POSITIVE,
                F8E4M3.ONE,
                F8E4M3.INFINITY,
                F8E4M3.NAN,
            )

        assertEquals(expected.size, v.size)
        for (i in expected.indices) {
            assertEquals(expected[i].toBits(), v[i].toBits())
        }
    }
}
