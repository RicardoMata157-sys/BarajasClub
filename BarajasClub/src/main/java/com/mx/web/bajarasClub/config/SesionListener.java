package com.mx.web.bajarasClub.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SesionListener implements ApplicationListener<SessionDestroyedEvent> {

//	@Override
//	public void sessionDestroyed(HttpSessionEvent se) {
//		try {
//			System.out.print("La session caduco por su navegador....");
////		      repo.liberarPorSesion(se.getSession().getId());
//		} catch (Exception ignore) {
//		}
//	}
	
	@Bean
	public static HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		event.getSecurityContexts().forEach(ctx -> {

			System.out.print("La session caduco por su navegador...." + event.getId());
		});

	}
}
