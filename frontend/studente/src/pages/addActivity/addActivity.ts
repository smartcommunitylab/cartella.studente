import { Component,OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import { Activity } from '../../classes/Activity.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes'
@Component({
  selector: 'page-add-activity',
  templateUrl: 'addActivity.html'
})


export class AddActivityPage implements OnInit {
  experienceContaniner:ExperienceContainer = new ExperienceContainer();
  activity:Activity= new Activity();
  dateFrom=new Date().toISOString();
  dateTo=new Date().toISOString();
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
      let activity = this.params.get('activity');
      if (activity!=null){
        this.experienceContaniner=JSON.parse(this.params.get('activity'));
        this.activity = this.experienceContaniner.attributes as Activity;
        this.dateFrom=new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
        this.dateTo=new Date(this.experienceContaniner.attributes.dateTo).toISOString();
      }
  }
  addActivity(): void {

        //TO DO certification


    this.experienceContaniner.attributes.certified=false;

    this.activity.type=ExperienceTypes.EXP_TYPE_ACTIVITY;
    this.activity.duration=10
    this.activity.geocode=[0,0]
    this.activity.dateFrom=new Date(this.dateFrom).getTime();
    this.activity.dateTo=new Date(this.dateTo).getTime();
    this.experienceContaniner.attributes=this.activity;

    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateActivity(this.experienceContaniner).then(activity=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
      }
    else {
      this.userService.addActivity(this.experienceContaniner).then(activity=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
    }
  }
  discard(): void {
    this.navCtrl.pop();
  }
}
