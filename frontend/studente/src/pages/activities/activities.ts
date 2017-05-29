import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddActivityPage } from '../addActivity/addActivity';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

@Component({
  selector: 'page-activities',
  templateUrl: 'activities.html'
})
export class ActivitiesPage {
  activities: StudentExperience[] = null
  order = true;
  icon = "ios-arrow-down";
  shownActivity = null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }

  toggleDetails(activity) {
    if (this.isDetailsShown(activity)) {
      this.shownActivity = null;
    } else {
      this.shownActivity = activity;
    }
  };
  isDetailsShown(activity) {
    return this.shownActivity === activity;
  };

  addNewActivity(): void {
    this.navCtrl.push(AddActivityPage);
  }

  updateActivity(activity): void {
    this.navCtrl.push(AddActivityPage, { activity: JSON.stringify(activity) });
  }

  onDeleted(stageId: string) {
    for (var i = 0; i < this.activities.length; i++) {
      if (this.activities[i].experience.id == stageId) {
        this.activities.splice(i, 1);
      }
    }
  }
  //loaded when it is showed
  ionViewWillEnter() {
    let loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    loader.present().then(() => {
      this.userService.getUserActivities().then(activities => {
        this.activities = activities
        loader.dismiss();
      })
    })
  }
}
