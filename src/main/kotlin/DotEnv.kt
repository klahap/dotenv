package io.github.klahap.dotenv

public data class DotEnv(
    private val data: Map<String, String>,
) : Map<String, String> by data {
    public fun getOrThrow(name: String): String =
        get(name) ?: throw NoSuchElementException("env variable '$name' not found")
}
