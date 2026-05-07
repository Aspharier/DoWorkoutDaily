package com.aspharier.doworkoutdaily.data.model

enum class WorkoutType(
    val displayName: String,
    val emoji: String
) {
    CHEST("Chest", "🏋️"),
    BACK("Back", "💪"),
    LEGS("Legs", "🦵"),
    SHOULDERS("Shoulders", "🤸"),
    ARMS("Arms", "💪"),
    CARDIO("Cardio", "🏃"),
    YOGA("Yoga", "🧘"),
    FULL_BODY("Full Body", "⚡"),
    HIIT("HIIT", "🔥"),
    STRETCHING("Stretching", "🙆"),
    CYCLING("Cycling", "🚴"),
    SWIMMING("Swimming", "🏊"),
    CUSTOM("Custom", "✨");

    companion object {
        fun fromName(name: String): WorkoutType {
            return entries.find { it.name == name } ?: CUSTOM
        }
    }
}
