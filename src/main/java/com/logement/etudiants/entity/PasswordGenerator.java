package com.logement.etudiants.entity;

/**
 * Classe utilitaire pour la génération de mots de passe
 */
@lombok.experimental.UtilityClass
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    /**
     * Génère un mot de passe aléatoire sécurisé
     */
    public static String generateSecurePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("La longueur du mot de passe doit être d'au moins 8 caractères");
        }

        String allChars = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder();

        // Garantir au moins un caractère de chaque type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Remplir le reste aléatoirement
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Mélanger les caractères
        return shuffleString(password.toString(), random);
    }

    /**
     * Mélange les caractères d'une chaîne
     */
    private static String shuffleString(String input, java.security.SecureRandom random) {
        java.util.List<Character> characters = new java.util.ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        java.util.Collections.shuffle(characters, random);

        StringBuilder shuffled = new StringBuilder();
        for (char c : characters) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }

    /**
     * Valide la force d'un mot de passe
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isLowerCase(c)) hasLowercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (SPECIAL_CHARS.indexOf(c) >= 0) hasSpecialChar = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    /**
     * Calcule le score de force d'un mot de passe (0-100)
     */
    public static int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Longueur
        if (password.length() >= 8) score += 25;
        if (password.length() >= 12) score += 15;
        if (password.length() >= 16) score += 10;

        // Complexité
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(c -> SPECIAL_CHARS.indexOf(c) >= 0);

        if (hasUppercase) score += 10;
        if (hasLowercase) score += 10;
        if (hasDigit) score += 10;
        if (hasSpecialChar) score += 10;

        // Diversité des caractères
        long uniqueChars = password.chars().distinct().count();
        if (uniqueChars >= password.length() * 0.7) score += 10;

        // Patterns répétitifs (pénalité)
        if (hasRepeatingPatterns(password)) score -= 20;

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Vérifie la présence de patterns répétitifs
     */
    private static boolean hasRepeatingPatterns(String password) {
        // Vérifier les caractères répétés consécutifs
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) &&
                    password.charAt(i + 1) == password.charAt(i + 2)) {
                return true;
            }
        }

        // Vérifier les séquences simples (abc, 123, etc.)
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);

            if ((c2 == c1 + 1 && c3 == c2 + 1) || (c2 == c1 - 1 && c3 == c2 - 1)) {
                return true;
            }
        }

        return false;
    }
}
