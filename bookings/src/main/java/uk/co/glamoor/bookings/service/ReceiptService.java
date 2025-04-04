package uk.co.glamoor.bookings.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import uk.co.glamoor.bookings.model.Booking;
import uk.co.glamoor.bookings.repository.BookingRepository;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ReceiptService {

    private final BookingRepository bookingRepository;

    public ReceiptService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public byte[] generateReceipt(String customerId, String bookingId) {
        // Validate booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Customer is not authorized to access this booking.");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Create PDF Writer
            PdfWriter writer = new PdfWriter(baos);

            // Create Document
            Document document = new Document(new PdfDocument(writer));

            // Add Booking Information
            document.add(new Paragraph("Booking Receipt"));
            document.add(new Paragraph("Booking ID: " + booking.getId()));
            document.add(new Paragraph("Customer Name: " + booking.getCustomer().getFirstName() + " "+ booking.getCustomer().getLastName()));
            document.add(new Paragraph("Booking Date: " + booking.getTime()));

            // Add a Table for Services
            Table table = new Table(4);
            table.addHeaderCell("Service Name");
            table.addHeaderCell("Price");
            table.addHeaderCell("Duration");
            table.addHeaderCell("Add-ons");

            booking.getServiceSpecifications().forEach(serviceSpec -> {
                table.addCell(serviceSpec.getService().getName());
                table.addCell(String.valueOf(serviceSpec.getOption().getPrice()));
                table.addCell(serviceSpec.getOption().getDurationMinutes() + "min");

                String addons = serviceSpec.getAddonSpecifications().stream()
                        .map(addonSpec -> addonSpec.getAddon().getName() + " (" + addonSpec.getOption().getPrice() + ")")
                        .reduce((a, b) -> a + ", " + b).orElse("None");
                table.addCell(addons);
            });

            document.add(table);

            // Add Total Price
            document.add(new Paragraph("Total Price: " + booking.getTotalAmount()));

            // Close Document
            document.close();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF receipt", e);
        }
    }
}

