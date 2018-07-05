package org.mariotaku.imgenie

import org.gradle.api.DefaultTask
import org.mariotaku.imgenie.asset.ImageAsset
import org.mariotaku.imgenie.model.FlavorScope
import java.io.File

open class ImageAssetsGeneratorTask : DefaultTask() {

    lateinit var config: ImageAssetsConfig

    lateinit var buildVariant: FlavorScope
    lateinit var buildType: String
    lateinit var genDir: File

    init {
        doFirst {
            outputs.files.forEach {
                val parentFile = it.parentFile
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
            }
        }
        doLast {
            inputs.files.forEach { file ->
                val defFormat = config.outputFormats.firstOrNull { (regex, _) ->
                    if (regex.matches(file.name)) return@firstOrNull true
                    return@firstOrNull false
                }?.second ?: config.outputFormat

                val asset = ImageAsset.get(file, defFormat)
                asset.generateImages(config, genDir)
            }
        }
    }


    fun setupInputOutput(outputName: String) {
        val imageTrees = (buildVariant.flavors + buildType + "main").map {
            return@map project.file(arrayOf("src", it, "images").joinToString(File.separator))
        }.filter { it.isDirectory }.map { project.fileTree(it) }
        if (!imageTrees.isEmpty()) {
            inputs.files(*imageTrees.toTypedArray())
            outputs.dir(genDir)
        }
    }
}
