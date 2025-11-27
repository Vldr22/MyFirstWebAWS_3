CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                            ip_address VARCHAR(45),
                            operation VARCHAR(50) NOT NULL,
                            file_name VARCHAR(1024),
                            created_at BIGINT NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            details TEXT
);

CREATE INDEX idx_user_id ON audit_logs(user_id);
CREATE INDEX idx_timestamp ON audit_logs(created_at);
CREATE INDEX idx_operation ON audit_logs(operation);