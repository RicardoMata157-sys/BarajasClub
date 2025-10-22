package com.mx.web.bajarasClub.config;


import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class WhatsAppClient {

	
	private static final Logger log = LoggerFactory.getLogger(WhatsAppClient.class);
  private final WebClient webClient;
  private final String phoneNumberId;
  @Value("${whatsapp.token}") String token;
  
  public WhatsAppClient(
      @Value("${whatsapp.baseUrl}") String baseUrl,
      @Value("${whatsapp.token}") String token,
      @Value("${whatsapp.phoneNumberId}") String phoneNumberId) {
    this.phoneNumberId = phoneNumberId;
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
  
  
  
  @PostConstruct
  public void sanityCheck() {
    try {
      String info = webClient.get()
        .uri(uriBuilder -> uriBuilder
          .pathSegment(phoneNumberId)
          .queryParam("fields", "id,display_phone_number,verified_name")
          .build())
        .retrieve()
        .onStatus(s -> !s.is2xxSuccessful(), resp ->
          resp.bodyToMono(String.class).defaultIfEmpty("")
              .flatMap(b -> Mono.error(new RuntimeException("SANITY " + resp.statusCode() + " -> " + b))))
        .bodyToMono(String.class)
        .block();
      log.info("WhatsApp phone_number_id OK → {}", info);
    } catch (Exception e) {
      log.error("SANITY CHECK: phone_number_id inválido o sin permisos: {}", e.getMessage());
    }
  }


  public Mono<String> sendText(String to, String body) {
	  String toE164 = normalizeE164(to);

	  Map<String, Object> payload = Map.of(
	      "messaging_product", "whatsapp",
	      "to", toE164,
	      "type", "text",
	      "text", Map.of("preview_url", false, "body", body)
	  );

	  return webClient.post()
			  .uri("/{id}/messages", phoneNumberId)
			  .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			  .contentType(MediaType.APPLICATION_JSON)
			  .bodyValue(Map.of(
			     "messaging_product", "whatsapp",
			     "to", toE164,
			     "type", "text",
			     "text", Map.of("body", body)
			  ))
			  .retrieve()
			  .bodyToMono(String.class);

	}

	private static String normalizeE164(String tel) {
	  if (tel == null) return null;
	  String s = tel.replaceAll("\\s+", "");
	  if (!s.startsWith("+")) {
	    String only = s.replaceAll("\\D", "");
	    if (only.length() == 10) return "+52" + only;  // MX
	    return "+" + only;
	  }
	  return s;
	}

  
  
  
  
  
  
  
 
  /** Mensaje con template aprobado */
  public Mono<String> sendTemplate(String toE164, String templateName, String langCode, List<String> params) {
    var components = List.of(
        Map.of("type","body",
               "parameters", params.stream()
                   .map(p -> Map.<String,Object>of("type","text","text",p))
                   .toList())
    );

    Map<String, Object> payload = Map.of(
        "messaging_product", "whatsapp",
        "to", toE164,
        "type", "template",
        "template", Map.of(
            "name", templateName,
            "language", Map.of("code", langCode),
            "components", components
        )
    );

    return webClient.post()
        .uri("/{phoneNumberId}/messages", phoneNumberId)
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(String.class);
  }
}