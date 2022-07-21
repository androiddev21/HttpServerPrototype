package server.utils

enum class StatusCode(val code: Int, val message: String) {
    OK(200, "Ok"),
    BAD_REQUEST(400, "Bad request"),
    INTERNAL_SERVER_ERROR(500, "Internal server error")
}