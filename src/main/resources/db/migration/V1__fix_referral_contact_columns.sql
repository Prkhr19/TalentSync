-- Migrate referral contact fields from legacy recruiter_* columns.
-- Safe for production DBs where Hibernate added contact_* but left recruiter_* in place.

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'referral'
    ) THEN
        RETURN;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'referral' AND column_name = 'contact_name'
    ) THEN
        ALTER TABLE referral ADD COLUMN contact_name VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'referral' AND column_name = 'contact_email'
    ) THEN
        ALTER TABLE referral ADD COLUMN contact_email VARCHAR(255);
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'referral' AND column_name = 'recruiter_name'
    ) THEN
        UPDATE referral
        SET contact_name = recruiter_name
        WHERE contact_name IS NULL AND recruiter_name IS NOT NULL;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'referral' AND column_name = 'recruiter_email'
    ) THEN
        UPDATE referral
        SET contact_email = recruiter_email
        WHERE contact_email IS NULL AND recruiter_email IS NOT NULL;
    END IF;
END $$;

ALTER TABLE referral DROP COLUMN IF EXISTS recruiter_name;
ALTER TABLE referral DROP COLUMN IF EXISTS recruiter_email;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'referral'
    ) THEN
        ALTER TABLE referral ALTER COLUMN contact_name SET NOT NULL;
        ALTER TABLE referral ALTER COLUMN contact_email SET NOT NULL;
    END IF;
END $$;
