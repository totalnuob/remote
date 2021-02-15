package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.DepartmentLookup;
import kz.nicnbk.service.api.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/email"})
public class EmailServiceRest {

    @Autowired
    private EmailService emailService;

    @Value("${attachment.invoice}")
    private String attachmentPath;

    //Currently testing using Postman
    @RequestMapping(path = "/sendMail", method = RequestMethod.GET)
    public String sendMail() {
        emailService.sendMail("magzumov@nicnbk.kz", "Test Email Subject", "This is test text");
        return "Simple mail send is success";
    }

    @RequestMapping(path = "/sendMailByGroup", method = RequestMethod.GET)
    public String sendMailByGroup() {
        emailService.sendMailByGroup("Test subject for group", "Test text for group", DepartmentLookup.DEV.getCode());
        return "Simple mail for group is outgoing";
    }

    @RequestMapping(path = "/sendMailWithAttachment", method = RequestMethod.GET)
    public String sendMailWithAttachment() {
        emailService.sendMailWithAttachments("tleugabylov@nicnbk.kz", "Test Email With Att Subject",
                "This is test text with attachment. Save attachment as PNG to view it.", attachmentPath);
        return "Simple mail with attachment is success";
    }
}
