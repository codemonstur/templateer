
[![GitHub Release](https://img.shields.io/github/release/codemonstur/templateer.svg)](https://github.com/codemonstur/templateer/releases) 
[![Maven Central](https://img.shields.io/maven-central/v/com.github.codemonstur/templateer.svg)](https://search.maven.org/artifact/com.github.codemonstur/templateer)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

# Templateer

A small tool that applies a set of variables to a template file and writes the result.
It supports **9 template engines** out of the box and can be used as a standalone CLI
tool or as a [Bob](https://github.com/codemonstur/bobthebuildtool) plugin.

## Supported engines

| Engine | Enum name | Auto-detected extension |
|--------|-----------|------------------------|
| Handlebars | `handlebars` | `.handlebars` |
| Jade (Jade4J) | `jade` | `.jade` |
| Jinjava | `jinjava` | `.jinjava` |
| Jtwig | `jtwig` | `.jtwig` |
| Mustache (Trimou) | `mustache` | `.mustache` |
| Pebble | `pebble` | `.pebble` |
| Thymeleaf | `thymeleaf` | `.thymeleaf` |
| Freemarker | `freemarker` | `.ftl` |
| Velocity | `velocity` | `.vm` |

When no engine is specified, templateer auto-detects it from the file extension.
Unknown extensions default to Pebble.

## Install

Get the jar
- Download the jar from the releases section. Or,
- Check out the codebase and compile with `mvn clean package`.

Execute the jar
- `java -jar templateer.jar [args]`
- Place it in a convenient location such as `/usr/local/bin`
- Create a nice alias for it `alias templateer='java -jar /usr/local/bin/templateer.jar'`

## Usage

```
templateer -f <inputFile> -o <outputFile> [-m <modelFile>] [-e] [-t <type>]
```

| Option | Long form | Description |
|--------|-----------|-------------|
| `-f <file>` | `--inputFile <file>` | The template file (mandatory) |
| `-o <file>` | `--outputFile <file>` | The output file (mandatory) |
| `-m <file>` | `--model <file>` | A properties file with variables to apply |
| `-e` | `--env` | Also load environment variables as model values |
| `-t <type>` | `--type <type>` | Force a template engine (overrides auto-detection) |

### Examples

```bash
# Auto-detect engine from extension, model from properties file
templateer -f template.ftl -o output.txt -m vars.properties

# Use environment variables as the model
templateer -f template.vm -o output.txt -e

# Force a specific engine regardless of extension
templateer -f input.txt -o output.txt -m vars.properties -t thymeleaf
```

## As a Bob plugin

Add the plugin to the top of your `bobfile`:

```
plugin codemonstur/templateer:6
```

Then use the `templateer` command in any task:

```
my-task
    templateer -f config/app.properties.template -o config/app.properties -m vars.properties
```

## Building

```bash
mvn clean package
```

The shaded jar is written to `target/templateer.jar`.

## License

MIT
