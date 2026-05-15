-- Keep one specialization row per name, repoint doctors, then enforce uniqueness.

UPDATE doctors d
JOIN specializations s ON s.id = d.specialization_id
JOIN (
    SELECT name, MIN(id) AS canonical_id
    FROM specializations
    GROUP BY name
) canonical ON canonical.name = s.name
SET d.specialization_id = canonical.canonical_id
WHERE d.specialization_id <> canonical.canonical_id;

DELETE duplicate_specialization
FROM specializations duplicate_specialization
JOIN specializations canonical_specialization
  ON duplicate_specialization.name = canonical_specialization.name
 AND duplicate_specialization.id > canonical_specialization.id;

ALTER TABLE specializations
ADD CONSTRAINT uk_specializations_name UNIQUE (name);
