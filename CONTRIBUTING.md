# Contributing to OpenUniverse

Thank you for considering contributing to OpenUniverse. Whether you're fixing bugs, improving documentation, building new features, or helping others, your contribution is welcome and appreciated.

This guide outlines how to contribute effectively and collaboratively.

---

## Table of Contents

- Code of Conduct
- Ways to Contribute
- Reporting Bugs
- Suggesting Enhancements
- Contributing Code
- Development Environment Setup
- Coding Guidelines
- Testing
- Submitting a Pull Request
- License

---

## Code of Conduct

All contributors are expected to adhere to the [Code of Conduct](./CODE_OF_CONDUCT.md). We are committed to providing a friendly, safe, and respectful environment for everyone.

---

## Ways to Contribute

You can contribute in many ways:

- Reporting bugs
- Suggesting new features
- Improving documentation
- Submitting code fixes or improvements
- Helping others by answering questions

No contribution is too small. Every bit of effort helps improve the project.

---

## Reporting Bugs

If you find a bug, please help us by submitting an issue on GitHub. When reporting a bug, include the following:

- A clear and descriptive title
- Steps to reproduce the issue
- Expected and actual behavior
- Version of OpenUniverse, Java, and OS
- Any relevant logs, stack traces, or configuration files

Please check if the issue already exists before submitting a new one.

---

## Suggesting Enhancements

Enhancement suggestions help us understand what features the community wants. To suggest an enhancement:

- Clearly describe the problem or use case
- Explain how the proposed feature would solve it
- Include examples or sketches if applicable
- Consider alternatives or related solutions

You can submit feature requests as GitHub issues or participate in the discussion if a related issue already exists.

---

## Contributing Code

Follow these steps to contribute code to OpenUniverse:

```bash
git clone https://github.com/YOUR_USERNAME/OpenUniverse.git
cd OpenUniverse
git checkout -b feature/my-feature-name
# Make your changes
git commit -m "Add custom trigger support"
git push origin feature/my-feature-name
# Then open a pull request against the `main` branch

---

## Development Environment Setup

OpenUniverse is a JVM-based project built with Gradle.

Requirements:

* Linux (preferred)
* Java 17 or newer
* Gradle
* Git

To build:

```bash
./gradlew clean build
```

To run:

```bash
java -jar build/libs/openuniverse.jar config/main.json
```

---

## Coding Guidelines

Maintain clean and consistent code. Follow these guidelines:

* Use Java 17 features where appropriate
* Apply the Google Java Style or equivalent formatting
* Keep classes focused and small
* Follow consistent naming conventions
* Add comments where logic is complex or non-obvious
* Avoid unnecessary dependencies
* Prefer immutability and thread-safety where applicable

Format your code before committing, using tools like `spotless` if configured:

```bash
./gradlew spotlessApply
```

---

## Testing

Contributions should include appropriate tests. We use JUnit 5 for unit testing.

* Write unit tests for new functionality
* Place tests in `src/test/java`
* Avoid test dependencies on external systems or network resources
* Keep tests fast and isolated

Run the test suite before submitting:

```bash
./gradlew test
```

---

## Submitting a Pull Request

Before submitting your pull request:

* Ensure your code builds without errors
* Run all tests and verify they pass
* Check for formatting issues
* Review your changes for clarity and purpose

In your pull request:

* Explain the motivation behind the changes
* Reference related issues if applicable
* Indicate if the change is breaking or backward compatible

A maintainer will review your PR and may request changes or clarifications.

---

## License

By contributing to OpenUniverse, you agree that your contributions will be licensed under the terms of the [MIT License](./LICENSE).

---

## Thank You

We appreciate your time and effort in making OpenUniverse better. Your contributions help improve the quality, usability, and reach of the project for the entire community.
