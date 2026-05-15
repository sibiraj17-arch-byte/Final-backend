-- Keep one patient row per user, repoint dependent records, then remove duplicates.

UPDATE appointments a
JOIN patients p ON p.id = a.patient_id
JOIN (
    SELECT user_id, MIN(id) AS canonical_id
    FROM patients
    GROUP BY user_id
) canonical ON canonical.user_id = p.user_id
SET a.patient_id = canonical.canonical_id
WHERE a.patient_id <> canonical.canonical_id;

UPDATE prescriptions pr
JOIN patients p ON p.id = pr.patient_id
JOIN (
    SELECT user_id, MIN(id) AS canonical_id
    FROM patients
    GROUP BY user_id
) canonical ON canonical.user_id = p.user_id
SET pr.patient_id = canonical.canonical_id
WHERE pr.patient_id <> canonical.canonical_id;

UPDATE reviews r
JOIN patients p ON p.id = r.patient_id
JOIN (
    SELECT user_id, MIN(id) AS canonical_id
    FROM patients
    GROUP BY user_id
) canonical ON canonical.user_id = p.user_id
SET r.patient_id = canonical.canonical_id
WHERE r.patient_id <> canonical.canonical_id;

UPDATE payments pay
JOIN patients p ON p.id = pay.patient_id
JOIN (
    SELECT user_id, MIN(id) AS canonical_id
    FROM patients
    GROUP BY user_id
) canonical ON canonical.user_id = p.user_id
SET pay.patient_id = canonical.canonical_id
WHERE pay.patient_id <> canonical.canonical_id;

UPDATE medical_histories mh
JOIN patients p ON p.id = mh.patient_id
JOIN (
    SELECT user_id, MIN(id) AS canonical_id
    FROM patients
    GROUP BY user_id
) canonical ON canonical.user_id = p.user_id
SET mh.patient_id = canonical.canonical_id
WHERE mh.patient_id <> canonical.canonical_id;

UPDATE medical_reports mr
JOIN patients p ON p.id = mr.patient_id
JOIN (
    SELECT user_id, MIN(id) AS canonical_id
    FROM patients
    GROUP BY user_id
) canonical ON canonical.user_id = p.user_id
SET mr.patient_id = canonical.canonical_id
WHERE mr.patient_id <> canonical.canonical_id;

DELETE duplicate_patient
FROM patients duplicate_patient
JOIN patients canonical_patient
  ON duplicate_patient.user_id = canonical_patient.user_id
 AND duplicate_patient.id > canonical_patient.id;
