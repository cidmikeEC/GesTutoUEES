package edu.uees.tutorias.adapter;

import edu.uees.tutorias.adapter.external.ZoomMeetingPayload;
import edu.uees.tutorias.adapter.external.ZoomSdkClient;
import edu.uees.tutorias.domain.Reserva;

/**
 * Adaptador concreto (Adapter) que implementa el contrato {@link ProveedorVideoconferencia}.
 *
 * Traduce las llamadas del dominio GesTutoUEES hacia la API incompatible de {@link ZoomSdkClient}:
 * 1. Extrae del objeto de dominio {@link Reserva} los atributos necesarios (tema, email docente, duración).
 * 2. Invoca el método propietario scheduleMeeting(...) del SDK externo.
 * 3. Transforma la respuesta propietaria {@link ZoomMeetingPayload} en la abstracción del dominio {@link ReunionVirtual}.
 *
 * Cumple con DIP (Dependency Inversion Principle) y OCP (Open/Closed Principle).
 */
public class ZoomServiceAdapter implements ProveedorVideoconferencia {

    private final ZoomSdkClient zoomSdk;

    public ZoomServiceAdapter(ZoomSdkClient zoomSdk) {
        if (zoomSdk == null) {
            throw new IllegalArgumentException("El cliente Zoom SDK no puede ser nulo");
        }
        this.zoomSdk = zoomSdk;
    }

    @Override
    public ReunionVirtual crearSala(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula para crear la sala");
        }

        // 1. Mapeo de parámetros de dominio hacia parámetros del SDK externo
        String topic = String.format("Tutoría UEES: %s - %s",
                reserva.getMateria(), reserva.getTema());
        String hostEmail = reserva.getHorario().getDocente().getCorreo();
        int durationMins = 60; // Duración estándar de sesión universitaria
        boolean waitingRoom = true; // Política de seguridad institucional

        // 2. Invocación a la API externa incompatible
        ZoomMeetingPayload payload = zoomSdk.scheduleMeeting(topic, durationMins, hostEmail, waitingRoom);

        // 3. Conversión de retorno propietario a entidad de dominio limpia
        return new ReunionVirtual(
                String.valueOf(payload.getZoomNumericId()),
                payload.getJoinUrl(),
                payload.getEncryptedPassword(),
                getNombreProveedor(),
                reserva.getHorario().getInicio(),
                durationMins
        );
    }

    @Override
    public boolean cancelarSala(String idReunion) {
        try {
            long numericId = Long.parseLong(idReunion);
            return zoomSdk.removeMeeting(numericId);
        } catch (NumberFormatException e) {
            System.err.printf("[ZOOM ADAPTER] El ID de reunión '%s' no es un formato numérico válido de Zoom%n", idReunion);
            return false;
        }
    }

    @Override
    public String getNombreProveedor() {
        return "Zoom Video Communications (UEES Enterprise)";
    }

    public ZoomSdkClient getZoomSdk() {
        return zoomSdk;
    }
}
