package io.github.lemcoder.koog.edge

import ai.koog.prompt.llm.LLModel

interface LocalModelLoader<T> {
    suspend fun loadModel(model: LLModel): T
}