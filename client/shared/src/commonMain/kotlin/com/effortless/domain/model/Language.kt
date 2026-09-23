package com.effortless.domain.model

/**
 * Supported languages in the Effortless system.
 */
data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val scriptCode: String? = null
) {
    companion object {
        val English = Language("en-IN", "English", "English")
        val Hindi = Language("hi-IN", "Hindi", "हिन्दी")
        val Bengali = Language("bn-IN", "Bengali", "বাংলা")
        val Tamil = Language("ta-IN", "Tamil", "தமிழ்")
        val Telugu = Language("te-IN", "Telugu", "తెలుగు")
        val Malayalam = Language("ml-IN", "Malayalam", "മലയാളം")
        val Marathi = Language("mr-IN", "Marathi", "मराठी")
        val Gujarati = Language("gu-IN", "Gujarati", "ગુજરાતી")
        val Kannada = Language("kn-IN", "Kannada", "ಕನ್ನಡ")
        val Odia = Language("od-IN", "Odia", "ଓଡ଼ିଆ")
        val Punjabi = Language("pa-IN", "Punjabi", "ਪੰਜਾਬੀ")

        val DefaultSource = Hindi
        val DefaultTarget = English

        val DefaultList = listOf(
            English,
            Hindi,
            Bengali,
            Tamil,
            Telugu,
            Malayalam,
            Marathi,
            Gujarati,
            Kannada,
            Odia,
            Punjabi
        )
    }
}
