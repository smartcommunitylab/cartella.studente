import { Component, OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import {Activity} from '../../classes/Activity.class'
@Component({
  selector: 'page-activities',
  templateUrl: 'activities.html'
})
export class ActivitiesPage implements OnInit {
  activities:Activity[]=[]
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
     this.userService.getUserActivities().then(activities =>{
      this.activities=activities
    });
  }
}
