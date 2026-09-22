package controle.acesso.view.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateUtils() {
    }

    public static String formatar(LocalDateTime dataHora) {
        return dataHora == null ? "" : FORMATO.format(dataHora);
    }
}
