import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';
import {TranslateService} from 'ng2-translate';
import {LoginService, LOGIN_STATUS} from '../services/login.service';

import { LoginPage } from '../pages/login/login';
import { HomePage } from '../pages/home/home';
import { TermsPage } from '../pages/terms/terms';

@Component({
  templateUrl: 'app.html'
})
export class MyApp {
  rootPage: any;

  constructor(platform: Platform, statusBar: StatusBar, splashScreen: SplashScreen, translate: TranslateService, private login: LoginService) {
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      statusBar.styleDefault();
      splashScreen.hide();
      translate.setDefaultLang('it');

      // this.rootPage = HomePage;
      
      // login.checkLoginStatus().then(
      //   status => {
      //     switch (status) {
      //       case LOGIN_STATUS.EXISTING: {
      //         // conditional setting of rootpage.
      //         login.readConsent().then(consent => {
      //           if (!consent.authorized) {
      //             this.rootPage = TermsPage;
      //           } else {
      //             this.rootPage = HomePage;
      //           } 
      //         });
      //         break;
      //       } 
      //       case LOGIN_STATUS.NEW: {
      //         this.rootPage = TermsPage; // Fix for reported issue on 01/12/17
      //         break;
      //       }
      //       default : this.rootPage = LoginPage;
      //     }          
      //   },
      //   error => {
      //     // TODO: handle error
      //   }
      // );
    });
  }
}

