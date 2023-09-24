package cafe.adriel.lyricist.processor.internal

public data class LyricistConfig(
    val packageName: String,
    val moduleName: String,
    val internalVisibility: Boolean,
    val defaultLanguageTag: String,
    val resourcesPath: String,
)

public val LyricistConfig.resourcesPathOrThrow: String get() {
    require(resourcesPath.isNotEmpty()) {
        "Lyricist KSP option ${ProcessorOptions.RESOURCES_PATH} must be defined for XML and Properties processor"
    }

    return resourcesPath
}
