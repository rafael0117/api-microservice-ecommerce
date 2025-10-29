package com.rafael0117.pedido_service.application.service.Impl;

import com.rafael0117.pedido_service.application.service.EmailService;
import com.rafael0117.pedido_service.domain.model.EstadoPedido;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void enviarCambioEstado(String to, String nombre, Long idPedido, EstadoPedido estado, String mensaje) {
        try {
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Actualización de tu pedido #" + idPedido);
            var body = """
                    Hola %s,
                    
                    Tu pedido #%d ahora está en estado: %s.
                    %s
                    
                    ¡Gracias por tu compra!
                    """.formatted(nombre != null ? nombre : "cliente", idPedido, estado, mensaje != null ? mensaje : "");
            helper.setText(body, false);
            mailSender.send(mime);
        } catch (Exception e) {
            // Loguea, pero no detengas el flujo del pedido
            System.err.println("No se pudo enviar email: " + e.getMessage());
        }
    }
}