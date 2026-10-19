-- Sprint 2: a viagem guarda uma fotografia da area e do cargo do empregado no momento da criacao,
-- preservando o historico mesmo que o empregado mude de area/cargo depois.
ALTER TABLE viagem ADD COLUMN area_solicitante_id BIGINT;
ALTER TABLE viagem ADD COLUMN cargo_solicitante_id BIGINT;

UPDATE viagem v
    SET area_solicitante_id = e.area_id,
        cargo_solicitante_id = e.cargo_id
    FROM empregado e
    WHERE e.id = v.empregado_id;

ALTER TABLE viagem
    ALTER COLUMN area_solicitante_id SET NOT NULL,
    ALTER COLUMN cargo_solicitante_id SET NOT NULL,
    ADD CONSTRAINT fk_viagem_area_solicitante FOREIGN KEY (area_solicitante_id) REFERENCES area (id),
    ADD CONSTRAINT fk_viagem_cargo_solicitante FOREIGN KEY (cargo_solicitante_id) REFERENCES cargo (id);

CREATE INDEX idx_viagem_area_solicitante_id ON viagem (area_solicitante_id);
CREATE INDEX idx_viagem_cargo_solicitante_id ON viagem (cargo_solicitante_id);
