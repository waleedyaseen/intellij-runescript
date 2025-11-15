package io.runescript.plugin.lang.psi.typechecker.symbol

/**
 * A table that contains [LocalVariableSymbol]s. The table provides helper functions for inserting and looking up local variable symbols.
 *
 * The table may or may not have a [parent]. To create a symbol table with a `parent`, see [createSubTable].
 *
 * See Also: [Symbol table](https://en.wikipedia.org/wiki/Symbol_table)
 * @see createSubTable
 */
class LocalVariableTable internal constructor(private val parent: LocalVariableTable? = null) {
    /**
     * Table of all symbols defined in the table. This does not include symbols
     * defined in a parent.
     */
    private val symbols: LinkedHashMap<String, LocalVariableSymbol> = LinkedHashMap()

    fun clone(): LocalVariableTable {
        val cloned = LocalVariableTable(parent?.clone())
        cloned.symbols.putAll(symbols)
        return cloned
    }

    /**
     * Inserts [symbol] into the table and indicates if the insertion was
     * successful.
     */
    fun insert(symbol: LocalVariableSymbol): Boolean {
        // prevent shadowing of symbols with the same type by traversing up
        // the tree to check if the symbol exists in any of them
        var current: LocalVariableTable? = this
        while (current != null) {
            if (current.symbols.contains(symbol.name)) {
                return false
            }
            current = current.parent
        }

        symbols[symbol.name] = symbol
        return true
    }

    /**
     * Searches for a symbol with [name] and [type]. If one is not found the
     * search it applied to the parent table recursively.
     */
    fun find(name: String): LocalVariableSymbol? {
        @Suppress("UNCHECKED_CAST")
        var symbol = symbols.get(name)
        if (symbol == null && parent != null) {
            symbol = parent.find(name)
        }
        return symbol
    }

    /**
     * Creates a new [SymbolType] with `this` as the parent.
     */
    fun createSubTable(): LocalVariableTable = LocalVariableTable(this)
}
