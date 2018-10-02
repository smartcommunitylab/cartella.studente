import { Component, OnInit, Injectable } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NavController, NavParams, LoadingController } from 'ionic-angular';
import { Http, BaseRequestOptions, RequestOptions } from '@angular/http'
import { TranslateService } from 'ng2-translate';

import { ConfigService } from '../../services/config.service'
import { ExperienceTypes } from '../../assets/conf/expTypes'
import { UserService } from '../../services/user.service';
import { Student } from '../../classes/Student.class';
import { StudentExperience } from '../../classes/StudentExperience.class';
import { UserRegistration } from './UserRegistration';
import { Registration } from './Registration';
import { Experience } from '../../classes/Experience.class';
import { Document } from '../../classes/Document.class';
import { ExperienceContainer } from '../../classes/ExperienceContainer.class';
import { Curriculum } from '../../classes/Curriculum.class';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service';

@Injectable()
export class DefaultRequestOptions extends BaseRequestOptions {

  constructor() {
    super();
    this.headers.set('Accept', 'application/json');
    this.headers.set('Content-Type', 'application/json');
    this.headers.set('Authorization', `Bearer ${sessionStorage.getItem('access_token')}`);
  }
}

@Component({
  selector: 'page-curriculum',
  templateUrl: 'curriculum.html'
})
export class CurriculumPage implements OnInit {

  student: Student = new Student();
  loader = null;
  profilePicture: string;

  experiences: StudentExperience[] = [];
  trainings: StudentExperience[] = [];
  registrations: Registration[] = [];
  skills: StudentExperience[] = [];
  attachments: Document[] = [];

  userRegistration: UserRegistration[] = [];
  agreegatedUserRegsMap = {};

  selectAllProfExp: Boolean = false;
  selectAllRegistrationsTrainings: Boolean = false;
  selectAllSkills: Boolean = false;
  selectAllAttachments: Boolean = false;

