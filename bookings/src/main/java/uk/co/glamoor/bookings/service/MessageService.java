package uk.co.glamoor.bookings.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import uk.co.glamoor.bookings.config.AppConfig;
import uk.co.glamoor.bookings.config.BookingsAppConfig;
import uk.co.glamoor.bookings.model.Booking;
import uk.co.glamoor.bookings.model.Message;
import uk.co.glamoor.bookings.repository.CustomMessageRepository;
import uk.co.glamoor.bookings.repository.MessageRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final BookingService bookingService;
    private final FileService fileService;
    private final MessageRepository messageRepository;
    private final CustomMessageRepository customMessageRepository;
    private final BookingsAppConfig bookingsAppConfig;
    private final AppConfig appConfig;

    public List<Message> getBookingMessages(String bookingId, String customerId, int offset) {

        Booking booking = bookingService.getBooking(customerId, bookingId);

        int batchSize = bookingsAppConfig.getMessagesRequestBatchSize();

        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        Pageable pageable = PageRequest.of(offset/ batchSize, batchSize, sort);

        booking.setCustomerUnreadMessagesCount(0);
        bookingService.saveBooking(booking);
        return messageRepository.findByBooking(bookingId, pageable).getContent();
    }

    public void markMessagesAsSeen(String customerId, String bookingId) {
        customMessageRepository.markMessagesAsSeen(customerId, bookingId);
    }

    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }

    public Mono<String> saveImage(FilePart file, String messageId) throws IOException {

        return Mono.fromCallable(() -> {
            final String bookingMessageImagesDir = appConfig.getImages().getDirectories().getBookingMessages();
            final String fileName = messageId + fileService.getFileExtension(file);
            return fileService.saveStaticFile(file, fileName, bookingMessageImagesDir).block();
        });

    }

    public Message addMessage(String customerId, Message message) {

        Booking booking = bookingService.getBooking(customerId, message.getBooking());

        booking.setStylistUnreadMessagesCount(booking.getStylistUnreadMessagesCount()+1);
        bookingService.saveBooking(booking);

        message.setTime(Instant.now());

        return messageRepository.save(message);
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}
