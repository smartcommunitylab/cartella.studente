import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule, LOCALE_ID } from '@angular/core';
import { IonicApp, IonicErrorHandler, IonicModule, DeepLinkConfig } from 'ionic-angular';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';
import { APP_INITIALIZER } from '@angular/core';
import { HttpModule, Http } from "@angular/http";
import { TranslateModule, TranslateStaticLoader, TranslateLoader, TranslateService } from 'ng2-translate';
import { Ng2OrderModule } from 'ng2-order-pipe';
import { FileSelectDirective, FileDropDirective } from 'ng2-file-upload';
import { PipesModule } from '../pipes/pipe.modules';
import { DatePicker } from 'ionic2-date-picker';

import { ConfigService } from '../services/config.service';
import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { MapModal } from '../pages/map/mapmodal';
import { LoginPage } from '../pages/login/login';
import { InstitutePage } from '../pages/institute/institute';
import { ActivitiesPage } from '../pages/activities/activities';
import { CertificationsPage } from '../pages/certifications/certifications';
import { CurriculumPage } from '../pages/curriculum/curriculum';
import { EventsPage } from '../pages/events/events';
import { StagesPage } from '../pages/stages/stages';
import { JobsPage } from '../pages/jobs/jobs';
import { MobilitiesPage } from '../pages/mobilities/mobilities';
import { ExamPage } from '../pages/exam/exams';
import { ProfilePage } from '../pages/profile/profile';
import { NotificationsPage } from '../pages/notifications/notifications';
import { AddStagePage } from '../pages/addStage/addStage';
import { AddJobPage } from '../pages/addJob/addJob';
import { AddMobilityPage } from '../pages/addMobility/addMobility';
import { AddActivityPage } from '../pages/addActivity/addActivity';
import { AddCertificationPage } from '../pages/addCertification/addCertification';
import { AddEventPage } from '../pages/addEvent/addEvent';
import { ConsentPage } from '../pages/consent/consent';
import { AppBar } from '../pages/components/app-bar/app-bar.component';
import { ButtonHome } from '../pages/components/button-home/button-home.component';
import { ButtonNotifications } from '../pages/components/button-notifications/button-notifications.component';
import { ButtonProfile } from '../pages/components/button-profile/button-profile.component';
import { LoginService } from '../services/login.service';
import { WebAPIConnectorService, requestOptionsProvider } from '../services/webAPIConnector.service';
import { TrainingService } from '../services/training.service';
import { UserService } from '../services/user.service';
import {ExperienceService} from '../services/experience.service'
import { UtilsService} from '../services/utils.services'
import { ExperienceFilterPipe } from '../pipes/experience-filter.pipe.ts';
import { OrderBy } from '../pipes/orderBy.pipe.ts';
import { NgInit } from '../directives/ngInit';
import { StagePanel } from '../pages/stages/stagePanel'
import { JobPanel } from '../pages/jobs/jobPanel'
import { MobilityPanel } from '../pages/mobilities/mobilityPanel'
import { ExamPanel } from '../pages/exam/examPanel'
import { ActivityPanel } from '../pages/activities/activityPanel'
import { CertificationPanel } from '../pages/certifications/certificationPanel'
import { RegistrationPanel } from '../pages/institute/registrationPanel'
import { Subjects } from '../pages/institute/subjects'
import { GeoService } from '../services/geo.service'

export const deepLinkConfig: DeepLinkConfig = {
  links: [
    { component: LoginPage, name: 'Login', segment: 'login' },
    { component: HomePage, name: 'Home', segment: 'home' },
    { component: InstitutePage, name: 'Institute', segment: 'institute' },
    { component: CertificationsPage, name: 'Certifications', segment: 'certifications' },
    { component: CurriculumPage, name: 'Curriculum', segment: 'curriculum' },
    { component: EventsPage, name: 'Events', segment: 'events' },
    { component: StagesPage, name: 'Stage', segment: 'stage' },
    { component: JobsPage, name: 'Job', segment: 'job' },
    { component: MobilitiesPage, name: 'Mobility', segment: 'mobility' },
    { component: ExamPage, name: 'Exam', segment: 'exam' },
    { component: ProfilePage, name: 'Profile', segment: 'profile' },
    { component: NotificationsPage, name: 'Notifications', segment: 'notifications' },
    { component: AddStagePage, name: 'AddStage', segment: 'addStage' },
    { component: AddJobPage, name: 'AddJob', segment: 'addJob' },
    { component: AddMobilityPage, name: 'AddMobility', segment: 'addMobility' },
    { component: AddEventPage, name: 'AddEvent', segment: 'addEvent' },
    { component: AddActivityPage, name: 'AddActivity', segment: 'addActivity' },
    { component: ConsentPage, name: 'Consent', segment: 'consent' },
    { component: AddCertificationPage, name: 'AddCertification', segment: 'addCertification' }
  ]
}
function initConfig(config: ConfigService) {
  return () => config.load()
}
@NgModule({
  declarations: [
    MyApp,
    LoginPage,
    HomePage,
    MapModal,
    InstitutePage,
    CertificationsPage,
    ActivitiesPage,
    CurriculumPage,
    EventsPage,
    ProfilePage,
    NotificationsPage,
    StagesPage,
    JobsPage,
    MobilitiesPage,
    ExamPage,
    AddStagePage,
    AddJobPage,
    AddMobilityPage,
    AddEventPage,
    AddActivityPage,
    AddCertificationPage,
    ConsentPage,
    AppBar,
    ButtonHome,
    ButtonProfile,
    ButtonNotifications,
    ExperienceFilterPipe,
    OrderBy,
    NgInit,
    FileSelectDirective,
    StagePanel,
    JobPanel,
    MobilityPanel,
    ExamPanel,
    CertificationPanel,
    ActivityPanel,
    Subjects,
    RegistrationPanel,
    DatePicker
  ],
  imports: [
    BrowserModule,
    PipesModule,
    HttpModule,
    Ng2OrderModule,
    IonicModule.forRoot(MyApp, {}, deepLinkConfig),
    TranslateModule.forRoot({
      provide: TranslateLoader,
      useFactory: (http: Http) => new TranslateStaticLoader(http, 'assets/i18n', '.json'),
      deps: [Http]
    })
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    LoginPage,
    HomePage,
        MapModal,

    InstitutePage,
    CertificationsPage,
    ActivitiesPage,
    CurriculumPage,
    EventsPage,
    StagesPage,
    JobsPage,
    MobilitiesPage,
    ExamPage,
    AddStagePage,
    AddJobPage,
    AddMobilityPage,
    AddActivityPage,
    AddEventPage,
    AddCertificationPage,
    NotificationsPage,
    ProfilePage,
    ConsentPage,
    DatePicker
  ],
  providers: [
    StatusBar,
    SplashScreen,
    { provide: ErrorHandler, useClass: IonicErrorHandler },
    LoginService,
    ConfigService,
    GeoService,
    UserService,
    ExperienceService,
    UtilsService,
    { provide: APP_INITIALIZER, useFactory: initConfig, deps: [ConfigService], multi: true },
    TranslateService,
    WebAPIConnectorService,
    TrainingService,
    requestOptionsProvider
  ]
})
export class AppModule { }
