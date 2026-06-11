-- ==========================================
-- NEXUS BODY v2.0 - SUPABASE SCHEMA INIT
-- ==========================================
-- Deskripsi: Jalankan script ini di SQL Editor Supabase
-- untuk menyiapkan seluruh tabel secara otomatis.

-- 1. Tabel Health Assessments (PAGD & Biomarkers)
CREATE TABLE IF NOT EXISTS health_assessments (
    id TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    dateStr TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    weightKg FLOAT4,
    heightCm FLOAT4,
    bodyFatPercentage FLOAT4,
    visceralFatLevel INT4,
    fastingBloodSugar FLOAT4,
    cholesterolTotal FLOAT4,
    systolicBp INT4,
    diastolicBp INT4,
    restingHeartRate INT4,
    sleepDurationHours FLOAT4,
    lilaCm FLOAT4,
    waistCircumferenceCm FLOAT4,
    sgotAst FLOAT4,
    sgptAlt FLOAT4,
    bilirubinTotal FLOAT4,
    bun FLOAT4,
    creatinine FLOAT4,
    egfr FLOAT4,
    hematocrit FLOAT4,
    hb FLOAT4,
    totalTestosterone FLOAT4,
    freeTestosterone FLOAT4,
    estradiolE2 FLOAT4,
    prolactin FLOAT4,
    bodyTemp FLOAT4,
    fastingInsulin FLOAT4,
    conditions JSONB DEFAULT '[]'::jsonb
);

-- 2. Tabel Nutrition Logs
CREATE TABLE IF NOT EXISTS nutrition_logs (
    id TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    foodName TEXT NOT NULL,
    portionGrams FLOAT4,
    calories INT4,
    proteinGrams FLOAT4,
    carbsGrams FLOAT4,
    fatGrams FLOAT4,
    isAiEstimated BOOLEAN DEFAULT FALSE
);

-- 3. Tabel Workout Sessions
CREATE TABLE IF NOT EXISTS workout_sessions (
    id TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    routineName TEXT NOT NULL,
    totalDurationMinutes INT4,
    clinicalNotes TEXT,
    xpEarned INT4 DEFAULT 0
);

-- 4. Tabel Workout Exercises (Detail Gerakan)
CREATE TABLE IF NOT EXISTS workout_exercises (
    id TEXT PRIMARY KEY,
    sessionId TEXT REFERENCES workout_sessions(id) ON DELETE CASCADE,
    exerciseName TEXT NOT NULL,
    targetMuscles JSONB DEFAULT '[]'::jsonb,
    sets INT4,
    reps INT4,
    weightKg FLOAT4
);

-- 5. Tabel Medications (Jadwal Obat)
CREATE TABLE IF NOT EXISTS medications (
    id TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    name TEXT NOT NULL,
    dosage TEXT,
    frequency TEXT,
    scheduledTimes JSONB DEFAULT '[]'::jsonb,
    startDate BIGINT,
    endDate BIGINT,
    isActive BOOLEAN DEFAULT TRUE,
    notes TEXT
);

-- 6. Tabel Gamification Profiles
CREATE TABLE IF NOT EXISTS gamification_profiles (
    id TEXT PRIMARY KEY,
    userId TEXT UNIQUE NOT NULL,
    totalNutritionXp INT4 DEFAULT 0,
    overallRank TEXT DEFAULT 'Bronze I',
    chestXp INT4 DEFAULT 0,
    backUpperXp INT4 DEFAULT 0,
    backLowerXp INT4 DEFAULT 0,
    shouldersXp INT4 DEFAULT 0,
    bicepsXp INT4 DEFAULT 0,
    tricepsXp INT4 DEFAULT 0,
    quadricepsXp INT4 DEFAULT 0,
    hamstringsXp INT4 DEFAULT 0,
    glutesXp INT4 DEFAULT 0,
    calvesXp INT4 DEFAULT 0,
    coreAbsXp INT4 DEFAULT 0,
    totalWorkoutsCompleted INT4 DEFAULT 0,
    totalNutritionLogs INT4 DEFAULT 0
);

-- 7. Tabel Quests (Custom Challenges)
CREATE TABLE IF NOT EXISTS quests (
    id TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    title TEXT NOT NULL,
    module TEXT,
    variable TEXT,
    operator TEXT,
    targetValue FLOAT4,
    xpReward INT4,
    isCompleted BOOLEAN DEFAULT FALSE
);

-- ==========================================
-- INDEXING UNTUK KECEPATAN (Opsional)
-- ==========================================
CREATE INDEX IF NOT EXISTS idx_health_user ON health_assessments(userId);
CREATE INDEX IF NOT EXISTS idx_nutrition_user ON nutrition_logs(userId);
CREATE INDEX IF NOT EXISTS idx_workout_user ON workout_sessions(userId);
CREATE INDEX IF NOT EXISTS idx_meds_user ON medications(userId);
