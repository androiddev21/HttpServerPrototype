package server

interface HttpHandler {

    fun handle(request: HttpRequest, response: HttpResponse): String
}