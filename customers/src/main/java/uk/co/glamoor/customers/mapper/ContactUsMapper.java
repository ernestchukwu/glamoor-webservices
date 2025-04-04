package uk.co.glamoor.customers.mapper;

import uk.co.glamoor.customers.dto.response.ContactUsRequest;
import uk.co.glamoor.customers.model.ContactUs;

public class ContactUsMapper {

    public static ContactUs toContactUs(ContactUsRequest request) {
        ContactUs contactUs = new ContactUs();
        contactUs.setName(request.getName());
        contactUs.setEmail(request.getEmail());
        contactUs.setMessage(request.getMessage());

        ContactUs.ContactUsMetadata metadata = new ContactUs.ContactUsMetadata();
        metadata.setCustomerId(request.getMetadata().getCustomerId());
        metadata.setCustomerUid(request.getMetadata().getCustomerUid());
        metadata.setAppVersion(request.getMetadata().getAppVersion());
        metadata.setAppBuildNumber(request.getMetadata().getAppBuildNumber());
        metadata.setPlatform(request.getMetadata().getPlatform());
        contactUs.setMetadata(metadata);

        return contactUs;
    }
}
