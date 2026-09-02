package controle.acesso.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexao {
    private static final String URL = "jdbc:mariadb://localhost:3306/controle_acesso";
    private static final String USUARIO_BANCO = "root";
    private static final String SENHA_BANCO = "";

    private Conexao() {}

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO_BANCO, SENHA_BANCO);
    }
}
