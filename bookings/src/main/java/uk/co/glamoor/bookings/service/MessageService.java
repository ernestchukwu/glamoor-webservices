package uk.co.glamoor.bookings.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.glamoor.bookings.config.AppConfig;
import uk.co.glamoor.bookings.model.Message;
import uk.co.glamoor.bookings.repository.CustomMessageRepository;
import uk.co.glamoor.bookings.repository.MessageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomMessageRepository customMessageRepository;
    private final AppConfig appConfig;

    public MessageService(MessageRepository messageRepository, CustomMessageRepository customMessageRepository, AppConfig appConfig) {
        this.messageRepository = messageRepository;
        this.customMessageRepository = customMessageRepository;
        this.appConfig = appConfig;
    }

    public List<Message> getBookingMessages(String bookingId, int offset) {

        int batchSize = appConfig.getMessagesRequestBatchSize();

        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        Pageable pageable = PageRequest.of(offset/ batchSize, batchSize, sort);

        return messageRepository.findByBooking(bookingId, pageable).getContent();
    }

    public void markMessagesAsSeen(String customerId, String bookingId) {
        customMessageRepository.markMessagesAsSeen(customerId, bookingId);
    }

    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }

    public void saveImage(MultipartFile image, String messageId) throws IOException {

        Path uploadDir = Paths.get(appConfig.getImages().getDirectories().getBookingMessages());
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path imagePath = uploadDir.resolve(messageId + getFileExtension(image.getOriginalFilename()));
        Files.write(imagePath, image.getBytes());

    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    public LocalDateTime addMessage(Message message) {
        LocalDateTime now = LocalDateTime.now();
        message.setTime(now);
        messageRepository.save(message);
        return now;
    }
}
