package uk.co.glamoor.bookings.controller;

import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uk.co.glamoor.bookings.dto.request.MessageRequest;
import uk.co.glamoor.bookings.dto.response.MessageResponse;
import uk.co.glamoor.bookings.mapper.MessageMapper;
import uk.co.glamoor.bookings.model.Message;
import uk.co.glamoor.bookings.service.BookingService;
import uk.co.glamoor.bookings.service.MessageService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/bookings/messages")
@RequiredArgsConstructor
@Validated
public class BookingMessageController {

    private final MessageService messageService;
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getBookingMessages(
            @RequestHeader(value = "X-User-Id", required = false) String customerId,
            @RequestParam @NotBlank(message = "bookingId must be a string.") String bookingId,
            @RequestParam @PositiveOrZero(message = "offset must be at least 0.") int offset) {

        List<Message> messages = messageService.getBookingMessages(bookingId, customerId, offset);
        return ResponseEntity.ok(messages.stream().map(
                MessageMapper::toMessageResponse).toList());
    }

    @PatchMapping("/seen")
    public ResponseEntity<String> markMessagesAsSeen(
            @RequestHeader(value = "X-User-Id", required = false) String customerId,
            @RequestParam String bookingId) {

        try {
            messageService.markMessagesAsSeen(customerId, bookingId);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("There was a problem updating messages as seen.");
        }
    }

    @PostMapping
    public Mono<ResponseEntity<MessageResponse>> sendMessage(
            @RequestHeader(value = "X-User-Id", required = false) String customerId,
            @RequestPart(value = "file", required = false) FilePart image,
            @RequestPart("messageRequest") MessageRequest messageRequest) {

        return Mono.fromCallable(() -> {
                    // Manual validation
                    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                    Set<ConstraintViolation<MessageRequest>> violations = validator.validate(messageRequest);
                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }

                    if (!customerId.equals(messageRequest.getSender())) {
                        throw new IllegalArgumentException("Sender mismatch");
                    }

                    // Map and persist the message
                    Message message = MessageMapper.toMessage(messageRequest);
                    Message savedMessage = messageService.addMessage(customerId, message); // blocking
                    savedMessage.setMessage(""); // clear message for image-only

                    // Handle image if provided
                    if (image != null) {
                        String imagePath = messageService.saveImage(image, savedMessage.getId()).block(); // blocking
                        savedMessage.setMessage(imagePath);
                        savedMessage.setContainsImage(true);
                        messageService.saveMessage(savedMessage); // blocking
                    }

                    return savedMessage;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(savedMessage -> ResponseEntity.ok(MessageMapper.toMessageResponse(savedMessage)))
                .onErrorResume(IOException.class, e -> Mono.fromRunnable(() -> messageService.deleteMessage("messageRequest.getId()"))
                        .subscribeOn(Schedulers.boundedElastic())
                        .then(Mono.error(new RuntimeException("Error saving image", e))));
    }

}
