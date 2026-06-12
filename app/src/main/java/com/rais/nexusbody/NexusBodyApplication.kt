package com.rais.nexusbody

import android.app.Application // Kelas dasar aplikasi Android
import dagger.hilt.android.HiltAndroidApp // Anotasi pemicu generator kode Hilt

/**
 * NEXUS BODY APPLICATION CLASS
 * Peran: Titik masuk utama aplikasi untuk inisialisasi Hilt.
 * Tech Stack: Hilt (Dagger) - Digunakan untuk Dependency Injection di seluruh aplikasi.
 * Alur: Android System -> NexusBodyApplication -> Hilt Components Init.
 */
@HiltAndroidApp
class NexusBodyApplication : Application()
