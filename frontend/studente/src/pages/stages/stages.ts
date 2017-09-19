import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddStagePage } from '../addStage/addStage';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
@Component({
  selector: 'page-stages',
  templateUrl: 'stages.html'
})
export class StagesPage {
  stages: StudentExperience[] = null;
  order: string = "a-z";
  icon = "ios-arrow-down";
  shownStage = null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }

  addNewStage(): void {
    this.navCtrl.push(AddStagePage);
  }

  onDeleted(stageId: string) {
    for (var i = 0; i < this.stages.length; i++) {
      if (this.stages[i].experience.id == stageId) {
        this.stages.splice(i, 1);
      }
    }
  }

  ionViewWillEnter() {
    let loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    loader.present().then(() => {
      this.userService.getUserStages().then(stages => {
        this.stages = stages
        loader.dismiss();
      })
    })
  }

  onSelectChange(selectedValue: any) {
    this.utilsService.sortExperience(selectedValue, this.stages).then(sortedList => {
      this.stages = sortedList;
    })
  }
  
}
