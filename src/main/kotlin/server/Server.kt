package server

import server.utils.ContentType
import server.utils.StatusCode
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Future

class Server(val httpHandler: HttpHandler) {

    private lateinit var server: AsynchronousServerSocketChannel

    fun bootstrap() {
        //open asynchronous socket
        server = AsynchronousServerSocketChannel.open()
        //bind socket to local address
        server.bind(InetSocketAddress("127.0.0.1", 8088))

        while (true) {
            //future is pending result
            //initiates an asynchronous operation to accept a connection made to this channel's socket.
            val future: Future<AsynchronousSocketChannel> = server.accept()
            println("new client connection")
            handleClient(future)
        }
    }

    private fun handleClient(future: Future<AsynchronousSocketChannel>) {
        //Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if available.
        val clientChannel: AsynchronousSocketChannel = future.get()

        while (clientChannel.isOpen) {
            //Allocates a new byte buffer.
            val buffer = ByteBuffer.allocate(BUFFER_SIZE)
            val stringBuilder = StringBuilder()
            var keepReading = true
            while (keepReading) {
                val readResult = clientChannel.read(buffer).get()
                keepReading = readResult == BUFFER_SIZE
                //Flips this buffer. The limit is set to the current position and then the position is set to zero.
                buffer.flip()
                val charBuffer = StandardCharsets.UTF_8.decode(buffer)
                stringBuilder.append(charBuffer)
                //Clears this buffer. The position is set to zero, the limit is set to the capacity, and the mark is discarded.

                buffer.clear()
            }

            val request = HttpRequest(stringBuilder.toString())
            val response = HttpResponse()

            try {
                val body = httpHandler.handle(response = response, request = request)
                if (body.isNotBlank()) {
                    if (response.headers[CONTENT_TYPE_HEADER].isNullOrBlank()) {
                        response.addHeader(CONTENT_TYPE_HEADER, ContentType.TEXT_HTML.name)
                    }
                    response.body = body
                }
            } catch (ex: Exception) {
                response.statusCode = StatusCode.INTERNAL_SERVER_ERROR.code
                response.status = StatusCode.INTERNAL_SERVER_ERROR.message
                response.addHeader(CONTENT_TYPE_HEADER, ContentType.TEXT_HTML.name)
                response.body = String.format(HTML_BODY_PATTERN, "Error happens")
            }

            val resp: ByteBuffer = ByteBuffer.wrap(response.getBytes())
            clientChannel.write(resp)

            clientChannel.close()
        }
    }
}