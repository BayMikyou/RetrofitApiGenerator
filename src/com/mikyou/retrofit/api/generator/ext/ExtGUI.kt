package com.mikyou.retrofit.api.generator.ext

import java.awt.Dialog
import java.awt.Toolkit

fun Dialog.showDialog(width: Int = 650, height: Int = 700, isInCenter: Boolean = true, isResizable: Boolean = false) {
    pack()
    this.isResizable = isResizable
    setSize(width, height)
    if (isInCenter) {
        setLocation(Toolkit.getDefaultToolkit().screenSize.width / 2 - width / 2, Toolkit.getDefaultToolkit().screenSize.height / 2 - height / 2)
    }
    isVisible = true
}