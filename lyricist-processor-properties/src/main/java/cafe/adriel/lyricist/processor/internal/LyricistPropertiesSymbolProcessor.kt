package cafe.adriel.lyricist.processor.internal

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

@Suppress("UNUSED")
internal class LyricistPropertiesSymbolProcessor(
    private val config: LyricistConfig,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val resourcesPath = config.resourcesPathOrThrow
        TODO("Properties files not supported")
    }
}
