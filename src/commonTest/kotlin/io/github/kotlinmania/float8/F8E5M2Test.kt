// port-lint: tests tests/f8e5m2.rs
package io.github.kotlinmania.float8

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.math.E as MATH_E
import kotlin.math.PI as MATH_PI

class F8E5M2Test {
    // num_traits AsPrimitive/ToPrimitive/FromPrimitive tests omitted: num-traits is an optional cargo feature not included in the core float8 port.

    @Test
    fun testF8e5m2Consts() {
        val digits = ((F8E5M2.MANTISSA_DIGITS.toFloat() - 1.0f) * log10(2.0f)).let { floor(it).toUInt() }
        assertEquals(F8E5M2.DIGITS, digits)

        val one = F8E5M2.fromF32(1.0f)
        val onePlusEpsilon = F8E5M2.fromBits((one.toBits().toUInt() + 1u).toUByte())
        val epsilon = F8E5M2.fromF32(onePlusEpsilon.toF32() - 1.0f)
        assertEquals(F8E5M2.EPSILON, epsilon)

        val max = F8E5M2.fromBits((F8E5M2.INFINITY.toBits().toUInt() - 1u).toUByte())
        val min = F8E5M2.fromBits((F8E5M2.NEG_INFINITY.toBits().toUInt() - 1u).toUByte())
        val minPos = F8E5M2.fromF32(2.0f.pow(F8E5M2.MIN_EXP - 1))
        assertEquals(F8E5M2.MAX, max)
        assertEquals(F8E5M2.MIN, min)
        assertEquals(F8E5M2.MIN_POSITIVE, minPos)

        val tenToMin = 10.0f.pow(F8E5M2.MIN_10_EXP)
        assertTrue(tenToMin / 10.0f < F8E5M2.MIN_POSITIVE.toF32())
        assertTrue(tenToMin > F8E5M2.MIN_POSITIVE.toF32())
        val tenToMax = 10.0f.pow(F8E5M2.MAX_10_EXP)
        assertTrue(tenToMax < F8E5M2.MAX.toF32())
        assertTrue(tenToMax * 10.0f > F8E5M2.MAX.toF32())
    }

    @Test
    fun testF8e5m2ConstsFromF32() {
        val one = F8E5M2.fromF32(1.0f)
        val zero = F8E5M2.fromF32(0.0f)
        val negZero = F8E5M2.fromF32(-0.0f)
        val negOne = F8E5M2.fromF32(-1.0f)
        val inf = F8E5M2.fromF32(Float.POSITIVE_INFINITY)
        val negInf = F8E5M2.fromF32(Float.NEGATIVE_INFINITY)
        val nan = F8E5M2.fromF32(Float.NaN)

        assertEquals(F8E5M2.ONE, one)
        assertEquals(F8E5M2.ZERO, zero)
        assertTrue(zero.isSignPositive())
        assertEquals(F8E5M2.NEG_ZERO, negZero)
        assertTrue(negZero.isSignNegative())
        assertEquals(F8E5M2.NEG_ONE, negOne)
        assertTrue(negOne.isSignNegative())
        assertEquals(F8E5M2.INFINITY, inf)
        assertEquals(F8E5M2.NEG_INFINITY, negInf)
        assertTrue(inf.isInfinite())
        assertTrue(F8E5M2.INFINITY.isInfinite())
        assertTrue(negInf.isInfinite())
        assertTrue(F8E5M2.NEG_INFINITY.isInfinite())
        assertTrue(nan.isNan())
        assertTrue(F8E5M2.NAN.isNan())

        val e = F8E5M2.fromF32(MATH_E.toFloat())
        val pi = F8E5M2.fromF32(MATH_PI.toFloat())
        val frac1Pi = F8E5M2.fromF32((1.0 / MATH_PI).toFloat())
        val frac1Sqrt2 = F8E5M2.fromF32((1.0 / sqrt(2.0)).toFloat())
        val frac2Pi = F8E5M2.fromF32((2.0 / MATH_PI).toFloat())
        val frac2SqrtPi = F8E5M2.fromF32((2.0 / sqrt(MATH_PI)).toFloat())
        val fracPi2 = F8E5M2.fromF32((MATH_PI / 2.0).toFloat())
        val fracPi3 = F8E5M2.fromF32((MATH_PI / 3.0).toFloat())
        val fracPi4 = F8E5M2.fromF32((MATH_PI / 4.0).toFloat())
        val fracPi6 = F8E5M2.fromF32((MATH_PI / 6.0).toFloat())
        val fracPi8 = F8E5M2.fromF32((MATH_PI / 8.0).toFloat())
        val ln10 = F8E5M2.fromF32(ln(10.0).toFloat())
        val ln2 = F8E5M2.fromF32(ln(2.0).toFloat())
        val log10E = F8E5M2.fromF32(log10(MATH_E).toFloat())
        val log102 = F8E5M2.fromF32(log10(2.0f))
        val log2E = F8E5M2.fromF32(log2(MATH_E).toFloat())
        val log210 = F8E5M2.fromF32(log2(10.0f))
        val sqrt2 = F8E5M2.fromF32(sqrt(2.0).toFloat())

        assertEquals(F8E5M2.E, e)
        assertEquals(F8E5M2.PI, pi)
        assertEquals(F8E5M2.FRAC_1_PI, frac1Pi)
        assertEquals(F8E5M2.FRAC_1_SQRT_2, frac1Sqrt2)
        assertEquals(F8E5M2.FRAC_2_PI, frac2Pi)
        assertEquals(F8E5M2.FRAC_2_SQRT_PI, frac2SqrtPi)
        assertEquals(F8E5M2.FRAC_PI_2, fracPi2)
        assertEquals(F8E5M2.FRAC_PI_3, fracPi3)
        assertEquals(F8E5M2.FRAC_PI_4, fracPi4)
        assertEquals(F8E5M2.FRAC_PI_6, fracPi6)
        assertEquals(F8E5M2.FRAC_PI_8, fracPi8)
        assertEquals(F8E5M2.LN_10, ln10)
        assertEquals(F8E5M2.LN_2, ln2)
        assertEquals(F8E5M2.LOG10_E, log10E)
        assertEquals(F8E5M2.LOG10_2, log102)
        assertEquals(F8E5M2.LOG2_E, log2E)
        assertEquals(F8E5M2.LOG2_10, log210)
        assertEquals(F8E5M2.SQRT_2, sqrt2)
    }

