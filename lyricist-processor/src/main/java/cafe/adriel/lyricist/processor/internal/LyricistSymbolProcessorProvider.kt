package cafe.adriel.lyricist.processor.internal

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import kotlin.reflect.full.primaryConstructor

internal class LyricistSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor = with(environment) {
        findProcessorForCurrentPlatform(createConfig())
    }

    private fun SymbolProcessorEnvironment.createConfig() = LyricistConfig(
        packageName = options.getOrDefault(PACKAGE_NAME, DEFAULT_PACKAGE_NAME),
        moduleName = options[MODULE_NAME].orEmpty(),
        internalVisibility = options[INTERNAL_VISIBILITY].toBoolean(),
        defaultLanguageTag = getOptionAndWarnIfDeprecated(
            deprecatedKey = XML_DEFAULT_LANGUAGE_TAG,
            nonDeprecatedKey = LANGUAGE_TAG,
            defaultValue = DEFAULT_LANGUAGE_TAG
        ),
        resourcesPath = getOptionAndWarnIfDeprecated(
            deprecatedKey = XML_RESOURCES_PATH,
            nonDeprecatedKey = RESOURCES_PATH,
            defaultValue = "",
        ).ifEmpty {
            throw IllegalArgumentException("Lyricist KSP argument \"$RESOURCES_PATH\" or \"$XML_RESOURCES_PATH\" not found")
        },
    )

    private fun SymbolProcessorEnvironment.getOptionAndWarnIfDeprecated(
        deprecatedKey: String,
        nonDeprecatedKey: String,
        defaultValue: String,
    ): String {
        val value = options[deprecatedKey]
        if (value != null) {
            logger.warn("Lyricist KSP argument \"$deprecatedKey\" is deprecated, " +
                    "use \"$nonDeprecatedKey\" instead.")
            return value
        }

        return options.getOrDefault(nonDeprecatedKey, defaultValue)
    }

    private fun SymbolProcessorEnvironment.findProcessorForCurrentPlatform(config: LyricistConfig): SymbolProcessor {
        val symbolProcessor = try {
            Class.forName(SYMBOL_PROCESSOR_PATH)
        } catch (exception: ClassNotFoundException) {
            print("symbol processor not found")
            throw exception
        }

        println("symbolProcessor = $symbolProcessor")

        return symbolProcessor.kotlin.primaryConstructor?.call(
            config,
            codeGenerator,
            logger
        ) as SymbolProcessor
    }

    private companion object {
        const val SYMBOL_PROCESSOR_PATH = "cafe.adriel.lyricist.processor.LyricistSymbolProcessor"

        // Common Options
        const val PACKAGE_NAME = "lyricist.packageName"
        const val MODULE_NAME = "lyricist.moduleName"
        const val INTERNAL_VISIBILITY = "lyricist.internalVisibility"
        const val LANGUAGE_TAG = "lyricist.defaultLanguageTag"
        const val RESOURCES_PATH = "lyricist.resourcesPath"

        const val DEFAULT_PACKAGE_NAME = "cafe.adriel.lyricist"
        const val DEFAULT_LANGUAGE_TAG = "en"

        // XML Processor options
        const val XML_RESOURCES_PATH = "lyricist.xml.resourcesPath"
        const val XML_DEFAULT_LANGUAGE_TAG = "lyricist.xml.defaultLanguageTag"
    }
}
