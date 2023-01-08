package com.langnerd.makker.client

import com.langnerd.makker.client.model.*

interface MakeClient {
    suspend fun getApps(filter: AppFilter? = null): Result<AppList>
    suspend fun getApp(appId: App.Id): Result<App>
    suspend fun getBlueprint(): Result<Blueprint>
    suspend fun copyBlueprint(blueprint: Blueprint, updates: List<ModuleUpdate>): Result<Blueprint>
}