import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { AddStagePage } from '../addStage/addStage';
import {TranslateService} from 'ng2-translate';
@Component({
  selector: 'page-stages',
  templateUrl: 'stages.html'
})
export class StagesPage  {
  stages:StudentExperience[]=[];
  order=true;
icon="ios-arrow-down";
  shownStage=null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController,private alertCtrl: AlertController, private translate: TranslateService){
  }

toggleDetails(stage) {
    if (this.isDetailsShown(stage)) {
        this.shownStage = null;
    } else {
        this.shownStage = stage;
    }
};
isDetailsShown(stage) {
    return this.shownStage === stage;
};

  addNewStage(): void {
    this.navCtrl.push(AddStagePage);
  }

updateStage(stage): void {
    this.navCtrl.push(AddStagePage, {stage:JSON.stringify(stage)});
  }

  deleteStage(stage): void {
   //ask confirmation

      let alert = this.alertCtrl.create({
    title: this.translate.instant('alert_delete_stage_title'),
    message:  this.translate.instant('alert_delete_stage_message'),
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
              this.userService.deleteStage(stage).then(stage =>{
       //remove stage from stage
      for (var i=0; i<this.stages.length;i++)
        {
          if (this.stages[i].id==stage.id)
            {
                 this.stages.splice(i, 1);
            }
          }
        loader.dismiss();
        })
        }
      }
    ]
  });
  alert.present();

  }
//loaded when it is showed
ionViewWillEnter () {
    let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
  loader.present().then(() => {
        this.userService.getUserStages().then(stages =>{
        this.stages=stages
          loader.dismiss();
  })
  })
}
}
