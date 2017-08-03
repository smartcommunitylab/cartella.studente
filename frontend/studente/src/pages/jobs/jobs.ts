import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddJobPage } from '../addJob/addJob';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
@Component({
  selector: 'page-jobs',
  templateUrl: 'jobs.html'
})
export class JobsPage {
  jobs: StudentExperience[] = null;
  order = true;
  icon = "ios-arrow-down";
  shownJob = null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }

  addNewJob(): void {
    this.navCtrl.push(AddJobPage);
  }

  onDeleted(jobId: string) {
    for (var i = 0; i < this.jobs.length; i++) {
      if (this.jobs[i].experience.id == jobId) {
        this.jobs.splice(i, 1);
      }
    }
  }

  ionViewWillEnter() {
    let loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    loader.present().then(() => {
      this.userService.getUserJobs().then(jobs => {
        this.jobs = jobs
        loader.dismiss();
      })
    })
  }
}
