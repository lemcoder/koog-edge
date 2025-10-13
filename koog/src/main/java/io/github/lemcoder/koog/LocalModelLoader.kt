package io.github.lemcoder.koog

import kotlinx.coroutines.flow.Flow

interface LocalModelLoader<T> {
    sealed class State() {
        object Loading : State()
        data class Success(val model: AndroidLocalModel) : State()
        data class Error(val error: Throwable) : State()
    }

    val state: Flow<State>

    suspend fun loadModel(model: AndroidLocalModel): T
}