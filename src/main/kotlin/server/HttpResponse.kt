package server

import server.utils.StatusCode

class HttpResponse {
    val headers = mutableMapOf(
        "server.Server" to "naive",
        "Connection" to "close"
    )
    var body: String = ""
        set(value) {
            field = value
            headers[CONTENT_LENGTH_HEADER] = value.length.toString()
        }

    var statusCode: Int = StatusCode.OK.code
    var status: String = StatusCode.OK.message

    fun addHeader(key: String, value: String) {
        headers[key] = value
    }

    fun addHeaders(headers: Map<String, String>) {
        this.headers.putAll(headers)
    }

    fun message(): String {
        val stringBuilder = StringBuilder("$HTTP_PROTOCOL $statusCode $status$NEW_LINE")
        headers.forEach { (key, value) ->
            stringBuilder.append("$key: $value$NEW_LINE")
        }
        stringBuilder.append("$NEW_LINE$body")
        return stringBuilder.toString()
    }

    fun getBytes(): ByteArray = message().toByteArray()
}