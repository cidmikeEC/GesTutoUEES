package edu.uees.tutorias.adapter.external;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Cliente de biblioteca externa (Adaptee en el patrón Adapter).
 * Simula el SDK propietario de Zoom Video Communications v5.x.
 *
 * Su interfaz es completamente incompatible con el contrato de nuestro dominio:
 * - Requiere parámetros primitivos y nombres en inglés (topic, durationMins, hostEmail, waitingRoom).
 * - Trabaja con IDs numéricos de tipo long generados por sus servidores.
 * - Retorna un objeto propietario {@link ZoomMeetingPayload}.
 */
public class ZoomSdkClient {

    private final String apiKey;
    private final String apiSecret;
    private final Map<Long, ZoomMeetingPayload> activeMeetings = new HashMap<>();
    private final Random random = new Random();

    public ZoomSdkClient(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * Firma incompatible externa:
     * scheduleMeeting(String topic, int durationMins, String hostEmail, boolean waitingRoom)
     */
    public ZoomMeetingPayload scheduleMeeting(String topic, int durationMins, String hostEmail, boolean waitingRoom) {
        if (topic == null || hostEmail == null) {
            throw new IllegalArgumentException("[ZOOM SDK ERROR] topic and hostEmail are mandatory");
        }

        long numericId = 80000000000L + (long)(random.nextDouble() * 10000000000L);
        String joinUrl = "https://zoom.us/j/" + numericId + "?pwd=uees" + random.nextInt(9000);
        String password = "Z" + (100000 + random.nextInt(900000));

        ZoomMeetingPayload payload = new ZoomMeetingPayload(numericId, joinUrl, password, 201, hostEmail);
        activeMeetings.put(numericId, payload);

        System.out.printf("[ZOOM SDK CLOUD] Sala creada exitosamente en Zoom Cloud -> ID: %d | Host: %s | Topic: '%s'%n",
                numericId, hostEmail, topic);

        return payload;
    }

    /**
     * Firma incompatible externa:
     * removeMeeting(long meetingId)
     */
    public boolean removeMeeting(long meetingId) {
        if (activeMeetings.containsKey(meetingId)) {
            activeMeetings.remove(meetingId);
            System.out.printf("[ZOOM SDK CLOUD] Sala %d eliminada de Zoom Cloud%n", meetingId);
            return true;
        }
        System.out.printf("[ZOOM SDK CLOUD] Sala %d no encontrada para eliminar%n", meetingId);
        return false;
    }

    public int getCantidadSalasActivas() {
        return activeMeetings.size();
    }
}
