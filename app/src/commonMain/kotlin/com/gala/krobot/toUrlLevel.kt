package com.gala.krobot

fun String.toUrlLevel(): String =
    replace('\n', '|')

fun String.toLevelDraw(): String =
    replace('|', '\n')
