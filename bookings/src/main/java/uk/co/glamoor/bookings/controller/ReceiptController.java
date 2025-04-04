package uk.co.glamoor.bookings.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.glamoor.bookings.service.ReceiptService;

@RestController
@RequestMapping("/api/bookings")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/receipt")
    public ResponseEntity<byte[]> getReceipt(@RequestParam String customerId, 
    		@RequestParam String bookingId) {
        byte[] pdf = receiptService.generateReceipt(customerId, bookingId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}

