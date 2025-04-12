package space.nanobreaker.configuration.monolith.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MessagingService {

    Connection natsConnection;

    @PostConstruct
    void init() throws Exception {
        natsConnection = Nats.connect("nats://localhost:4222");
    }

    @PreDestroy
    void cleanup() throws Exception {
        if (natsConnection != null) {
            natsConnection.close();
        }
    }

    public <T> Uni<T> request(String subject, Record record, Class<T> clazz) {
        var mapper = new ObjectMapper();
        CompletionStage<Message> cs = null;

        try {
            cs = natsConnection.request(subject, mapper.writeValueAsBytes(record));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return Uni.createFrom().completionStage(cs).onItem().transform(message -> {
            try {
                return mapper.readValue(message.getData(), clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
