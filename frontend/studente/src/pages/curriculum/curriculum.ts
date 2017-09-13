import { Component, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UserService } from '../../services/user.service';
import { Student } from '../../classes/Student.class';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { Experience } from '../../classes/Experience.class';
import { ExperienceContainer } from '../../classes/ExperienceContainer.class';

@Component({
  selector: 'page-curriculum',
  templateUrl: 'curriculum.html'
})
export class CurriculumPage implements OnInit {

  student: Student = new Student();
  loader = null;

  experiences: StudentExperience[] = [];
  skills: StudentExperience[] = [];
  trainings: StudentExperience[] = [];
  attachments: StudentExperience[] = [];

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
      this.initExperiences();
      this.initTrainings();
      this.initSkills();
      this.initAttachments();
      this.hideSpinner()
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
    // call experience API.
    let exp1 = new StudentExperience();
    exp1.experience = new ExperienceContainer();
    exp1.experience.attributes = new Experience();
    exp1.experience.attributes.id = "Experience 1";
    exp1.experience.attributes.instituteId = "ABC";
    this.experiences.push(exp1);
    let exp2 = new StudentExperience();
    exp2.experience = new ExperienceContainer();
    exp2.experience.attributes = new Experience();
    exp2.experience.attributes.id = "Experience 2";
    exp2.experience.attributes.instituteId = "DEF";
    this.experiences.push(exp2);
  }

  private initSkills() {
    // call skills API.
    let skill1 = new StudentExperience();
    skill1.experience = new ExperienceContainer();
    skill1.experience.attributes = new Experience();
    skill1.experience.attributes.id = "Skill 1";
    skill1.experience.attributes.instituteId = "ABC";
    this.skills.push(skill1);
    let skill2 = new StudentExperience();
    skill2.experience = new ExperienceContainer();
    skill2.experience.attributes = new Experience();
    skill2.experience.attributes.id = "SKill 2";
    skill2.experience.attributes.instituteId = "DEF";
    this.skills.push(skill2);
  }

  private initTrainings() {
    // call training API.
    let training1 = new StudentExperience();
    training1.experience = new ExperienceContainer();
    training1.experience.attributes = new Experience();
    training1.experience.attributes.id = "Training 1";
    training1.experience.attributes.instituteId = "ABC";
    this.trainings.push(training1);
    let training2 = new StudentExperience();
    training2.experience = new ExperienceContainer();
    training2.experience.attributes = new Experience();
    training2.experience.attributes.id = "Training 2";
    training2.experience.attributes.instituteId = "DEF";
    this.trainings.push(training2);
  }

  private initAttachments() {
    // call attachment API.
    let attach1 = new StudentExperience();
    attach1.experience = new ExperienceContainer();
    attach1.experience.attributes = new Experience();
    attach1.experience.attributes.id = "attachment 1";
    this.attachments.push(attach1);
    let attach2 = new StudentExperience();
    attach2.experience = new ExperienceContainer();
    attach2.experience.attributes = new Experience();
    attach2.experience.attributes.id = "attachment 2";
    this.attachments.push(attach2);
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
