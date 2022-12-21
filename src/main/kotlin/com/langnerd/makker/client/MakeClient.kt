package com.langnerd.makker.client

import com.langnerd.makker.model.Module

interface MakeClient {

    fun getModules(application: String): List<Module>
}