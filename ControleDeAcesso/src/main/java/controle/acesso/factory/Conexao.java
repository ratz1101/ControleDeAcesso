package controle.acesso.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Conexao {
    // Timeouts evitam que a tela congele se o banco estiver desligado ou travado.
    private static final String URL = "jdbc:mariadb://localhost:3306/controle_acesso"
            + "?connectTimeout=3000&socketTimeout=15000";
    private static final String USUARIO_BANCO = "root";
    private static final String SENHA_BANCO = "";

    private static boolean esquemaVerificado = false;

    private Conexao() {}

    public static Connection conectar() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USUARIO_BANCO, SENHA_BANCO);
        garantirEsquema(conn);
        return conn;
    }

    // Bancos criados com o script antigo só aceitam ATIVO/BLOQUEADO em usuario.status.
    // Só faz o ALTER quando INATIVO realmente falta, e nunca espera mais de 3 s por um bloqueio.
    private static synchronized void garantirEsquema(Connection conn) {
        if (esquemaVerificado) return;
        try (Statement stmt = conn.createStatement()) {
            boolean temInativo = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT COLUMN_TYPE FROM information_schema.COLUMNS "
                    + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'usuario' AND COLUMN_NAME = 'status'")) {
                temInativo = rs.next() && rs.getString(1).contains("INATIVO");
            }
            if (!temInativo) {
                stmt.execute("SET SESSION lock_wait_timeout = 3");
                stmt.executeUpdate("ALTER TABLE usuario MODIFY status "
                        + "ENUM('ATIVO', 'BLOQUEADO', 'INATIVO') NOT NULL DEFAULT 'ATIVO'");
            }
            esquemaVerificado = true;
        } catch (SQLException e) {
            System.err.println("Aviso: não foi possível atualizar usuario.status: " + e.getMessage());
        }
    }
}
