import {Component, Output, EventEmitter,Input} from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import {StudentExperience} from '../../classes/StudentExperience.class'
import {AddCertificationPage} from '../addCertification/addCertification'
import {UserService} from '../../services/user.service'
@Component({
  selector: 'certification-panel',
  templateUrl: './certification.html'

})

export class CertificationPanel {
    @Input() certification: StudentExperience;
    @Output() onDeleted = new EventEmitter<string>();
      constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }
    opened: Boolean = false;
    toggle () {
      this.opened = !this.opened;
    }
    getCertification(): StudentExperience {
      return this.certification;
    }

     updateCertification(): void {
    this.navCtrl.push(AddCertificationPage, { certification: JSON.stringify(this.certification) });
  }

  deleteCertification(): void {
    //ask confirmation

    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_delete_certification_title'),
      message: this.translate.instant('alert_delete_certification_message'),
      buttons: [
        {
          text: this.translate.instant('alert_cancel'),
          cssClass: 'pop-up-button',
          role: 'cancel'

        },
        {
          text: this.translate.instant('alert_confirm'),
          cssClass: 'pop-up-button',
          handler: () => {
            let loader = this.loading.create({
              content: this.translate.instant('loading'),
            });
            this.userService.deleteCertification(this.getCertification()).then(certification => {
              // remove certification from certification
              this.onDeleted.emit(certification.id);
              loader.dismiss();
             this.utilsService.toast( this.translate.instant('toast_delete_certification'),3000,'middle');

            })
          }
        }
      ]
    });
    alert.present();

  }
  }

