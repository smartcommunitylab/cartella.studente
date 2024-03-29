import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddMobilityPage } from '../addMobility/addMobility'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'

import { ConfigService } from '../../services/config.service';

@Component({
  selector: 'mobility-panel',
  templateUrl: './mobility.html'

})

export class MobilityPanel implements OnInit {
  @Input() mobility: StudentExperience;
  @Input() index: number;
  @Output() onDeleted = new EventEmitter<string>();
  @Output() onUpdated = new EventEmitter<string>();
  documentInstitutional = false;
  loader = null;
  uploader: FileUploader = new FileUploader({});

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    public loading: LoadingController,
    private alertCtrl: AlertController,
    private translate: TranslateService,
    private webAPIConnectorService: WebAPIConnectorService,
    private utilsService: UtilsService,
    private config: ConfigService) {
  }

  ngOnInit(): void {

    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      this.mobility.document = JSON.parse(response);
      this.documentInstitutional = false;
      this.hideSpinner();
    };

  }

  downloadDocument(document) {
    window.open(this.config.getConfig('apiUrl') + document.url, '_blank');
  }

  deleteDocument(document) {
    this.userService.deleteDocumentInPromise(document.experienceId, document.storageId).then(document => {
      this.onUpdated.emit();
    });    
}

  toggle() {
    this.index = this.index == 0 ? -1 : 0;
  }

  getMobility(): StudentExperience {
    return this.mobility;
  }
  updateMobility(): void {
    this.navCtrl.push(AddMobilityPage, { mobility: JSON.stringify(this.mobility) });
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
            this.userService.deleteDocument(this.mobility).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_document'), 3000, 'middle');
              this.hideSpinner();
              this.documentInstitutional = false
              this.mobility.document = null
            })
          }
        }
      ]
    });
    alert.present();


  }
  uploadDocument(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createDocument(this.mobility.experience).then(document => {
        this.webAPIConnectorService.uploadDocument(this.uploader, this.userService.getUserId(), document.experienceId, item, document.storageId);
        resolve();
      })
    })

  }
  saveCertification(): void {
    this.showSpinner();
    this.uploadDocument(this.uploader.queue[0]).then((document) => {
    })
  }
  deleteMobility(): void {
    //ask confirmation

    let alert = this.alertCtrl.create({
      title: this.translate.instant('alert_delete_mobility_title'),
      message: this.translate.instant('alert_delete_mobility_message'),
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
            this.userService.deleteMobility(this.getMobility()).then(mobility => {
              // remove mobility from mobility
              this.onDeleted.emit(mobility.id);
              this.hideSpinner();
              this.utilsService.toast(this.translate.instant('toast_delete_mobility'), 3000, 'middle');

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
