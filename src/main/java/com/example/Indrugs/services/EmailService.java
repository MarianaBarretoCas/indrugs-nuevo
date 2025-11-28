package com.example.Indrugs.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final SendGrid sendGrid;
    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    @Value("${spring.sendgrid.from}")
    private String fromEmail;

    public EmailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }
    private SendGrid getClient() {
        return new SendGrid(apiKey);
    }

    private void enviarCorreo(String destinatario, String asunto, String contenido) throws Exception {

        Email from = new Email(fromEmail);
        Email to = new Email(destinatario);
        Content content = new Content("text/plain", contenido);
        Mail mail = new Mail(from, asunto, to, content);

        SendGrid sg = getClient();
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        System.out.println("Correo enviado con estado: " + response.getStatusCode());
    }

    public void enviarCorreoRegistro(String destinatario, String nombre) {
        try {
            String msg = "Hola " + nombre + ",\n\nTu registro fue exitoso.\n\n¡Gracias por unirte!";
            enviarCorreo(destinatario, "¡Bienvenido a Indrugs!", msg);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo de registro: " + e.getMessage());
        }
    }

    public void enviarCorreoControl(String destinatario, String nombre) {
        try {
            String msg = "Hola " + nombre + ",\n\nTu control se registró exitosamente.";
            enviarCorreo(destinatario, "¡Registro de control completo!", msg);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo de control: " + e.getMessage());
        }
    }


    public void enviarMasivoConBcc(List<String> correos, String asunto, String mensaje) {

        Email from = new Email(fromEmail);
        Content content = new Content("text/plain", mensaje);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(asunto);
        mail.addContent(content);

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(fromEmail)); // obligatorio para SendGrid (el TO principal)

        for (String correo : correos) {
            personalization.addBcc(new Email(correo));
        }

        mail.addPersonalization(personalization);

        try {
            SendGrid sg = getClient();
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("MASIVO BCC → Status: " + response.getStatusCode());
            System.out.println(response.getBody());

        } catch (Exception e) {
            System.out.println("Error enviando masivo BCC: " + e.getMessage());
        }
    }



}
