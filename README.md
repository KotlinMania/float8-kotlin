# float8-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Ffloat8-kotlin-blue.svg)](https://github.com/KotlinMania/float8-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/float8-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/float8-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/float8-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/float8-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`float8`](https://github.com/LaurentMazare/float8).

**Original Project:** This port is based on [`float8`](https://github.com/LaurentMazare/float8). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `float8`

> The text below is reproduced from [`https://github.com/LaurentMazare/float8`](https://github.com/LaurentMazare/float8). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs where appropriate so they continue to resolve from this repository.

# `float8`: 8-bit floating point types for Rust

This crate provides 2 types:
- `F8E4M3`: Sign + 4-bit exponent + 3-bit mantissa. More precise but less dynamic range.
- `F8E5M2`: Sign + 5-bit exponent + 2-bit mantissa. Less precise but more dynamic range (same exponent as `f16`).

Generally, this crate is modelled after the `half` crate, so it can be
used alongside and with minimal code changes.

- This crate provides `no_std` support
- Requires Rust 1.70 or greater

## Optional features
- `std` - Enable features that depend on the Rust standard library.
- `serde` - Add support for the `serde` crate with `Serialize` and `Deserialize` traits.
- `num-traits` - Implement traits from `num-traits` such as `ToPrimitive`, `FromPrimitive`, `AsPrimitive`, `Num`, `Float`, `FloatCore`, and `Bounded`.
- `bytemuck` - Implement traits from `bytemuck` including `Zeroable` and `Pod`
- `zerocopy` - Implement traits from `zerocopy` including `AsBytes` and `FromBytes`
- `rand_distr` - Implement traits from `rand_distr` including `Distribution` and others
- `rkyv` - Enable zero-copy deserialization with `rkyv`.

## Resources
- Good introduction: https://en.wikipedia.org/wiki/Minifloat
- Paper: https://arxiv.org/pdf/2209.05433

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:float8-kotlin:0.1.0")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same license terms as the upstream [`float8`](https://github.com/LaurentMazare/float8). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the float8 authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`float8`](https://github.com/LaurentMazare/float8) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
