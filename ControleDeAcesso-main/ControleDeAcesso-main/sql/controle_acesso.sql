CREATE DATABASE IF NOT EXISTS controle_acesso
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE controle_acesso;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    cargo VARCHAR(80) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL,
    status ENUM('ATIVO', 'BLOQUEADO') NOT NULL DEFAULT 'ATIVO',
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissao_acesso (
    id_permissao INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    horario_inicio TIME NOT NULL,
    horario_fim TIME NOT NULL,
    dias_semana VARCHAR(50) NOT NULL,
    CONSTRAINT fk_permissao_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS biometria_facial (
    id_biometria INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    caminho_imagem VARCHAR(255) NOT NULL,
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_biometria_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rfid_tag (
    id_tag INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    codigo_tag VARCHAR(100) NOT NULL UNIQUE,
    status ENUM('ATIVO', 'BLOQUEADO') NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT fk_rfid_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS administrador (
    id_usuario INT PRIMARY KEY,
    nivel_acesso ENUM('BASICO', 'GERENTE', 'MASTER') NOT NULL DEFAULT 'BASICO',
    CONSTRAINT fk_admin_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dispositivo (
    id_dispositivo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    localizacao VARCHAR(150) NOT NULL,
    status ENUM('ATIVO', 'INATIVO') NOT NULL DEFAULT 'ATIVO'
);

CREATE TABLE IF NOT EXISTS camera (
    id_camera INT AUTO_INCREMENT PRIMARY KEY,
    id_dispositivo INT NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    CONSTRAINT fk_camera_dispositivo FOREIGN KEY (id_dispositivo)
        REFERENCES dispositivo(id_dispositivo) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS log_acesso (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NULL,
    id_dispositivo INT NULL,
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_acesso ENUM('ENTRADA', 'SAIDA') NOT NULL,
    resultado ENUM('AUTORIZADO', 'NEGADO') NOT NULL,
    motivo VARCHAR(255),
    CONSTRAINT fk_log_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE SET NULL,
    CONSTRAINT fk_log_dispositivo FOREIGN KEY (id_dispositivo)
        REFERENCES dispositivo(id_dispositivo) ON DELETE SET NULL
);

-- Usuário de exemplo para testar rapidamente a listagem.
-- A senha abaixo é "123456" em SHA-256.
INSERT INTO usuario (nome, cpf, cargo, email, senha, status)
SELECT 'Usuário de Teste', '000.000.000-00', 'Professor', 'teste@ifsul.edu.br',
       SHA2('123456', 256), 'ATIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE cpf = '000.000.000-00'
);
