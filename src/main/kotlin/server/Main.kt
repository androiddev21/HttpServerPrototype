package server

fun main() {
    val server = Server(object : HttpHandler {
        override fun handle(request: HttpRequest, response: HttpResponse): String {
            return String.format(HTML_BODY_PATTERN, "Hello, naive")
        }
    })
    server.bootstrap()
}

