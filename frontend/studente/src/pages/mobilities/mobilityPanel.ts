import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddMobilityPage } from '../addMobility/addMobility'
import { UserService } from '../../services/user.service'
import { FileUploader } from 'ng2-file-upload';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'

@Component({
  selector: 'mobility-panel',
  templateUrl: './mobility.html'

})

export class MobilityPanel implements OnInit {
  @Input() mobility: StudentExperience;
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
      this.mobility.certificate = JSON.parse(response);
      this.certificateInstitutional = false;
      this.hideSpinner();
    };
    // this.loader = this.loading.create({
    //   content: this.translate.instant('loading'),
    // });
  }
  toggle() {
    this.opened = !this.opened;
  }
  getMobility(): StudentExperience {
    return this.mobility;
  }
  updateMobility(): void {
    this.navCtrl.push(AddMobilityPage, { mobility: JSON.stringify(this.mobility) });
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
            this.userService.deleteCertificate(this.mobility).then(() => {
              this.utilsService.toast(this.translate.instant('toast_delete_certificate'), 3000, 'middle');
              this.hideSpinner();
              this.certificateInstitutional = false
              this.mobility.certificate = null
            })
          }
        }
      ]
    });
    alert.present();


  }
  uploadCertificate(item): Promise<void> {
    return new Promise<void>((resolve, reject) => {

      this.userService.createCertificate(this.mobility.experience).then(experienceId => {
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
}

//  this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
//       this.mobility.certificate = JSON.parse(response);
//       this.certificateInstitutional = false;
//       this.hideSpinner();
//     };