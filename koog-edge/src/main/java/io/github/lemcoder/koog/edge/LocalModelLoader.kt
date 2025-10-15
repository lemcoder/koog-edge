package io.github.lemcoder.koog.edge

import ai.koog.prompt.llm.LLModel
import kotlinx.coroutines.flow.Flow

interface LocalModelLoader<T> {
    sealed class State() {
        object Loading : State()
        data class Success(val modelId: String) : State()
        data class Error(val error: Throwable) : State()
    }

    val state: Flow<State>

    suspend fun loadModel(model: LLModel): T
}