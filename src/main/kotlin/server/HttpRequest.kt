package server

import server.utils.HttpMethod

class HttpRequest(private val message: String) {

    private val httpMethod: HttpMethod
    private val url: String
    private val headers: Map<String, String>
    private val body: String

    init {
        val parts = message.split(DELIMITER)
        val head = parts[0]
        val headers = head.split(NEW_LINE)
        val firstLine = headers[0].split(" ")
        httpMethod = HttpMethod.valueOf(firstLine[0])
        url = firstLine[1]
        this.headers = hashMapOf()
        headers.forEach { header ->
            if(header.contains(HEADER_DELIMITER)) {
                val headerParts = header.split(HEADER_DELIMITER, limit = 2)
                this.headers.put(headerParts[0].trim(), headerParts[1].trim())
            }
        }
        val bodyLength = this.headers[CONTENT_LENGTH_HEADER]
        val length = bodyLength?.toInt() ?: 0
        body = if (parts.isNotEmpty()) parts[1].trim().substring(0, length) else ""
    }
}