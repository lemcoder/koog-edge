package io.github.lemcoder.koog.edge.cactus.internal

import com.cactus.CactusLM
import io.github.lemcoder.koog.edge.LocalModelLoader

internal expect fun cactusModelLoader(context: Any?): LocalModelLoader<CactusLM?>