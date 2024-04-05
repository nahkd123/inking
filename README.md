# Inking
_Reading pen tablet inputs from Java. Actually no, it should be "Java bridge to pen tablet drivers"._

## Build Inking
### Prerequisites
- .NET 8 SDK (Software Development Kit). .NET runtime is **not needed** (but it will be included with your .NET SDK).
- Maven 3+
- JDK (Java Development Kit) 21.
- Git.
- GNU `make`.

### Instructions
- Clone this repository with Git: `git clone https://github.com/nahkd123/inking.git`
- Build natives for current platform: `cd inking-otd && make current-platform`
- Build Java with Maven: `mvn install`. This will also install Inking components to your local Maven repository.

## Components
### Inking API
The main API component. Inking API by itself is not that useful; you need an implementation.

### Inking Implementation (Inking OpenTabletDriver bridge)
> **Note:** As of 06/04/2024, there's only `inking-otd` implementation, which is the OpenTabletDriver bridge.

Inking implementations like `inking-otd` or `inking-windows` (Windows Ink) implements the `TabletDriver` interface. You can listen for packets, which are states reported from the tablet (pen states, tablet button states, etc).

### Inking Manager
Inking Manager provides a simple tablet configuration and filtering system. Configurations are serialized (and deserialized) with Mojang's DataFixerUpper `Codec`. Filters are connected in a chain, such that the first filter in the list applied first, and then second, the third and so on.

Inking Mananger also provide 2 basic filters: `AreaMappingTabletFilter` and `PressureMappingTabletFilter`.

### Inking Internals (`inking-internal-java21`)
Inking internals code that shouldn't be used by your application.

### Inking Maven Plugin
The Maven plugin that have 1 single Mojo: `strip-preview-flags`, which, as the name already said, strips preview flags on all `.class` files that was included from `--enable-preview`. This allows Inking to be used on Java 21 JRE without having to explicitly enable preview features.

> **Warning:** This behaviour might be changed in the future.

### Inking Samples (`inking-sample-otd`)
Inking usage through samples. Made for those that want to learn how to use Inking from samples.

## License
MIT License. See [LICENSE](./LICENSE) for notices.