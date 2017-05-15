import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { IonicApp, IonicErrorHandler, IonicModule,DeepLinkConfig } from 'ionic-angular';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';
import { APP_INITIALIZER } from '@angular/core';
import {HttpModule,Http} from "@angular/http";
//import {TranslateService, TranslatePipe, TranslateLoader, TranslateStaticLoader} from 'ng2-translate/ng2-translate';
import {TranslateModule,TranslateStaticLoader,TranslateLoader} from 'ng2-translate';
import { Ng2OrderModule } from 'ng2-order-pipe';

import { ConfigService }       from '../services/config.service';
import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { LoginPage } from '../pages/login/login';
import { InstitutePage } from '../pages/institute/institute';
import { ActivitiesPage } from '../pages/activities/activities';
import { CertificationsPage } from '../pages/certifications/certifications';
import { CurriculumPage } from '../pages/curriculum/curriculum';
import { EventsPage } from '../pages/events/events';
import { StagesPage } from '../pages/stages/stages';
import { AddStagePage } from '../pages/addStage/addStage';
import { AppBar } from '../pages/components/app-bar.component';
import {LoginService} from '../services/login.service';
import {WebAPIConnectorService} from '../services/webAPIConnector.service';
import {TrainingService} from '../services/training.service';
import {UserService} from '../services/user.service';
import {ExperienceFilterPipe} from '../pipes/experience-filter.pipe.ts';

export const deepLinkConfig: DeepLinkConfig = {
      links: [
    { component: LoginPage, name: 'Login', segment: 'login' },
    { component: HomePage, name: 'Home', segment: 'home' },
    { component: InstitutePage, name: 'Institute', segment: 'institute' },
    { component: CertificationsPage, name: 'Certifications', segment: 'certifications' },
    { component: CurriculumPage, name: 'Curriculum', segment: 'curriculum' },
    { component: EventsPage, name: 'Events', segment: 'events' },
    { component: StagesPage, name: 'Stage', segment: 'stage' },
    { component: AddStagePage, name: 'AddStage', segment: 'addStage' }
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
    CertificationsPage,
    ActivitiesPage,
    CurriculumPage,
    EventsPage,
    StagesPage,
    AddStagePage,
    AppBar,
    ExperienceFilterPipe
  ],
  imports: [
    BrowserModule,
    HttpModule,
    Ng2OrderModule,
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
    InstitutePage,
    CertificationsPage,
    ActivitiesPage,
    CurriculumPage,
    EventsPage,
    StagesPage,
    AddStagePage
  ],
  providers: [
    StatusBar,
    SplashScreen,
    {provide: ErrorHandler, useClass: IonicErrorHandler},
    LoginService,
    ConfigService,
    UserService,
    { provide: APP_INITIALIZER, useFactory: initConfig, deps: [ConfigService], multi: true },

    WebAPIConnectorService,
    TrainingService
  ]
})
export class AppModule {}
