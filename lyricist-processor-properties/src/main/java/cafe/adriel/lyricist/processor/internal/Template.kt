package cafe.adriel.lyricist.processor.internal

internal fun createTemplate(className: String, fields: String): String =
    """
    |public data class $className(
    |    $fields
    |)
    """.trimMargin()
