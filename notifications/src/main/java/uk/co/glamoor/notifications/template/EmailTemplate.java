package uk.co.glamoor.notifications.template;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import uk.co.glamoor.notifications.model.messaging.BookingMessage;

public class EmailTemplate {

	public static final String BUSINESS_NAME = "Glamoor";
	public static final String BOOKING_CANCELATION_SUBJECT = "[Glamoor] Booking Cancellation";

	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM");
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h a");

	public static String getCancellationBodyText(BookingMessage bookingMessage,
												 boolean isCustomerInitiated) {

		String recipientName = isCustomerInitiated ? bookingMessage.getStylist().getName() :
			bookingMessage.getCustomer().getName();

		String initiatorName = isCustomerInitiated ? bookingMessage.getCustomer().getName() :
			bookingMessage.getStylist().getName();



		return "Dear "+recipientName+",\n"
				+ "\n"
				+ "We regret to inform you that your booking with "
				+ initiatorName + " on " + bookingMessage.getTime().format(dateFormatter)
				+ " at " + bookingMessage.getTime().format(timeFormatter)+ " has been cancelled.\n"
				+ "\n"
				+ "REASON: " + bookingMessage.getCancellationReason() +".\n"
				+ "\n"
				+ "Thank you for understanding.\n"
				+ "\n"
				+ "Best regards,  \n"
				+ "Customer Support";
	}
	
	public static String getCancellationBodyHtml(BookingMessage bookingMessage,
												 boolean isCustomerInitiated) {

		String recipientName = isCustomerInitiated ? bookingMessage.getStylist().getName() :
			bookingMessage.getCustomer().getName();

		String initiatorName = isCustomerInitiated ? bookingMessage.getCustomer().getName() :
			bookingMessage.getStylist().getName();

        String headTag = "<head>\n"
                + "    <style>\n"
                + "        body {\n"
                + "            font-family: Arial, sans-serif;\n"
                + "            line-height: 1.6;\n"
                + "        }\n"
                + "        .container {\n"
                + "            max-width: 600px;\n"
                + "            margin: 0 auto;\n"
                + "            padding: 20px;\n"
                + "            border: 1px solid #ddd;\n"
                + "            border-radius: 8px;\n"
                + "            background-color: #f9f9f9;\n"
                + "        }\n"
                + "        .header {\n"
                + "            text-align: center;\n"
                + "            margin-bottom: 20px;\n"
                + "        }\n"
                + "        .header h1 {\n"
                + "            font-size: 24px;\n"
                + "            color: #333;\n"
                + "        }\n"
                + "        .content {\n"
                + "            margin-bottom: 20px;\n"
                + "        }\n"
                + "        .footer {\n"
                + "            text-align: center;\n"
                + "            font-size: 12px;\n"
                + "            color: #777;\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>";
        return "<html> " + headTag + "<body>\n"
				+ "    <div class=\"container\">\n"
				+ "        <div class=\"header\">\n"
				+ "            <h1>Booking Cancellation Notice</h1>\n"
				+ "        </div>\n"
				+ "        <div class=\"content\">\n"
				+ "            <p>Dear <strong>"+recipientName+"</strong>,</p>\n"
				+ "            <p>We regret to inform you that your booking with <strong>"+initiatorName
				+ "</strong> on <strong>" + bookingMessage.getTime().format(dateFormatter)
				+ " </strong> at <strong>"+ bookingMessage.getTime().format(timeFormatter) +"</strong> has been cancelled.</p>\n"
				+ "            <p><strong>REASON: </strong> "+ bookingMessage.getCancellationReason() +".</p>\n"
				+ "            <p>Thank you for your understanding.</p>\n"
				+ "        </div>\n"
				+ "        <div class=\"footer\">\n"
				+ "            <p>&copy; "+LocalDateTime.now().getYear()+" "+BUSINESS_NAME+". All rights reserved.</p>\n"
				+ "        </div>\n"
				+ "    </div>\n"
				+ "</body>\n"
				+ "</html>";
	}

}
