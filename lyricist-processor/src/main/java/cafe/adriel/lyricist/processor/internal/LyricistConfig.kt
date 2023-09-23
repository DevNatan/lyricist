package cafe.adriel.lyricist.processor.internal

public data class LyricistConfig(
    val packageName: String,
    val moduleName: String,
    val internalVisibility: Boolean,
    val defaultLanguageTag: String,
    val resourcesPath: String,
)
