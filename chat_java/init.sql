-- Script de inicialização do banco de dados
-- Executado automaticamente quando o container inicia

-- Cria tabela de mensagens
CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    sender VARCHAR(100) NOT NULL,
    receiver VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cria índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender);
CREATE INDEX IF NOT EXISTS idx_messages_receiver ON messages(receiver);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);

-- Insere dados de exemplo (opcional)
INSERT INTO messages (sender, receiver, content) VALUES
('Alice', 'Bob', 'Olá Bob! Tudo bem?'),
('Bob', 'Alice', 'Oi Alice! Tudo sim, e você?'),
('Alice', 'Bob', 'Também tudo bem! Vamos programar?'),
('Bob', 'Alice', 'Claro! Vamos fazer um chat em Java!')
ON CONFLICT DO NOTHING;

-- Cria um usuário extra para a aplicação (opcional)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'app_user') THEN
        CREATE USER app_user WITH PASSWORD 'app_password';
    END IF;
END
$$;

-- Concede permissões
GRANT CONNECT ON DATABASE chat_db TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- Log
DO LANGUAGE plpgsql $$
BEGIN
    RAISE NOTICE '✅ Banco de dados chat_db inicializado com sucesso!';
END
$$;