    @Test
    fun testF8e5m2ConstsFromF64() {
        val one = F8E5M2.fromF64(1.0)
        val zero = F8E5M2.fromF64(0.0)
        val negZero = F8E5M2.fromF64(-0.0)
        val inf = F8E5M2.fromF64(Double.POSITIVE_INFINITY)
        val negInf = F8E5M2.fromF64(Double.NEGATIVE_INFINITY)
        val nan = F8E5M2.fromF64(Double.NaN)

        assertEquals(F8E5M2.ONE, one)
        assertEquals(F8E5M2.ZERO, zero)
        assertTrue(zero.isSignPositive())
        assertEquals(F8E5M2.NEG_ZERO, negZero)
        assertTrue(negZero.isSignNegative())
        assertEquals(F8E5M2.INFINITY, inf)
        assertEquals(F8E5M2.NEG_INFINITY, negInf)
        assertTrue(nan.isNan())
        assertTrue(F8E5M2.NAN.isNan())

        val e = F8E5M2.fromF64(MATH_E)
        val pi = F8E5M2.fromF64(MATH_PI)
        val frac1Pi = F8E5M2.fromF64(1.0 / MATH_PI)
        val frac1Sqrt2 = F8E5M2.fromF64(1.0 / sqrt(2.0))
        val frac2Pi = F8E5M2.fromF64(2.0 / MATH_PI)
        val frac2SqrtPi = F8E5M2.fromF64(2.0 / sqrt(MATH_PI))
        val fracPi2 = F8E5M2.fromF64(MATH_PI / 2.0)
        val fracPi3 = F8E5M2.fromF64(MATH_PI / 3.0)
        val fracPi4 = F8E5M2.fromF64(MATH_PI / 4.0)
        val fracPi6 = F8E5M2.fromF64(MATH_PI / 6.0)
        val fracPi8 = F8E5M2.fromF64(MATH_PI / 8.0)
        val ln10 = F8E5M2.fromF64(ln(10.0))
        val ln2 = F8E5M2.fromF64(ln(2.0))
        val log10E = F8E5M2.fromF64(log10(MATH_E))
        val log102 = F8E5M2.fromF64(log10(2.0))
        val log2E = F8E5M2.fromF64(log2(MATH_E))
        val log210 = F8E5M2.fromF64(log2(10.0))
        val sqrt2 = F8E5M2.fromF64(sqrt(2.0))

        assertEquals(F8E5M2.E, e)
        assertEquals(F8E5M2.PI, pi)
        assertEquals(F8E5M2.FRAC_1_PI, frac1Pi)
        assertEquals(F8E5M2.FRAC_1_SQRT_2, frac1Sqrt2)
        assertEquals(F8E5M2.FRAC_2_PI, frac2Pi)
        assertEquals(F8E5M2.FRAC_2_SQRT_PI, frac2SqrtPi)
        assertEquals(F8E5M2.FRAC_PI_2, fracPi2)
        assertEquals(F8E5M2.FRAC_PI_3, fracPi3)
        assertEquals(F8E5M2.FRAC_PI_4, fracPi4)
        assertEquals(F8E5M2.FRAC_PI_6, fracPi6)
        assertEquals(F8E5M2.FRAC_PI_8, fracPi8)
        assertEquals(F8E5M2.LN_10, ln10)
        assertEquals(F8E5M2.LN_2, ln2)
        assertEquals(F8E5M2.LOG10_E, log10E)
        assertEquals(F8E5M2.LOG10_2, log102)
        assertEquals(F8E5M2.LOG2_E, log2E)
        assertEquals(F8E5M2.LOG2_10, log210)
        assertEquals(F8E5M2.SQRT_2, sqrt2)
    }

