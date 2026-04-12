package fr.robinjesson.mybudgetapi.businesses;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class SseBusiness {

    // Un seul bus pour tout le monde (Multicast)
    // On utilise directBestEffort() pour ne pas bloquer sur backpressure
    private final Sinks.Many<ServerSentEvent<?>> globalSink = Sinks.many()
            .multicast()
            .directBestEffort();

    public Flux<ServerSentEvent<?>> createConnectionFlux(String chatId) {
        return globalSink.asFlux()
                // On ne laisse passer que les messages destinés à cet utilisateur
                .filter(event -> event.comment() != null && event.comment().equals(chatId))
                // On envoie un petit message de ping pour valider la connexion
                .startWith(ServerSentEvent.builder().comment(chatId).event("connected").data("OK").build());
    }

    public void sendNotification(String chatId, String eventName, Object data) {
        ServerSentEvent<?> event = ServerSentEvent.builder()
                .event(eventName)
                .data(data)
                // On utilise le champ 'comment' pour stocker l'ID destinataire (caché du data)
                .comment(chatId)
                .id(UUID.randomUUID().toString())
                .build();

        // On émet dans le bus global, le filtre du Flux fera le reste
        globalSink.tryEmitNext(event);
    }
}





