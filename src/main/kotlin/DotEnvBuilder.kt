package io.github.klahap.dotenv

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readLines


public class DotEnvBuilder {
    private var minor = 0
    private val envVariables = mutableListOf<Pair<EnvMap, Priority>>()
    private var _systemEnvPriority = Priority(major = 0, minor = 0)

    public var addSystemEnv: Boolean = false
    public var systemEnvPriority: Int
        get() = _systemEnvPriority.major
        set(value) {
            _systemEnvPriority = getPrio(major = value)
        }

    public fun addFile(path: String, priority: Int = 0): Unit = addFile(Path(path), priority = priority)
    public fun addFile(file: File, priority: Int = 0): Unit = addFile(file.path, priority = priority)

    public fun addFile(path: Path, priority: Int = 0) {
        val envMap = path.parse()
        envVariables.add(envMap to getPrio(priority))
    }

    public fun addEnv(key: String, value: String, priority: Int = 0): Unit =
        addEnvs(mapOf(key to value), priority = priority)

    public fun addEnvs(envVars: Map<String, String>, priority: Int = 0) {
        val envMap = EnvMap(envVars.map { EnvKey(it.key) to EnvValue(it.value) }.toMap())
        envVariables.add(envMap to getPrio(priority))
    }

    private fun getPrio(major: Int) = Priority(major = major, minor = minor++)

    private fun build() = buildList {
        if (addSystemEnv)
            add(systemEnv to Priority(0, 0))
        addAll(envVariables)
    }.merge().entries.associate { it.key.key to it.value.value }


    @JvmInline
    private value class EnvKey(val key: String)

    @JvmInline
    private value class EnvValue(val value: String)

    private class EnvMap(data: Map<EnvKey, EnvValue>) : LinkedHashMap<EnvKey, EnvValue>(data)

    private data class Priority(val major: Int, val minor: Int) : Comparable<Priority> {
        override fun compareTo(other: Priority): Int = if (major == other.major)
            minor.compareTo(other.minor)
        else
            major.compareTo(other.major)
    }

    public companion object {
        public fun dotEnv(block: DotEnvBuilder.() -> Unit): Map<String, String> = DotEnvBuilder().apply(block).build()

        private val systemEnv
            get() = System.getenv().map { EnvKey(it.key) to EnvValue(it.value) }.let { EnvMap(it.toMap()) }

        private fun List<Pair<EnvMap, Priority>>.merge(): EnvMap = sortedBy { it.second }.map { it.first }
            .fold(EnvMap(emptyMap())) { acc, e ->
                val result = acc.toMutableMap()
                e.entries.forEach { (k, v) -> result[k] = v }
                EnvMap(result)
            }

        private fun Path.parse(): EnvMap {
            if (!exists()) throw FileNotFoundException(toString())
            return readLines()
                .map { it.trim() }
                .filter { it.isNotBlank() && !it.startsWith('#') && it.contains('=') }
                .associate {
                    val key = EnvKey(it.substringBefore('=').trim())
                    val value = it.substringAfter('=').trim().unescapeEnvVar()
                    key to value
                }.let { EnvMap(it) }
        }

        private fun String.unescapeEnvVar(): EnvValue {
            if (length < 2) return EnvValue(this)
            if ((startsWith('\'') && endsWith('\'')) || startsWith('"') && endsWith('"'))
                return substring(1, length - 1)
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\'", "'")
                    .replace("\\\\", "\\")
                    .let { EnvValue(it) }
            return EnvValue(this)
        }
    }
}
