package kz.nicnbk.service.api.email;

import kz.nicnbk.service.api.base.BaseService;

public interface EmailService extends BaseService {

    void sendMail(String to, String subject, String text);

    void sendMailMultipleRecipients(String[] to, String subject, String text);

    void sendMailWithAttachments(String to, String subject, String text, String pathToAttachment);

    void sendMailWithAttachmentsMultipleRecipeints(String[] to, String subject, String text,
                                                   String pathToAttachment);

    void sendMailByGroup(String subject, String text, int id);

    void sendMailAll(String subject, String text);

    void sendMailStaff(String subject, String text);
}
