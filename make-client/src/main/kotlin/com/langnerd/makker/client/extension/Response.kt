package com.langnerd.makker.client.extension

import com.langnerd.makker.client.model.App
import com.langnerd.makker.client.model.AppFilter
import com.langnerd.makker.client.model.AppList
import kotlinx.serialization.json.JsonObject

fun JsonObject.toApp(): Result<App> = TODO()
fun JsonObject.toAppList(filter: AppFilter?): Result<AppList> = TODO()