package edu.uees.tutorias.adapter.external;

/**
 * Representa la respuesta propietaria de la API de Zoom (SDK de terceros).
 * Posee nombres de campos, tipos numéricos y estructuras ajenas al modelo de dominio UEES.
 */
public class ZoomMeetingPayload {

    private final long zoomNumericId;
    private final String joinUrl;
    private final String encryptedPassword;
    private final int statusCode;
    private final String hostUsername;

    public ZoomMeetingPayload(long zoomNumericId, String joinUrl, String encryptedPassword,
                              int statusCode, String hostUsername) {
        this.zoomNumericId = zoomNumericId;
        this.joinUrl = joinUrl;
        this.encryptedPassword = encryptedPassword;
        this.statusCode = statusCode;
        this.hostUsername = hostUsername;
    }

    public long getZoomNumericId() {
        return zoomNumericId;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHostUsername() {
        return hostUsername;
    }
}
