package com.gala.krobot.engine.level.entity

data class Key(
    override val collected: () -> Unit,
) : Collectable
