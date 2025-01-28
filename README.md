# Dotenv Parser for Kotlin

![GitHub License](https://img.shields.io/github/license/klahap/dotenv)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/klahap/dotenv/check.yml)
![Static Badge](https://img.shields.io/badge/coverage-100%25-success)

A Kotlin Gradle plugin for parsing and loading environment variables from `.env` files. Simplify your configuration management by seamlessly integrating environment variables into your Kotlin projects.

## Features

- Parse `.env` files and load environment variables
- Support for multiple `.env` files with different priorities
- Add environment variables programmatically
- Customize whether system environment variables are included

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
plugins {
    id("io.github.klahap.dotenv") version "$VERSION"
}
```

## Example `build.gradle.kts`

Assume you have a `.env` files:

**.env**
```
foobar=hello world
```

Using the plugin as follows:

```kotlin
import io.github.klahap.dotenv.DotEnvBuilder

val envVars = DotEnvBuilder.dotEnv {
    // addSystemEnv()
    addFile("$rootDir/.env")
}

envVars.get("foobar") // = null
envVars.getOrThrow("foobar") // = "hello world"

envVars.get("test") // = null
envVars.getOrThrow("test") // ERROR
```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.
