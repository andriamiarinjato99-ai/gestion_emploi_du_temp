package com.edt.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regroupe les règles de validation utilisées par tous les formulaires.
 * Chaque méthode lève une IllegalArgumentException avec un message
 * compréhensible en cas d'échec, que le contrôleur affiche ensuite
 * dans une AlertUtil.showError(...).
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    /** Longueur imposée pour un numéro de téléphone : exactement 10 chiffres. */
    public static final int LONGUEUR_TELEPHONE = 10;

    /** Un numéro valide = 10 chiffres, ni plus ni moins. */
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{" + LONGUEUR_TELEPHONE + "}$");

    /** Message unique réutilisé partout pour le téléphone. */
    public static final String MESSAGE_TELEPHONE =
            "Le numéro de téléphone doit être 10 chiffres exactement.";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Formes avec séparateur : 8, 08, 8:30, 8h30, 8.30, 8-30, 8 30 ... */
    private static final Pattern HEURE_SEPAREE =
            Pattern.compile("^(\\d{1,2})\\s*(?:[:hH.,\\-\\s]\\s*(\\d{1,2}))?$");

    /** Formes collées : 830 -> 08:30, 0830 -> 08:30, 1245 -> 12:45 */
    private static final Pattern HEURE_COLLEE = Pattern.compile("^(\\d{3,4})$");

    private ValidationUtil() {
    }

    // =====================================================================
    //  Champs génériques
    // =====================================================================

    public static void requireNonEmpty(String value, String champ) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ \"" + champ + "\" est obligatoire.");
        }
    }

    public static void requireEmail(String value, String champ) {
        requireNonEmpty(value, champ);
        if (!EMAIL_PATTERN.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("Le champ \"" + champ + "\" doit être une adresse email valide.");
        }
    }

    public static int requirePositiveInt(String value, String champ) {
        requireNonEmpty(value, champ);
        int result;
        try {
            result = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le champ \"" + champ + "\" doit être un nombre entier.");
        }
        if (result < 0) {
            throw new IllegalArgumentException("Le champ \"" + champ + "\" doit être positif.");
        }
        return result;
    }

    public static void requireSelection(Object value, String champ) {
        if (value == null) {
            throw new IllegalArgumentException("Veuillez sélectionner une valeur pour \"" + champ + "\".");
        }
    }

    // =====================================================================
    //  Téléphone : exactement 10 chiffres
    // =====================================================================

    /** Ne conserve que les chiffres d'une saisie (retire espaces, points, tirets...). */
    public static String nettoyerChiffres(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    /** true si la valeur contient exactement 10 chiffres. */
    public static boolean estTelephoneValide(String value) {
        return value != null && PHONE_PATTERN.matcher(value.trim()).matches();
    }

    /**
     * Le téléphone est obligatoire et doit contenir exactement 10 chiffres :
     * 9 chiffres ou 11 chiffres sont refusés avec le même message explicite.
     */
    public static void requirePhone(String value, String champ) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Le champ \"" + champ + "\" est obligatoire. " + MESSAGE_TELEPHONE);
        }
        if (!estTelephoneValide(value)) {
            throw new IllegalArgumentException(MESSAGE_TELEPHONE);
        }
    }

    /** Variante tolérant un champ vide, mais imposant 10 chiffres s'il est rempli. */
    public static void requirePhoneOptional(String value, String champ) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        if (!estTelephoneValide(value)) {
            throw new IllegalArgumentException(MESSAGE_TELEPHONE);
        }
    }

    // =====================================================================
    //  Heures : correction automatique
    // =====================================================================

    /**
     * Corrige automatiquement une heure saisie librement et la renvoie au
     * format HH:mm.
     *
     * <pre>
     *   "8"     -> "08:00"      "8:30"  -> "08:30"
     *   "08"    -> "08:00"      "8h30"  -> "08:30"
     *   "830"   -> "08:30"      "0830"  -> "08:30"
     *   "14"    -> "14:00"      "14.05" -> "14:05"
     * </pre>
     *
     * @return l'heure normalisée, ou {@code null} si la saisie est vide
     *         ou impossible à interpréter (ex : "abc", "25:00", "8:75").
     */
    public static String normaliserHeure(String value) {
        if (value == null) {
            return null;
        }
        String texte = value.trim();
        if (texte.isEmpty()) {
            return null;
        }

        int heure;
        int minute = 0;

        Matcher collee = HEURE_COLLEE.matcher(texte);
        Matcher separee = HEURE_SEPAREE.matcher(texte);

        if (collee.matches()) {
            String chiffres = collee.group(1);
            if (chiffres.length() == 3) {          // 830 -> 8h30
                heure = Integer.parseInt(chiffres.substring(0, 1));
                minute = Integer.parseInt(chiffres.substring(1));
            } else {                                // 0830 -> 08h30
                heure = Integer.parseInt(chiffres.substring(0, 2));
                minute = Integer.parseInt(chiffres.substring(2));
            }
        } else if (separee.matches()) {
            heure = Integer.parseInt(separee.group(1));
            String minuteTexte = separee.group(2);
            if (minuteTexte != null) {
                minute = Integer.parseInt(minuteTexte);
            }
        } else {
            return null;
        }

        if (heure < 0 || heure > 23 || minute < 0 || minute > 59) {
            return null;
        }
        return String.format("%02d:%02d", heure, minute);
    }

    /**
     * Analyse une heure en appliquant d'abord la correction automatique :
     * l'utilisateur peut donc saisir "8" ou "8:30".
     */
    public static LocalTime parseTime(String value, String champ) {
        requireNonEmpty(value, champ);
        String normalise = normaliserHeure(value);
        if (normalise == null) {
            throw new IllegalArgumentException("Le champ \"" + champ + "\" doit être une heure valide "
                    + "comprise entre 00:00 et 23:59 (ex : 8, 8:30 ou 08:30).");
        }
        return LocalTime.parse(normalise, TIME_FORMAT);
    }

    public static void requireEndAfterStart(LocalTime debut, LocalTime fin) {
        if (!fin.isAfter(debut)) {
            throw new IllegalArgumentException("L'heure de fin doit être postérieure à l'heure de début.");
        }
    }
}
