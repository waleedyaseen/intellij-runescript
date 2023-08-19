# intellij-runescript Changelog

## [Unreleased]

### Added
- Add a formatter option for space around commas in return lists.
- Add a formatter option for space within return list parenthesis.
- Add error recovery for statements.

### Changed
- The build window will now display internal compiler errors as well.

### Fixed
- Fix a bug with script block indentation.

## [1.2.0] - 2023-08-18

### Added
- Support for basic spell checking and custom dictionary.
- Support for specific parameter types in triggers.
- Support for game variable type checking/refactoring/find usages.
- Support for constant type checking/refactoring/find usages.

### Changed
- Fix issue with symbol file type inspection using outdated types.

## [1.1.0] - 2023-08-11

### Added
- Support for injecting languages into string literals.
- Support for parsing hooks from string literals. 
- The plugin now has an icon instead of the default.

### Changed
- The new file action no longer shows on top of everything else.

[Unreleased]: https://github.com/waleedyaseen/intellij-runescript/compare/v1.2.0...HEAD
[1.2.0]: https://github.com/waleedyaseen/intellij-runescript/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/waleedyaseen/intellij-runescript/commits/v1.1.0
