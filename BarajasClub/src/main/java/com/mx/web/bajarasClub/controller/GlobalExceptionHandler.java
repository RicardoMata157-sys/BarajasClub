package com.mx.web.bajarasClub.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public String handleAny(Exception ex, RedirectAttributes ra) {
    // log.error("Error inesperado", ex);
    ra.addFlashAttribute("error", "Ocurrió un error inesperado.");
    return "redirect:/admin/rifas/editar";
  }

}
