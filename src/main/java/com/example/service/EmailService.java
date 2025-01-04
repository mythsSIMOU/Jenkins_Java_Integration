package com.example.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;

import java.io.IOException;

public class EmailService {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: EmailService <to_email> <subject> <body>");
            System.exit(1);
        }

        String toEmail = args[0];
        String subject = args[1];
        String body = args[2];

        try {
            // Récupérez la clé API à partir des variables d'environnement
            String apiKey = "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\n";
            if (apiKey == null || apiKey.isEmpty()) {
                System.out.println("ERREUR : Clé API SendGrid non définie");
                System.exit(1);
            }

            // Créer l'email
            Email from = new Email("lw_beldjoudi@esi.dz"); // Votre email d'expédition
            Email to = new Email(toEmail);
            Content content = new Content("text/plain", body);
            Mail mail = new Mail(from, subject, to, content);

            // Envoyer l'email
            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            sg.api(request);
            System.out.println("Email envoyé avec succès à " + toEmail);

        } catch (IOException ex) {
            System.err.println("Erreur lors de l'envoi de l'email : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}