package aivlelibraryminiproj.infra;

import aivlelibraryminiproj.config.kafka.KafkaProcessor;
import aivlelibraryminiproj.domain.*;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PublicationRepository publicationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BookPublishRequested'"
    )
    public void wheneverBookPublishRequested_PublicationRegistration(
        @Payload BookPublishRequested bookPublishRequested
    ) {
        BookPublishRequested event = bookPublishRequested;
        System.out.println(
            "\n\n##### listener PublicationRegistration : " +
            bookPublishRequested +
            "\n\n"
        );

        // Sample Logic //
        Publication.publicationRegistration(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BookCoverImagePlotRequest'"
    )
    public void wheneverBookCoverImagePlotRequest_BookCoverImagePlotCompleted(
        @Payload BookCoverImagePlotRequest bookCoverImagePlotRequest
    ) {
        BookCoverImagePlotRequest event = bookCoverImagePlotRequest;
        System.out.println("\n\n##### listener BookCoverImagePlotCompleted : " + event + "\n\n");

        Publication.BookCoverImagePlotCompleted(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='RegenerationRequested'"
    )
    public void wheneverRegenerationRequest_RegenerateCompleted(
        @Payload RegenerationRequested regenerationRequested
    ) {
        RegenerationRequested event = regenerationRequested;
        System.out.println("\n\n##### listener RegenerateCompleted : " + event + "\n\n");

        Publication.RegenerateCompleted(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
