package cafe.adriel.lyricist.processor.internal

import com.fleshgrinder.extensions.kotlin.toLowerCamelCase
import com.fleshgrinder.extensions.kotlin.toUpperCamelCase
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File
import java.io.FileInputStream
import java.util.Properties

@Suppress("UNUSED")
internal class LyricistPropertiesSymbolProcessor(
    private val config: LyricistConfig,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val languages = resolveLanguages(config.resourcesPathOrThrow)
            .mapValues { (_, file) ->
                Properties().apply { load(FileInputStream(file)) }
            }

        val (_, props) = languages.entries.first()
        val fields: String = resolveProperties(props)

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true),
            packageName = config.packageName,
            fileName = "${config.moduleName.toUpperCamelCase()}Strings"
        ).use { stream ->
            stream.write(
                createTemplate(
                    className = "${config.moduleName.toLowerCamelCase()}Strings",
                    fields = fields,
                ).toByteArray()
            )
        }

        return emptyList()
    }

    private fun resolveLanguages(resourcesPath: String): Map<String, File> {
        val resources = File(resourcesPath)
        if (!resources.exists())
            return emptyMap()

        return resources.walk()
            .filterNot(File::isDirectory)
            .map { file ->
                file.name.substringBefore(".properties") to file
            }
            .toMap()
    }
}
