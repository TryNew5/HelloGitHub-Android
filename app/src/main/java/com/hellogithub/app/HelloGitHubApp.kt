package com.hellogithub.app

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.hellogithub.app.di.appModule
import com.hellogithub.app.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class HelloGitHubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HelloGitHubApp)
            modules(networkModule, appModule)
        }
        // Set Coil's default ImageLoader to use our OkHttpClient with Referer header
        val imageLoader = GlobalContext.get().get<ImageLoader>()
        Coil.setImageLoader(imageLoader)
    }
}
