package io.github.kotlinmania.float8

import io.github.kotlinmania.half.F16
import io.github.kotlinmania.half.FpCategory
import kotlin.math.PI as MATH_PI
import kotlin.math.E as MATH_E
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.sqrt

/**
 * Eight bit floating point type with 5-bit exponent and 2-bit mantissa.
 */
public data class F8E5M2(public val rawBits: UByte = 0u) : Comparable<F8E5M2> {
    public val bits: Byte
        get() = rawBits.toByte()

    public fun toBits(): UByte = rawBits

    public fun toF16(): F16 = F16.fromBits(convertFp8ToFp16(rawBits, INTERPRETATION))

    public fun toF32(): Float = toF16().toF32Const()

    public fun toFloat(): Float = toF32()

    public fun toF64(): Double = toF16().toF64Const()

    public fun toDouble(): Double = toF64()

    public fun totalCmp(other: F8E5M2): Int {
        var left = toBits().toByte().toInt()
        var right = other.toBits().toByte().toInt()
        left = left xor (((left shr 7) and 0xFF) ushr 1)
        right = right xor (((right shr 7) and 0xFF) ushr 1)
        return left.compareTo(right)
    }

    public fun isSignPositive(): Boolean = (rawBits.toUInt() and 0x80u) == 0u

    public fun isSignNegative(): Boolean = (rawBits.toUInt() and 0x80u) != 0u

    public fun isNan(): Boolean = rawBits == 0x7Eu.toUByte() || rawBits == 0xFEu.toUByte()

    public fun isNaN(): Boolean = isNan()

    public fun isInfinite(): Boolean = (rawBits.toUInt() and 0x7Fu) == 0x7Bu

    public fun isFinite(): Boolean = !(isInfinite() || isNan())

    public fun isNormal(): Boolean {
        val exp = rawBits.toUInt() and 0b0_11111_00u
        return exp != 0u && isFinite()
    }

    public fun min(other: F8E5M2): F8E5M2 =
        if (!other.isNan() && (isNan() || other < this)) other else this

    public fun max(other: F8E5M2): F8E5M2 =
        if (!other.isNan() && (isNan() || other > this)) other else this

    public fun clamp(min: F8E5M2, max: F8E5M2): F8E5M2 {
        require(!min.isNan() && !max.isNan() && min <= max)
        if (isNan()) return this
        var x = this
        if (x < min) x = min
        if (x > max) x = max
        return x
    }

    public fun copysign(sign: F8E5M2): F8E5M2 =
        F8E5M2(((sign.rawBits.toUInt() and 0x80u) or (rawBits.toUInt() and 0x7Fu)).toUByte())

    public fun signum(): F8E5M2 = when {
        isNan() -> this
        (rawBits.toUInt() and 0x80u) != 0u -> NEG_ONE
        else -> ONE
    }

    public fun classify(): FpCategory = when {
        isInfinite() -> FpCategory.Infinite
        isNan() -> FpCategory.Nan
        (rawBits.toUInt() and 0x7Fu) == 0u -> FpCategory.Zero
        !isNormal() -> FpCategory.Subnormal
        else -> FpCategory.Normal
    }

    public operator fun unaryMinus(): F8E5M2 = fromF32(-toF32())

    public operator fun plus(rhs: F8E5M2): F8E5M2 = fromF32(toF32() + rhs.toF32())

    public operator fun minus(rhs: F8E5M2): F8E5M2 = fromF32(toF32() - rhs.toF32())

    public operator fun times(rhs: F8E5M2): F8E5M2 = fromF32(toF32() * rhs.toF32())

    public operator fun div(rhs: F8E5M2): F8E5M2 = fromF32(toF32() / rhs.toF32())

    public operator fun rem(rhs: F8E5M2): F8E5M2 = fromF32(toF32() % rhs.toF32())

