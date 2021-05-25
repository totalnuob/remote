package kz.nicnbk.service.impl.email;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.email.EmailService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    //This is the "from" address that will be used to send emails from UNIC
    //Should be something like "unic@nicnbk.kz"
    private static final String FROM_ADDRESS = "unic@nicnbk.kz";

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            logger.error("Mail exception occured while sending simple email");
        }
    }

    @Override
    public void sendHtmlMail(String to, String subject, String message) {
//        try {
//            Message result = emailSender.createMimeMessage();
//            result.setFrom(new InternetAddress(FROM_ADDRESS));
//            InternetAddress[] toAddresses = {new InternetAddress(to)};
//            result.setRecipients(Message.RecipientType.TO, toAddresses);
//            result.setSubject(subject);
//            result.setContent(message, "text/html");
//            Transport.send(result);
//        } catch (MessagingException e) {
//            logger.error("Mail exception occured while sending html email");
//        }

        try {
            InternetAddress[] parsed = {};
            try {
                parsed = InternetAddress.parse(to);
            } catch (AddressException e) {
                logger.error("Not valid email: " + to, e);
            }

            MimeMessage mailMessage = emailSender.createMimeMessage();
            mailMessage.setSubject(subject, "UTF-8");

            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, "UTF-8");
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(parsed);
            helper.setText(message, true);

            emailSender.send(mailMessage);
        } catch (MessagingException ex) {
            logger.error("Mail exception occured while sending html email");
        }
    }

    @Override
    public void sendMailMultipleRecipients(String[] to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            logger.error("Mail exception occured while sending simple email to multiple recipients");
        }
    }

    @Override
    public void sendMailWithAttachments(String to,
                                          String subject,
                                          String text,
                                          String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Attachment", file);

            emailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Mail exception occured while sending email with attachment");
        }
    }

    @Override
    public void sendMailWithAttachmentsMultipleRecipeints(String[] to, String subject, String text,
                                                          String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Attachment", file);

            emailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Mail exception occured while sending email with attachment to multiple recipients");
        }
    }

    @Override
    public void sendMailByGroup(String subject, String text, int id) {
        try {
            List<String> employeeEmails = getEmployeeEmails(id);
            String[] to = employeeEmails.toArray(new String[0]);

            sendMailMultipleRecipients(to, subject, text);
        } catch (MailException exception) {
            logger.error("Mail exception occured while sending simple email to a group");
        }
    }

    private List<String> getEmployeeEmails(int id) {
        List<EmployeeDto> employees = this.employeeService.findByDepartmentAndActive(id, true);
        List<String> employeeEmails = new ArrayList<>();

        for (EmployeeDto employeeDto : employees) {
            if (employeeDto.getEmail() != null || !employeeDto.getEmail().isEmpty()) {
                String email = employeeDto.getEmail();
                employeeEmails.add(email);
            }
        }
        return employeeEmails;
    }

    @Override
    public void sendMailStaff(String subject, String text) {
        try {
            List<String> employeeEmails = new ArrayList<>();
            for (int i = 1; i <= 11; i++) {
                List<String> emails = getEmployeeEmails(i);
                employeeEmails.addAll(emails);
            }
            String[] to = employeeEmails.toArray(new String[0]);

            sendMailMultipleRecipients(to, subject, text);
        }catch (MailException exception) {
            logger.error("Mail exception occured while sending simple email to staff");
        }
    }

    @Override
    public void sendMailAll(String subject, String text) {
        try {
            List<EmployeeDto> employees = this.employeeService.findActiveAll();
            List<String> employeeEmails = new ArrayList<>();

            for (EmployeeDto employeeDto : employees) {
                if (employeeDto.getEmail() != null || !employeeDto.getEmail().isEmpty()) {
                    String email = employeeDto.getEmail();
                    employeeEmails.add(email);
                }
            }
            String[] to = employeeEmails.toArray(new String[0]);

            sendMailMultipleRecipients(to, subject, text);
        } catch (MailException exception) {
            logger.error("Mail exception occured while sending simple email to Staff");
        }
    }
}
