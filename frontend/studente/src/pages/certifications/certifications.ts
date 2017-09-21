import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddCertificationPage } from '../addCertification/addCertification';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

@Component({
  selector: 'page-certifications',
  templateUrl: 'certifications.html'
})
export class CertificationsPage {
  certifications: StudentExperience[] = null;
  order: string = "latest";
  icon = "ios-arrow-down";
  shownCertification = null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
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
    this.navCtrl.push(AddCertificationPage, { certification: JSON.stringify(certification) });
  }


  onDeleted(stageId: string) {
    for (var i = 0; i < this.certifications.length; i++) {
      if (this.certifications[i].experience.id == stageId) {
        this.certifications.splice(i, 1);
      }
    }
  }
  //loaded when it is showed
  ionViewDidEnter() {
    let loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    loader.present().then(() => {
      this.userService.getUserCertifications().then(certifications => {
        this.utilsService.sortExperience(this.order, certifications).then(sortedList => {
          this.certifications = sortedList;
          loader.dismiss();
        })

      })
    })
  }

  onSelectChange(selectedValue: any) {
    this.utilsService.sortExperience(selectedValue, this.certifications).then(sortedList => {
      this.certifications = sortedList;

    })
  }

}
