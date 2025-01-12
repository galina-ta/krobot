package com.gala.maze.di

import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.common.arena.RobotExecutor
import com.gala.maze.common.arena.RobotStatesApplier
import com.gala.maze.platform.arena.AndroidRobotExecutor
import com.gala.maze.platform.arena.AndroidRobotStatesApplier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideRobotExecutor(): RobotExecutor = AndroidRobotExecutor()

    @Provides
    fun provideStatesApplier(): RobotStatesApplier = AndroidRobotStatesApplier()
}

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideCreateRobotControllerHolder(): CreateRobotControllerHolder =
        CreateRobotControllerHolder()
}
