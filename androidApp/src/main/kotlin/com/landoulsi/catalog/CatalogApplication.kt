package com.landoulsi.catalog

import android.app.Application
import android.content.pm.ApplicationInfo
import com.landoulsi.catalog.shared.di.androidPlatformModule
import com.landoulsi.catalog.shared.di.commonModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class CatalogApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@CatalogApplication)
            modules(commonModules + androidPlatformModule)
        }
    }
}
