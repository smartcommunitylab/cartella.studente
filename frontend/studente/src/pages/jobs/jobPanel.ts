import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddJobPage } from '../addJob/addJob'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'

import { ConfigService } from '../../services/config.service';

@Component({
  selector: 'job-panel',
  templateUrl: './job.html'

})

export class JobPanel implements OnInit {
  @Input() job: StudentExperience;
  @Input() index: number;
  @Output() onDeleted = new EventEmitter<string>();
  documentInstitutional = false;
  loader = null;
  uploader: FileUploader = new FileUploader({});
  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    public loading: LoadingController,
    private alertCtrl: AlertController,
    private translate: TranslateService,
    private utilsService: UtilsService,
    private webAPIConnectorService: WebAPIConnectorService,
    private config: ConfigService) {
  }

  ngOnInit(): void {

    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      this.job.document = JSON.parse(response);
      this.documentInstitutional = false;
      this.hideSpinner();
    };

  }

  downloadDocument(document) {
    window.open(this.config.getConfig('apiUrl') + document.url, '_blank');
  }

  toggle() {
    this.index = this.index == 0 ? -1 : 0;
  }

  getJob(): StudentExperience {
    return this.job;
  }
  
  updateJob(): void {
    this.navCtrl.push(AddJobPage, { job: JSON.stringify(this.job) });
  }

  addDocument(): void {
    this.documentInstitutional = true;
  }

  removeCertification(): void {
    this.uploader.clearQueue();
    (<HTMLInputElement>document.getElementById("uploadInputFile")).value = "";
    this.documentInstitutional = false;
  }

  removeActualDocument(): void {
    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_remove_document_mobility_title'),
      message: this.translate.instant('alert_remove_document_mobility_message'),
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
            this.userService.deleteDocument(this.job).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_document'), 3000, 'middle');
              this.hideSpinner();
              this.documentInstitutional = false
              this.job.document = null
            })
          }
        }
      ]
    });
    alert.present();

  }

  uploadDocument(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createDocument(this.job.experience).then(experienceId => {
        this.webAPIConnectorService.uploadDocument(this.uploader, this.userService.getUserId(), experienceId, item);
        resolve();
      })
    })

  }

  saveCertification(): void {
    this.showSpinner();
    this.uploadDocument(this.uploader.queue[0]).then((document) => {
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

  private getFileUrl(file): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      this.webAPIConnectorService.getUrlFile(this.userService.getUserId(), file.experienceId, file.storageId).then(url => {
        //add url to file
        // this.certification.documents[0]['documentUri']=url;
        resolve(url);
      }
      )
    }
    )
  }
}

