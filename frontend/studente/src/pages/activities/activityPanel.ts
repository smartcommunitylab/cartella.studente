import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { AddActivityPage } from '../addActivity/addActivity'
import { UserService } from '../../services/user.service'
@Component({
  selector: 'activity-panel',
  templateUrl: './activity.html'

})

export class ActivityPanel implements OnInit {
  @Input() activity: StudentExperience;
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
  getActivity(): StudentExperience {
    return this.activity;
  }
  updateActivity(): void {
    this.navCtrl.push(AddActivityPage, { activity: JSON.stringify(this.activity) });
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
}

