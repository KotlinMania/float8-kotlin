package io.github.kotlinmania.float8

internal enum class Kind {
    E4M3,
    E5M2,
}

internal enum class SaturationType {
    NoSat,
    SatFinite,
}

internal fun convertToFp8(x: Double, saturate: SaturationType, fp8Interpretation: Kind): UByte {
    val xbits: ULong = x.toRawBits().toULong()

    val fp8Maxnorm: UByte
    val fp8MantissaMask: UByte
    val fp8ExpBias: UShort
    val fp8SignificandBits: ULong
    val fp8MindenormO2: ULong
    val fp8OverflowThreshold: ULong
    val fp8Minnorm: ULong

    when (fp8Interpretation) {
        Kind.E4M3 -> {
            fp8Maxnorm = 0x7Eu
            fp8MantissaMask = 0x7u
            fp8ExpBias = 7u
            fp8SignificandBits = 4uL
            fp8MindenormO2 = 0x3F50000000000000uL
            fp8OverflowThreshold = 0x407D000000000000uL
            fp8Minnorm = 0x3F90000000000000uL
        }

        Kind.E5M2 -> {
            fp8Maxnorm = 0x7Bu
            fp8MantissaMask = 0x3u
            fp8ExpBias = 15u
            fp8SignificandBits = 3uL
            fp8MindenormO2 = 0x3EE0000000000000uL
            fp8OverflowThreshold = 0x40EE000000000000uL - 1uL
            fp8Minnorm = 0x3F10000000000000uL
        }
    }

    val dpInfBits = 0x7FF0000000000000uL
    val fp8DpHalfUlp: ULong = 1uL shl (53 - fp8SignificandBits.toInt() - 1)
    val sign: UByte = (((xbits shr 63) shl 7) and 0xFFuL).toUByte()
    val exp: UByte = (((((xbits shr 52).toUInt() and 0x7FFu) - 1023u + fp8ExpBias.toUInt()) and 0xFFu)).toUByte()
    val mantissa: UByte = ((xbits shr (53 - fp8SignificandBits.toInt())) and fp8MantissaMask.toULong()).toUByte()
    val absx: ULong = xbits and 0x7FFFFFFFFFFFFFFFuL

    val res: UByte =
        when {
            absx <= fp8MindenormO2 -> {
                0u
            }

            absx > dpInfBits -> {
                when (fp8Interpretation) {
                    Kind.E4M3 -> 0x7Fu
                    Kind.E5M2 -> (0x7Eu or mantissa.toUInt()).toUByte()
                }
            }

            absx > fp8OverflowThreshold -> {
                when (saturate) {
                    SaturationType.SatFinite -> {
                        fp8Maxnorm
                    }

                    SaturationType.NoSat -> {
                        when (fp8Interpretation) {
                            Kind.E4M3 -> 0x7Fu
                            Kind.E5M2 -> 0x7Cu
                        }
                    }
                }
            }

            absx >= fp8Minnorm -> {
                var r = ((exp.toUInt() shl (fp8SignificandBits.toInt() - 1)) or mantissa.toUInt()) and 0xFFu
                val round = xbits and ((fp8DpHalfUlp shl 1) - 1uL)
                if ((round > fp8DpHalfUlp) || ((round == fp8DpHalfUlp) && ((mantissa.toUInt() and 1u) != 0u))) {
                    r = (r + 1u) and 0xFFu
                }
                r.toUByte()
            }

            else -> {
                val shift = (1u - exp.toUInt()) and 0xFFu
                val m = mantissa.toUInt() or (1u shl (fp8SignificandBits.toInt() - 1))
                var r = (m shr shift.toInt()) and 0xFFu
                val round = (xbits or (1uL shl 52)) and ((fp8DpHalfUlp shl (shift.toInt() + 1)) - 1uL)
                val threshold = fp8DpHalfUlp shl shift.toInt()
                if ((round > threshold) || ((round == threshold) && ((r and 1u) != 0u))) {
                    r = (r + 1u) and 0xFFu
                }
                r.toUByte()
            }
        }

    return (res.toUInt() or sign.toUInt()).toUByte()
}

internal fun convertFp8ToFp16(x: UByte, fp8Interpretation: Kind): UShort {
    var ur = (x.toUInt() shl 8) and 0xFFFFu

    when (fp8Interpretation) {
        Kind.E5M2 -> {
            if ((ur and 0x7FFFu) > 0x7C00u) {
                ur = 0x7FFFu
            }
        }

        Kind.E4M3 -> {
            val sign = ur and 0x8000u
            var exponent = (((ur and 0x7800u) shr 1) + 0x2000u) and 0xFFFFu
            var mantissa = (ur and 0x0700u) shr 1
            val absx = 0x7Fu and x.toUInt()

            if (absx == 0x7Fu) {
                ur = 0x7FFFu
            } else if (exponent == 0x2000u) {
                if (mantissa != 0u) {
                    mantissa = (mantissa shl 1) and 0xFFFFu
                    while ((mantissa and 0x0400u) == 0u) {
                        mantissa = (mantissa shl 1) and 0xFFFFu
                        exponent = (exponent - 0x0400u) and 0xFFFFu
                    }
                    mantissa = mantissa and 0x03FFu
                } else {
                    exponent = 0u
                }
                ur = sign or exponent or mantissa
            } else {
                ur = sign or exponent or mantissa
            }
        }
    }

    return ur.toUShort()
}
