import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddJobPage } from '../addJob/addJob'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'

@Component({
  selector: 'job-panel',
  templateUrl: './job.html'

})

export class JobPanel implements OnInit {
  @Input() job: StudentExperience;
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
      this.job.certificate = JSON.parse(response);
      this.certificateInstitutional = false;
      this.hideSpinner();
    };
   }
  toggle() {
    this.opened = !this.opened;
  }
  getJob(): StudentExperience {
    return this.job;
  }
  updateJob(): void {
    this.navCtrl.push(AddJobPage, { job: JSON.stringify(this.job) });
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
            this.userService.deleteCertificate(this.job).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_certificate'), 3000, 'middle');
              this.hideSpinner();
              this.certificateInstitutional = false
              this.job.certificate = null
            })
          }
        }
      ]
    });
    alert.present();


  }
  uploadCertificate(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createCertificate(this.job.experience).then(experienceId => {
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
  deleteJob(): void {
    //ask confirmation

    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_delete_job_title'),
      message: this.translate.instant('alert_delete_job_message'),
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
            this.userService.deleteJob(this.getJob()).then(job => {
              // remove job from job
              this.onDeleted.emit(job.id);
              this.hideSpinner();
              this.utilsService.toast(this.translate.instant('toast_delete_job'), 3000, 'middle');

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

