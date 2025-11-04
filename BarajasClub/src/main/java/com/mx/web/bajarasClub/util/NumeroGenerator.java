package com.mx.web.bajarasClub.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.mx.web.bajarasClub.dto.CompraRequest;
import com.mx.web.bajarasClub.model.Cliente;

public class NumeroGenerator {

	static final int MONEY_SCALE = 2;
	
	static final RoundingMode MONEY_RM = RoundingMode.HALF_UP;
	
	public static BigDecimal montoPorNumero(Double precioPorNumero,   long totalNumeros) {
	    BigDecimal precio = (precioPorNumero == null)
	            ? BigDecimal.ZERO
	            : BigDecimal.valueOf(precioPorNumero);

	  

	    return precio
	            .multiply(BigDecimal.valueOf(totalNumeros))
	            .setScale(MONEY_SCALE, MONEY_RM);
	}
	
	public static Cliente generaCliente(CompraRequest request) {
		Cliente cliente = new Cliente(request.getNombre(), request.getApellidoP(), request.getApellidoM(),
				request.getMunicipio(), request.getEstado(), request.getCodigoPostal(), request.getEmail(),
				request.getTelefono());
		return cliente;
	}
	
	public static List<String> parseNumerosCsv(String csv) {
		if (csv == null || csv.isBlank())
			return Collections.emptyList();
		return Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).map(String::valueOf).distinct()
				.sorted().toList();
	}
	
//	public static String generarFolio(LocalDateTime fechaCompra, List<String> numerosLabels, Integer clienteId) {
//		Objects.requireNonNull(fechaCompra, "fechaCompra");
//		Objects.requireNonNull(numerosLabels, "numerosLabels");
//
//// 2) Canonicalizar los números: limpiamos, conservamos ceros y ordenamos por valor numérico
//		List<String> labels = numerosLabels.stream().map(s -> s == null ? "" : s.trim()).filter(s -> !s.isEmpty())
//				.sorted(Comparator.comparingInt(s -> Integer.parseInt(s))) // orden numérico, pero imprimimos con ceros
//				.toList();
//		String numsCanonical = String.join(",", labels); // p.ej. "0031,0051,0111"
//
//// 3) Cliente en base36 para hacerlo corto
//		String cliB36 = Long.toUnsignedString(clienteId, 36).toUpperCase(Locale.ROOT);
//
//// 4) Hash (6 chars base36) de los datos para evitar colisiones
//		CRC32 crc = new CRC32();
//		String toHash = numsCanonical + "|" + clienteId;
//		crc.update(toHash.getBytes(StandardCharsets.UTF_8));
//		String hash = Long.toString(crc.getValue(), 36).toUpperCase(Locale.ROOT);
//// normalizamos a 6 (padding con 0 a la izquierda si es corto)
//		hash = String.format("%6s", hash).replace(' ', '0');
//
//		return "RF-" + fechaCompra + "-" + cliB36 + "-" + hash;
//	}

	
	public static String generarFolio(LocalDateTime fechaCompra, List<String> numerosLabels, Integer clienteId) {
		Objects.requireNonNull(fechaCompra, "fechaCompra");
		Objects.requireNonNull(numerosLabels, "numerosLabels");
		Objects.requireNonNull(clienteId, "clienteId");

// 1) Fecha: YYYYMMDD
		String fecha = fechaCompra.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd

// 2) Números seleccionados solo concatenados, respetando ceros a la izquierda.
//    - Se limpian caracteres no numéricos por si vinieran con espacios/comas.
//    - No se reordena: respeta el orden en que llegaron.
		String numerosConcat = numerosLabels.stream().filter(Objects::nonNull).map(String::trim)
				.filter(s -> !s.isEmpty()).map(s -> s.replaceAll("[^\\d]", "")) // deja solo dígitos (conserva ceros)
				.filter(s -> !s.isEmpty()).collect(Collectors.joining());

// 3) Cliente: al final, con 2 dígitos mínimo (01, 02, … 10, 11, …)
//    Si el id >= 100, se imprime completo (no se corta).
		String cliStr = (clienteId < 100) ? String.format("%02d", clienteId) : String.valueOf(clienteId);

// 4) Folio final
		return  "RF-" + fecha + numerosConcat + cliStr;
	}
	
	

	
	public static BigDecimal calcularTotal(BigDecimal precioBoleto, Integer cantidad) {
	    if (precioBoleto == null || cantidad == null || cantidad <= 0) {
	        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	    }
	    return precioBoleto
	            .multiply(BigDecimal.valueOf(cantidad))
	            .setScale(2, RoundingMode.HALF_UP);
	}
	
	
	public static String normalizaTelefono(String q) {
	        if (q == null) return "";
	        // quita símbolos, conserva dígitos (útil si guardas el tel normalizado)
	        return q.replaceAll("\\D", "");
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
        validarDigitos(digitos); // e.g., digitos >= 1
        if (cantidad < 0) throw new IllegalArgumentException("cantidad debe ser >= 0");

        long capacidad = pow10(digitos);                 // 10^digitos
        int toGenerate = (int) Math.min((long) cantidad, capacidad);

        String fmt = "%0" + digitos + "d";
        List<String> out = new ArrayList<>(toGenerate);
        for (int i = 0; i <= toGenerate; i++) {           // <--- ¡aquí va '<', no '<='!
            out.add(String.format(fmt, i));
        }
        return out;
    }

    private static long pow10(int d) {
        long x = 1;
        for (int i = 0; i < d; i++) x *= 10L;
        return x;
    }

    
    
    // ===================== helpers =====================

    public static Set<String> normalizeEstados(String estado) {
        if (estado == null || estado.isBlank()) {
            return Set.of("VENDIDO", "APARTADO"); // por defecto la vista muestra ambos
        }
        return Set.of(estado.trim().toUpperCase(Locale.ROOT));
    }

    public static String emptyToNull(String s){
        return (s == null || s.isBlank()) ? null : s;
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