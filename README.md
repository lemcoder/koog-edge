# Koog Edge

[![Maven Central](https://img.shields.io/maven-central/v/io.github.lemcoder/koog-edge.svg)](https://search.maven.org/artifact/io.github.lemcoder/koog-edge)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> Stable? That's for horses 🐴 This library is in early experimental stage, do not use it for any serious work!


Koog Edge is a Kotlin Multiplatform library that brings **on-device AI capabilities** to your mobile
applications. It provides seamless integration between
the [Koog Agents framework](https://github.com/JetBrains/koog) and local Small Language Models (SLMs),
enabling privacy-focused, offline AI experiences.

## Features

✨ **On-Device AI** - Run AI models locally without internet connectivity  
🔧 **Tool Calling Support** - Enable agents to interact with external tools and APIs  
🌐 **Kotlin Multiplatform** - Support for Android and iOS platforms  
🔌 **Multiple Backends** - Support for Cactus Compute and Leap SDK  
🎯 **Koog Integration** - Works seamlessly with Koog Agents Core  
📦 **Easy Model Management** - Download and load models with simple APIs

## Supported Platforms

- ✅ **Android** (API 31+)
- ✅ **iOS** (arm64)

## Supported Models

### Cactus Compute Models

| Model | Size | Context Length | Tool Calling |
|-------|------|----------------|--------------|
| Qwen 3 0.6B | 394 MB | 16,384 tokens | ✅ Yes |
| Qwen 3 1.7B | 1,161 MB | 16,384 tokens | ✅ Yes |

### Leap SDK Models

| Model | Context Length | Tool Calling |
|-------|----------------|--------------|
| LFM2 1.2B Tool | 32,768 tokens | ✅ Yes |

## Installation

Add the dependency to your Gradle build file:

### Kotlin DSL (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.lemcoder:koog-edge:0.0.2")
    
    // Also add Koog Agents Core if not already included
    implementation("ai.koog:agents-core:0.5.3")
}
```

### Groovy DSL (build.gradle)

```groovy
dependencies {
    implementation 'io.github.lemcoder:koog-edge:0.0.2'
    
    // Also add Koog Agents Core if not already included
    implementation 'ai.koog:agents-core:0.5.3'
}
```

## Quick Start

### 1. Choose Your Backend

Koog Edge supports two inference backends:

#### **Cactus Compute** (Recommended for most use cases)

```kotlin
import io.github.lemcoder.koog.edge.cactus.getCactusLLMClient
import io.github.lemcoder.koog.edge.cactus.CactusModels
import io.github.lemcoder.koog.edge.cactus.CactusLLMParams

// Create the LLM client (pass Android context on Android, null on iOS)
val llmClient = getCactusLLMClient(context)

// Create a prompt executor
val executor = SingleLLMPromptExecutor(llmClient)

// Use with Koog Agents
val agent = AIAgent(
    promptExecutor = executor,
    strategy = yourStrategy,
    agentConfig = AIAgentConfig(
        prompt = prompt("assistant") {
            system("You are a helpful assistant.")
        },
        model = CactusModels.Chat.Qwen3_0_6B,
        maxAgentIterations = 10
    )
)
```

#### **Leap SDK** (Android only)

```kotlin
import io.github.lemcoder.koog.edge.leap.getLeapLLMClient
import io.github.lemcoder.koog.edge.leap.LeapModels

// Specify the path to your downloaded model
val modelPath = "/path/to/model"
val llmClient = getLeapLLMClient(modelPath)

val executor = SingleLLMPromptExecutor(llmClient)
```

### 2. Create an AI Agent

```kotlin
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.requestLLM
import ai.koog.prompt.dsl.prompt

val strategy = functionalStrategy<String, String>("Chat Agent") { input ->
    val response = requestLLM(input, allowToolCalls = false)
    return@functionalStrategy response.content
}

val agent = AIAgent(
    promptExecutor = executor,
    strategy = strategy,
    agentConfig = AIAgentConfig(
        prompt = prompt("chat", params = CactusLLMParams()) {
            system("You are a helpful assistant.")
        },
        model = CactusModels.Chat.Qwen3_0_6B,
        maxAgentIterations = 10
    )
)

// Execute the agent
val result = agent.execute("Hello! How are you?")
println(result)
```

## Advanced Usage

### Downloading Models

```kotlin
import io.github.lemcoder.koog.edge.LocalModelDownloader
import io.github.lemcoder.koog.edge.cactus.CactusModels

// Download a Cactus model with progress tracking
val downloader = LocalModelDownloader.downloadCactusModel(CactusModels.Chat.Qwen3_0_6B)

downloader.collect { progress ->
    println("Download progress: ${(progress * 100).toInt()}%")
}
```

### Custom LLM Parameters

#### Cactus Parameters

```kotlin
import io.github.lemcoder.koog.edge.cactus.CactusLLMParams
import com.cactus.InferenceMode

val params = CactusLLMParams(
    temperature = 0.7,
    maxTokens = 512,
    topK = 40,
    topP = 0.95,
    stopSequences = listOf("</s>", "[DONE]"),
    inferenceMode = InferenceMode.LOCAL_FIRST, // LOCAL, CLOUD, or LOCAL_FIRST
    cactusToken = "your-cactus-token" // For cloud inference
)

val agentConfig = AIAgentConfig(
    prompt = prompt("assistant", params = params) {
        system("You are a helpful assistant.")
    },
    model = CactusModels.Chat.Qwen3_0_6B,
    maxAgentIterations = 10
)
```

#### Leap Parameters

```kotlin
import io.github.lemcoder.koog.edge.leap.LeapLLMParams

val params = LeapLLMParams(
    temperature = 0.7f,
    topP = 0.95f,
    minP = 0.05f,
    repetitionPenalty = 1.1f,
    jsonSchemaConstraint = """{"type": "object", "properties": {...}}"""
)
```

### Streaming Responses (Leap SDK only)

```kotlin
val agent = AIAgent(
    promptExecutor = executor,
    strategy = strategy,
    agentConfig = agentConfig
)

// Execute with streaming
agent.executeStreaming("Tell me a story").collect { frame ->
    when (frame) {
        is StreamFrame.Append -> print(frame.text)
        is StreamFrame.End -> println("\nFinished: ${frame.finishReason}")
        is StreamFrame.ToolCall -> println("Tool called: ${frame.name}")
    }
}
```

### Custom Logging

```kotlin
import io.github.lemcoder.koog.edge.log.KoogEdgeLog
import io.github.lemcoder.koog.edge.log.KoogEdgeLogger

// Implement custom logger
object MyLogger : KoogEdgeLogger {
    override fun info(message: () -> String) {
        Log.i("KoogEdge", message())
    }
    
    override fun warning(message: () -> String) {
        Log.w("KoogEdge", message())
    }
    
    override fun error(message: String, error: Throwable?) {
        Log.e("KoogEdge", message, error)
    }
}

// Set custom logger
KoogEdgeLog.setLogger(MyLogger)
```

## Architecture

Koog Edge acts as a bridge between the Koog Agents framework and on-device inference engines:

```
┌─────────────────────┐
│   Your Android/iOS  │
│    Application      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│    Koog Agents      │  ← High-level AI agent framework
│       Core          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│    Koog Edge        │  ← This library (LLM client adapters)
│   (This Library)    │
└──────────┬──────────┘
           │
      ┌────┴────┐
      ▼         ▼
┌──────────┐  ┌──────────┐
│ Cactus   │  │ Leap SDK │  ← On-device inference engines
│ Compute  │  │          │
└──────────┘  └──────────┘
```

## Requirements

- **Android**: Min SDK 31 (Android 12)
- **iOS**: arm64 architecture
- **Kotlin**: 2.2.21+
- **Java**: 17+

## Dependencies

Koog Edge is built on top of:

- [Koog Agents Core](https://github.com/JetBrains/koog) - AI agent framework
- [Cactus Compute](https://cactuscompute.com/) - On-device AI inference
- [Leap SDK](https://leap.liquid.ai) - Mobile AI SDK (Android only)

## Example App

This repository includes a complete example app demonstrating:

- 💬 Chat agents
- 🧮 Calculator tool integration
- 🌤️ Weather tool integration
- 🎛️ Different model backends

Check out the `app/` directory for full examples.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

```
Copyright 2025 Mikołaj Lemański

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Links

- 📦 [Maven Central](https://search.maven.org/artifact/io.github.lemcoder/koog-edge)
- 📧 [Contact](mailto:lemanski.dev@gmail.com)

---

Made with ❤️ by [Mikołaj Lemański](https://github.com/lemcoder)
