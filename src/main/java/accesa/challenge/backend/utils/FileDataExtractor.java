package accesa.challenge.backend.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class FileDataExtractor {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String extractSupermarket(String filename) {
        String[] parts = getFilenameParts(filename);
        return parts[0];
    }

    public static LocalDate extractCreationDate(String filename) {
        return extractDateFromFilename(filename, 1);
    }

    public static LocalDate extractCreationDateDiscounts(String filename) {
        return extractDateFromFilename(filename, 2);
    }

    private static LocalDate extractDateFromFilename(String filename, int index) {
        String[] parts = getFilenameParts(filename);
        if (parts.length <= index) {
            throw new IllegalArgumentException("Filename does not contain date at index " + index + ": " + filename);
        }
        try {
            return LocalDate.parse(parts[index], DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in filename: " + filename, e);
        }
    }

    private static String[] getFilenameParts(String filename) {
        if (filename == null || !filename.endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }
        return filename.replace(".csv", "").split("_");
    }
}
