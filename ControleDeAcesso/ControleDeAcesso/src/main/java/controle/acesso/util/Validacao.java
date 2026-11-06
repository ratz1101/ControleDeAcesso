package controle.acesso.util;

import java.sql.SQLException;
import java.util.regex.Pattern;

public final class Validacao {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validacao() {}

    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    // Aceita com ou sem pontuação; confere os dois dígitos verificadores.
    public static boolean cpfValido(String cpf) {
        if (cpf == null) return false;
        String n = cpf.replaceAll("\\D", "");
        if (n.length() != 11 || n.chars().distinct().count() == 1) return false;
        return digito(n, 9) == n.charAt(9) - '0' && digito(n, 10) == n.charAt(10) - '0';
    }

    private static int digito(String n, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (n.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    // Formata 11 dígitos como 000.000.000-00; outro formato volta sem alteração.
    public static String formatarCpf(String cpf) {
        String n = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (n.length() != 11) return cpf == null ? "" : cpf.trim();
        return n.substring(0, 3) + "." + n.substring(3, 6) + "." + n.substring(6, 9) + "-" + n.substring(9);
    }

    // Traduz erros comuns do banco para mensagens que o usuário entende.
    public static String mensagemErro(Exception e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql) {
                String msg = String.valueOf(sql.getMessage()).toLowerCase();
                if (sql.getErrorCode() == 1062 || msg.contains("duplicate")) {
                    if (msg.contains("cpf")) return "Já existe um usuário com esse CPF.";
                    if (msg.contains("email")) return "Já existe um usuário com esse e-mail.";
                    return "Já existe um registro com esses dados.";
                }
                if (msg.contains("communications link") || msg.contains("connection refused")
                        || msg.contains("unknown database") || msg.contains("could not connect")) {
                    return "Não foi possível conectar ao banco de dados.\nVerifique se o MariaDB está ligado e se o script sql/controle_acesso.sql foi executado.";
                }
            }
        }
        return e.getMessage();
    }
}
