package com.nammamistri.util

object MaterialCalculator {
    data class MaterialResult(val bricks: Int, val cementBags: Int, val sandCFT: Double, val volume: Double)
    data class SlabResult(val cementBags: Int, val sandCFT: Double, val aggregateCFT: Double, val steelKg: Int, val volume: Double)
    data class PlasterResult(val cementBags: Int, val sandCFT: Double, val area: Double)

    fun calculateWall(lengthM: Double, heightM: Double, thicknessM: Double): MaterialResult {
        val wallVolume = lengthM * heightM * thicknessM
        val bricks = (wallVolume / 0.002 * 1.05).toInt()
        val mortarVolume = wallVolume * 0.30
        val cementBags = (mortarVolume * 1.5).toInt() + 1
        val sandCFT = mortarVolume * 0.9 * 35.315
        return MaterialResult(bricks, cementBags, sandCFT, wallVolume)
    }

    fun calculateSlab(lengthM: Double, widthM: Double, thicknessM: Double): SlabResult {
        val volume = lengthM * widthM * thicknessM
        val cementBags = (volume * 8).toInt() + 1
        val sandCFT = volume * 14.0
        val aggregateCFT = volume * 28.0
        val steelKg = (volume * 80).toInt()
        return SlabResult(cementBags, sandCFT, aggregateCFT, steelKg, volume)
    }

    fun calculatePlaster(lengthM: Double, heightM: Double, thicknessMM: Double): PlasterResult {
        val area = lengthM * heightM
        val volume = area * (thicknessMM / 1000.0) * 1.3
        val cementBags = (volume * 6.3).toInt() + 1
        val sandCFT = volume * 4 * 35.315
        return PlasterResult(cementBags, sandCFT, area)
    }
}
