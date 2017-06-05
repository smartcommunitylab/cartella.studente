import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddStagePage } from '../addStage/addStage'
import { UserService } from '../../services/user.service'
@Component({
  selector: 'stage-panel',
  templateUrl: './stage.html'

})

export class StagePanel implements OnInit {
  @Input() stage: StudentExperience;
  @Input() index: number;
  @Output() onDeleted = new EventEmitter<string>();
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }
  opened: Boolean = false;
  ngOnInit(): void {
    if (this.index == 0) {
      this.toggle();
    }
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
            let loader = this.loading.create({
              content: this.translate.instant('loading'),
            });
            this.userService.deleteStage(this.getStage()).then(stage => {
              // remove stage from stage
              this.onDeleted.emit(stage.id);
              loader.dismiss();
              this.utilsService.toast(this.translate.instant('toast_delete_stage'), 3000, 'middle');

            })
          }
        }
      ]
    });
    alert.present();

  }
}

