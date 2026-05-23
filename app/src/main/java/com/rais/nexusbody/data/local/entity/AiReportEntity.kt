package com.rais.nexusbody.data.local.entity

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.Date

@DatabaseTable(tableName = "ai_reports")
data class AiReportEntity(
    @DatabaseField(id = true, columnName = "id")
    val id: String = "",

    @DatabaseField(columnName = "user_id", index = true, canBeNull = false)
    val userId: String = "",

    @DatabaseField(columnName = "start_date", dataType = DataType.DATE_LONG)
    val startDate: Date = Date(),

    @DatabaseField(columnName = "end_date", dataType = DataType.DATE_LONG)
    val endDate: Date = Date(),

    @DatabaseField(columnName = "diagnosis_text", dataType = DataType.LONG_STRING)
    val diagnosisText: String = "",

    @DatabaseField(columnName = "clinical_risk_flags_json", dataType = DataType.LONG_STRING)
    val clinicalRiskFlagsJson: String = "[]",

    @DatabaseField(columnName = "workout_recommendations_json", dataType = DataType.LONG_STRING)
    val workoutRecommendationsJson: String = "[]",

    @DatabaseField(columnName = "nutrition_recommendation_json", dataType = DataType.LONG_STRING)
    val nutritionRecommendationJson: String = "{}",

    @DatabaseField(columnName = "overall_health_score")
    val overallHealthScore: Int = 0,

    @DatabaseField(columnName = "generated_at", dataType = DataType.DATE_LONG, index = true)
    val generatedAt: Date = Date(),

    @DatabaseField(columnName = "model_version")
    val modelVersion: String = "",

    @DatabaseField(columnName = "disclaimer", dataType = DataType.LONG_STRING)
    val disclaimer: String = ""
)