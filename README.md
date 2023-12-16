[![Build Status](https://github.com/opatry/todoist-kt/actions/workflows/Build.yml/badge.svg)](https://github.com/opatry/todoist-kt/actions/workflows/Build.yml)

# Todoist REST API Kotlin bindings

Kotlin binding for [Todoist REST API](https://developer.todoist.com/rest/v2) using [Ktor Http Client](https://ktor.io/) and [Gson](https://github.com/google/gson) as Json marshaller.

## Example

```kotlin
val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        gson()
    }
    install(Auth) {
        bearer {
            sendWithoutRequest { true }
            loadTokens {
                // see https://developer.todoist.com/rest/v2/#authorization
                // see https://app.todoist.com/app/settings/integrations/developer
                val apiToken = System.getenv("TODOIST_API_TOKEN")
                BearerTokens(apiToken, "")
            }
        }
    }
    defaultRequest {
        if (url.host.isEmpty()) {
            val defaultUrl = URLBuilder().takeFrom("https://api.todoist.com/rest")
            url.host = defaultUrl.host
            url.protocol = defaultUrl.protocol
            if (!url.encodedPath.startsWith('/')) {
                val basePath = defaultUrl.encodedPath
                url.encodedPath = "$basePath/${url.encodedPath}"
            }
        }
    }
}

val todoService = HttpTodoistService(httpClient)
runBlocking {
    withContext(Dispatchers.IO) {
        todoService.getTasks().forEach(::println)
    }
}
```