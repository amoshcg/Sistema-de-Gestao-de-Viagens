-- Regra nova: a matricula do empregado deve seguir o formato XXXX-X (somente digitos).
-- As matriculas semeadas em V3 nao seguem o formato; ajustamos antes de travar o CHECK.
UPDATE empregado SET matricula = '0001-1' WHERE matricula = 'E001';
UPDATE empregado SET matricula = '0002-2' WHERE matricula = 'E002';
UPDATE empregado SET matricula = '0003-3' WHERE matricula = 'E003';

ALTER TABLE empregado
    ADD CONSTRAINT ck_empregado_matricula_formato CHECK (matricula ~ '^[0-9]{4}-[0-9]$');
