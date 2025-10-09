package com.mx.web.bajarasClub.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import com.mx.web.bajarasClub.model.Numero;

public class NumeroGenerator {

	static final int MONEY_SCALE = 2;
	
	static final RoundingMode MONEY_RM = RoundingMode.HALF_UP;
	
	public static BigDecimal montoPorNumero(Double precioPorNumero, List<Numero> numVendidos) {
	    BigDecimal precio = (precioPorNumero == null)
	            ? BigDecimal.ZERO
	            : BigDecimal.valueOf(precioPorNumero);

	    long totalNumeros = (numVendidos == null) ? 0L : numVendidos.stream()
	            .filter(Objects::nonNull)
	            // si tienes un flag/estado de vendido en Numero, descomenta:
	            // .filter(Numero::isVendido)
	            .count();

	    return precio
	            .multiply(BigDecimal.valueOf(totalNumeros))
	            .setScale(MONEY_SCALE, MONEY_RM);
	}
	
	
	
	
	public static String generarFolio(LocalDateTime fechaCompra, List<String> numerosLabels, Integer clienteId) {
		Objects.requireNonNull(fechaCompra, "fechaCompra");
		Objects.requireNonNull(numerosLabels, "numerosLabels");

// 2) Canonicalizar los números: limpiamos, conservamos ceros y ordenamos por valor numérico
		List<String> labels = numerosLabels.stream().map(s -> s == null ? "" : s.trim()).filter(s -> !s.isEmpty())
				.sorted(Comparator.comparingInt(s -> Integer.parseInt(s))) // orden numérico, pero imprimimos con ceros
				.toList();
		String numsCanonical = String.join(",", labels); // p.ej. "0031,0051,0111"

// 3) Cliente en base36 para hacerlo corto
		String cliB36 = Long.toUnsignedString(clienteId, 36).toUpperCase(Locale.ROOT);

// 4) Hash (6 chars base36) de los datos para evitar colisiones
		CRC32 crc = new CRC32();
		String toHash = numsCanonical + "|" + clienteId;
		crc.update(toHash.getBytes(StandardCharsets.UTF_8));
		String hash = Long.toString(crc.getValue(), 36).toUpperCase(Locale.ROOT);
// normalizamos a 6 (padding con 0 a la izquierda si es corto)
		hash = String.format("%6s", hash).replace(' ', '0');

		return "RF-" + fechaCompra + "-" + cliB36 + "-" + hash;
	}


	
	public static BigDecimal calcularTotal(BigDecimal precioBoleto, Integer cantidad) {
	    if (precioBoleto == null || cantidad == null || cantidad <= 0) {
	        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	    }
	    return precioBoleto
	            .multiply(BigDecimal.valueOf(cantidad))
	            .setScale(2, RoundingMode.HALF_UP);
	}
	
	
	
	
	
	
	
	  public static List<String> parseCsvLabels(String csv) {
		    if (csv == null) return List.of();
		    return Arrays.stream(csv.split(","))
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .collect(Collectors.toList());
		  }
	
	
	
	 public static BigDecimal calcularMontoEsperado(
	            int maxValor,
	            int numerosPorBoleto,
	            BigDecimal precioBoleto,
	            boolean incluyeCero // true => 0..max ; false => 1..max
	    ) {
	        if (precioBoleto == null) precioBoleto = BigDecimal.ZERO;
	        if (numerosPorBoleto <= 0) numerosPorBoleto = 1;

	        int totalNumeros = incluyeCero ? (maxValor + 1) : maxValor;
	        if (totalNumeros <= 0) return BigDecimal.ZERO;

	        // división entera con redondeo hacia arriba
	        int boletosNecesarios = (totalNumeros + numerosPorBoleto - 1) / numerosPorBoleto;

	        return precioBoleto.multiply(BigDecimal.valueOf(boletosNecesarios))
	                           .setScale(2, RoundingMode.HALF_UP);
	    }
	
	
	
    public static List<String> generarPorMaximo(int digitos, int maxInclusive) {
        validarDigitos(digitos);
        if (maxInclusive < 0) throw new IllegalArgumentException("maxInclusive debe ser >= 0");

        int limitePorDigitos = (int) Math.pow(10, digitos) - 1;
        int hasta = Math.min(maxInclusive, limitePorDigitos);

        String fmt = "%0" + digitos + "d";
        List<String> out = new ArrayList<>(hasta + 1);
        for (int i = 0; i <= hasta; i++) {
            out.add(String.format(fmt, i));
        }
        return out;
    }

    /**
     * Genera una CANTIDAD dada: 0..cantidad-1 (EXCLUSIVO en el tope),
     * con relleno de ceros.
     */
    public static List<String> generarPorCantidad(int digitos, int cantidad) {
        validarDigitos(digitos);                  // e.g., digitos >= 1
        if (cantidad < 0) throw new IllegalArgumentException("cantidad debe ser >= 0");

        long limitePorDigitos = (long) Math.pow(10, digitos); // 10^digitos
        // No se pueden generar más que los que caben con ese número de dígitos
        int toGenerate = (int) Math.min((long) cantidad, limitePorDigitos);

        String fmt = "%0" + digitos + "d";
        List<String> out = new ArrayList<>(toGenerate);
        for (int i = 0; i <= toGenerate; i++) {   // <--- OJO: aquí va '<', no '<='
            out.add(String.format(fmt, i));
        }
        return out;
    }


    /**
     * Genera un rango arbitrario DESDE..HASTA (ambos INCLUSIVOS),
     * con relleno de ceros y recorte por límite de "digitos".
     */
    public static List<String> generarPorRango(int digitos, int desde, int hasta) {
        validarDigitos(digitos);
        if (desde < 0 || hasta < 0) throw new IllegalArgumentException("desde/hasta deben ser >= 0");
        if (desde > hasta) throw new IllegalArgumentException("desde no puede ser mayor que hasta");

        int limite = (int) Math.pow(10, digitos) - 1;
        int ini = Math.min(desde, limite);
        int fin = Math.min(hasta, limite);

        String fmt = "%0" + digitos + "d";
        List<String> out = new ArrayList<>(fin - ini + 1);
        for (int i = ini; i <= fin; i++) {
            out.add(String.format(fmt, i));
        }
        return out;
    }

    private static void validarDigitos(int digitos) {
        if (digitos < 1 || digitos > 9) { // 9 por seguridad con pow(int) -> int
            throw new IllegalArgumentException("digitos debe estar entre 1 y 9");
        }
    }
}