import { Component, OnInit } from '@angular/core';
import { NavController, LoadingController } from 'ionic-angular';

import { LoginService } from '../../services/login.service'
import { TranslateService } from 'ng2-translate';

import { HomePage } from '../home/home';
import { LoginPage } from '../login/login';

@Component({
  selector: 'page-consent',
  templateUrl: 'consent.html'
})
export class ConsentPage implements OnInit {

  bodyText: string;

  ngOnInit(): void {

  }
  constructor(private navCtrl: NavController, private loading: LoadingController, private login: LoginService, translate: TranslateService) {
    this.bodyText = translate.instant('consent_text');
  }

  accept(): void {
    this.login.consent().then(result => {
      if (result){
      this.navCtrl.setRoot(HomePage);
      } else {
        this.reject();
      }
    },
      err => {
        console.error(err)
      });
  }

  reject(): void {
    this.login.logout().then(result => {
      this.navCtrl.setRoot(LoginPage);
    });
  }

}
