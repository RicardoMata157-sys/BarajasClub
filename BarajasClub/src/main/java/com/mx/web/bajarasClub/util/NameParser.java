package com.mx.web.bajarasClub.util;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class NameParser {

    public static record NameParts(String nombres, String apPat, String apMat) {}

    // Partículas comunes en apellidos (mayúsculas)
    private static final Set<String> PARTICULAS = Set.of("DE","DEL","DELA","DE LAS","DE LOS","LA","LAS","LOS","DA","DOS","DO");

    public static NameParts parse(String q, boolean removeAccents) {
        if (q == null || q.isBlank()) return new NameParts("", "", "");

        String s = normalize(q, removeAccents);
        // tokens en MAYÚSCULAS
        List<String> toks = Arrays.stream(s.split("\\s+"))
                                  .filter(t -> !t.isBlank())
                                  .collect(Collectors.toCollection(ArrayList::new));
        if (toks.isEmpty()) return new NameParts("", "", "");

        // Regla base:
        // >=3 tokens:     NOMBRES...(resto)  APELLIDO_PATERNO(penúltimo)  APELLIDO_MATERNO(último)
        // ==2 tokens:     NOMBRES(primero)  APELLIDO_PATERNO(último)
        // ==1 token:      NOMBRES(único)

        String apMat = "";
        String apPat = "";
        String nombres;

        if (toks.size() >= 3) {
            // Ver si las últimas 2-3 palabras forman una partícula compuesta (“DE LA”, “DE LOS”, etc.)
            // Unimos las partículas con el apellido materno si aparecen al final.
            apMat = joinApellidoConParticulasDesdeFinal(toks, 1); // intenta armar el materno
            // Elimina del final lo que tomamos para apMat
            int tokCountRemoved = apMat.split("\\s+").length;
            for (int i = 0; i < tokCountRemoved; i++) toks.remove(toks.size() - 1);

            // Ahora armar apPat
            apPat = joinApellidoConParticulasDesdeFinal(toks, 1);
            tokCountRemoved = apPat.split("\\s+").length;
            for (int i = 0; i < tokCountRemoved; i++) toks.remove(toks.size() - 1);

            nombres = String.join(" ", toks);
        } else if (toks.size() == 2) {
            apPat = toks.remove(1);
            nombres = toks.get(0);
        } else {
            nombres = toks.get(0);
        }

        return new NameParts(nombres.trim(), apPat.trim(), apMat.trim());
    }

    private static String normalize(String s, boolean removeAccents) {
        String x = s.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        if (!removeAccents) return x;
        // quita acentos
        String n = Normalizer.normalize(x, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", ""); // elimina diacríticos
    }

    // Toma N=1 o más tokens desde el final; si detecta partícula, la “pega” al apellido
    private static String joinApellidoConParticulasDesdeFinal(List<String> toks, int minBase) {
        // minBase=1 → al menos 1 token es el núcleo del apellido
        if (toks.size() < minBase) return "";
        List<String> out = new ArrayList<>();
        // núcleo (último token)
        out.add(toks.get(toks.size() - 1));

        // mira hacia atrás si hay partículas (DE, DEL, DE LA, etc.)
        int i = toks.size() - 2;
        while (i >= 0) {
            String candidate = toks.get(i);
            // intenta combinar “DE LA”, “DE LOS” (dos palabras)
            if (i - 1 >= 0) {
                String bi = toks.get(i - 1) + " " + candidate; // ojo: orden inverso en la lista
                String biN = bi.replaceAll("\\s+", " ");
                if (PARTICULAS.contains(biN)) {
                    out.add(0, biN); // inserta al inicio
                    i -= 2;
                    continue;
                }
            }
            // una palabra
            if (PARTICULAS.contains(candidate)) {
                out.add(0, candidate);
                i -= 1;
            } else {
                break;
            }
        }
        return String.join(" ", out);
    }
}

