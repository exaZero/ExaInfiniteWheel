package com.exazero.exainfinitewheel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exaIWheel.setOnDragListener(object : ExaInfiniteWheelEvent {
            override fun onDragStart(position: Int) {
                exaStatus.text = "OnDragStart"
                exaPosition.text = getString(R.string.wheel_position, position)
            }

            override fun onDragMove(position: Int) {
                exaStatus.text = "OnDragMove"
                exaPosition.text = getString(R.string.wheel_position, position)
            }

            override fun onDragEnd(position: Int) {
                exaStatus.text = "OnDragFinish"
                exaPosition.text = getString(R.string.wheel_position, position)
            }
        })
    }



}