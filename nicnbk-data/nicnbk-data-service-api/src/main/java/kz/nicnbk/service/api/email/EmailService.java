package kz.nicnbk.service.api.email;

import kz.nicnbk.service.api.base.BaseService;

public interface EmailService extends BaseService {
    void sendMail(String to, String subject, String text);

    void sendMailWithAttachments(String to, String subject, String text, String pathToAttachment);
}
