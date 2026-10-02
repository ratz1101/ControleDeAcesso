package controle.acesso.dao;

import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import controle.acesso.factory.Conexao;
import controle.acesso.util.Seguranca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // AÇÃO JDBC 1: CADASTRAR USUÁRIO (INSERT + PreparedStatement)
    public void cadastrarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, cargo, email, senha, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getCargo());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, Seguranca.sha256(usuario.getSenha()));
            stmt.setString(6, usuario.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) usuario.setIdUsuario(rs.getInt(1));
            }
        }
    }

    // AÇÃO JDBC 2: LISTAR USUÁRIOS (SELECT + PreparedStatement + ResultSet)
    public List<Usuario> listarUsuarios() throws SQLException {
        String sql = "SELECT id_usuario, nome, cpf, cargo, email, status, data_cadastro "
                   + "FROM usuario ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    // AÇÃO JDBC 6: ATUALIZAR USUÁRIO (UPDATE + PreparedStatement)
    // A senha só é alterada quando uma nova senha é informada (não vazia).
    public void atualizarUsuario(Usuario usuario, boolean alterarSenha) throws SQLException {
        String sql = alterarSenha
                ? "UPDATE usuario SET nome = ?, cpf = ?, cargo = ?, email = ?, senha = ?, status = ? WHERE id_usuario = ?"
                : "UPDATE usuario SET nome = ?, cpf = ?, cargo = ?, email = ?, status = ? WHERE id_usuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int indice = 1;
            stmt.setString(indice++, usuario.getNome());
            stmt.setString(indice++, usuario.getCpf());
            stmt.setString(indice++, usuario.getCargo());
            stmt.setString(indice++, usuario.getEmail());
            if (alterarSenha) {
                stmt.setString(indice++, Seguranca.sha256(usuario.getSenha()));
            }
            stmt.setString(indice++, usuario.getStatus().name());
            stmt.setInt(indice, usuario.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    // AÇÃO JDBC 7: EXCLUIR USUÁRIO (DELETE + PreparedStatement)
    public void excluirUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setCargo(rs.getString("cargo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setStatus(StatusEnum.valueOf(rs.getString("status")));
        if (rs.getTimestamp("data_cadastro") != null) {
            usuario.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
        }
        return usuario;
    }
}
