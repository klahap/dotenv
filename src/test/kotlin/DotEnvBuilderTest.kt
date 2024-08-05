import io.github.klahap.dotenv.DotEnvBuilder.Companion.dotEnv
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.io.FileNotFoundException
import kotlin.io.path.Path
import kotlin.test.Test

class DotEnvBuilderTest {
    @Test
    fun `test empty`() {
        dotEnv { } shouldHaveSize 0
    }

    @Test
    fun `test system env`() {
        dotEnv { addSystemEnv() }.size shouldBeGreaterThan 0
    }

    @Test
    fun `test single system env`() {
        dotEnv {
            addSystemEnv("PATH") shouldBe true
        } shouldContainExactly mapOf("PATH" to System.getenv("PATH"))
        dotEnv {
            addSystemEnv("NOT_EXIST_ENV_123") shouldBe false
        } shouldContainExactly emptyMap()
    }

    @Test
    fun `test file not exists`() {
        shouldThrowExactly<FileNotFoundException> {
            dotEnv { addFile(Path("./not-exists.env")) }
        }
    }

    @Test
    fun `test env file`() {
        val expectedResult = mapOf(
            "foobar1" to "hello world",
            "foobar2" to "hello world",
            "foobar3" to "hello world",
            "foobar4" to "hello world",
            "foobarA" to "hello",
        )
        dotEnv {
            addFile(pathA)
        } shouldContainExactly expectedResult
        dotEnv {
            addFile(pathA.toFile())
        } shouldContainExactly expectedResult
        dotEnv {
            addFile(pathA.toString())
        } shouldContainExactly expectedResult
    }

    @Test
    fun `test 2 env files`() {
        dotEnv {
            addFile(pathA)
            addFile(pathB)
        } shouldContainExactly mapOf(
            "foobar1" to "howdy guys",
            "foobar2" to "howdy guys",
            "foobar3" to "hello world",
            "foobar4" to "hello world",
            "foobarA" to "hello",
            "foobarB" to "howdy",
        )
        dotEnv {
            addFile(pathB)
            addFile(pathA)
        } shouldContainExactly mapOf(
            "foobar1" to "hello world",
            "foobar2" to "hello world",
            "foobar3" to "hello world",
            "foobar4" to "hello world",
            "foobarA" to "hello",
            "foobarB" to "howdy",
        )
    }

    @Test
    fun `test 2 env files with priority`() {
        dotEnv {
            addFile(pathA, priority = 1)
            addFile(pathB)
        } shouldContainExactly mapOf(
            "foobar1" to "hello world",
            "foobar2" to "hello world",
            "foobar3" to "hello world",
            "foobar4" to "hello world",
            "foobarA" to "hello",
            "foobarB" to "howdy",
        )
    }

    @Test
    fun `test 2 env files and custom vars`() {
        dotEnv {
            addFile(pathA)
            addFile(pathB)
            addEnv("foobar2", "hi")
            addEnvs(mapOf("foobar4" to "hi"))
        } shouldContainExactly mapOf(
            "foobar1" to "howdy guys",
            "foobar2" to "hi",
            "foobar3" to "hello world",
            "foobar4" to "hi",
            "foobarA" to "hello",
            "foobarB" to "howdy",
        )
    }

    @Test
    fun `test 2 env files and custom vars and priority`() {
        dotEnv {
            addFile(pathA, priority = 0)
            addFile(pathB, priority = -1)
            addEnv("foobar2", "hi", priority = -2)
            addEnv("foobarC", "hi", priority = -4)
            addEnvs(mapOf("foobar4" to "hi"), priority = -2)
        } shouldContainExactly mapOf(
            "foobar1" to "hello world",
            "foobar2" to "hello world",
            "foobar3" to "hello world",
            "foobar4" to "hello world",
            "foobarA" to "hello",
            "foobarB" to "howdy",
            "foobarC" to "hi",
        )
    }

    @Test
    fun `test special values`() {
        dotEnv {
            addFile(pathSpecialValues)
        } shouldContainExactly mapOf(
            "foobar1" to "a",
            "foobar2" to "\"hello",
            "foobar3" to "'hello",
            "foobar4" to "hello\"",
            "foobar5" to "hello'",
            "foobar6" to "\"hello\nworld\"",
            "foobar7" to "'hello\nworld'",
            "foobar8" to "'hello\tworld'",
        )
    }

    companion object {
        val pathA = Path(DotEnvBuilderTest::class.java.getResource(".a.env")!!.path)
        val pathB = Path(DotEnvBuilderTest::class.java.getResource(".b.env")!!.path)
        val pathSpecialValues = Path(DotEnvBuilderTest::class.java.getResource(".special.values.env")!!.path)
    }
}