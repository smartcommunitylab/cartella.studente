import { Component } from '@angular/core';
import { TranslateService } from 'ng2-translate';
import { Storage } from '@ionic/storage';
import { Headers, Http } from '@angular/http';
import { NavController, ViewController, AlertController, Platform } from 'ionic-angular';
import { HomePage } from '../home/home';

@Component({
    selector: 'credits',
    templateUrl: 'credits.html'
})

export class CreditsPage {

    menu_about: string;
    // patImage: string;
    // fbkImage: string;

    constructor(private http: Http, public translate: TranslateService, public storage: Storage, public nav: NavController,
        public viewCtrl: ViewController, public alertCtrl: AlertController, public platform: Platform) {
        // this.patImage = "/assets/images/pat.png";
        // this.fbkImage = "/assets/images/fbk.png";
        this.menu_about = translate.instant('menu_about');
    }

}
