package controle.acesso.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Conexao {
    private static final String URL = "jdbc:mariadb://localhost:3306/controle_acesso";
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
    // Esta migração (idempotente) acrescenta INATIVO uma única vez por execução.
    private static synchronized void garantirEsquema(Connection conn) {
        if (esquemaVerificado) return;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE usuario MODIFY status "
                    + "ENUM('ATIVO', 'BLOQUEADO', 'INATIVO') NOT NULL DEFAULT 'ATIVO'");
            esquemaVerificado = true;
        } catch (SQLException e) {
            // Sem permissão de ALTER: o script sql/controle_acesso.sql atualizado resolve.
            System.err.println("Aviso: não foi possível atualizar usuario.status: " + e.getMessage());
        }
    }
}
