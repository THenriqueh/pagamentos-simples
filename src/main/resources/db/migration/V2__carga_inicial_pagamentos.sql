-- Dados iniciais para testar a API (um pagamento em cada status).
INSERT INTO pagamentos (id, valor, remetente, destinatario, chave_idempotencia, status, criado_em, atualizado_em) VALUES
('11111111-1111-1111-1111-111111111111',  150.00, 'joao@pix.com',   'maria@pix.com',  'aaaaaaaa-0000-0000-0000-000000000001', 'SOLICITADO', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours'),
('22222222-2222-2222-2222-222222222222', 4999.90, 'ana@pix.com',    'pedro@pix.com',  'aaaaaaaa-0000-0000-0000-000000000002', 'SOLICITADO', NOW() - INTERVAL '1 hour',  NOW() - INTERVAL '1 hour'),
('33333333-3333-3333-3333-333333333333', 7500.00, 'carlos@pix.com', 'lucia@pix.com',  'aaaaaaaa-0000-0000-0000-000000000003', 'SOLICITADO', NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '30 minutes'),
('44444444-4444-4444-4444-444444444444',   89.90, 'maria@pix.com',  'joao@pix.com',   'aaaaaaaa-0000-0000-0000-000000000004', 'CAPTURADO',  NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day'),
('55555555-5555-5555-5555-555555555555', 6200.00, 'pedro@pix.com',  'carlos@pix.com', 'aaaaaaaa-0000-0000-0000-000000000005', 'FALHOU',     NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day');
