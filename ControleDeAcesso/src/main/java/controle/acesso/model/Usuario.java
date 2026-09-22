package controle.acesso.model;

import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String cpf;
    private String cargo;
    private String email;
    private String senha;
    private StatusEnum status;
    private LocalDateTime dataCadastro;

    public Usuario() {
        this.status = StatusEnum.ATIVO;
    }

    public Usuario(String nome, String cpf, String cargo, String email, String senha, StatusEnum status) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.email = email;
        this.senha = senha;
        this.status = status;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public StatusEnum getStatus() { return status; }
    public void setStatus(StatusEnum status) { this.status = status; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public boolean alterarSenha(String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) return false;
        this.senha = novaSenha;
        return true;
    }

    public boolean atualizarCadastro() {
        return idUsuario > 0 && nome != null && !nome.isBlank();
    }
}
