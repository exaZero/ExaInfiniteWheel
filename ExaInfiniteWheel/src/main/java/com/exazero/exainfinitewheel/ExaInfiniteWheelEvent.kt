package com.exazero.exainfinitewheel

/**
 *   Created by jazcorra96 on 9/9/2020
 */
interface ExaInfiniteWheelEvent {
    fun onDragStart(position: Int)
    fun onDragMove(position: Int)
    fun onDragEnd(position: Int)
}