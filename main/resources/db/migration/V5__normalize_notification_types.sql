-- Normalize legacy/invalid notification types to a safe enum-backed value.

UPDATE notifications
SET type = 'INFO'
WHERE type IS NULL
   OR TRIM(type) = ''
   OR UPPER(type) NOT IN (
       'EMAIL',
       'SMS',
       'PUSH',
       'INFO',
       'SUCCESS',
       'WARNING',
       'APPOINTMENT',
       'PRESCRIPTION',
       'PAYMENT'
   );
