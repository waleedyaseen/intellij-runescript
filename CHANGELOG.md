# intellij-runescript Changelog

## [Unreleased]
### Fixed
- Fix regular expressions not being allowed in hook transmits list.
- Fix invalid symbol file inspection reporting an error for "commands.sym".
- Fix "Create procedure" placement of script.
- Fix switch statements indentation when formatting.
- Fix type checking for variable assignments.
- Fix renaming for config/sym references.

### Changed
- Inspections that require symbols will no longer run on files outside the project.
- Update the file extension for commands from ".op" to ".cs2".
- Improve the handling and parsing of block comments.
- Add line indentation support to the formatter.

## [1.3.0] - 2023-08-30

### Added
- Add a formatter option for space around commas in return lists.
- Add a formatter option for space within return list parenthesis.
- Add error recovery for statements.
- Add support for parsing empty statements.
- Add an inspection for missing script symbols.
- Add clientscript.sym to valid symbol file types.

### Changed
- The build window will now display internal compiler errors as well.
- The build system will now save all documents automatically upon building.

### Fixed
- Fix a bug with script block indentation.
- Fix missing indentation for file block.
- Fix type checking for expression lists.
- Fix type checking when there is a type error.
- Fix type checking for varbit game variables.
- Fix parsing trailing tabs in .sym file format.
- Fix bundled custom dictionary loading.

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

[Unreleased]: https://github.com/waleedyaseen/intellij-runescript/compare/v1.3.0...HEAD
[1.3.0]: https://github.com/waleedyaseen/intellij-runescript/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/waleedyaseen/intellij-runescript/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/waleedyaseen/intellij-runescript/commits/v1.1.0
