import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddStagePage } from '../addStage/addStage'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'

@Component({
  selector: 'stage-panel',
  templateUrl: './stage.html'

})

export class StagePanel implements OnInit {
  @Input() stage: StudentExperience;
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
    private utilsService: UtilsService,
    private webAPIConnectorService: WebAPIConnectorService) {
  }
  opened: Boolean = false;
  ngOnInit(): void {
    if (this.index == 0) {
      this.toggle();
    }
    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      this.stage.certificate = JSON.parse(response);
      this.certificateInstitutional = false;
      this.hideSpinner();
    };
   }
  toggle() {
    this.opened = !this.opened;
  }
  getStage(): StudentExperience {
    return this.stage;
  }
  updateStage(): void {
    this.navCtrl.push(AddStagePage, { stage: JSON.stringify(this.stage) });
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
            this.userService.deleteCertificate(this.stage).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_certificate'), 3000, 'middle');
              this.hideSpinner();
              this.certificateInstitutional = false
              this.stage.certificate = null
            })
          }
        }
      ]
    });
    alert.present();


  }
  uploadCertificate(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createCertificate(this.stage.experience).then(experienceId => {
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
  deleteStage(): void {
    //ask confirmation

    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_delete_stage_title'),
      message: this.translate.instant('alert_delete_stage_message'),
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
            this.userService.deleteStage(this.getStage()).then(stage => {
              // remove stage from stage
              this.onDeleted.emit(stage.id);
              this.hideSpinner();
              this.utilsService.toast(this.translate.instant('toast_delete_stage'), 3000, 'middle');

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

