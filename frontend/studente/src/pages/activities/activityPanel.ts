import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddActivityPage } from '../addActivity/addActivity'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'

@Component({
  selector: 'activity-panel',
  templateUrl: './activity.html'

})

export class ActivityPanel implements OnInit {
  @Input() activity: StudentExperience;
  @Input() index: number;
  @Output() onDeleted = new EventEmitter<string>();
  certificateInstitutional = false;
  loader = null;
  uploader: FileUploader = new FileUploader({});

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    public loading: LoadingController,
    private alertCtrl: AlertController,
    private translate: TranslateService,
    private webAPIConnectorService: WebAPIConnectorService,
    private utilsService: UtilsService) {
  }
  opened: Boolean = false;
  ngOnInit(): void {
    if (this.index == 0) {
      this.toggle();
    }
    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      this.activity.certificate = JSON.parse(response);
      this.certificateInstitutional = false;
      this.hideSpinner();
    };
    this.loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
  }
  toggle() {
    this.opened = !this.opened;
  }
  getActivity(): StudentExperience {
    return this.activity;
  }
  updateActivity(): void {
    this.navCtrl.push(AddActivityPage, { activity: JSON.stringify(this.activity) });
  }
addCertificate(): void {
    this.certificateInstitutional = true;
  }
  removeCertification(): void {
    this.uploader.clearQueue();
    (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
    this.certificateInstitutional = false;
  }
  removeActualCertificate(): void {
    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_remove_certificate_mobility_title'),
      message: this.translate.instant('alert_remove_certificate_mobility_message'),
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
            this.showSpinner();
            this.userService.deleteCertificate(this.activity).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_certificate'), 3000, 'middle');
              this.hideSpinner();
              this.certificateInstitutional = false
              this.activity.certificate = null
            })
          }
        }
      ]
    });
    alert.present();


  }
  uploadCertificate(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createCertificate(this.activity.experience).then(experienceId => {
        this.webAPIConnectorService.uploadCertificate(this.uploader, this.userService.getUserId(), experienceId, item);
        resolve();
      })
    })

  }
  saveCertification(): void {
    this.showSpinner();
    this.uploadCertificate(this.uploader.queue[0]).then((certificate) => {
    })
  }
  deleteActivity(): void {
    //ask confirmation

    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_delete_activity_title'),
      message: this.translate.instant('alert_delete_activity_message'),
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
            this.userService.deleteActivity(this.getActivity()).then(activity => {
              // remove activity from activity
              this.onDeleted.emit(activity.id);
              loader.dismiss();
              this.utilsService.toast(this.translate.instant('toast_delete_activity'), 3000, 'middle');

            })
          }
        }
      ]
    });
    alert.present();

  }
    private showSpinner() {
    this.loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    this.loader.present().catch(() => { });
  }

  private hideSpinner() {
    if (this.loading !== undefined) {
      this.loader.dismiss().catch(() => { });
    }
  }
}

