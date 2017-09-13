import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NavController, NavParams, LoadingController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UserService } from '../../services/user.service';
import { Student } from '../../classes/Student.class';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { Registration } from '../../classes/Registration.class';
import { Experience } from '../../classes/Experience.class';
import { Document } from '../../classes/Document.class';
import { ExperienceContainer } from '../../classes/ExperienceContainer.class';

@Component({
  selector: 'page-curriculum',
  templateUrl: 'curriculum.html'
})
export class CurriculumPage implements OnInit {

  student: Student = new Student();
  loader = null;

  experiences: StudentExperience[] = [];
  trainings: StudentExperience[] = [];
  registrations: Registration[] = [];
  skills: StudentExperience[] = [];
  attachments: Document[] = [];

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    public loading: LoadingController,
    private translate: TranslateService) { }

  ngOnInit(): void { }

  ionViewWillEnter() {

    this.experiences = [];
    this.skills = [];
    this.trainings = [];
    this.attachments = [];

    this.showSpinner();
    this.userService.getUserInfo().then(student => {
      this.student = student;

      Promise.all([
        this.initExperiences(),
        this.initTrainingRegistrations(),
        this.initSkills(),
      ]).then(value => {
        // load documents only after experiences gets loaded.
        this.initAttachments().then(resp => {
          console.log("total number of attachments is " + this.attachments.length);
          this.hideSpinner();
        })

      });

    }
    );
  }

  private showSpinner() {
    this.loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    this.loader.present().catch(() => { });
  }

  private hideSpinner() {
    if (this.loading !== undefined) {
      this.loader.dismiss().catch(() => { });
    }
  }

  private initExperiences() {

    return new Promise<StudentExperience[]>((resolve, reject) => {
      // call experience API (STAGE + JOB).
      var p1 = this.userService.getUserStages();

      var p2 = this.userService.getUserJobs();

      Promise.all([p1, p2]).then(values => {
        for (var v = 0; v < values.length; v++) {
          this.experiences = this.experiences.concat(values[v]);
          console.log(values[v].length);//https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all
        }
        resolve();
        console.log("total number of experiences (job + stage) is " + this.experiences.length);
      }).catch((error: any): any => {
        reject()

      })


    });


  }

  private initSkills(): Promise<any> {
    debugger;
    // call skills API.
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.userService.getUserCertifications().then(cert => {
        for (var c = 0; c < cert.length; c++) {
          if (cert[c].experience.attributes.type.toLowerCase() == "lang") {
            this.skills.push(cert[c]);
          }
        }
        resolve(this.skills);
        console.log("total number of certificates with language type is " + this.skills.length);
      }).catch((error: any): any => {
        reject()

      })
    })

  }

  private initTrainingRegistrations() {
    debugger;

    // call training API.
    return new Promise<StudentExperience[]>((resolve, reject) => {
      var p1 = this.userService.getUserMobilities();
      var p2 = this.userService.getUserRegistrations();

      Promise.all([p1, p2]).then(values => {
        // mobility.
        this.trainings = this.trainings.concat(values[0]);
        console.log("total number of trainings (mobility) is " + this.trainings.length);
        // registration.
        // this.registrations = this.registrations.concat(values[1]); // registration -> [{registrtions[], teachingUnit}, {registrations[], teachingUnit}..]
        // this.registrations[0].registrations
        console.log("total number of registrations is " + this.registrations.length);
        resolve();
      }).catch((error: any): any => {
        reject()
      });
    });

  }

  private initAttachments() {
    return new Promise<Document[]>((resolve, reject) => {
      for (var e = 0; e < this.experiences.length; e++) {
        if (this.experiences[e].documents)
        this.attachments = this.attachments.concat(this.experiences[e].documents);
      }
      for (var t = 0; t < this.trainings.length; t++) {
        if (this.trainings[t].documents)
          this.attachments = this.attachments.concat(this.trainings[t].documents);
      }
      for (var s = 0; s < this.skills.length; s++) {
        if (this.skills[s].documents)
          this.attachments = this.attachments.concat(this.skills[s].documents);
      }
     
      resolve();
    });

  }

  toggle(event, experience) {
    // call PUT CV update.
    if (experience.checked) {
      alert("CHECKED")
    } else {
      alert("UNCHECKED")
    }

  }

  selectAll(expList) {
    // call PUT CV update.
    for (var i = 0; i < expList.length; i++) {
      expList[i].checked = true;
    }
  }

  downloadCV() {
    alert(this.experiences[0].checked);
    alert(this.trainings[0].checked);
    alert(this.skills[0].checked);
    alert(this.attachments[0].checked);
  }

}
