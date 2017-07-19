import { Component, OnInit } from '@angular/core';
import { NavController, LoadingController } from 'ionic-angular';
import { Registration } from '../../classes/Registration.class'
import { Exam } from '../../classes/Exam.class'
import { Experience } from '../../classes/Experience.class'
import { InstitutePage } from '../institute/institute'
import { StagesPage } from '../stages/stages'
import { ActivitiesPage } from '../activities/activities'
import { EventsPage } from '../events/events'
import { MobilitiesPage } from '../mobilities/mobilities'
import { ExamPage } from '../exam/exams'
import { JobsPage } from '../jobs/jobs'
import { CurriculumPage } from '../curriculum/curriculum'
import { CertificationsPage } from '../certifications/certifications'
import { UserService } from '../../services/user.service'

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage implements OnInit {

  registrations: Registration[] = [];
  experiences: Experience[] = [];
  constructor(public navCtrl: NavController, private userService: UserService, public loading: LoadingController) {

  }
  openRegistration(registration: Registration): void {
    this.navCtrl.push(InstitutePage, { paramRegistration: registration })
  }
  openExam(): void {
    this.navCtrl.push(ExamPage);
  }
  openStages(): void {
    this.navCtrl.push(StagesPage)
  }
  openJobs(): void {
    this.navCtrl.push(JobsPage)
  }
  openMobilities(): void {
    this.navCtrl.push(MobilitiesPage)
  }
  openActivities(): void {
    this.navCtrl.push(ActivitiesPage)
  }
  openEvents(): void {
    this.navCtrl.push(EventsPage)
  }
  openCV(): void {
    this.navCtrl.push(CurriculumPage)
  }
  openCertifications(): void {
    this.navCtrl.push(CertificationsPage)
  }

  ngOnInit(): void {
    let loader = this.loading.create({
      content: 'Getting latest entries...',
    });
    loader.present().then(() => {
      this.userService.getUserRegistrations().then(registrations => {
        this.registrations = registrations
        loader.dismiss();
      }).catch((ex) => {
        console.error('Error fetching users', ex);
        loader.dismiss();
      });;
    })
  }

}