  constructor(public navCtrl: NavController,
    public params: NavParams,
    private userService: UserService,
    public loading: LoadingController,
    private translate: TranslateService,
    private http: Http,
    private webAPIConnector: WebAPIConnectorService,
    private config: ConfigService) { }

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
        this.userService.getUserImage()
      ]).then(value => {
        this.userService.getUserImage().then(url => {
          if (value[3]) {
            this.profilePicture = url;
          } else {
            this.profilePicture = "assets/images/profile-pictures.png";
          }
          this.hideSpinner();
        }
        );
      });
    }
    );
  }

  /**
   * initialize experiences(expTYPE: STAGE, JOB)
   */
  private initExperiences() {

    return new Promise<StudentExperience[]>((resolve, reject) => {
      // call experience API (STAGE + JOB).
      var p1 = this.userService.getUserStages();
      var p2 = this.userService.getUserJobs();

      Promise.all([p1, p2]).then(values => {
        for (var v = 0; v < values.length; v++) {
          this.experiences = this.experiences.concat(values[v]);
          //console.log(values[v].length);//https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all
        }
        resolve();
        //console.log("total number of experiences (job + stage) is " + this.experiences.length);
      }).catch((error: any): any => {
        reject()
      })
    });

  }

  /**
   * initialize skills(expTYPE: CERTIFICATION)
   */
  private initSkills(): Promise<any> {
    // call skills API.
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.userService.getUserCertifications().then(cert => {
        for (var c = 0; c < cert.length; c++) {
          if (cert[c].experience.attributes.type.toLowerCase() == "lang") {
            this.skills.push(cert[c]);
          }
        }
        resolve(this.skills);
        //console.log("total number of certificates with language type is " + this.skills.length);
      }).catch((error: any): any => {
        reject()

      })
    })

  }

  /**
   * initialize trainings(expTYPE: MOBILITY) and registrations(/registration).
   */
  private initTrainingRegistrations() {
    // call training API.
    return new Promise<StudentExperience[]>((resolve, reject) => {
      var p1 = this.userService.getUserMobilities();
      var p2 = this.getUserRegistrations();

      Promise.all([p1, p2]).then(values => {

        // mobility.
        this.trainings = this.trainings.concat(values[0]);
        //console.log("total number of trainings (mobility) is " + this.trainings.length);

        // registration.
        this.registrations = this.agreegatedRegs(values[1]);

        // for (var r = 0; r < values[1].length; r++) {
        //   // agreegate registrations.
        //   this.registrations
        //   this.registrations = this.registrations.concat(values[1][r].registrations);
        // }
        //console.log("total number of registrations is " + this.registrations.length);

        resolve();
      }).catch((error: any): any => {
        reject()
      });
    });

  }

  agreegatedRegs(unAgreegated) {
    var agreegatedRegs: Registration[] = [];

    for (var r = 0; r < unAgreegated.length; r++) {

      for (var sr = 0; sr < unAgreegated[r].registrations.length; sr++) {

        var key = this.getKey(unAgreegated[r].registrations[sr]);

        if (!this.agreegatedUserRegsMap[key]) {
          this.agreegatedUserRegsMap[key] = [];
        }

        this.agreegatedUserRegsMap[key].push(unAgreegated[r].registrations[sr]);
      }

    }

    // order sub list
    for (var key in this.agreegatedUserRegsMap) {

      this.agreegatedUserRegsMap[key].sort(function (reg1, reg2) {

        if (reg1.dateFrom > reg2.dateFrom) {
          return -1;

        } else if (reg1.dateFrom < reg2.dateFrom) {
          return 1;

        } else {
          return 0;
        }
      });;

    }

    // assign it to viewable list after modification.
    for (var key in this.agreegatedUserRegsMap) {

      var subList = this.agreegatedUserRegsMap[key];

      if (subList && subList.length > 0) {
        var agreegatedRegistration: Registration;

        agreegatedRegistration = subList[subList.length - 1];
        agreegatedRegistration.dateTo = subList[0].dateTo;

        agreegatedRegs.push(agreegatedRegistration);
      }

    }

    return agreegatedRegs;

  }

  getUserRegistrations(): Promise<UserRegistration[]> {

    return new Promise<UserRegistration[]>((resolve, reject) => {
      let options = new DefaultRequestOptions();
      let url: string = this.config.getConfig('apiUrl') + 'student/' + this.userService.getUserId() + '/registration';

      return this.http.get(url, options).timeout(5000).toPromise().then(response => {
        var tmp: UserRegistration[] = [];
        tmp = response.json();
        resolve(tmp);// registration -> [{registrtions[], teachingUnit}, {registrations[], teachingUnit}..]
      }).catch(response => {
        return this.handleError;
      });
    })
  }

  getKey(regListElement) {
    return regListElement.teachingUnit.name + "_" + regListElement.course;
  }

  /**
   * initialize attachments(documents) from experiences, certificates
   */
  private initAttachments() {

    var oldCount = this.attachments.length;

    this.attachments = [];
    
    return new Promise<Document[]>((resolve, reject) => {
      for (var e = 0; e < this.experiences.length; e++) {
        if (this.experiences[e].documents && this.experiences[e].checked)
          this.attachments = this.attachments.concat(this.experiences[e].documents);
      }
      for (var t = 0; t < this.trainings.length; t++) {
        if (this.trainings[t].documents && this.trainings[t].checked)
          this.attachments = this.attachments.concat(this.trainings[t].documents);
      }
      for (var s = 0; s < this.skills.length; s++) {
        if (this.skills[s].documents && this.skills[s].checked)
          this.attachments = this.attachments.concat(this.skills[s].documents);
      }

      if (this.attachments.length != oldCount) {
        this.selectAllAttachments = false;
      }

      resolve();
    }).catch(error => {
      return this.handleError;
    });

  }



  selectAllProfessionExperience(event, expList) {

    // call PUT CV update.
    for (var i = 0; i < expList.length; i++) {
      expList[i].checked = this.selectAllProfExp;
    }
    this.initAttachments().then(resp => { });
  }

  toggleProfessionExperience(event, experience) {
    // call PUT CV update.
    if (experience.checked && experience.documents) {
      // alert("CHECKED")
      this.attachments = this.attachments.concat(experience.documents);
    } else if (!experience.checked && experience.documents) {
      // uncheck SELECTALL if one is unselected.
      this.selectAllProfExp = false;
      // unchecked the documents and remove from the attachment list.
      for (var d = 0; d < experience.documents.length; d++) {
        experience.documents[d].checked = false;
      }
      this.initAttachments().then(resp => { });
    }
  }

  selectAllRegistrationTrainingsObjs(event, regs, trainings) {

    // call PUT CV update.
    for (var i = 0; i < regs.length; i++) {
      regs[i].checked = this.selectAllRegistrationsTrainings;
    }

    for (var i = 0; i < trainings.length; i++) {
      trainings[i].checked = this.selectAllRegistrationsTrainings;
    }

    this.initAttachments().then(resp => { });
  }

  toggleRegistration(event, experience) {
    // call PUT CV update.
    if (experience.checked && experience.documents) {
      // alert("CHECKED")
      this.attachments = this.attachments.concat(experience.documents);
    } else if (!experience.checked) { //&& experience.documents -> since registration has no documents.
      // uncheck SELECTALL if one is unselected.
      this.selectAllRegistrationsTrainings = false;
      // unchecked the documents and remove from the attachment list.
      // for (var d = 0; d < experience.documents.length; d++) {
      //   experience.documents[d].checked = false;
      // }
      // this.initAttachments().then(resp => { });
    }
  }

  toggleTraining(event, experience) {
    // call PUT CV update.
    if (experience.checked && experience.documents) {
      // alert("CHECKED")
      this.attachments = this.attachments.concat(experience.documents);
    } else if (!experience.checked && experience.documents) {
      // uncheck SELECTALL if one is unselected.
      this.selectAllRegistrationsTrainings = false;
      // unchecked the documents and remove from the attachment list.
      for (var d = 0; d < experience.documents.length; d++) {
        experience.documents[d].checked = false;
      }
      this.initAttachments().then(resp => { });
    }
  }


  selectAllSkillsObjs(event, expList) {

    // call PUT CV update.
    for (var i = 0; i < expList.length; i++) {
      expList[i].checked = this.selectAllSkills;
    }
    this.initAttachments().then(resp => { });
  }

  toggleSkill(event, experience) {
    // call PUT CV update.
    if (experience.checked && experience.documents) {
      // alert("CHECKED")
      this.attachments = this.attachments.concat(experience.documents);
    } else if (!experience.checked && experience.documents) {
      // uncheck SELECTALL if one is unselected.
      this.selectAllSkills = false;
      // unchecked the documents and remove from the attachment list.
      for (var d = 0; d < experience.documents.length; d++) {
        experience.documents[d].checked = false;
      }
      this.initAttachments().then(resp => { });
    }
  }

  selectAllAttachmentsObjs(event, expList) {

    // call PUT CV update.
    for (var i = 0; i < expList.length; i++) {
      expList[i].checked = this.selectAllAttachments;
    }
    this.initAttachments().then(resp => { });
  }

  toggleAttachment(event, experience) {
    // call PUT CV update.
    if (!experience.checked) {
      // uncheck SELECTALL if one is unselected.
      this.selectAllAttachments = false;
    }
  }

  toggle(event, experience) {
    // call PUT CV update.
    if (experience.checked && experience.documents) {
      // alert("CHECKED")
      this.attachments = this.attachments.concat(experience.documents);
    } else if (!experience.checked && experience.documents) {
      // unchecked the documents and remove from the attachment list.
      for (var d = 0; d < experience.documents.length; d++) {
        experience.documents[d].checked = false;
      }
      this.initAttachments().then(resp => { });
    }
  }

  selectAll(event, expList) {

    // call PUT CV update.
    for (var i = 0; i < expList.length; i++) {
      expList[i].checked = true;
    }
    this.initAttachments().then(resp => { });
  }

  selectAllDocument() {
    for (var d = 0; d < this.attachments.length; d++) {
      this.attachments[d].checked = true;
    }
  }

  selectAllTrainingRegistration() {
    // select all trainings.
    for (var t = 0; t < this.trainings.length; t++) {
      this.trainings[t].checked = true;
    }
    // select all registrations.
    for (var r = 0; r < this.registrations.length; r++) {
      this.registrations[r].checked = true;
    }

    this.initAttachments().then(resp => { });

  }

  downloadCV() {

    /** save and then download CV.**/

    // post body.
    var post = {};

    //1. collect checked experiences.
    var studentExperienceIdMap = {};
    var job = [];
    var stage = [];
    for (var e = 0; e < this.experiences.length; e++) {
      if (this.experiences[e].checked) {
        if (this.experiences[e].experience.type.toUpperCase() == ExperienceTypes.EXP_TYPE_STAGE) {
          stage.push(this.experiences[e].id);
        } else if (this.experiences[e].experience.type.toUpperCase() == ExperienceTypes.EXP_TYPE_JOB) {
          job.push(this.experiences[e].id);
        }
      }
    }

    //2. collect checked certificates.
    var certification = [];
    for (var c = 0; c < this.skills.length; c++) {
      if (this.skills[c].checked) {
        certification.push(this.skills[c].id);
      }


    }
    //3. collect documents.
    var storageIdList = [];
    for (var d = 0; d < this.attachments.length; d++) {
      if (this.attachments[d].checked) {
        storageIdList.push(this.attachments[d].storageId);
      }
    }

    //4. collect checked trainings(MOBILITY)
    var mobility = [];
    for (var t = 0; t < this.trainings.length; t++) {
      if (this.trainings[t].checked) {
        mobility.push(this.trainings[t].id);
      }
    }

    //5. collect checked registrations
    var registrationIdList = [];
    for (var r = 0; r < this.registrations.length; r++) {
      if (this.registrations[r].checked) {
        // take all registration for the key in map.
        var key = this.getKey(this.registrations[r]);
        for (var cr = 0; cr < this.agreegatedUserRegsMap[key].length; cr++) {
          console.log(this.agreegatedUserRegsMap[key][cr].id);
          registrationIdList.push(this.agreegatedUserRegsMap[key][cr].id);
        }

        // registrationIdList.push(this.registrations[r].id);
      }
    }

    studentExperienceIdMap['STAGE'] = stage;
    studentExperienceIdMap['JOB'] = job;
    studentExperienceIdMap['CERTIFICATION'] = certification;
    studentExperienceIdMap['MOBILITY'] = mobility;
    post['studentExperienceIdMap'] = studentExperienceIdMap;
    post['registrationIdList']
    post['registrationIdList'] = registrationIdList;
    post['storageIdList'] = storageIdList;


    console.log(JSON.stringify(post));

    // call GET CV.
    var curriculum: Curriculum;
    let options = new DefaultRequestOptions();
    let url: string = this.config.getConfig('apiUrl') + 'student/' + this.userService.getUserId() + '/registration';

    this.userService.getUserCV().then(cv => {
      curriculum = cv;
      if (curriculum.id) {
        // update CV.
        post['id'] = curriculum.id;
        this.userService.updateUserCV(post).then(updatedCV => {

          if (updatedCV.id == curriculum.id) {
            this.userService.downloadUserCVInODTFormat();
          }
        });

      } else {
        // create CV.
        this.userService.addUserCV(post).then(addedCV => {

          if (addedCV.id) {
            this.userService.downloadUserCVInODTFormat();
          }
        }).catch(error => {
          return this.handleError;
        })
      }


    }).catch(error => {
      // create CV.
      this.userService.addUserCV(post).then(addedCV => {
        if (addedCV.id) {
          this.userService.downloadUserCVInODTFormat();
        }
      }).catch(error => {
        return this.handleError;
      })
    });

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

  private handleError(error: any): Promise<any> {
    //console.error('An error occurred', error);
    return Promise.reject(error);
  }
}
