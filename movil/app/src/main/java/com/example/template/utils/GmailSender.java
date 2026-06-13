package com.example.template.utils;

import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class GmailSender {
    private static final String TAG = "GmailSender";

    public static void sendWelcomeEmail(String recipientEmail, String tenantName) {
        new Thread(() -> {
            try {
                String smtpHost = "smtp.gmail.com";
                int smtpPort = 465;
                String user = "bolclick.official@gmail.com";
                String pass = "qjti qafk xhku hweo"; // App password

                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket socket = (SSLSocket) factory.createSocket(smtpHost, smtpPort);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                Log.d(TAG, "Connected to SMTP server: " + reader.readLine());

                // EHLO
                writer.println("EHLO localhost");
                readResponses(reader);

                // AUTH LOGIN
                writer.println("AUTH LOGIN");
                Log.d(TAG, "AUTH LOGIN response: " + reader.readLine());

                // User
                String userBase64 = Base64.encodeToString(user.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
                writer.println(userBase64);
                Log.d(TAG, "User response: " + reader.readLine());

                // Pass
                String passBase64 = Base64.encodeToString(pass.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
                writer.println(passBase64);
                Log.d(TAG, "Pass response: " + reader.readLine());

                // MAIL FROM
                writer.println("MAIL FROM:<" + user + ">");
                Log.d(TAG, "MAIL FROM response: " + reader.readLine());

                // RCPT TO
                writer.println("RCPT TO:<" + recipientEmail + ">");
                Log.d(TAG, "RCPT TO response: " + reader.readLine());

                // DATA
                writer.println("DATA");
                Log.d(TAG, "DATA response: " + reader.readLine());

                // Headers & Body
                writer.println("From: \"BolClick\" <" + user + ">");
                writer.println("To: " + recipientEmail);
                
                // Base64 encode the subject to support UTF-8 characters like ¡ and 🚀
                String subjectBase64 = Base64.encodeToString(("¡Bienvenido a BolClick, " + tenantName + "!").getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
                writer.println("Subject: =?UTF-8?B?" + subjectBase64 + "?=");
                
                writer.println("MIME-Version: 1.0");
                writer.println("Content-Type: text/html; charset=UTF-8");
                writer.println();
                writer.println("<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 25px; border: 1px solid #e2e8f0; border-radius: 12px;\">");
                writer.println("  <h2 style=\"color: #0d9488; margin-bottom: 20px; font-size: 24px; font-weight: bold;\">¡Hola " + tenantName + "! 🚀</h2>");
                writer.println("  <p style=\"font-size: 16px; line-height: 1.6; color: #1e293b; margin: 0;\">Tu cuenta comercial ha sido creada exitosamente. Hemos recibido tu solicitud para abrir un espacio en BolClick.</p>");
                writer.println("</div>");
                writer.println(".");
                Log.d(TAG, "Final response: " + reader.readLine());

                // QUIT
                writer.println("QUIT");
                socket.close();
                Log.d(TAG, "Email sent successfully to: " + recipientEmail);
            } catch (Exception e) {
                Log.e(TAG, "Error sending email", e);
            }
        }).start();
    }

    private static void readResponses(BufferedReader reader) throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            Log.d(TAG, "Server: " + line);
            if (!line.startsWith("250-")) {
                break;
            }
        }
    }
}
