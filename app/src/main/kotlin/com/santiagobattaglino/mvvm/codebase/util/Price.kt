package com.santiagobattaglino.mvvm.codebase.util

import kotlin.math.round

fun precioMayor(manufacturingCost: Int): Double {
    return round(manufacturingCost * 1.8)
}

fun precioCapilla(manufacturingCost: Int): Double {
    return round(precioMayor(manufacturingCost) * 1.3)
}

fun precioMenor(manufacturingCost: Int): Double {
    return round(precioMayor(manufacturingCost) * 2)
}

fun precioMl(manufacturingCost: Int): Double {
    return round(precioMenor(manufacturingCost) + precioMenor(manufacturingCost) * 0.12)
}