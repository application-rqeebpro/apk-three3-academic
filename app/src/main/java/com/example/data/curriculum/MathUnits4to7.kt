package com.example.data.curriculum

import com.example.data.local.entities.LessonEntity
import com.example.data.local.entities.UnitEntity

object MathUnits4to7 {

    fun getUnits(): List<UnitEntity> =
        MathUnits4and5.getUnits() + MathUnits6and7.getUnits()

    fun getLessons(): List<LessonEntity> =
        MathUnits4and5.getLessons() + MathUnits6and7.getLessons()
}
