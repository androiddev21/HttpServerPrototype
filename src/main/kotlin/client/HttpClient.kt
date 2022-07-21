import java.net.Socket

fun main() {
    //create socket
    val socket = Socket("httpbin.org", 80)

    //response from server
    val response = socket.getInputStream()
    //request to server
    val request = socket.getOutputStream()

    val data: ByteArray = ("GET / HTTP/1.1\n" +
            "Host: httpbin.org\n\n").toByteArray()

    request.write(data)

    var c: Int

    while ((response.read().also { c = it }) != -1) {
        print(c.toChar())
    }

    socket.close()
}