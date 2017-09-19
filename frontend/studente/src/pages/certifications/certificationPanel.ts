import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
import { CertificationsTypes } from '../../assets/conf/certificationsTypes'
import { Certification } from '../../classes/Certification.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddCertificationPage } from '../addCertification/addCertification'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'
import { Observable } from 'rxjs/Rx';

@Component({
  selector: 'certification-panel',
  templateUrl: './certification.html'

})

export class CertificationPanel implements OnInit {
  @Input() certification: StudentExperience;
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
    private webAPIConnectorService: WebAPIConnectorService,
    private utilsService: UtilsService) {
  }

  ngOnInit(): void {
    // if (this.index == 0) {
    //   this.toggle();
    // }
    Observable.forkJoin(
      this.certification.documents.map(
        (i, index) => this.getFileUrl(i).then(url => {
          //da finire con il giusto ordine TO DO
          this.certification.documents[index]['documentUri'] = url;
          console.log("get file")
        })
      )
    ).subscribe(() => console.log("done"))
    //load urls of documents

    this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      this.certification.document = JSON.parse(response);
      this.documentInstitutional = false;
      this.hideSpinner();
    };
  }

  toggle() {
    this.index = this.index == 0 ? -1 : 0;
  }

  getCertification(): StudentExperience {
    return this.certification;
  }


  isLanguageDocument(): boolean {
    var certification = this.certification.experience.attributes as Certification;
    return (certification.type == CertificationsTypes.CERT_TYPE_LANG);
  }

  updateCertification(): void {
    this.navCtrl.push(AddCertificationPage, { certification: JSON.stringify(this.certification) });
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
            this.userService.deleteDocument(this.certification).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_document'), 3000, 'middle');
              this.hideSpinner();
              this.documentInstitutional = false
              this.certification.document = null
            })
          }
        }
      ]
    });
    alert.present();


  }

  uploadDocument(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createDocument(this.certification).then(experienceId => {
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
              this.utilsService.toast(this.translate.instant('toast_delete_certification'), 3000, 'middle');

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

  ionViewDidEnter() {
    // if (this.index == 0) {
    //   this.toggle();
    // }
  }

}


