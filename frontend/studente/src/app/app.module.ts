import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { IonicApp, IonicErrorHandler, IonicModule,DeepLinkConfig } from 'ionic-angular';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';
import { APP_INITIALIZER } from '@angular/core';
import {HttpModule,Http} from "@angular/http";
//import {TranslateService, TranslatePipe, TranslateLoader, TranslateStaticLoader} from 'ng2-translate/ng2-translate';
import {TranslateModule,TranslateStaticLoader,TranslateLoader} from 'ng2-translate';

import { ConfigService }       from '../services/config.service';
import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { LoginPage } from '../pages/login/login';
import { InstitutePage } from '../pages/institute/institute';
import { AppBar } from '../pages/components/app-bar.component';
import {LoginService} from '../services/login.service';
import {WebAPIConnectorService} from '../services/webAPIConnector.service';
import {TrainingService} from '../services/training.service';

export const deepLinkConfig: DeepLinkConfig = {
      links: [
    { component: LoginPage, name: 'Login', segment: 'login' },
    { component: HomePage, name: 'Home', segment: 'home' },
    { component: InstitutePage, name: 'Institute', segment: 'institute' }
  ]
    }
function initConfig(config: ConfigService){
    return () => config.load()
}
@NgModule({
  declarations: [
    MyApp,
    LoginPage,
    HomePage,
    InstitutePage,
    AppBar
  ],
  imports: [
    BrowserModule,
    HttpModule,
    IonicModule.forRoot(MyApp,{},deepLinkConfig),
    TranslateModule.forRoot({
            provide: TranslateLoader,
            useFactory: (http: Http) => new TranslateStaticLoader(http, '/assets/i18n', '.json'),
            deps: [Http]
        })
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    LoginPage,
    HomePage,
    InstitutePage
  ],
  providers: [
    StatusBar,
    SplashScreen,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    LoginService,
    ConfigService,
    { provide: APP_INITIALIZER, useFactory: initConfig, deps: [ConfigService], multi: true },

    WebAPIConnectorService,
    TrainingService
  ]
})
export class AppModule {}
