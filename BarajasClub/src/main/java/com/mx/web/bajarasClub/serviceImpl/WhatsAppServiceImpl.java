package com.mx.web.bajarasClub.serviceImpl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.mx.web.bajarasClub.config.WhatsAppClient;

import reactor.core.publisher.Mono;

@Service
public class WhatsAppServiceImpl {
  private final WhatsAppClient client;

  public WhatsAppServiceImpl(WhatsAppClient client) {
    this.client = client;
  }

  

  
  
  
  public void enviarConfirmacionCompra(String telefonoMX, String resumen) {
    String e164 = normalizaE164(telefonoMX);
    // Puedes usar .block() si necesitas llamado sincrónico:
    client.sendText(e164, resumen).subscribe();
  }

  /** Convierte “5512345678” -> “+525512345678” si detecta MX (10 dígitos). */
  public static String normalizaE164(String tel) {
    String digits = tel == null ? "" : tel.replaceAll("\\D", "");
    if (digits.length() == 10) return "+52" + digits;   // México
    if (!digits.startsWith("+")) return "+" + digits;
    return tel;
  }
}
