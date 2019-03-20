package com.anandp.application

interface UiChange<T>{
    fun render(t: T)
}