-- Add version column for optimistic locking
ALTER TABLE recipes ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- Add audit timestamp columns
ALTER TABLE recipes ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL;
ALTER TABLE recipes ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL;

-- Change ingredients and instructions to TEXT type for better PostgreSQL compatibility
ALTER TABLE recipes ALTER COLUMN ingredients TYPE TEXT;
ALTER TABLE recipes ALTER COLUMN instructions TYPE TEXT;

-- Create index on commonly queried columns
CREATE INDEX idx_recipes_vegetarian ON recipes(vegetarian);
CREATE INDEX idx_recipes_servings ON recipes(servings);
CREATE INDEX idx_recipes_created_at ON recipes(created_at);
