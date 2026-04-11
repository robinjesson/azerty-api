package fr.robinjesson.mybudgetapi.api;

import fr.robinjesson.mybudgetapi.api.dto.SimulationSendRequest;
import fr.robinjesson.mybudgetapi.api.dto.SimulationSendResponse;
import fr.robinjesson.mybudgetapi.businesses.SseBusiness;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @GetMapping(value = "/open", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter openConnection(@RequestParam String userId) {
        return sseBusiness.createConnection(userId);
    }

    @PostMapping("/send")
    public ResponseEntity<SimulationSendResponse> send(@RequestBody SimulationSendRequest request) {
        String userId = request.userId();
        String content = request.content();
        String msgId = UUID.randomUUID().toString();

        // Schedule async notification after 2 seconds
        scheduler.schedule(() -> {
            boolean success = Math.random() > 0.2; // 80% success, 20% error

            if (success) {
                sseBusiness.sendNotification(userId, "ack", "Simulation : Message '" + content + "' reçu par le broker !");
            } else {
                sseBusiness.sendNotification(userId, "error", "Simulation : Échec critique du broker imaginaire.");
            }
        }, 2, TimeUnit.SECONDS);

        return ResponseEntity.accepted().body(new SimulationSendResponse("PENDING", msgId));
    }
}