    @Test
    fun testNanConversionToSmaller() {
        val nan64 = Double.fromBits(0x7FF0_0000_0000_0001L)
        val negNan64 = Double.fromBits(0xFFF0_0000_0000_0001uL.toLong())
        val nan32 = Float.fromBits(0x7F80_0001)
        val negNan32 = Float.fromBits(0xFF80_0001u.toInt())
        val nan32From64 = nan64.toFloat()
        val negNan32From64 = negNan64.toFloat()
        val nan8From64 = F8E5M2.fromF64(nan64)
        val negNan8From64 = F8E5M2.fromF64(negNan64)
        val nan8From32 = F8E5M2.fromF32(nan32)
        val negNan8From32 = F8E5M2.fromF32(negNan32)

        assertTrue(nan64.isNaN() && ((nan64.toRawBits().toULong() and 0x8000_0000_0000_0000uL) == 0uL))
        assertTrue(negNan64.isNaN() && ((negNan64.toRawBits().toULong() and 0x8000_0000_0000_0000uL) != 0uL))
        assertTrue(nan32.isNaN() && ((nan32.toRawBits().toUInt() and 0x8000_0000u) == 0u))
        assertTrue(negNan32.isNaN() && ((negNan32.toRawBits().toUInt() and 0x8000_0000u) != 0u))

        assertTrue(nan32From64.isNaN())
        assertTrue(negNan32From64.isNaN())
        assertTrue(nan8From64.isNan())
        assertTrue(negNan8From64.isNan())
        assertTrue(nan8From32.isNan())
        assertTrue(negNan8From32.isNan())
    }

    @Test
    fun testNanConversionToLarger() {
        val nan8 = F8E5M2.fromBits(0x7Eu)
        val negNan8 = F8E5M2.fromBits(0xFEu)
        val nan32 = Float.fromBits(0x7F80_0001)
        val negNan32 = Float.fromBits(0xFF80_0001u.toInt())
        val nan32From8 = nan8.toF32()
        val negNan32From8 = negNan8.toF32()
        val nan64From8 = nan8.toF64()
        val negNan64From8 = negNan8.toF64()
        val nan64From32 = nan32.toDouble()
        val negNan64From32 = negNan32.toDouble()

        assertTrue(nan8.isNan() && nan8.isSignPositive())
        assertTrue(negNan8.isNan() && negNan8.isSignNegative())
        assertTrue(nan32.isNaN() && ((nan32.toRawBits().toUInt() and 0x8000_0000u) == 0u))
        assertTrue(negNan32.isNaN() && ((negNan32.toRawBits().toUInt() and 0x8000_0000u) != 0u))

        assertTrue(nan32From8.isNaN())
        assertTrue(negNan32From8.isNaN())
        assertTrue(nan64From8.isNaN())
        assertTrue(negNan64From8.isNaN())
        assertTrue(nan64From32.isNaN())
        assertTrue(negNan64From32.isNaN())
    }

