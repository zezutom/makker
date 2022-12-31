package com.langnerd.makker.extensions

import com.langnerd.makker.model.Module

fun Module.move(x: Int, y: Int): Module =
    this.copy(metadata = Module.ModuleMetadata(Module.ModuleMetadata.Designer(x, y)))