    override fun equals(other: Any?): Boolean {
        if (other !is F8E5M2) return false
        if (isNan() || other.isNan()) return false
        return (rawBits == other.rawBits) || (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7Fu) == 0u)
    }

    override fun hashCode(): Int = rawBits.hashCode()

    override fun compareTo(other: F8E5M2): Int {
        val neg = (rawBits.toUInt() and 0x80u) != 0u
        val otherNeg = (other.rawBits.toUInt() and 0x80u) != 0u
        return when {
            !neg && !otherNeg -> rawBits.compareTo(other.rawBits)
            !neg && otherNeg -> if (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7Fu) == 0u) 0 else 1
            neg && !otherNeg -> if (((rawBits.toUInt() or other.rawBits.toUInt()) and 0x7Fu) == 0u) 0 else -1
            else -> other.rawBits.compareTo(rawBits)
        }
    }

    override fun toString(): String = toF32().toString()

    public companion object {
        private val INTERPRETATION: Kind = Kind.E5M2

        public operator fun invoke(bits: UByte): F8E5M2 = F8E5M2(bits)

        public fun invokeByte(bits: Byte): F8E5M2 = F8E5M2(bits.toUByte())

        public fun fromBits(bits: UByte): F8E5M2 = F8E5M2(bits)

        public fun fromByteBits(bits: Byte): F8E5M2 = F8E5M2(bits.toUByte())

        public fun fromF64(x: Double): F8E5M2 =
            F8E5M2(convertToFp8(x, SaturationType.SatFinite, INTERPRETATION))

        public fun fromDouble(x: Double): F8E5M2 = fromF64(x)

        public fun fromF32(x: Float): F8E5M2 = fromF64(x.toDouble())

        public fun fromFloat(x: Float): F8E5M2 = fromF32(x)

        public fun from(x: F16): F8E5M2 = fromF32(x.toF32())

        public fun from(x: Float): F8E5M2 = fromF32(x)

        public fun from(x: Double): F8E5M2 = fromF64(x)

        public fun fromStr(src: String): F8E5M2 = fromF32(src.toFloat())

        public const val MANTISSA_DIGITS: UInt = 2u
        public val MAX: F8E5M2 = fromBits((0x7Bu - 1u).toUByte())
        public val MIN: F8E5M2 = fromBits((0xFBu - 1u).toUByte())
        public val INFINITY: F8E5M2 = fromBits(0x7Bu.toUByte())
        public val NEG_INFINITY: F8E5M2 = fromBits(0xFBu.toUByte())
        public val MIN_POSITIVE: F8E5M2 = fromBits(0b0_00001_00u.toUByte())
        public val MIN_POSITIVE_SUBNORMAL: F8E5M2 = fromBits(0b0_00000_01u.toUByte())
        public val MAX_SUBNORMAL: F8E5M2 = fromBits(0b0_00000_11u.toUByte())
        public val EPSILON: F8E5M2 = fromBits(0b0_01101_00u.toUByte())
        public val NAN: F8E5M2 = fromBits(0x7Eu.toUByte())
        public val ONE: F8E5M2 = fromBits(0b0_01111_00u.toUByte())
        public val ZERO: F8E5M2 = fromBits(0b0_00000_00u.toUByte())
        public val NEG_ONE: F8E5M2 = fromBits(0b1_01111_00u.toUByte())
        public val NEG_ZERO: F8E5M2 = fromBits(0b1_00000_00u.toUByte())
        public const val MIN_EXP: Int = -13
        public const val MIN_10_EXP: Int = -4
        public const val MAX_EXP: Int = 15
        public const val MAX_10_EXP: Int = 4
        public const val DIGITS: UInt = 0u

        public val PI: F8E5M2 = fromF64(MATH_PI)
        public val TAU: F8E5M2 = fromF64(2.0 * MATH_PI)
        public val FRAC_PI_2: F8E5M2 = fromF64(MATH_PI / 2.0)
        public val FRAC_PI_3: F8E5M2 = fromF64(MATH_PI / 3.0)
        public val FRAC_PI_4: F8E5M2 = fromF64(MATH_PI / 4.0)
        public val FRAC_PI_6: F8E5M2 = fromF64(MATH_PI / 6.0)
        public val FRAC_PI_8: F8E5M2 = fromF64(MATH_PI / 8.0)
        public val FRAC_1_PI: F8E5M2 = fromF64(1.0 / MATH_PI)
        public val FRAC_2_PI: F8E5M2 = fromF64(2.0 / MATH_PI)
        public val FRAC_2_SQRT_PI: F8E5M2 = fromF64(2.0 / sqrt(MATH_PI))
        public val SQRT_2: F8E5M2 = fromF64(sqrt(2.0))
        public val FRAC_1_SQRT_2: F8E5M2 = fromF64(1.0 / sqrt(2.0))
        public val E: F8E5M2 = fromF64(MATH_E)
        public val LOG2_10: F8E5M2 = fromF64(log2(10.0))
        public val LOG2_E: F8E5M2 = fromF64(log2(MATH_E))
        public val LOG10_2: F8E5M2 = fromF64(log10(2.0))
        public val LOG10_E: F8E5M2 = fromF64(log10(MATH_E))
        public val LN_2: F8E5M2 = fromF64(ln(2.0))
        public val LN_10: F8E5M2 = fromF64(ln(10.0))
    }
}
