import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddCertificationPage } from '../addCertification/addCertification';
import {TranslateService} from 'ng2-translate';
@Component({
  selector: 'page-certifications',
  templateUrl: 'certifications.html'
})
export class CertificationsPage  {
  certifications:StudentExperience[]=[];
  order=true;
icon="ios-arrow-down";
  shownCertification=null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController,private alertCtrl: AlertController, private translate: TranslateService){
  }

toggleDetails(certification) {
    if (this.isDetailsShown(certification)) {
        this.shownCertification = null;
    } else {
        this.shownCertification = certification;
    }
};
isDetailsShown(certification) {
    return this.shownCertification === certification;
};

  addNewCertification(): void {
    this.navCtrl.push(AddCertificationPage);
  }

updateCertification(certification): void {
    this.navCtrl.push(AddCertificationPage, {certification:JSON.stringify(certification)});
  }

  deleteCertification(certification): void {
   //ask confirmation

      let alert = this.alertCtrl.create({
    title: this.translate.instant('alert_delete_certification_title'),
    message:  this.translate.instant('alert_delete_certification_message'),
    buttons: [
      {
        text: this.translate.instant('alert_cancel'),
        role: 'cancel'

      },
      {
        text: this.translate.instant('alert_confirm'),
        handler: () => {
              let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
              this.userService.deleteCertification(certification).then(certification =>{
       //remove stage from stage
      for (var i=0; i<this.certifications.length;i++)
        {
          if (this.certifications[i].experience.id==certification.id)
            {
                 this.certifications.splice(i, 1);
            }
          }
        loader.dismiss();
        })
        }
      }
    ]
  });
  alert.present();

  }
//loaded when it is showed
ionViewWillEnter () {
    let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
  loader.present().then(() => {
        this.userService.getUserCertifications().then(certifications =>{
        this.certifications=certifications
          loader.dismiss();
  })
  })
}
}