    @Test
    fun testF8e5m2ToF32() {
        val f = F8E5M2.fromF32(7.0f)
        assertEquals(7.0f, f.toF32())

        val f71 = F8E5M2.fromF32(7.1f)
        val diff = abs(f71.toF32() - 7.1f)
        assertTrue(diff <= 4.0f * F8E5M2.EPSILON.toF32())

        assertEquals(2.0f.pow(-16), F8E5M2.fromBits(0x01u).toF32())
        assertEquals(3.0f * 2.0f.pow(-16), F8E5M2.fromBits(0x03u).toF32())

        assertEquals(F8E5M2.fromBits(0x01u), F8E5M2.fromF32(2.0f.pow(-16)))
        assertEquals(F8E5M2.fromBits(0x03u), F8E5M2.fromF32(3.0f * 2.0f.pow(-16)))
    }

    @Test
    fun testF8e5m2ToF64() {
        val f = F8E5M2.fromF64(7.0)
        assertEquals(7.0, f.toF64())

        val f71 = F8E5M2.fromF64(7.1)
        val diff = abs(f71.toF64() - 7.1)
        assertTrue(diff <= 4.0 * F8E5M2.EPSILON.toF64())

        assertEquals(2.0.pow(-16), F8E5M2.fromBits(0x01u).toF64())
        assertEquals(3.0 * 2.0.pow(-16), F8E5M2.fromBits(0x03u).toF64())

        assertEquals(F8E5M2.fromBits(0x01u), F8E5M2.fromF64(2.0.pow(-16)))
        assertEquals(F8E5M2.fromBits(0x03u), F8E5M2.fromF64(3.0 * 2.0.pow(-16)))
    }

    @Test
    fun testComparisons() {
        val zero = F8E5M2.fromF64(0.0)
        val one = F8E5M2.fromF64(1.0)
        val negZero = F8E5M2.fromF64(-0.0)
        val negOne = F8E5M2.fromF64(-1.0)

        assertEquals(0, zero.compareTo(negZero))
        assertEquals(0, negZero.compareTo(zero))
        assertTrue(zero == negZero)
        assertTrue(negZero == zero)
        assertFalse(zero != negZero)
        assertFalse(negZero != zero)
        assertFalse(zero < negZero)
        assertFalse(negZero < zero)
        assertTrue(zero <= negZero)
        assertTrue(negZero <= zero)
        assertFalse(zero > negZero)
        assertFalse(negZero > zero)
        assertTrue(zero >= negZero)
        assertTrue(negZero >= zero)

        assertTrue(one.compareTo(negZero) > 0)
        assertTrue(negZero.compareTo(one) < 0)
        assertFalse(one == negZero)
        assertFalse(negZero == one)
        assertTrue(one != negZero)
        assertTrue(negZero != one)
        assertFalse(one < negZero)
        assertTrue(negZero < one)
        assertFalse(one <= negZero)
        assertTrue(negZero <= one)
        assertTrue(one > negZero)
        assertFalse(negZero > one)
        assertTrue(one >= negZero)
        assertFalse(negZero >= one)

        assertTrue(one.compareTo(negOne) > 0)
        assertTrue(negOne.compareTo(one) < 0)
        assertFalse(one == negOne)
        assertFalse(negOne == one)
        assertTrue(one != negOne)
        assertTrue(negOne != one)
        assertFalse(one < negOne)
        assertTrue(negOne < one)
        assertFalse(one <= negOne)
        assertTrue(negOne <= one)
        assertTrue(one > negOne)
        assertFalse(negOne > one)
        assertTrue(one >= negOne)
        assertFalse(negOne >= one)
    }

    @Test
    fun arithmetic() {
        assertEquals(F8E5M2.fromF32(2.0f), F8E5M2.ONE + F8E5M2.ONE)
        assertEquals(F8E5M2.ZERO, F8E5M2.ONE - F8E5M2.ONE)
        assertEquals(F8E5M2.ONE, F8E5M2.ONE * F8E5M2.ONE)
        assertEquals(F8E5M2.fromF32(4.0f), F8E5M2.fromF32(2.0f) * F8E5M2.fromF32(2.0f))
        assertEquals(F8E5M2.ONE, F8E5M2.ONE / F8E5M2.ONE)
        assertEquals(F8E5M2.fromF32(2.0f), F8E5M2.fromF32(4.0f) / F8E5M2.fromF32(2.0f))
        assertEquals(F8E5M2.fromF32(1.0f), F8E5M2.fromF32(4.0f) % F8E5M2.fromF32(3.0f))
    }

    @Test
    fun formatting() {
        val f = F8E5M2.fromF32(0.1152344f)
        assertTrue(f.toString().startsWith("0.117") || f.toString().startsWith("0.1"))
    }
}
