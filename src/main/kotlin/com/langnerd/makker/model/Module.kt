package com.langnerd.makker.model

data class Module(
    val id: Long,
    val module: String,
    val version: Int,
    val parameters: Map<String, Any>,
    val metadata: ModuleMetadata? = null,
    val mapper: Map<String, Any>? = null
) {
    data class ModuleMetadata(val designer: Designer) {
        data class Designer(val x: Int, val y: Int)
    }
}