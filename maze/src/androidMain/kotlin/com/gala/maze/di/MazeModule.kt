package com.gala.maze.di

import com.gala.maze.common.arena.CreateRobotControllerHolder
import com.gala.maze.common.arena.RobotExecutor
import com.gala.maze.common.arena.RobotStatesApplier
import com.gala.maze.common.program.ClipboardReceiver
import com.gala.maze.platform.arena.AndroidRobotExecutor
import com.gala.maze.platform.arena.AndroidRobotStatesApplier
import com.gala.maze.platform.program.AndroidClipboardReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object MazeModule {

    @Provides
    fun provideRobotExecutor(): RobotExecutor = AndroidRobotExecutor()

    @Provides
    fun provideStatesApplier(): RobotStatesApplier = AndroidRobotStatesApplier()

    @Provides
    fun provideClipboardReceiver(): ClipboardReceiver =
        AndroidClipboardReceiver(AppHolder.instance)
}

@Module
@InstallIn(SingletonComponent::class)
object MazeSingletonModule {

    @Provides
    @Singleton
    fun provideCreateRobotControllerHolder(): CreateRobotControllerHolder =
        CreateRobotControllerHolder()
}
