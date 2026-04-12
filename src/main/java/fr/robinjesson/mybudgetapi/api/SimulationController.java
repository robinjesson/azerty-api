package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.api.dto.SimulationSendRequest;
import fr.robinjesson.mybudgetapi.api.dto.SimulationSendResponse;
import fr.robinjesson.mybudgetapi.businesses.SseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/simu")
@RequiredArgsConstructor
public class SimulationController {

    private final SseBusiness sseBusiness;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Operation(hidden = true)
    @GetMapping(value = "/open", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> openConnection(@RequestParam String chatId) {
        return sseBusiness.createConnectionFlux(chatId);
    }

    @PostMapping("/send")
    public ResponseEntity<SimulationSendResponse> send(@RequestBody SimulationSendRequest request) {
        String chatId = request.chatId();
        String content = request.content();
        String msgId = UUID.randomUUID().toString();

        // Schedule async notification after 2 seconds
        scheduler.schedule(() -> {
            boolean success = Math.random() > 0.2; // 80% success, 20% error

            if (success) {
                sseBusiness.sendNotification(chatId, "ack", "Simulation : Message '" + content + "' reçu par le broker !");
            } else {
                sseBusiness.sendNotification(chatId, "error", "Simulation : Échec critique du broker imaginaire.");
            }
        }, 2, TimeUnit.SECONDS);

        return ResponseEntity.accepted().body(new SimulationSendResponse("PENDING", msgId));
    }
}



