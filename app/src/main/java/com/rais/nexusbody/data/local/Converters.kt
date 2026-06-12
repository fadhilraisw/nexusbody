package com.rais.nexusbody.data.local

import androidx.room.TypeConverter // Penanda fungsi pengubah tipe data Room
import com.google.gson.Gson // Library GSON: Pengolah data JSON
import com.google.gson.reflect.TypeToken // Helper GSON: Membaca tipe data generik

/**
 * DATABASE TYPE CONVERTERS
 * Peran: Jembatan pengubah data kompleks Kotlin (List/Map) ke format database (String/JSON).
 * Kenapa dibutuhkan? SQLite/Room tidak bisa menyimpan List<String> secara langsung.
 */
class Converters {
    private val gson = Gson() // Instansi engine JSON

    // --- CONVERTER UNTUK LIST STRING ---
    // Digunakan untuk kolom: scheduledTimes (Medication) dan targetMuscles (Workout)

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        // Mengubah List ["08:00", "20:00"] menjadi Teks JSON '["08:00", "20:00"]'
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        // Mengembalikan Teks JSON dari database menjadi List asli di Kotlin
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
