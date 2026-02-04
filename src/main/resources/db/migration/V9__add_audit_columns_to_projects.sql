DO $$ 
BEGIN
    -- 1. Inyectar 'created_at' si falta
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='projects' AND column_name='created_at') THEN
        
        -- Usamos DEFAULT NOW() para rellenar registros existentes y satisfacer NOT NULL
        ALTER TABLE projects 
        ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
        
    END IF;

    -- 2. Inyectar 'updated_at' si falta
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='projects' AND column_name='updated_at') THEN
        
        ALTER TABLE projects 
        ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
        
    END IF;
END $$;
