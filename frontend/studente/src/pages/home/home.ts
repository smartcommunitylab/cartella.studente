import { Component,OnInit } from '@angular/core';
import { NavController } from 'ionic-angular';
import {Registration } from '../../classes/Registration.interface'
import {Exam } from '../../classes/Exam.interface'
import {Experience } from '../../classes/Experience.interface'
import {InstitutePage } from '../institute/institute'
import {StagesPage } from '../stages/stages'
import {ActivitiesPage } from '../activities/activities'
import {EventsPage } from '../events/events'
import {CurriculumPage } from '../curriculum/curriculum'
import {CertificationsPage } from '../certifications/certifications'
import {UserService} from '../../services/user.service'
@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage implements OnInit{

  registrations:Registration[]=[];
  exams:Exam[]=[];
  experiences:Experience[]=[];
  constructor(public navCtrl: NavController, private userService: UserService ) {

  }
openRegistration(registration: Registration):void {
  this.navCtrl.push(InstitutePage,{paramRegistration:registration})
}
  openStages():void {
  this.navCtrl.push(StagesPage)
}
  openActivities():void {
  this.navCtrl.push(ActivitiesPage)
}
  openEvents():void {
  this.navCtrl.push(EventsPage)
}
    openCV():void {
  this.navCtrl.push(CurriculumPage)
}
    openCertifications():void {
  this.navCtrl.push(CertificationsPage)
}

 ngOnInit(): void {
   //load left column
    this.userService.getUserRegistrations().then(registrations =>{
      this.registrations=registrations
    });
    this.userService.getUserExams().then(exams =>{
      this.exams=exams
    });
  }
}
