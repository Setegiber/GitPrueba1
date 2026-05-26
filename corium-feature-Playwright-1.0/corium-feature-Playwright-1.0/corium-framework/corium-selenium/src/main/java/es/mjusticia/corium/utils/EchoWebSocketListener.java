package es.mjusticia.corium.utils;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * The {@code EchoWebSocketListener} class is a WebSocket listener implementation that echoes received messages back to the sender.
 *
 * <p>It extends the WebSocketListener class from the okhttp3 library and overrides its methods to handle WebSocket events.</p>
 *
 * @author Paul Raad
 */

public class EchoWebSocketListener extends WebSocketListener {

    private String lastWebsocketMessage = null;
    private Response websocketResponse = null;

    private final CompletableFuture<WebSocket> webSocketFuture = new CompletableFuture<>();
    private final CompletableFuture<Void> webSocketCloseFuture = new CompletableFuture<>();
    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();

    /**
     * Sends a message via WebSocket and prints the received response within a specified timeout period.
     *
     * @param webSocket      The WebSocket connection.
     * @param message        The message to send.
     * @param timeoutSeconds The timeout period in seconds.
     */
    public void sendMessageAndPrintResponse(WebSocket webSocket, String message, int timeoutSeconds) {
        System.out.println("Sending message: " + message);
        webSocket.send(message);

        CompletableFuture<String> responseWithTimeout = responseFuture.orTimeout(timeoutSeconds, TimeUnit.SECONDS);

        try {
            String response = responseWithTimeout.get();
            System.out.println("Received response: " + response);
            setLastWebsocketMessage(response);
        } catch (Exception e) {
            System.out.println("Timeout reached. No response received within " + timeoutSeconds + " seconds.");
        }
    }

    /**
     * Sends a message via WebSocket and returns a CompletableFuture to wait for the response asynchronously.
     *
     * @param webSocket The WebSocket connection.
     * @param message   The message to send.
     * @return A CompletableFuture representing the response.
     */
    public CompletableFuture<String> sendAndWaitForResponse(WebSocket webSocket, String message) {
        webSocket.send(message);
        return responseFuture.thenApply(result -> result);
    }

    /**
     * Retrieves the CompletableFuture for obtaining the WebSocket connection.
     *
     * @return A CompletableFuture for obtaining the WebSocket connection.
     */
    public CompletableFuture<WebSocket> getWebSocketFuture() {
        return webSocketFuture;
    }

    /**
     * Retrieves the CompletableFuture for closing the WebSocket connection.
     *
     * @return A CompletableFuture for closing the WebSocket connection.
     */
    public CompletableFuture<Void> getWebSocketCloseFuture() {
    	 return webSocketCloseFuture.thenApply(v -> null);
    }

    /**
     * Overrides the method called when the WebSocket connection is opened.
     * Completes the WebSocket future with the opened WebSocket.
     *
     * @param webSocket The WebSocket instance that has been opened.
     * @param response  The response received upon opening the WebSocket connection.
     */
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("WebSocket opened");
        System.out.println(response.code());
        setWebsocketResponse(response);
        webSocketFuture.complete(webSocket);
    }

    /**
     * Overrides the method called when a text message is received on the WebSocket.
     * Completes the response future with the received message.
     *
     * @param webSocket The WebSocket instance receiving the message.
     * @param text      The text message received.
     */
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("Received message: " + text);
        responseFuture.complete(text);
    }

    /**
     * Overrides the method called when binary data is received on the WebSocket.
     *
     * @param webSocket The WebSocket instance receiving the binary data.
     * @param bytes     The binary data received.
     */
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("Received bytes: " + bytes.hex());
    }

    /**
     * Overrides the method called when the WebSocket connection is closed.
     * Completes the WebSocket close future.
     *
     * @param webSocket The WebSocket instance that has been closed.
     * @param code      The status code of the close.
     * @param reason    The reason for the close.
     */
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("WebSocket closed: " + code + ", Reason: " + reason);
        webSocketCloseFuture.complete(null);
    }

    /**
     * Overrides the method called when an error occurs on the WebSocket.
     * Completes the WebSocket close future exceptionally with the error.
     *
     * @param webSocket The WebSocket instance where the failure occurred.
     * @param t         The throwable representing the failure.
     * @param response  The response associated with the failure, if any.
     */
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        webSocketCloseFuture.completeExceptionally(t);
    }

	public String getLastWebsocketMessage() {
		return lastWebsocketMessage;
	}

	public void setLastWebsocketMessage(String lastWebsocketMessage) {
		this.lastWebsocketMessage = lastWebsocketMessage;
	}

	public Response getWebsocketResponse() {
		return websocketResponse;
	}

	public void setWebsocketResponse(Response websocketResponse) {
		this.websocketResponse = websocketResponse;
	}
}
