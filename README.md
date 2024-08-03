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

## Usage

Here's how you can use the `Dotenv` plugin in your Kotlin project:

```kotlin
val envVariables: Map<String, String> = dotEnv {
    addSystemEnv()
    addFile(".env1")
    addFile(".env2", priority = -1)
    addEnv("MY_ENV_VAR", "hello world")
    addEnvs(
        mapOf(
            "ANOTHER_ENV_VAR" to "hi",
        ),
        priority = -2,
    )
}
```

### Priority

The priority determines the order in which the environment variables are loaded. Variables from files or entries with a higher priority (numerically greater) will override those with a lower priority.

## Example

Assume you have two `.env` files:

**.env1**
```
# this is a comment
foobar=hello world
foobarA=hello
```

**.env2**
```
foobar=howdy guys
foobarB=howdy
```

Using the plugin as follows:

```kotlin
dotEnv {
    addFile(".env1")
    addFile(".env2") // .env2 overwrites variables from .env1
} 
/* Result:
    foobar  -> howdy guys
    foobarA -> hello
    foobarB -> howdy
*/


dotEnv {
    addFile(".env1", priority = 1)
    addFile(".env2") // .env2 does not overwrite anything because of lower priority (default priority = 0)
}
/* Result:
    foobar  -> hello world
    foobarA -> hello
    foobarB -> howdy
*/
```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.
