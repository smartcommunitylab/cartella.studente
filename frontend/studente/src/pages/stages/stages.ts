import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import { ExperienceContainer } from '../../classes/ExperienceContainer.class';
import { AddStagePage } from '../addStage/addStage';

@Component({
  selector: 'page-stages',
  templateUrl: 'stages.html'
})
export class StagesPage  {
  stages:ExperienceContainer[]=[];
  order=true;
icon="ios-arrow-down";
  showDetails:boolean=false;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController){
  }

  toggleDetails():void {
    this.showDetails=!this.showDetails;
    if (this.showDetails)
      {
        this.icon="ios-arrow-up"
      }
    else {
        this.icon="ios-arrow-down"
      }
  }
  addNewStage(): void {
    this.navCtrl.push(AddStagePage);
  }

updateStage(stage): void {
    this.navCtrl.push(AddStagePage, {stage:JSON.stringify(stage)});
  }

  deleteStage(stage): void {
   //ask confirmation
    this.userService.deleteStage(stage).then(stage =>{
       //remove stage from stage
      for (var i=0; i<this.stages.length;i++)
        {
          if (this.stages[i].id==stage.id)
            {
                 this.stages.splice(i, 1);
            }
        }
  })
  }
//loaded when it is showed
ionViewWillEnter () {
    let loader = this.loading.create({
    content: 'Getting latest entries...',
  });
  loader.present().then(() => {
        this.userService.getUserStages().then(stages =>{
        this.stages=stages
          loader.dismiss();
  })
  })
}
}
