package droid.ankit.livongodemo

import android.app.Application
import droid.ankit.googlefit.GoogleFitServiceImpl
import droid.ankit.livongodemo.util.PermissionManager
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

class LivongoApp:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(applicationContext,listOf(appModule))
    }
}

val appModule: Module = module {
    single { DataRepository() }
    single { GoogleFitServiceImpl(get()) }
    single { PermissionManager(get()) }
}