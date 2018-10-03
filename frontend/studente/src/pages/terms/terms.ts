import { Component } from '@angular/core';
import { TranslateService} from 'ng2-translate';
import { Http } from '@angular/http';
import { NavController, ViewController, AlertController, Platform } from 'ionic-angular';
import { HomePage } from '../home/home';
import { LoginService } from '../../services/login.service'


@Component({
    selector: 'terms',
    templateUrl: 'terms.html',
    styleUrls: ['/pages/terms/terms-component.scss']
})

export class TermsPage {
   
    lbl_terms_of_service: String;
    termsFile: any;
    accepting: Boolean;
      
    constructor(private login: LoginService, private http: Http, public translate: TranslateService, public nav: NavController,
        public viewCtrl: ViewController, public alertCtrl: AlertController, public platform: Platform) {

        // load html file.
        var url = 'assets/terms/terms.html';
        this.load(url).then(resp => this.termsFile = resp);

        this.accepting = true;
       
        this.lbl_terms_of_service = translate.instant('lbl_terms_of_service');

    }

    load(url: string): Promise<String> {
        var promise = this.http.get(url).map(res => res.text()).toPromise();
        return promise;
    }

    goToProposalsList = function () {
        this.nav.setRoot(HomePage);

    }


    acceptPrivacy = function () {
        this.login.consent().then(result => {
            if (result) {
                this.accept = result.authorized;
                this.goToProposalsList();        
            }
          },
            err => {
        });        
     
    };

    
    refusePrivacy = function () {

        let prompt = this.alertCtrl.create({
            title: '',
            subTitle: this.translate.instant('terms_refused_alert_text')
        });
        prompt.present();
        setTimeout(function () {
            this.navigator.app.exitApp(); // sometimes doesn't work with Ionic View
            // this.platform.exitApp();
            console.log('App closed');
            prompt.dismiss();
        }, 1800) //close the popup after 1.8 seconds for some reason

    };

}
