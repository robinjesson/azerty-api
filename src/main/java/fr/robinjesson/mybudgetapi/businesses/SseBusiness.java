package fr.robinjesson.mybudgetapi.businesses;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseBusiness {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createConnection(String userId) {
        // Timeout de 2 minutes (ajustable selon ton besoin)
        SseEmitter emitter = new SseEmitter(120_000L);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);
        return emitter;
    }

    public void sendNotification(String userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data)
                        .id(UUID.randomUUID().toString()));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}
