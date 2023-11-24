package com.uz.base.imagekit.injection.component

import com.uz.base.imagekit.ImageKit
import com.uz.base.imagekit.ImagekitUrlConstructor
import com.uz.base.imagekit.injection.module.ContextModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [(ContextModule::class)])
@Singleton
interface UtilComponent {
    fun inject(app: ImageKit)
    fun inject(imagekitUrlConstructor: ImagekitUrlConstructor)
}